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

import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RteSettings;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.entity.RteEntity;
import org.bremersee.peregrinus.entity.RteEntityProperties;
import org.bremersee.peregrinus.entity.RteEntitySettings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Component
public class RteMapper extends AbstractFeatureMapper implements FeatureMapper {

  @Override
  public boolean supports(final Object obj) {
    if (obj instanceof Tuple2) {
      return supports(((Tuple2) obj).getT1());
    }
    return obj instanceof Rte || obj instanceof RteSettings
        || obj instanceof RteEntity || obj instanceof RteEntitySettings
        || obj instanceof RteProperties || obj instanceof RteEntityProperties;
  }

  @Override
  public Mono<RteEntity> mapFeature(final Feature feature) {
    //noinspection unchecked
    return Mono.just(super.mapFeature(feature, RteEntity::new, RteEntityProperties::new));
  }

  @Override
  public Mono<RteEntitySettings> mapFeatureSettings(final FeatureSettings featureSettings,
      final String userId) {
    return Mono.just(super.mapSettings(featureSettings, userId, RteEntitySettings::new));
  }

  @Override
  public Mono<Rte> mapFeatureEntity(
      final FeatureEntity featureEntity,
      final FeatureEntitySettings featureEntitySettings) {
    //noinspection unchecked
    return Mono.just(super.mapFeatureEntity(
        featureEntity,
        featureEntitySettings,
        Rte::new,
        RteProperties::new,
        RteSettings::new));
  }

  @Override
  public Mono<RteEntitySettings> defaultSettings(
      final FeatureEntity featureEntity,
      final String userId) {
    final RteEntitySettings settings = new RteEntitySettings();
    settings.setFeatureId(featureEntity.getId());
    settings.setUserId(userId);
    return Mono.just(settings);
  }

}
