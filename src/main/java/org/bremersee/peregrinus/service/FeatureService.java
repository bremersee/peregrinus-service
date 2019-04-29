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

package org.bremersee.peregrinus.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface FeatureService {

  default Flux<Feature> persistFeatures(
      @NotNull Collection<? extends Feature> features,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups) {

    if (!features.isEmpty()) {
      return Flux.concat(
          features
              .stream()
              .map(feature -> persistFeature(feature, userId, roles, groups))
              .collect(Collectors.toList()));
    }
    return Flux.empty();
  }

  Mono<Feature> persistFeature(
      @NotNull Feature feature,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Feature> findFeatureById(
      @NotNull String id,
      @NotNull String userId);

  Flux<Feature> findFeaturesById(
      @NotNull Set<String> featureIds,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Boolean> renameFeature(
      @NotNull String id,
      @NotNull String name,
      @NotNull String userId);

  Mono<Gpx> exportGpx(
      @NotNull Set<String> featureIds,
      @NotNull GpxExportSettings exportSettings,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

}
