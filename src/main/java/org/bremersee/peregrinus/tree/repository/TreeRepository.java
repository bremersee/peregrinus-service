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

package org.bremersee.peregrinus.tree.repository;

import java.util.Collection;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.model.NodeSettings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public interface TreeRepository {

  <T> Mono<T> persist(T entity);

  Mono<Void> delete(Object entity);

  <T extends Node> Mono<T> findNodeById(
      Class<T> clazz,
      String id,
      String permission,
      boolean includePublic,
      String userId,
      Collection<String> roles,
      Collection<String> groups);

  <T extends Node> Flux<T> findNodesByParentId(
      Class<T> clazz,
      String parentId);

  <T extends Node> Flux<T> findNodesByParentId(
      Class<T> clazz,
      String parentId,
      String permission,
      boolean includePublic,
      String userId,
      Collection<String> roles,
      Collection<String> groups);

  <T extends NodeSettings> Mono<T> findNodeSettings(Class<T> clazz, String nodeId, String userId);

  Mono<Void> deleteNodeSettings(String nodeId, String userId);

}
