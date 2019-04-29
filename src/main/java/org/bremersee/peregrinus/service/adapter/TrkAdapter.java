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
import org.bremersee.peregrinus.entity.TrkEntity;
import org.bremersee.peregrinus.entity.TrkEntityProperties;
import org.bremersee.peregrinus.entity.TrkEntitySettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.TrkProperties;
import org.bremersee.peregrinus.model.TrkSettings;
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
public class TrkAdapter extends AbstractAdapter implements FeatureAdapter {

  public TrkAdapter(
      AclMapper<AclEntity> aclMapper,
      ModelMapper modelMapper) {
    super(aclMapper, modelMapper);
  }

  @Override
  public @NotNull String[] getSupportedKeys() {
    return new String[]{
        Feature.TRK_TYPE,
        Trk.class.getName(),
        TrkProperties.class.getName(),
        TrkSettings.class.getName(),
        TrkEntity.class.getName(),
        TrkEntityProperties.class.getName(),
        TrkEntitySettings.class.getName()
    };
  }

  @Override
  public FeatureEntitySettings buildFeatureEntitySettings(
      @NotNull FeatureEntity featureEntity,
      @NotNull String userId) {

    return TrkEntitySettings.builder()
        .featureId(featureEntity.getId())
        .userId(userId)
        .build();
  }

  @Override
  public FeatureEntitySettings updateFeatureEntitySettings(
      @NotNull FeatureEntitySettings featureEntitySettings,
      @Nullable FeatureSettings featureSettings) {

    final TrkEntitySettings destination = (TrkEntitySettings) featureEntitySettings;
    if (featureSettings != null) {
      final TrkSettings source = (TrkSettings) featureSettings;
      destination.setDisplayColor(source.getDisplayColor());
    }
    return destination;
  }

  @Override
  public Mono<Feature> buildFeature(
      @NotNull FeatureEntity featureEntity,
      @NotNull FeatureEntitySettings featureEntitySettings) {

    final TrkEntity trkEntity = (TrkEntity) featureEntity;
    final TrkEntitySettings trkEntitySettings = (TrkEntitySettings) featureEntitySettings;
    return Mono.just(Trk.builder()
        .bbox(trkEntity.getBbox())
        .geometry(trkEntity.getGeometry())
        .id(trkEntity.getId())
        .properties(TrkProperties.builder()
            .acl(getAclMapper().map(trkEntity.getProperties().getAcl()))
            .created(trkEntity.getProperties().getCreated())
            .createdBy(trkEntity.getProperties().getCreatedBy())
            .eleLines(trkEntity.getProperties().getEleLines())
            .links(trkEntity.getProperties().getLinks())
            .markdownDescription(trkEntity.getProperties().getMarkdownDescription())
            .modified(trkEntity.getProperties().getModified())
            .modifiedBy(trkEntity.getProperties().getModifiedBy())
            .name(trkEntity.getProperties().getName())
            .plainTextDescription(trkEntity.getProperties().getPlainTextDescription())
            .settings(TrkSettings.builder()
                .displayColor(trkEntitySettings.getDisplayColor())
                .featureId(trkEntitySettings.getFeatureId())
                .id(trkEntitySettings.getId())
                .userId(trkEntitySettings.getUserId())
                .build())
            .departureTime(trkEntity.getProperties().getDepartureTime())
            .arrivalTime(trkEntity.getProperties().getArrivalTime())
            .timeLines(trkEntity.getProperties().getTimeLines())
            .build())
        .build());
  }

  @Override
  public FeatureEntity buildFeatureEntity(
      final Feature feature,
      final String userId,
      final FeatureEntity featureEntity) {

    final Trk trk = (Trk) feature;
    final TrkProperties trkProperties = (TrkProperties) feature.getProperties();
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
    return TrkEntity.builder()
        .bbox(GeometryUtils.getBoundingBox(trk.getGeometry()))
        .geometry(trk.getGeometry())
        .id(featureEntity != null ? featureEntity.getId() : null)
        .properties(TrkEntityProperties.builder()
            .acl(getAclMapper().map(acl))
            .created(featureEntity != null ? featureEntity.getProperties().getCreated() : null)
            .createdBy(
                featureEntity != null ? featureEntity.getProperties().getCreatedBy() : userId)
            .eleLines(trkProperties.getEleLines())
            .links(trkProperties.getLinks())
            .markdownDescription(trkProperties.getMarkdownDescription())
            .modified(OffsetDateTime.now(Clock.systemUTC()))
            .modifiedBy(userId)
            .name(trkProperties.getName())
            .plainTextDescription(trkProperties.getPlainTextDescription())
            .departureTime(trkProperties.getDepartureTime())
            .arrivalTime(trkProperties.getArrivalTime())
            .build())
        .build();
  }
}
