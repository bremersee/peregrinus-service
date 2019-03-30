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

package org.bremersee.peregrinus.service;

import java.util.Set;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.model.Branch;
import org.hibernate.validator.constraints.Length;
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
      @NotNull String userId);

  Mono<Branch> createBranch(
      @NotNull @Length(min = 1) String name,
      @NotNull String parentId,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Flux<Branch> loadBranches(
      boolean openAll,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Branch> openBranch(
      @NotNull String branchId,
      boolean openAll,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Void> closeBranch(
      @NotNull String branchId,
      @NotNull String userId);

  Mono<Boolean> renameNode(
      @NotNull String nodeId,
      @NotNull @Length(min = 1) String name,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  /*
  Mono<Boolean> updateAccessControl(
      @NotNull String nodeId,
      @NotNull AccessControlList acl);

  Mono<Boolean> removeNode(
      @NotNull String nodeId);

  */

}
