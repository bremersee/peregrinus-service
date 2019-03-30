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
import javax.validation.constraints.NotNull;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.Node;
import org.bremersee.peregrinus.model.NodeSettings;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface TreeRepository {

  <T extends Node> Mono<T> persistNode(
      @NotNull T node,
      @NotNull String userId);

  default Flux<Branch> findRootBranches(
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    return findNodesByParentId(
        null, permission, includePublic, userId, roles, groups)
        .filter(node -> node instanceof Branch)
        .cast(Branch.class);
  }

  default Mono<Branch> findBranchById(
      @NotNull String id,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    return findNodeById(id, permission, includePublic, userId, roles, groups)
        .filter(node -> node instanceof Branch)
        .cast(Branch.class);
  }

  Mono<Node> findNodeById(
      @NotNull String id,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  Flux<Node> findNodesByParentId(
      @NotNull String parentId,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  Mono<Boolean> updateName(
      @NotNull String id,
      @NotNull String name,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  Mono<Boolean> updateAccessControl(
      @NotNull String id,
      @NotNull AccessControlList acl,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  Mono<Boolean> removeNode(
      @NotNull String id,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  <T extends NodeSettings> Mono<T> persistNodeSettings(
      @NotNull T settings,
      @NotNull String userId);

  Mono<Boolean> closeBranch(
      @NotNull String branchId,
      @NotNull String userId);

}
