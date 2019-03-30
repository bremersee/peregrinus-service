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

package org.bremersee.peregrinus.content.repository.mapper;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureProperties;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntityProperties;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;

/**
 * @author Christian Bremer
 */
public abstract class AbstractFeatureMapper {

  @Getter(AccessLevel.PROTECTED)
  private ModelMapper modelMapper;

  public AbstractFeatureMapper() {
    this.modelMapper = new ModelMapper();
  }

  <T extends Feature<G, P>, G extends Geometry, P extends FeatureProperties<S>, S extends FeatureSettings> T mapFeatureEntity(
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
        mapFeatureEntityProperties(
            entity.getProperties(),
            entitySettings,
            propertiesSupplier,
            settingsSupplier));
    return feature;
  }

  <FE extends FeatureEntity<G, P>, G extends Geometry, P extends FeatureEntityProperties> FE mapFeature(
      final Feature<G, ?> feature,
      final Supplier<FE> featureSupplier,
      final Supplier<P> propertiesSupplier) {

    FE featureEntity = featureSupplier.get();
    featureEntity.setBbox(GeometryUtils.getBoundingBox(feature.getGeometry()));
    featureEntity.setGeometry(feature.getGeometry());
    featureEntity.setId(feature.getId());
    featureEntity.setProperties(mapFeatureProperties(feature.getProperties(), propertiesSupplier));
    return featureEntity;
  }

  private <P extends FeatureProperties<S>, S extends FeatureSettings> P mapFeatureEntityProperties(
      final FeatureEntityProperties entityProperties,
      final FeatureEntitySettings entitySettings,
      final Supplier<P> propertiesSupplier,
      final Supplier<S> settingsSupplier) {

    P properties = propertiesSupplier.get();
    modelMapper.map(entityProperties, properties);
    //properties.getAcl().removeAdminAccess();
    properties.setSettings(mapSettings(entitySettings, settingsSupplier));
    return properties;
  }

  private <P extends FeatureEntityProperties> P mapFeatureProperties(
      final FeatureProperties properties,
      final Supplier<P> supplier) {
    P entityProperties = supplier.get();
    modelMapper.map(properties, entityProperties);
    //entityProperties.getAccessControl().ensureAdminAccess();
    return entityProperties;
  }

  private <S extends FeatureSettings> S mapSettings(
      final FeatureEntitySettings entitySettings,
      final Supplier<S> settingsSupplier) {

    S settings = settingsSupplier.get();
    modelMapper.map(entitySettings, settings);
    return settings;
  }

  <S extends FeatureEntitySettings> S mapSettings(
      final FeatureSettings settings,
      final String userId,
      final Supplier<S> supplier) {
    S entitySettings = supplier.get();
    if (settings != null) {
      modelMapper.map(settings, entitySettings);
    }
    entitySettings.setUserId(userId);
    return entitySettings;
  }

}
