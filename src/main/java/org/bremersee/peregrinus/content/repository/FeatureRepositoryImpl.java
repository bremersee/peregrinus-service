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

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.FeatureProperties;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RteSettings;
import org.bremersee.peregrinus.content.model.Trk;
import org.bremersee.peregrinus.content.model.TrkProperties;
import org.bremersee.peregrinus.content.model.TrkSettings;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.peregrinus.content.model.WptSettings;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntity;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntityProperties;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.RteEntity;
import org.bremersee.peregrinus.content.repository.entity.RteEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntity;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.TrkEntity;
import org.bremersee.peregrinus.content.repository.entity.TrkEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.WptEntity;
import org.bremersee.peregrinus.content.repository.entity.WptEntitySettings;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

/**
 * @author Christian Bremer
 */
@Repository
public class FeatureRepositoryImpl implements FeatureRepository {

  private ModelMapper modelMapper = new ModelMapper();

  private ReactiveMongoOperations mongoOperations;

  public FeatureRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
    this.modelMapper.addConverter(new AbstractConverter<Date, OffsetDateTime>() {
      @Override
      protected OffsetDateTime convert(Date date) {
        return date == null ? null : OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("Z"));
      }
    });
    this.modelMapper.addConverter(new AbstractConverter<OffsetDateTime, Date>() {
      @Override
      protected Date convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : Date.from(offsetDateTime.toInstant());
      }
    });
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
    return Mono.empty();
    /*
    return findByIds(entity.getProperties().getRtePtIds(), RtePt.class)
        .collectList()
        .map(rtePts -> {
          entity.getProperties().setRtePts(rtePts);
          return entity;
        });
        */
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
    return Mono.empty();
    /*
    rte.getProperties().getRtePtIds().clear();
    return persistAll(rte.getProperties().getRtePts())
        .map(RtePt::getId)
        .collectList()
        .flatMap(ids -> doSaveRte(rte, ids));
        */
  }

  private Mono<Rte> doSaveRte(final Rte rte, final List<String> rtePtIds) {
    //rte.getProperties().setRtePtIds(rtePtIds);
    return Mono.empty();
  }

  @Override
  public <T> Flux<T> persistAll(final Collection<T> entities) {
    return Flux.fromIterable(entities).flatMap(this::persist);
  }

  @Override
  public Mono<Void> delete(Object entity) {
    return mongoOperations.remove(entity).flatMap(deleteResult -> Mono.empty());
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


  public Mono<RtePt> persistRtePt(final RtePt rtePt) { // TODO Do I want to save settings, too?
    mongoOperations.findOne(Query.query(Criteria.where("id").is(rtePt.getId())), RtePt.class)
        .map(dto -> new RtePtEntity())
        .map(entity -> mongoOperations.save(entity))
    ;
    return Mono.empty();
  }

  public Mono<RtePt> findRtePtById(final String id, final String userId) {
    return mongoOperations
        .findOne(Query.query(Criteria.where("id").is(id)), RtePtEntity.class)
        .zipWith(mongoOperations
            .findOne(Query.query(featureSettingsCriteria(id, userId)),
                RtePtEntitySettings.class)
            .switchIfEmpty(Mono.just(new RtePtEntitySettings(id, userId))))
        .map(this::mapRtePt);
  }

  private Mono<List<RtePt>> findRtePtsByIds(final List<String> ids, final String userId) {
    return mongoOperations.find(Query.query(Criteria.where("id").in(ids)), RtePtEntity.class)
        .flatMap(rtePtEntity -> Mono.zip(
            Mono.just(rtePtEntity),
            mongoOperations
                .findOne(Query.query(featureSettingsCriteria(rtePtEntity.getId(), userId)),
                    RtePtEntitySettings.class)
                .switchIfEmpty(Mono.just(new RtePtEntitySettings(rtePtEntity.getId(), userId)))))
        .map(this::mapRtePt)
        .collectList()
        ;
  }

  private RtePt mapRtePt(Tuple2<RtePtEntity, RtePtEntitySettings> tuple) {
    RtePt rtePt = new RtePt();
    return rtePt;
  }

  public Flux<Rte> findRtes(final String userId) {
    return mongoOperations.find(null, RteEntity.class)
        .flatMap(entity -> zipRte(entity, userId))
        .map(this::mapRte);
  }

  public Mono<Rte> findRteById(final String id, final String userId) {
    return mongoOperations
        .findOne(Query.query(Criteria.where("id").is(id)), RteEntity.class)
        .flatMap(entity -> zipRte(entity, userId))
        .map(this::mapRte);
  }

  private Mono<Tuple3<RteEntity, RteEntitySettings, List<RtePt>>> zipRte(
      final RteEntity entity, final String userId) {
    return Mono.zip(
        Mono.just(entity),
        mongoOperations
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

  public Mono<Trk> findTrkById(final String id, final String userId) {
    return mongoOperations
        .findOne(Query.query(Criteria.where("id").is(id)), TrkEntity.class)
        .zipWith(mongoOperations
            .findOne(Query.query(featureSettingsCriteria(id, userId)),
                TrkEntitySettings.class)
            .switchIfEmpty(Mono.just(new TrkEntitySettings(id, userId))))
        .map(this::mapTrk);
  }

  private Trk mapTrk(Tuple2<TrkEntity, TrkEntitySettings> tuple) {
    return mapFeature(tuple.getT1(), tuple.getT2(), Trk::new, TrkProperties::new, TrkSettings::new);
  }

  public Mono<Wpt> findWptById(final String id, final String userId) {
    return mongoOperations
        .findOne(Query.query(Criteria.where("id").is(id)), WptEntity.class)
        .zipWith(mongoOperations
            .findOne(Query.query(featureSettingsCriteria(id, userId)),
                WptEntitySettings.class)
            .switchIfEmpty(Mono.just(new WptEntitySettings(id, userId))))
        .map(this::mapWpt);
  }

  private Wpt mapWpt(Tuple2<WptEntity, WptEntitySettings> tuple) {
    return mapFeature(tuple.getT1(), tuple.getT2(), Wpt::new, WptProperties::new, WptSettings::new);
  }

  private <T extends Feature<G, P>, G extends Geometry, P extends FeatureProperties<S>, S extends FeatureSettings> T mapFeature(
      final FeatureEntity<G, ?> entity,
      final FeatureEntitySettings entitySettings,
      final Supplier<T> featureSupplier,
      final Supplier<P> propertiesSupplier,
      final Supplier<S> settingsSupplier) {

    T feature = featureSupplier.get();
    feature.setBbox(entity.getBbox());
    feature.setGeometry(entity.getGeometry());
    feature.setId(entity.getId());
    feature.setProperties(
        mapProperties(
            entity.getProperties(),
            entitySettings,
            propertiesSupplier,
            settingsSupplier));
    return feature;
  }

  private <P extends FeatureProperties<S>, S extends FeatureSettings> P mapProperties(
      final FeatureEntityProperties entityProperties,
      final FeatureEntitySettings entitySettings,
      final Supplier<P> propertiesSupplier,
      final Supplier<S> settingsSupplier) {

    P properties = propertiesSupplier.get();
    modelMapper.map(entityProperties, properties);
    properties.setSettings(mapSettings(entitySettings, settingsSupplier));
    return properties;
  }

  private <T extends FeatureSettings> T mapSettings(
      final FeatureEntitySettings entitySettings,
      final Supplier<T> settingsSupplier) {

    T settings = settingsSupplier.get();
    modelMapper.map(entitySettings, settings);
    return settings;
  }

  private Criteria featureSettingsCriteria(String featureId, String userId) {
    return new Criteria().andOperator(
        Criteria.where("featureId").is(featureId),
        Criteria.where("userId").is(userId));
  }

}
