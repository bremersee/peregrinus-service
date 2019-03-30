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

import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bremersee.peregrinus.entity.NodeEntitySettings;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface TreeRepository {

  default Mono<BranchEntity> findBranchById(
      @NotNull String id,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    return findNodeById(id, permission, includePublic, userId, roles, groups)
        .filter(node -> node instanceof BranchEntity)
        .cast(BranchEntity.class);
  }

  Mono<NodeEntity> findNodeById(
      @NotNull String id,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);

  <T extends NodeEntity> Mono<T> persistNode(
      @NotNull T node);

  default Flux<BranchEntity> findRootBranches(
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    return findNodesByParentId(
        null, permission, includePublic, userId, roles, groups)
        .filter(node -> node instanceof BranchEntity)
        .cast(BranchEntity.class);
  }

  Flux<NodeEntity> findNodesByParentId(
      @NotNull String parentId,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups);


  <T extends NodeEntitySettings> Mono<T> persistNodeSettings(
      @NotNull T nodeSettings);

  Mono<NodeEntitySettings> findNodeSettings(
      @NotNull String nodeId,
      @NotNull String userId);

  // Result is currently not used, can be update result
  Mono<Boolean> openBranch(@NotNull String settingsId);

  Mono<Void> closeBranch(@NotNull String branchId, @NotNull String userId);

/*
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

 */

}
