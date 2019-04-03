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

package org.bremersee.peregrinus.repository;

import static org.bremersee.peregrinus.entity.FeatureEntity.ID_PATH;
import static org.bremersee.peregrinus.entity.FeatureEntity.MODIFIED_BY_PATH;
import static org.bremersee.peregrinus.entity.FeatureEntity.MODIFIED_PATH;
import static org.bremersee.peregrinus.entity.FeatureEntity.NAME_PATH;
import static org.bremersee.peregrinus.entity.FeatureEntitySettings.FEATURE_ID_PATH;
import static org.bremersee.peregrinus.entity.FeatureEntitySettings.USER_ID_PATH;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collection;
import java.util.Date;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.security.access.AclMapper;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Repository
public class FeatureRepositoryImpl extends AbstractMongoRepository implements FeatureRepository {

  public FeatureRepositoryImpl(
      ReactiveMongoOperations mongoOperations,
      AclMapper<AclEntity> aclMapper) {
    super(mongoOperations, aclMapper);
  }

  @Override
  protected String aclPath() {
    return FeatureEntity.ACL_PATH;
  }

  @Override
  public Mono<FeatureEntity> findFeatureById(final String id) {
    return getMongoOperations().findOne(query(where(ID_PATH).is(id)), FeatureEntity.class);
  }

  @Override
  public Mono<FeatureEntity> findFeatureById(
      @NotNull String id,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    final Query query = queryAnd(
        where(FeatureEntity.ID_PATH).is(id), includePublic, userId, roles, groups, permission);
    return getMongoOperations()
        .findOne(query, FeatureEntity.class);
  }

  @Override
  public Mono<FeatureEntitySettings> findFeatureEntitySettings(
      final String featureId,
      final String userId) {
    return getMongoOperations()
        .findOne(query(featureSettingsCriteria(featureId, userId)), FeatureEntitySettings.class);
  }

  @Override
  public <S extends FeatureEntitySettings> Mono<S> persistFeatureSettings(
      final S featureSettings) {
    return getMongoOperations().save(featureSettings);
  }

  @Override
  public <F extends FeatureEntity> Mono<F> persistFeature(
      final F feature) {
    return getMongoOperations().save(feature);
  }

  @Override
  public Mono<Boolean> renameFeature(
      final String id,
      final String name,
      final String userId) {

    final Query query = query(where(ID_PATH).is(id));
    final Update update = Update.update(NAME_PATH, name)
        .set(MODIFIED_PATH, new Date())
        .set(MODIFIED_BY_PATH, userId);
    return getMongoOperations()
        .findAndModify(query, update, FeatureEntity.class)
        .flatMap(result -> Mono.just(true))
        .switchIfEmpty(Mono.just(false));
  }

  private Criteria featureSettingsCriteria(final String featureId, final String userId) {
    return new Criteria().andOperator(
        where(FEATURE_ID_PATH).is(featureId),
        where(USER_ID_PATH).is(userId));
  }

}