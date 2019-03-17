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

import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RtePtProperties;
import org.bremersee.peregrinus.content.model.RtePtSettings;
import org.bremersee.peregrinus.content.model.RteSettings;
import org.bremersee.peregrinus.content.repository.entity.RteEntity;
import org.bremersee.peregrinus.content.repository.entity.RteEntityProperties;
import org.bremersee.peregrinus.content.repository.entity.RteEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntity;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntityProperties;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntitySettings;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

/**
 * @author Christian Bremer
 */
public class RteRepositoryImpl extends AbstractFeatureRepositoryImpl implements RteRepository {

  public RteRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    super(mongoOperations);
  }

  public Mono<RtePt> persistRtePt(final RtePt rtePt, final String userId) {
    final RtePtEntity featureEntity = mapFeature(rtePt, RtePtEntity::new,
        RtePtEntityProperties::new);
    final RtePtEntitySettings settingsEntity = mapSettings(rtePt.getProperties().getSettings(),
        RtePtEntitySettings::new);
    settingsEntity.setUserId(userId);
    return getMongoOperations()
        .save(featureEntity)
        .flatMap(entity -> {
          settingsEntity.setFeatureId(entity.getId());
          return Mono.zip(Mono.just(entity), getMongoOperations().save(settingsEntity));
        })
        .map(this::mapRtePt);
  }

  private Flux<RtePt> persistRtePts(final List<RtePt> rtePts, final String userId) {
    return Flux.fromIterable(rtePts).flatMap(rtePt -> persistRtePt(rtePt, userId));
  }

  public Mono<RtePt> findRtePtById(final String id, final String userId) {
    return getMongoOperations()
        .findOne(Query.query(Criteria.where("id").is(id)), RtePtEntity.class)
        .zipWith(getMongoOperations()
            .findOne(Query.query(featureSettingsCriteria(id, userId)),
                RtePtEntitySettings.class)
            .switchIfEmpty(Mono.just(new RtePtEntitySettings(id, userId))))
        .map(this::mapRtePt);
  }

  private Mono<List<RtePt>> findRtePtsByIds(final List<String> ids, final String userId) {
    return getMongoOperations().find(Query.query(Criteria.where("id").in(ids)), RtePtEntity.class)
        .flatMap(rtePtEntity -> Mono.zip(
            Mono.just(rtePtEntity),
            getMongoOperations()
                .findOne(Query.query(featureSettingsCriteria(rtePtEntity.getId(), userId)),
                    RtePtEntitySettings.class)
                .switchIfEmpty(Mono.just(new RtePtEntitySettings(rtePtEntity.getId(), userId)))))
        .map(this::mapRtePt)
        .collectList()
        ;
  }

  private RtePt mapRtePt(final Tuple2<RtePtEntity, RtePtEntitySettings> tuple) {
    return mapFeature(
        tuple.getT1(), tuple.getT2(), RtePt::new, RtePtProperties::new, RtePtSettings::new);
  }

  public Mono<Rte> persistRte(final Rte rte, final String userId) {
    final RteEntity featureEntity = mapFeature(rte, RteEntity::new, RteEntityProperties::new);
    final RteEntitySettings settingsEntity = mapSettings(rte.getProperties().getSettings(),
        RteEntitySettings::new);
    settingsEntity.setUserId(userId);
    return removeObsoleteRtePts(rte)
        .flatMap(ignored -> getMongoOperations().save(featureEntity))
        .flatMap(entity -> {
          settingsEntity.setFeatureId(entity.getId());
          return Mono.zip(
              Mono.just(entity),
              getMongoOperations().save(settingsEntity),
              persistRtePts(rte.getProperties().getRtePts(), userId).collectList());
        })
        .map(this::mapRte);
  }

  private Mono<Long> removeObsoleteRtePts(final Rte rte) {
    if (!StringUtils.hasText(rte.getId())) {
      return Mono.just(0L);
    }
    final Set<String> newRtePtIds = rte.getProperties().getRtePts()
        .stream()
        .filter(rtePt -> StringUtils.hasText(rtePt.getId()))
        .map(Feature::getId)
        .collect(Collectors.toSet());
    return getMongoOperations()
        .findOne(Query.query(Criteria.where("id").is(rte.getId())), RteEntity.class)
        .map(
            entity -> entity.getProperties().getRtePtIds()
                .stream()
                .filter(id -> !newRtePtIds.contains(id))
                .collect(Collectors.toSet()))
        .flatMap(
            set -> getMongoOperations()
                .remove(Query.query(Criteria.where("id").in(set)), RtePtEntity.class))
        .map(DeleteResult::getDeletedCount);
  }

  public Flux<Rte> findRtes(final String userId) {
    //TODO demo
    return getMongoOperations().find(null, RteEntity.class)
        .flatMap(entity -> zipRte(entity, userId))
        .map(this::mapRte);
  }

  public Mono<Rte> findRteById(final String id, final String userId) {
    return getMongoOperations()
        .findOne(Query.query(Criteria.where("id").is(id)), RteEntity.class)
        .flatMap(entity -> zipRte(entity, userId))
        .map(this::mapRte);
  }

  private Mono<Tuple3<RteEntity, RteEntitySettings, List<RtePt>>> zipRte(
      final RteEntity entity, final String userId) {
    return Mono.zip(
        Mono.just(entity),
        getMongoOperations()
            .findOne(Query.query(featureSettingsCriteria(entity.getId(), userId)),
                RteEntitySettings.class)
            .switchIfEmpty(Mono.just(new RteEntitySettings(entity.getId(), userId))),
        findRtePtsByIds(entity.getProperties().getRtePtIds(), userId)
    );
  }

  private Rte mapRte(final Tuple3<RteEntity, RteEntitySettings, List<RtePt>> tuple) {
    final Rte rte = mapFeature(
        tuple.getT1(), tuple.getT2(), Rte::new, RteProperties::new, RteSettings::new);
    rte.getProperties().setRtePts(tuple.getT3());
    return rte;
  }

}
