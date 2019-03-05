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

import org.bremersee.peregrinus.content.model.FeatureSettings;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public interface FeatureRepository {

  <T> Mono<T> persist(T entity);

  Mono<Void> delete(Object entity);

  <T extends FeatureSettings> Mono<T> findFeatureSettings(
      Class<T> clazz,
      String featureId,
      String userId);

  Mono<Void> deleteFeatureSettings(String featureId, String userId);

}
