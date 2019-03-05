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

package org.bremersee.peregrinus.tree.service;

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.tree.model.AbstractLeaf;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface LeafAdapter { // N = Node, C = Content?

  boolean supportsLeaf(AbstractLeaf leaf);

  Mono<AbstractLeaf> setLeafName(@NotNull AbstractLeaf leaf);

  Mono<AbstractLeaf> setLeafSettings(@NotNull AbstractLeaf leaf, @NotNull String userId);

  Mono<AbstractLeaf> setLeafContent(@NotNull AbstractLeaf leaf, @NotNull String userId);

  Mono<Void> renameLeaf(@NotNull AbstractLeaf leaf, @NotNull String name);

  Mono<AccessControl> updateAccessControl(
      @NotNull AbstractLeaf leaf,
      @NotNull AccessControl accessControl);

  Mono<Void> delete(@NotNull AbstractLeaf leaf, @NotNull String userId);
}
