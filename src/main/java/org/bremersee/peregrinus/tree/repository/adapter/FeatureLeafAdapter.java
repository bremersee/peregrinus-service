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

package org.bremersee.peregrinus.tree.repository.adapter;

import java.util.Collection;
import org.bremersee.peregrinus.content.model.FeatureProperties;
import org.bremersee.peregrinus.content.repository.FeatureRepository;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.tree.model.FeatureLeaf;
import org.bremersee.peregrinus.tree.model.FeatureLeafSettings;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.model.NodeSettings;
import org.bremersee.peregrinus.tree.repository.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.tree.repository.entity.FeatureLeafEntitySettings;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntity;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntitySettings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Component
public class FeatureLeafAdapter extends AbstractNodeAdapter implements NodeAdapter {

  private FeatureRepository featureRepository;

  public FeatureLeafAdapter(
      final ModelMapper modelMapper,
      final FeatureRepository featureRepository) {
    super(modelMapper);
    Assert.notNull(featureRepository, "Feature repository must not be null.");
    this.featureRepository = featureRepository;
  }

  @Override
  public Class<?>[] getSupportedClasses() {
    return new Class[]{
        FeatureLeafEntity.class,
        FeatureLeafEntitySettings.class,
        FeatureLeaf.class,
        FeatureLeafSettings.class
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public Mono<Tuple2<FeatureLeafEntity, FeatureLeafEntitySettings>> mapNode(
      final Node node,
      final String userId) {

    final FeatureLeaf leaf = (FeatureLeaf) node;
    return Mono.just(mapNode(node, userId, FeatureLeafEntity::new, FeatureLeafEntitySettings::new))
        .flatMap(tuple -> Mono.zip(
            updateFeature(leaf)
                .flatMap(b -> Mono.just(tuple.getT1())),
            Mono.just(tuple.getT2())
        ))
        .map(tuple -> {
          if (leaf.getFeature() != null) {
            tuple.getT1().setFeatureId(leaf.getFeature().getId());
          }
          return tuple;
        });
  }

  private Mono<Boolean> updateFeature(final FeatureLeaf leaf) {
    if (leaf.getFeature() == null || leaf.getFeature().getProperties() == null) {
      return Mono.just(Boolean.FALSE);
    }
    final FeatureProperties properties = leaf.getFeature().getProperties();
    final String newName = leaf.getName().equals(properties.getName()) ? null : leaf.getName();
    final AccessControlDto newAccessControl = leaf.getAccessControl()
        .equals(properties.getAccessControl())
        ? null
        : leaf.getAccessControl();
    if (newName == null && newAccessControl == null) {
      return Mono.just(Boolean.FALSE);
    }
    return featureRepository.updateNameAndAccessControl(
        leaf.getFeature().getId(), newName, newAccessControl)
        .switchIfEmpty(Mono.just(Boolean.FALSE));
  }

  @Override
  public Mono<? extends NodeEntitySettings> mapNodeSettings(
      final NodeSettings nodeSettings,
      final String userId) {

    return Mono.just(super.mapNodeSettings(nodeSettings, userId, FeatureLeafEntitySettings::new));
  }

  @Override
  public Mono<FeatureLeaf> mapNodeEntity(
      final NodeEntity nodeEntity,
      final NodeEntitySettings nodeEntitySettings) {

    final FeatureLeafEntity leaf = (FeatureLeafEntity) nodeEntity;
    final FeatureLeafEntitySettings leafSettings = (FeatureLeafEntitySettings) nodeEntitySettings;

    final FeatureLeaf featureLeaf = mapNodeEntity(
        leaf,
        leafSettings,
        FeatureLeaf::new,
        FeatureLeafSettings::new);
    return featureRepository.findById(leaf.getFeatureId(), leafSettings.getUserId())
        .map(feature -> {
          featureLeaf.setFeature(feature);
          featureLeaf.setName(feature.getProperties().getName());
          return featureLeaf;
        });
  }

  @Override
  public Mono<FeatureLeafSettings> mapNodeEntitySettings(
      final NodeEntitySettings nodeSettings) {

    return Mono.just(super.mapNodeEntitySettings(nodeSettings, FeatureLeafSettings::new));
  }

  @Override
  public Mono<NodeEntity> updateName(
      final NodeEntity nodeEntity,
      final String name,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    if (nodeEntity instanceof FeatureLeafEntity
        && ((FeatureLeafEntity) nodeEntity).getFeatureId() != null) {
      final FeatureLeafEntity featureLeafEntity = (FeatureLeafEntity) nodeEntity;
      return featureRepository
          .updateName(
              featureLeafEntity.getFeatureId(), name, userId, roles, groups)
          .flatMap(b -> Mono.just(nodeEntity))
          .switchIfEmpty(Mono.just(nodeEntity));
    }
    return Mono.just(nodeEntity);
  }

  @Override
  public Mono<NodeEntity> updateAccessControl(
      final NodeEntity nodeEntity,
      final AccessControl accessControl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    if (nodeEntity instanceof FeatureLeafEntity
        && ((FeatureLeafEntity) nodeEntity).getFeatureId() != null) {
      final FeatureLeafEntity featureLeafEntity = (FeatureLeafEntity) nodeEntity;
      return featureRepository
          .updateAccessControl(
              featureLeafEntity.getFeatureId(), accessControl, userId, roles, groups)
          .flatMap(b -> Mono.just(nodeEntity))
          .switchIfEmpty(Mono.just(nodeEntity));
    }
    return Mono.just(nodeEntity);
  }

  @Override
  public Mono<NodeEntity> removeNode(
      final NodeEntity nodeEntity,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    if (nodeEntity instanceof FeatureLeafEntity
        && ((FeatureLeafEntity) nodeEntity).getFeatureId() != null) {
      final FeatureLeafEntity featureLeafEntity = (FeatureLeafEntity) nodeEntity;
      return featureRepository.removeById(featureLeafEntity.getFeatureId(), userId, roles, groups)
          .map(result -> nodeEntity)
          .switchIfEmpty(Mono.just(nodeEntity));
    }
    return Mono.just(nodeEntity);
  }

  @Override
  public Mono<FeatureLeafEntitySettings> defaultSettings(
      final NodeEntity nodeEntity,
      final String userId) {

    final FeatureLeafEntitySettings settings = new FeatureLeafEntitySettings();
    settings.setNodeId(nodeEntity.getId());
    settings.setUserId(userId);
    return Mono.just(settings);
  }

}
