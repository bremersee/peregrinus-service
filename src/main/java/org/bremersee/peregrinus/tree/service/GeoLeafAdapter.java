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

import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.repository.FeatureRepository;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.tree.model.GeoLeaf;
import org.bremersee.peregrinus.tree.model.GeoLeafSettings;
import org.bremersee.peregrinus.tree.model.Leaf;
import org.bremersee.peregrinus.tree.model.LeafSettings;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.repository.TreeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class GeoLeafAdapter implements LeafAdapter {

  private TreeRepository treeRepository;

  private FeatureRepository featureRepository;

  public GeoLeafAdapter(TreeRepository treeRepository,
      FeatureRepository featureRepository) {
    this.treeRepository = treeRepository;
    this.featureRepository = featureRepository;
  }

  @Override
  public boolean supportsLeaf(Leaf leaf) {
    return leaf instanceof GeoLeaf;
  }

  @Override
  public Mono<Leaf> setLeafName(final Leaf leaf) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    geoLeaf.setName(geoLeaf.getFeature().getProperties().getName());
    return Mono.just(leaf);
  }

  @Override
  public Mono<Leaf> setLeafSettings(final Leaf leaf, final String userId) {
    return treeRepository.findNodeSettings(LeafSettings.class, leaf.getId(), userId)
        .switchIfEmpty(createLeafSettings(leaf, userId))
        .map(leafSettings -> {
          leaf.setSettings(leafSettings);
          return leaf;
        });
  }

  @Override
  public Mono<Leaf> setLeafContent(
      final Leaf leaf,
      final String userId) {
    return featureRepository
        .findFeatureSettings(FeatureSettings.class, ((GeoLeaf) leaf).getFeature().getId(), userId)
        .switchIfEmpty(createFeatureSettings(((GeoLeaf) leaf).getFeature(), userId))
        .map(featureSettings -> {
          //noinspection unchecked
          ((GeoLeaf) leaf).getFeature().getProperties().setSettings(featureSettings);
          return leaf;
        });
  }

  @Override
  public Mono<Void> renameLeaf(final Leaf leaf, final String name) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    geoLeaf.setName(name);
    geoLeaf.getFeature().getProperties().setName(name);
    return featureRepository.persist(geoLeaf.getFeature()).flatMap(entity -> Mono.empty());
  }

  @Override
  public Mono<AccessControl> updateAccessControl(
      final Leaf leaf,
      final AccessControl accessControl) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    final Feature feature = geoLeaf.getFeature();
    feature.getProperties().setAccessControl(accessControl);
    return featureRepository
        .persist(feature)
        .flatMap(feature0 -> {
          geoLeaf.setFeature(feature);
          geoLeaf.setAccessControl(accessControl);
          return treeRepository.persist(geoLeaf)
              .map(Node::getAccessControl);
        });
  }

  @Override
  public Mono<Void> delete(final Leaf leaf, final String userId) {
    final GeoLeaf geoLeaf = (GeoLeaf) leaf;
    return treeRepository
        .deleteNodeSettings(leaf.getId(), userId)
        .and(treeRepository.delete(leaf))
        .and(featureRepository.delete(geoLeaf.getFeature()))
        .and(featureRepository.deleteFeatureSettings(geoLeaf.getFeature().getId(), userId));
  }

  private Mono<FeatureSettings> createFeatureSettings(
      final Feature feature, final String userId) {

    final FeatureSettings featureSettings = feature
        .getProperties()
        .createDefaultSettings(feature.getId(), userId);
    return featureRepository.persist(featureSettings);
  }

  private Mono<GeoLeafSettings> createLeafSettings(final Leaf leaf, final String userId) {
    final GeoLeafSettings settings = new GeoLeafSettings(leaf.getId(), userId);
    settings.setDisplayedOnMap(true);
    return treeRepository.persist(settings);
  }
}
