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
import org.bremersee.peregrinus.entity.WptEntity;
import org.bremersee.peregrinus.entity.WptEntityProperties;
import org.bremersee.peregrinus.entity.WptEntitySettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.WptProperties;
import org.bremersee.peregrinus.model.WptSettings;
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
public class WptAdapter extends AbstractAdapter implements FeatureAdapter {

  public WptAdapter(
      AclMapper<AclEntity> aclMapper,
      ModelMapper modelMapper) {
    super(aclMapper, modelMapper);
  }

  @Override
  public @NotNull String[] getSupportedKeys() {
    return new String[]{
        Feature.WPT_TYPE,
        Wpt.class.getName(),
        WptProperties.class.getName(),
        WptSettings.class.getName(),
        WptEntity.class.getName(),
        WptEntityProperties.class.getName(),
        WptEntitySettings.class.getName()
    };
  }

  @Override
  public FeatureEntitySettings buildFeatureEntitySettings(
      final FeatureEntity featureEntity,
      final String userId) {

    return WptEntitySettings.builder()
        .featureId(featureEntity.getId())
        .userId(userId)
        .build();
  }

  @Override
  public FeatureEntitySettings updateFeatureEntitySettings(
      @NotNull FeatureEntitySettings featureEntitySettings,
      @Nullable FeatureSettings featureSettings) {

    return featureEntitySettings;
    // nothing to do at the moment
    //final WptEntitySettings destination = (WptEntitySettings) featureEntitySettings;
    //if (featureSettings != null) {
    //  final WptSettings source = (WptSettings) featureSettings;
    //}
    //return destination;
  }

  @Override
  public Mono<Feature> buildFeature(
      final FeatureEntity featureEntity,
      final FeatureEntitySettings featureEntitySettings) {

    final WptEntity wptEntity = (WptEntity) featureEntity;
    final WptEntitySettings wptEntitySettings = (WptEntitySettings) featureEntitySettings;
    return Mono.just(Wpt.builder()
        .bbox(wptEntity.getBbox())
        .geometry(wptEntity.getGeometry())
        .id(wptEntity.getId())
        .properties(WptProperties.builder()
            .acl(getAclMapper().map(wptEntity.getProperties().getAcl()))
            .address(wptEntity.getProperties().getAddress())
            .created(wptEntity.getProperties().getCreated())
            .createdBy(wptEntity.getProperties().getCreatedBy())
            .ele(wptEntity.getProperties().getEle())
            .internalComments(wptEntity.getProperties().getInternalComments())
            .internalType(wptEntity.getProperties().getInternalType())
            .links(wptEntity.getProperties().getLinks())
            .markdownDescription(wptEntity.getProperties().getMarkdownDescription())
            .modified(wptEntity.getProperties().getModified())
            .modifiedBy(wptEntity.getProperties().getModifiedBy())
            .name(wptEntity.getProperties().getName())
            .phoneNumbers(wptEntity.getProperties().getPhoneNumbers())
            .plainTextDescription(wptEntity.getProperties().getPlainTextDescription())
            .settings(WptSettings.builder()
                .featureId(wptEntitySettings.getFeatureId())
                .id(wptEntitySettings.getId())
                .userId(wptEntitySettings.getUserId())
                .build())
            .startTime(wptEntity.getProperties().getStartTime())
            .stopTime(wptEntity.getProperties().getStopTime())
            .build())
        .build());
  }

  @Override
  public FeatureEntity buildFeatureEntity(
      final Feature feature,
      final String userId,
      final FeatureEntity featureEntity) {

    final Wpt wpt = (Wpt) feature;
    final WptProperties wptProperties = (WptProperties) feature.getProperties();
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
    return WptEntity.builder()
        .bbox(GeometryUtils.getBoundingBox(wpt.getGeometry()))
        .geometry(wpt.getGeometry())
        .id(featureEntity != null ? featureEntity.getId() : null)
        .properties(WptEntityProperties.builder()
            .acl(getAclMapper().map(acl))
            .address(wptProperties.getAddress())
            .created(featureEntity != null ? featureEntity.getProperties().getCreated() : null)
            .createdBy(
                featureEntity != null ? featureEntity.getProperties().getCreatedBy() : userId)
            .ele(wptProperties.getEle())
            .internalComments(wptProperties.getInternalComments())
            .internalType(wptProperties.getInternalType())
            .links(wptProperties.getLinks())
            .markdownDescription(wptProperties.getMarkdownDescription())
            .modified(OffsetDateTime.now(Clock.systemUTC()))
            .modifiedBy(userId)
            .name(wptProperties.getName())
            .phoneNumbers(wptProperties.getPhoneNumbers())
            .plainTextDescription(wptProperties.getPlainTextDescription())
            .startTime(wpt.getProperties().getStartTime())
            .stopTime(wpt.getProperties().getStopTime())
            .build())
        .build();
  }
}
