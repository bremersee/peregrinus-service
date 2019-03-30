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
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.entity.WptEntitySettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.Wpt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class WptAdapter implements FeatureAdapter {

  @Override
  public @NotNull Class<?>[] getSupportedClasses() {
    return new Class[0];
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
  public Mono<Feature> buildFeature(
      final FeatureEntity featureEntity,
      final FeatureEntitySettings featureEntitySettings) {

    return Mono.just(Wpt.builder().build()); // TODO
  }
}
