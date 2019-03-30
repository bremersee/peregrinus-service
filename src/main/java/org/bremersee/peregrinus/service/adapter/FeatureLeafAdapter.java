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

package org.bremersee.peregrinus.service.adapter;

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.entity.FeatureLeafEntitySettings;
import org.bremersee.peregrinus.entity.LeafEntity;
import org.bremersee.peregrinus.entity.LeafEntitySettings;
import org.bremersee.peregrinus.model.FeatureLeaf;
import org.bremersee.peregrinus.model.FeatureLeafSettings;
import org.bremersee.peregrinus.model.Leaf;
import org.bremersee.peregrinus.service.FeatureService;
import org.bremersee.security.access.AclMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class FeatureLeafAdapter extends AbstractAdapter implements LeafAdapter {

  private final FeatureService featureService;

  public FeatureLeafAdapter(
      AclMapper<AclEntity> aclMapper,
      ModelMapper modelMapper,
      FeatureService featureService) {
    super(aclMapper, modelMapper);
    Assert.notNull(featureService, "Feature service must not be null.");
    this.featureService = featureService;
  }

  @Override
  public @NotNull Class<?>[] getSupportedClasses() {
    return new Class[]{
        FeatureLeafEntity.class,
        FeatureLeafEntitySettings.class,
        FeatureLeaf.class,
        FeatureLeafSettings.class
    };
  }

  @Override
  public LeafEntitySettings buildLeafEntitySettings(
      @NotNull LeafEntity leafEntity,
      @NotNull String userId) {
    return FeatureLeafEntitySettings.builder()
        .displayedOnMap(false)
        .nodeId(leafEntity.getId())
        .userId(userId)
        .build();
  }

  @Override
  public Mono<Leaf> buildLeaf(
      @NotNull LeafEntity leafEntity,
      @NotNull LeafEntitySettings leafEntitySettings) {

    final FeatureLeafEntity featureLeafEntity = (FeatureLeafEntity) leafEntity;
    final FeatureLeafEntitySettings featureLeafEntitySettings
        = (FeatureLeafEntitySettings) leafEntitySettings;
    final String userId = leafEntitySettings.getUserId();
    return featureService.findFeatureById(featureLeafEntity.getFeatureId(), userId)
        .map(feature -> FeatureLeaf.builder()
            .acl(getAclMapper().map(featureLeafEntity.getAcl()))
            .created(featureLeafEntity.getCreated())
            .createdBy(featureLeafEntity.getCreatedBy())
            .feature(feature)
            .id(featureLeafEntity.getId())
            .modified(featureLeafEntity.getModified())
            .modifiedBy(featureLeafEntity.getModifiedBy())
            .name(feature.getProperties().getName())
            .parentId(featureLeafEntity.getParentId())
            .settings(FeatureLeafSettings.builder()
                .displayedOnMap(featureLeafEntitySettings.getDisplayedOnMap())
                .id(featureLeafEntitySettings.getId())
                .nodeId(featureLeafEntity.getId())
                .userId(featureLeafEntitySettings.getUserId())
                .build())
            .build());
  }

  @Override
  public Mono<Boolean> renameLeaf(
      final LeafEntity leafEntity,
      final String name,
      final String userId) {
    final FeatureLeafEntity featureLeafEntity = (FeatureLeafEntity) leafEntity;
    return featureService.renameFeature(featureLeafEntity.getFeatureId(), name, userId)
        .switchIfEmpty(Mono.just(false));
  }
}
