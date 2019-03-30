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

package org.bremersee.peregrinus.repository;

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface FeatureRepository {

  <S extends FeatureEntitySettings> Mono<S> persistFeatureSettings(@NotNull S featureSettings);

  <F extends FeatureEntity> Mono<F> persistFeature(@NotNull F feature);


  /*
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
      AccessControlList accessControl,
      String userId,
      Collection<String> roles,
      Collection<String> groups);

  Mono<Boolean> updateNameAndAccessControl(
      @Nullable String featureId,
      @Nullable String name,
      @Nullable AccessControlList accessControl);
  */

}
