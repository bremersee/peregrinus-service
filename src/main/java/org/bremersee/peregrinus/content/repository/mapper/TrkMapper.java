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
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.TrkProperties;
import org.bremersee.peregrinus.model.TrkSettings;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.entity.TrkEntity;
import org.bremersee.peregrinus.entity.TrkEntityProperties;
import org.bremersee.peregrinus.entity.TrkEntitySettings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Component
public class TrkMapper extends AbstractFeatureMapper implements FeatureMapper {

  @Override
  public boolean supports(final Object obj) {
    if (obj instanceof Tuple2) {
      return supports(((Tuple2) obj).getT1());
    }
    return obj instanceof Trk || obj instanceof TrkSettings
        || obj instanceof TrkEntity || obj instanceof TrkEntitySettings
        || obj instanceof TrkProperties || obj instanceof TrkEntityProperties;
  }

  @Override
  public Mono<TrkEntity> mapFeature(final Feature feature) {
    //noinspection unchecked
    return Mono.just(super.mapFeature(feature, TrkEntity::new, TrkEntityProperties::new));
  }

  @Override
  public Mono<TrkEntitySettings> mapFeatureSettings(final FeatureSettings featureSettings,
      final String userId) {
    return Mono.just(super.mapSettings(featureSettings, userId, TrkEntitySettings::new));
  }

  @Override
  public Mono<Trk> mapFeatureEntity(
      final FeatureEntity featureEntity,
      final FeatureEntitySettings featureEntitySettings) {
    //noinspection unchecked
    return Mono.just(super.mapFeatureEntity(
        featureEntity,
        featureEntitySettings,
        Trk::new,
        TrkProperties::new,
        TrkSettings::new));
  }

  @Override
  public Mono<TrkEntitySettings> defaultSettings(
      final FeatureEntity featureEntity,
      final String userId) {
    final TrkEntitySettings settings = new TrkEntitySettings();
    settings.setFeatureId(featureEntity.getId());
    settings.setUserId(userId);
    return Mono.just(settings);
  }

}
