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

import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.peregrinus.content.model.WptSettings;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntity;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.content.repository.entity.WptEntity;
import org.bremersee.peregrinus.content.repository.entity.WptEntityProperties;
import org.bremersee.peregrinus.content.repository.entity.WptEntitySettings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Component
public class WptMapper extends AbstractFeatureMapper implements FeatureMapper {

  @Override
  public boolean supports(final Object obj) {
    if (obj instanceof Tuple2) {
      return supports(((Tuple2) obj).getT1());
    }
    return obj instanceof Wpt || obj instanceof WptSettings
        || obj instanceof WptEntity || obj instanceof WptEntitySettings
        || obj instanceof WptProperties || obj instanceof WptEntityProperties;
  }

  @Override
  public Mono<WptEntity> mapFeature(final Feature feature) {
    //noinspection unchecked
    return Mono.just(super.mapFeature(feature, WptEntity::new, WptEntityProperties::new));
  }

  @Override
  public Mono<WptEntitySettings> mapFeatureSettings(final FeatureSettings featureSettings,
      final String userId) {
    return Mono.just(super.mapSettings(featureSettings, userId, WptEntitySettings::new));
  }

  @Override
  public Mono<Wpt> mapFeatureEntity(
      final FeatureEntity featureEntity,
      final FeatureEntitySettings featureEntitySettings) {
    //noinspection unchecked
    return Mono.just(super.mapFeatureEntity(
        featureEntity,
        featureEntitySettings,
        Wpt::new,
        WptProperties::new,
        WptSettings::new));
  }

  @Override
  public Mono<WptEntitySettings> defaultSettings(
      final FeatureEntity featureEntity,
      final String userId) {
    final WptEntitySettings settings = new WptEntitySettings();
    settings.setFeatureId(featureEntity.getId());
    settings.setUserId(userId);
    return Mono.just(settings);
  }

}
