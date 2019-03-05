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

import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Repository
public class FeatureRepositoryImpl implements FeatureRepository {

  private ReactiveMongoOperations mongoOperations;

  public FeatureRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  @Override
  public <T> Mono<T> persist(T entity) {
    return mongoOperations.save(entity);
  }

  @Override
  public Mono<Void> delete(Object entity) {
    return mongoOperations.remove(entity).flatMap(deleteResult -> Mono.empty());
  }

  private Criteria featureSettingsCriteria(String featureId, String userId) {
    return new Criteria().andOperator(
        Criteria.where("featureId").is(featureId),
        Criteria.where("userId").is(userId));
  }

  @Override
  public <T extends FeatureSettings> Mono<T> findFeatureSettings(
      Class<T> clazz,
      String featureId,
      String userId) {
    return mongoOperations.findOne(Query.query(featureSettingsCriteria(featureId, userId)), clazz);
  }

  @Override
  public Mono<Void> deleteFeatureSettings(String featureId, String userId) {
    return mongoOperations
        .remove(Query.query(featureSettingsCriteria(featureId, userId)), FeatureSettings.class)
        .flatMap(deleteResult -> Mono.empty());
  }

}
