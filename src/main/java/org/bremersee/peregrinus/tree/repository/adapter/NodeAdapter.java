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

package org.bremersee.peregrinus.tree.repository.adapter;

import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.model.NodeSettings;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntity;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntitySettings;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Validated
public interface NodeAdapter {

  @NotNull
  Class<?>[] getSupportedClasses();

  <T1 extends NodeEntity, T2 extends NodeEntitySettings> Mono<Tuple2<T1, T2>> mapNode(
      @NotNull Node node,
      @NotNull String userId);

  Mono<? extends NodeEntitySettings> mapNodeSettings(
      @NotNull NodeSettings nodeSettings,
      @NotNull String userId);

  Mono<? extends Node> mapNodeEntity(
      @NotNull NodeEntity nodeEntity,
      @NotNull NodeEntitySettings nodeEntitySettings);

  Mono<? extends NodeSettings> mapNodeEntitySettings(
      @NotNull NodeEntitySettings nodeEntitySettings);

  Mono<? extends NodeEntitySettings> defaultSettings(
      @NotNull NodeEntity nodeEntity,
      @NotNull String userId);

  Mono<NodeEntity> updateName(
      @NotNull NodeEntity nodeEntity,
      @NotNull String name,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  Mono<NodeEntity> updateAccessControl(
      @NotNull NodeEntity nodeEntity,
      @NotNull AccessControlList acl,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  Mono<NodeEntity> removeNode(
      @NotNull NodeEntity nodeEntity,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

}
