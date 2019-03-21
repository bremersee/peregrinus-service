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

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntity;
import org.bremersee.peregrinus.content.repository.entity.FeatureEntitySettings;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface FeatureMapper {

  boolean supports(Object obj);

  Mono<? extends FeatureEntity> mapFeature(@NotNull Feature feature);

  Mono<? extends FeatureEntitySettings> mapFeatureSettings(
      @NotNull FeatureSettings featureSettings,
      @NotNull String userId);

  Mono<? extends Feature> mapFeatureEntity(
      @NotNull FeatureEntity featureEntity,
      @NotNull FeatureEntitySettings featureEntitySettings);

  Mono<? extends FeatureEntitySettings> defaultSettings(
      @NotNull FeatureEntity featureEntity,
      @NotNull String userId);

}
