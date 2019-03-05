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

package org.bremersee.peregrinus.tree.service;

import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeature;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeatureSettings;
import org.bremersee.peregrinus.geo.repository.GeoJsonFeatureRepository;
import org.bremersee.peregrinus.geo.repository.GeoJsonFeatureSettingsRepository;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.tree.model.AbstractLeaf;
import org.bremersee.peregrinus.tree.model.AbstractNode;
import org.bremersee.peregrinus.tree.model.GeoLeaf;
import org.bremersee.peregrinus.tree.model.GeoLeafSettings;
import org.bremersee.peregrinus.tree.repository.LeafRepository;
import org.bremersee.peregrinus.tree.repository.LeafSettingsRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class GeoLeafAdapter implements LeafAdapter {

  private LeafRepository leafRepository;

  private LeafSettingsRepository leafSettingsRepository;

  private GeoJsonFeatureRepository featureRepository;

  private GeoJsonFeatureSettingsRepository featureSettingsRepository;

  public GeoLeafAdapter(LeafRepository leafRepository,
      LeafSettingsRepository leafSettingsRepository,
      GeoJsonFeatureRepository featureRepository,
      GeoJsonFeatureSettingsRepository featureSettingsRepository) {
    this.leafRepository = leafRepository;
    this.leafSettingsRepository = leafSettingsRepository;
    this.featureRepository = featureRepository;
    this.featureSettingsRepository = featureSettingsRepository;
  }

  @Override
  public boolean supportsLeaf(AbstractLeaf leaf) {
    return leaf instanceof GeoLeaf;
  }

  @Override
  public Mono<AbstractLeaf> setLeafName(final AbstractLeaf leaf) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    geoLeaf.setName(geoLeaf.getFeature().getProperties().getName());
    return Mono.just(leaf);
  }

  @Override
  public Mono<AbstractLeaf> setLeafSettings(final AbstractLeaf leaf, final String userId) {
    return leafSettingsRepository.findByNodeIdAndUserId(leaf.getId(), userId)
        .switchIfEmpty(createLeafSettings(leaf, userId))
        .map(leafSettings -> {
          leaf.setSettings(leafSettings);
          return leaf;
        });
  }

  @Override
  public Mono<AbstractLeaf> setLeafContent(
      final AbstractLeaf leaf,
      final String userId) {
    return featureSettingsRepository
        .findByFeatureIdAndUserId(((GeoLeaf) leaf).getFeature().getId(), userId)
        .switchIfEmpty(createFeatureSettings(((GeoLeaf) leaf).getFeature(), userId))
        .map(featureSettings -> {
          //noinspection unchecked
          ((GeoLeaf) leaf).getFeature().getProperties().setSettings(featureSettings);
          return leaf;
        });
  }

  @Override
  public Mono<Void> renameLeaf(final AbstractLeaf leaf, final String name) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    geoLeaf.setName(name);
    geoLeaf.getFeature().getProperties().setName(name);
    return featureRepository.persist(geoLeaf.getFeature()).flatMap(entity -> Mono.empty());
  }

  @Override
  public Mono<AccessControl> updateAccessControl(
      final AbstractLeaf leaf,
      final AccessControl accessControl) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    final AbstractGeoJsonFeature feature = geoLeaf.getFeature();
    feature.getProperties().setAccessControl(accessControl);
    return featureRepository
        .persist(feature)
        .flatMap(feature0 -> {
          geoLeaf.setFeature(feature);
          geoLeaf.setAccessControl(accessControl);
          return leafRepository.save(geoLeaf)
              .map(AbstractNode::getAccessControl);
        });
  }

  @Override
  public Mono<Void> delete(final AbstractLeaf leaf, final String userId) {
    return leafSettingsRepository
        .deleteByNodeIdAndUserId(leaf.getId(), userId)
        .and(leafRepository.delete(leaf));
  }

  private Mono<AbstractGeoJsonFeatureSettings> createFeatureSettings(
      final AbstractGeoJsonFeature feature, final String userId) {

    final AbstractGeoJsonFeatureSettings featureSettings = feature
        .getProperties()
        .createDefaultSettings(feature.getId(), userId);
    return featureSettingsRepository.save(featureSettings);
  }

  private Mono<GeoLeafSettings> createLeafSettings(final AbstractLeaf leaf, final String userId) {
    final GeoLeafSettings settings = new GeoLeafSettings(leaf.getId(), userId);
    settings.setDisplayedOnMap(true);
    return leafSettingsRepository.persist(settings);
  }
}
