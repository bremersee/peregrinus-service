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
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.FeatureProperties;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RtePtProperties;
import org.bremersee.peregrinus.content.model.RtePtSettings;
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
import org.bremersee.peregrinus.content.repository.entity.RteEntityProperties;
import org.bremersee.peregrinus.content.repository.entity.RteEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntity;
import org.bremersee.peregrinus.content.repository.entity.RtePtEntityProperties;
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
abstract class AbstractFeatureRepositoryImpl {

  @Getter(AccessLevel.PACKAGE)
  private ModelMapper modelMapper = new ModelMapper();

  @Getter(AccessLevel.PACKAGE)
  private ReactiveMongoOperations mongoOperations;

  public AbstractFeatureRepositoryImpl(
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


  <T extends Feature<G, P>, G extends Geometry, P extends FeatureProperties<S>, S extends FeatureSettings> T mapFeature(
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

  <T extends FeatureEntity<G, P>, G extends Geometry, P extends FeatureEntityProperties> T mapFeature(
      final Feature<G, ?> feature,
      final Supplier<T> featureSupplier,
      final Supplier<P> propertiesSupplier) {

    T featureEntity = featureSupplier.get();
    featureEntity.setBbox(GeometryUtils.getBoundingBox(feature.getGeometry()));
    featureEntity.setGeometry(feature.getGeometry());
    featureEntity.setId(feature.getId());
    featureEntity.setProperties(mapProperties(feature.getProperties(), propertiesSupplier));
    return featureEntity;
  }

  <P extends FeatureProperties<S>, S extends FeatureSettings> P mapProperties(
      final FeatureEntityProperties entityProperties,
      final FeatureEntitySettings entitySettings,
      final Supplier<P> propertiesSupplier,
      final Supplier<S> settingsSupplier) {

    P properties = propertiesSupplier.get();
    modelMapper.map(entityProperties, properties);
    properties.setSettings(mapSettings(entitySettings, settingsSupplier));
    return properties;
  }

  <P extends FeatureEntityProperties> P mapProperties(
      final FeatureProperties properties,
      final Supplier<P> supplier) {
    P entityProperties = supplier.get();
    modelMapper.map(properties, entityProperties);
    return entityProperties;
  }

  <S extends FeatureSettings> S mapSettings(
      final FeatureEntitySettings entitySettings,
      final Supplier<S> settingsSupplier) {

    S settings = settingsSupplier.get();
    modelMapper.map(entitySettings, settings);
    return settings;
  }

  <S extends FeatureEntitySettings> S mapSettings(
      final FeatureSettings settings,
      final Supplier<S> supplier) {
    S entitySettings = supplier.get();
    if (settings != null) {
      modelMapper.map(settings, entitySettings);
    }
    return entitySettings;
  }

  Criteria featureSettingsCriteria(String featureId, String userId) {
    return new Criteria().andOperator(
        Criteria.where("featureId").is(featureId),
        Criteria.where("userId").is(userId));
  }

}
