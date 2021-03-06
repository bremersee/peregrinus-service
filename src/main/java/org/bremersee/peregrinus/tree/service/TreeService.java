/*
 * Copyright 2018 the original author or authors.
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

package org.bremersee.peregrinus.tree.service;

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.tree.model.Branch;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface TreeService {

  Mono<Branch> createBranch(
      @NotNull @Length(min = 1) String name,
      @Nullable String parentId,
      @Nullable AccessControl accessControl,
      @NotNull Authentication authentication);

  Flux<Branch> loadBranches(
      boolean openAll,
      boolean includePublic,
      @NotNull Authentication authentication);

  Mono<Void> renameNode(
      @NotNull String nodeId,
      @NotNull @Length(min = 1) String name,
      @NotNull Authentication authentication);

  Mono<AccessControl> updateAccessControl(
      @NotNull String nodeId,
      boolean recursive,
      @NotNull AccessControl accessControl,
      @NotNull Authentication authentication);

  Mono<Void> deleteNode(
      @NotNull String nodeId,
      @NotNull Authentication authentication);

  Mono<Branch> openBranch(
      @NotNull String branchId,
      boolean openAll,
      @NotNull Authentication authentication);

  Mono<Void> closeBranch(
      @NotNull String branchId,
      @NotNull Authentication authentication);

}
