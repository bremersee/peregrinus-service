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

import java.util.Collection;
import java.util.List;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RtePt;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
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
  public <T> Mono<T> findById(final String id, final Class<T> cls) {
    return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), cls)
        .flatMap(this::postLoad);
  }

  @Override
  public <T> Flux<T> findByIds(final Collection<String> ids, final Class<T> cls) {
    return mongoOperations.find(Query.query(Criteria.where("id").in(ids)), cls)
        .flatMap(this::postLoad);
  }

  private <T> Mono<T> postLoad(final T entity) {
    if (entity instanceof Rte) {
      //noinspection unchecked
      return (Mono<T>) postLoadRte((Rte) entity);
    }
    return Mono.just(entity);
  }

  private Mono<Rte> postLoadRte(final Rte entity) {
    return findByIds(entity.getProperties().getRtePtIds(), RtePt.class)
        .collectList()
        .map(rtePts -> {
          entity.getProperties().setRtePts(rtePts);
          return entity;
        });
  }

  @Override
  public <T> Mono<T> persist(final T entity) {
    if (entity instanceof Rte) {
      //noinspection unchecked
      return (Mono<T>) doSaveRte((Rte) entity);
    }
    return mongoOperations.save(entity);
  }

  private Mono<Rte> doSaveRte(final Rte rte) {
    rte.getProperties().getRtePtIds().clear();
    return persistAll(rte.getProperties().getRtePts())
        .map(RtePt::getId)
        .collectList()
        .flatMap(ids -> doSaveRte(rte, ids));
  }

  private Mono<Rte> doSaveRte(final Rte rte, final List<String> rtePtIds) {
    rte.getProperties().setRtePtIds(rtePtIds);
    return mongoOperations.save(rte);
  }

  @Override
  public <T> Flux<T> persistAll(final Collection<T> entities) {
    return Flux.fromIterable(entities).flatMap(this::persist);
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
