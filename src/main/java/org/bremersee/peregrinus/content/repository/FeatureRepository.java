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

package org.bremersee.peregrinus.content.repository;

import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface FeatureRepository {

  <F extends Feature> Mono<F> persist(@NotNull F feature, @NotNull String userId);

  <F extends Feature> Mono<F> findById(@NotNull String id, @NotNull String userId);

  Mono<Boolean> removeById(
      @NotNull String id,
      @NotNull String userId,
      Collection<String> roles,
      Collection<String> groups);

  Mono<Boolean> updateName(
      String id,
      String name,
      String userId,
      Collection<String> roles,
      Collection<String> groups);

  Mono<Boolean> updateAccessControl(
      String id,
      AccessControl accessControl,
      String userId,
      Collection<String> roles,
      Collection<String> groups);

  Mono<Boolean> updateNameAndAccessControl(
      @Nullable String featureId,
      @Nullable String name,
      @Nullable AccessControlDto accessControl);

  /*
  <F extends Feature<G, P>,
      G extends Geometry, P extends FeatureProperties<S>,
      S extends FeatureSettings> Mono<F> findById(
      @NotNull String id, @NotNull Class<F> cls, @NotNull String userId);

  <F extends Feature<G, P>,
      G extends Geometry, P extends FeatureProperties<S>,
      S extends FeatureSettings> Flux<F> findByIds(
      @NotNull Collection<String> ids, @NotNull Class<F> cls, @NotNull String userId);

  <F extends Feature<G, P>,
      G extends Geometry, P extends FeatureProperties<S>,
      S extends FeatureSettings> Mono<F> persistNodeSettings(
      @NotNull F feature, @NotNull String userId);

  <F extends Feature<G, P>,
      G extends Geometry, P extends FeatureProperties<S>,
      S extends FeatureSettings> Flux<F> persistAll(
      @NotNull Collection<F> features, @NotNull String userId);

  <F extends Feature<G, P>,
      G extends Geometry, P extends FeatureProperties<S>,
      S extends FeatureSettings> Mono<Boolean> delete(@NotNull F feature);
      */

}
