/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.peregrinus.content.repository;

import static org.bremersee.security.access.PermissionConstants.ADMINISTRATION;
import static org.bremersee.security.access.PermissionConstants.DELETE;
import static org.bremersee.security.access.PermissionConstants.WRITE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.util.Assert.notNull;
import static reactor.core.publisher.Mono.just;
import static reactor.core.publisher.Mono.zip;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.peregrinus.repository.AbstractMongoRepository;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureProperties;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.content.repository.mapper.FeatureMapper;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclMapper;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Repository
public class FeatureRepositoryImpl extends AbstractMongoRepository implements FeatureRepository {

  private final List<FeatureMapper> featureMappers;

  public FeatureRepositoryImpl(
      final ReactiveMongoOperations mongoOperations,
      final AclMapper<AclEntity> aclMapper,
      final List<FeatureMapper> featureMappers) {

    super(mongoOperations, aclMapper);
    notNull(featureMappers, "Feature mapper must not be null.");
    this.featureMappers = featureMappers;
  }

  private ReactiveMongoOperations mongo() {
    return getMongoOperations();
  }

  private AclMapper<AclEntity> aclMapper() {
    return getAclMapper();
  }

  @Override
  protected String aclPath() {
    return "properties.acl";
  }

  @Override
  public <F extends Feature> Mono<F> persist(final F feature, final String userId) {

    notNull(feature, "Feature must not be null.");
    notNull(feature.getProperties(), "Feature properties must not be null.");
    final FeatureProperties props = feature.getProperties();
    props.setModified(OffsetDateTime.now(Clock.systemUTC()));
    return zip(mapFeature(feature), mapFeatureSettings(props.getSettings(), userId))
        .flatMap(this::persist);
  }

  private <F extends Feature> Mono<F> persist(
      final Tuple2<? extends FeatureEntity, ? extends FeatureEntitySettings> tuple) {

    return mongo()
        .save(tuple.getT1())
        .flatMap(entity -> {
          tuple.getT2().setFeatureId(entity.getId());
          return zip(just(entity), mongo().save(tuple.getT2()));
        })
        .flatMap(this::mapFeature);
  }

  @Override
  public <F extends Feature> Mono<F> findById(final String id, final String userId) {

    return mongo()
        .findOne(query(where("id").is(id)), FeatureEntity.class)
        .flatMap(featureEntity -> zip(just(featureEntity), findSettings(featureEntity, userId)))
        .flatMap(this::mapFeature);
  }

  @Override
  public Mono<Boolean> removeById(
      final String id,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Query query = queryAnd(where("id").is(id), true, userId, roles, groups, DELETE);
    return mongo()
        .remove(query, FeatureEntity.class)
        .map(deleteResult -> deleteResult.getDeletedCount() > 0L)
        .zipWith(mongo()
            .remove(query(featureSettingsCriteria(id, userId)), FeatureEntitySettings.class))
        .map(Tuple2::getT1);
  }

  @Override
  public Mono<Boolean> updateName(
      final String id,
      final String name,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Update update = Update.update("properties.name", name)
        .set("properties.modified", OffsetDateTime.now(Clock.systemUTC()));
    final Query query = queryAnd(where("id").is(id), true, userId, roles, groups, WRITE);
    return update(update, query);
  }

  @Override
  public Mono<Boolean> updateAccessControl(
      final String id,
      final AccessControlList acl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Update update = null; //createUpdate(aclMapper().map(acl), userId);
    final Query query = queryAnd(where("id").is(id), true, userId, roles, groups, ADMINISTRATION);
    return update(update, query);
  }

  private Mono<Boolean> update(final Update update, final Query query) {
    return mongo()
        .findAndModify(query, update, FeatureEntity.class)
        .flatMap(entity -> just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> updateNameAndAccessControl(
      final String featureId,
      final String name,
      final AccessControlList acl) {

    if (featureId == null || (name == null && acl == null)) {
      return just(Boolean.FALSE);
    }
    Update update = Update.update("properties.modified", OffsetDateTime.now(Clock.systemUTC()));
    if (StringUtils.hasText(name)) {
      update = update.set("properties.name", name);
    }
    if (acl != null) {
      update = null; //extendUpdate(aclMapper().map(acl), update);
    }
    return mongo()
        .findAndModify(query(where("id").is(featureId)), update, FeatureEntity.class)
        .flatMap(entity -> just(Boolean.TRUE));
  }

  private Mono<? extends FeatureEntitySettings> findSettings(
      final FeatureEntity featureEntity, final String userId) {

    final Query query = query(featureSettingsCriteria(featureEntity.getId(), userId));
    return mongo()
        .findOne(query, FeatureEntitySettings.class)
        .switchIfEmpty(findFeatureMapper(featureEntity).defaultSettings(featureEntity, userId));
  }

  private Mono<? extends FeatureEntity> mapFeature(final Feature feature) {
    return findFeatureMapper(feature).mapFeature(feature);
  }

  private Mono<? extends FeatureEntitySettings> mapFeatureSettings(
      final FeatureSettings featureSettings,
      final String userId) {
    return findFeatureMapper(featureSettings).mapFeatureSettings(featureSettings, userId);
  }

  private <F extends Feature> Mono<F> mapFeature(
      final Tuple2<? extends FeatureEntity, ? extends FeatureEntitySettings> tuple) {
    //noinspection unchecked
    return (Mono<F>) findFeatureMapper(tuple).mapFeatureEntity(tuple.getT1(), tuple.getT2());
  }

  private FeatureMapper findFeatureMapper(final Object obj) {
    return featureMappers
        .stream()
        .filter(featureMapper -> featureMapper.supports(obj))
        .findAny()
        .orElseThrow(RuntimeException::new);
  }

  private Criteria featureSettingsCriteria(String featureId, String userId) {
    return new Criteria().andOperator(
        where("featureId").is(featureId),
        where("userId").is(userId));
  }

}
