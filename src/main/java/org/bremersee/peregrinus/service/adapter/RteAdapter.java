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

import java.time.Clock;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.entity.RteEntity;
import org.bremersee.peregrinus.entity.RteEntityProperties;
import org.bremersee.peregrinus.entity.RteEntitySettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RteSettings;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.AclMapper;
import org.modelmapper.ModelMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class RteAdapter extends AbstractAdapter implements FeatureAdapter {

  public RteAdapter(
      AclMapper<AclEntity> aclMapper,
      ModelMapper modelMapper) {
    super(aclMapper, modelMapper);
  }

  @Override
  public @NotNull String[] getSupportedKeys() {
    return new String[]{
        Feature.RTE_TYPE,
        Rte.class.getName(),
        RteProperties.class.getName(),
        RteSettings.class.getName(),
        RteEntity.class.getName(),
        RteEntityProperties.class.getName(),
        RteEntitySettings.class.getName()
    };
  }

  @Override
  public FeatureEntitySettings buildFeatureEntitySettings(
      @NotNull FeatureEntity featureEntity,
      @NotNull String userId) {

    return RteEntitySettings.builder()
        .featureId(featureEntity.getId())
        .userId(userId)
        .build();
  }

  @Override
  public FeatureEntitySettings updateFeatureEntitySettings(
      @NotNull FeatureEntitySettings featureEntitySettings,
      @Nullable FeatureSettings featureSettings) {

    final RteEntitySettings destination = (RteEntitySettings) featureEntitySettings;
    if (featureSettings != null) {
      final RteSettings source = (RteSettings) featureSettings;
      destination.setDisplayColor(source.getDisplayColor());
    }
    return destination;
  }

  @Override
  public Mono<Feature> buildFeature(
      @NotNull FeatureEntity featureEntity,
      @NotNull FeatureEntitySettings featureEntitySettings) {

    final RteEntity rteEntity = (RteEntity) featureEntity;
    final RteEntitySettings rteEntitySettings = (RteEntitySettings) featureEntitySettings;
    return Mono.just(Rte.builder()
        .bbox(rteEntity.getBbox())
        .geometry(rteEntity.getGeometry())
        .id(rteEntity.getId())
        .properties(RteProperties.builder()
            .acl(getAclMapper().map(rteEntity.getProperties().getAcl()))
            .created(rteEntity.getProperties().getCreated())
            .createdBy(rteEntity.getProperties().getCreatedBy())
            .internalComments(rteEntity.getProperties().getInternalComments())
            .links(rteEntity.getProperties().getLinks())
            .markdownDescription(rteEntity.getProperties().getMarkdownDescription())
            .modified(rteEntity.getProperties().getModified())
            .modifiedBy(rteEntity.getProperties().getModifiedBy())
            .name(rteEntity.getProperties().getName())
            .plainTextDescription(rteEntity.getProperties().getPlainTextDescription())
            .rtePts(rteEntity.getProperties().getRtePts())
            .settings(RteSettings.builder()
                .displayColor(rteEntitySettings.getDisplayColor())
                .featureId(rteEntitySettings.getFeatureId())
                .id(rteEntitySettings.getId())
                .userId(rteEntitySettings.getUserId())
                .build())
            .startTime(rteEntity.getProperties().getStartTime())
            .stopTime(rteEntity.getProperties().getStopTime())
            .build())
        .build());
  }

  @Override
  public FeatureEntity buildFeatureEntity(
      final Feature feature,
      final String userId,
      final FeatureEntity featureEntity) {

    final Rte rte = (Rte) feature;
    final RteProperties rteProperties = (RteProperties) feature.getProperties();
    final AccessControlList acl;
    if (featureEntity != null) {
      acl = getAclMapper().map(featureEntity.getProperties().getAcl());
    } else {
      acl = AclBuilder.builder()
          .from(getAclMapper().defaultAcl(userId))
          .from(feature.getProperties().getAcl())
          .owner(userId)
          .buildAccessControlList();
    }
    return RteEntity.builder()
        .bbox(GeometryUtils.getBoundingBox(rte.getGeometry()))
        .geometry(rte.getGeometry())
        .id(featureEntity != null ? featureEntity.getId() : null)
        .properties(RteEntityProperties.builder()
            .acl(getAclMapper().map(acl))
            .created(featureEntity != null ? featureEntity.getProperties().getCreated() : null)
            .createdBy(
                featureEntity != null ? featureEntity.getProperties().getCreatedBy() : userId)
            .internalComments(rteProperties.getInternalComments())
            .links(rteProperties.getLinks())
            .markdownDescription(rteProperties.getMarkdownDescription())
            .modified(OffsetDateTime.now(Clock.systemUTC()))
            .modifiedBy(userId)
            .name(rteProperties.getName())
            .plainTextDescription(rteProperties.getPlainTextDescription())
            .rtePts(rteProperties.getRtePts())
            .startTime(null)
            .stopTime(null)
            .build())
        .build();
  }
}
