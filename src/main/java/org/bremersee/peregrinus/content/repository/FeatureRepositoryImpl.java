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

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntity;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.content.repository.mapper.FeatureMapper;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.security.access.repository.MongoRepositoryUtils;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Repository
public class FeatureRepositoryImpl implements FeatureRepository {

  private final ReactiveMongoOperations mongoOperations;

  private final List<FeatureMapper> featureMappers;

  public FeatureRepositoryImpl(
      final ReactiveMongoOperations mongoOperations,
      final List<FeatureMapper> featureMappers) {
    this.mongoOperations = mongoOperations;
    this.featureMappers = featureMappers;
  }

  @Override
  public <F extends Feature> Mono<F> persist(final F feature, final String userId) {

    Assert.notNull(feature, "Feature must not be null.");
    Assert.notNull(feature.getProperties(), "Feature properties must not be null.");
    feature.getProperties().setModified(OffsetDateTime.now(Clock.systemUTC()));
    return Mono.zip(
        mapFeature(feature),
        mapFeatureSettings(feature.getProperties().getSettings(), userId))
        .flatMap(this::persist);
  }

  private <F extends Feature> Mono<F> persist(
      final Tuple2<? extends FeatureEntity, ? extends FeatureEntitySettings> tuple) {

    return mongoOperations
        .save(tuple.getT1())
        .flatMap(entity -> {
          tuple.getT2().setFeatureId(entity.getId());
          return Mono.zip(Mono.just(entity), mongoOperations.save(tuple.getT2()));
        })
        .flatMap(this::mapFeature);
  }

  @Override
  public <F extends Feature> Mono<F> findById(final String id, final String userId) {

    return mongoOperations
        .findOne(Query.query(Criteria.where("id").is(id)), FeatureEntity.class)
        .flatMap(featureEntity -> Mono.zip(
            Mono.just(featureEntity),
            findSettings(featureEntity, userId)
        ))
        .flatMap(this::mapFeature);
  }

  @Override
  public Mono<Boolean> removeById(
      final String id,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {
    final List<Criteria> criteriaList = MongoRepositoryUtils.buildCriteriaList(
        PermissionConstants.DELETE, true, userId, roles, groups);
    final Criteria one = Criteria.where("id").is(id);
    final Criteria two = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    final Criteria oneAndTwo = new Criteria().andOperator(one, two);
    return mongoOperations
        .remove(Query.query(oneAndTwo), FeatureEntity.class)
        .map(deleteResult -> deleteResult.getDeletedCount() > 0L)
        .zipWith(mongoOperations
            .remove(Query.query(featureSettingsCriteria(id, userId)), FeatureEntitySettings.class))
        .map(Tuple2::getT1);
  }

  @Override
  public Mono<Boolean> updateName(
      final String id,
      final String name,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final List<Criteria> criteriaList = MongoRepositoryUtils.buildCriteriaList(
        PermissionConstants.WRITE, true, userId, roles, groups);
    final Criteria one = Criteria.where("id").is(id);
    final Criteria two = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    final Criteria oneAndTwo = new Criteria().andOperator(one, two);
    final Update update = new Update()
        .set("properties.modified", OffsetDateTime.now(Clock.systemUTC()))
        .set("properties.name", name);
    return mongoOperations.findAndModify(
        Query.query(oneAndTwo),
        update,
        FeatureEntity.class)
        .flatMap(entity -> Mono.just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> updateAccessControl(
      final String id,
      final AccessControl accessControl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final List<Criteria> criteriaList = MongoRepositoryUtils.buildCriteriaList(
        PermissionConstants.ADMINISTRATION, true, userId, roles, groups);
    final Criteria one = Criteria.where("id").is(id);
    final Criteria two = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    final Criteria oneAndTwo = new Criteria().andOperator(one, two);
    final Update update = new Update()
        .set("properties.modified", OffsetDateTime.now(Clock.systemUTC()))
        .set("properties.accessControl",
            new AccessControlEntity(accessControl.ensureAdminAccess()));
    return mongoOperations.findAndModify(
        Query.query(oneAndTwo),
        update,
        FeatureEntity.class)
        .flatMap(entity -> Mono.just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> updateNameAndAccessControl(
      final String featureId,
      final String name,
      final AccessControlDto accessControl) {

    if (!StringUtils.hasText(featureId) || (!StringUtils.hasText(name) && accessControl == null)) {
      return Mono.just(Boolean.FALSE);
    }
    Update update = new Update()
        .set("properties.modified", OffsetDateTime.now(Clock.systemUTC()));
    if (StringUtils.hasText(name)) {
      update = update.set("properties.name", name);
    }
    if (accessControl != null) {
      update = update.set("properties.accessControl", accessControl.ensureAdminAccess());
    }
    return mongoOperations.findAndModify(
        Query.query(Criteria.where("id").is(featureId)),
        update,
        FeatureEntity.class)
        .flatMap(entity -> Mono.just(Boolean.TRUE));
  }

  private Mono<? extends FeatureEntitySettings> findSettings(
      final FeatureEntity featureEntity, final String userId) {
    return mongoOperations
        .findOne(Query.query(featureSettingsCriteria(featureEntity.getId(), userId)),
            FeatureEntitySettings.class)
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
        Criteria.where("featureId").is(featureId),
        Criteria.where("userId").is(userId));
  }

}
