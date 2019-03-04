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
import org.bremersee.peregrinus.tree.model.AbstractNode;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface NodeRepositoryCustom {

  Mono<AbstractNode> findById(
      @NotNull String nodeId,
      @NotNull String permission,
      @Nullable String userId,
      @Nullable Collection<String> roles,
      @Nullable Collection<String> groups);

  /**
   * Find children of a branch. The branch is identified by the {@code parentId}.
   *
   * @param parentId the ID of the parent branch
   * @param userId   the id of an user
   * @param roles    the roles of the user
   * @param groups   the groups of the user
   * @return the children of the branch
   */
  Flux<AbstractNode> findByParentId(
      @NotNull String parentId,
      @Nullable String userId,
      @Nullable Collection<String> roles,
      @Nullable Collection<String> groups);

}
