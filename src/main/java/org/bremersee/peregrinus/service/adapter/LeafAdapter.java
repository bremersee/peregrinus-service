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

package org.bremersee.peregrinus.service.adapter;

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.LeafEntity;
import org.bremersee.peregrinus.entity.LeafEntitySettings;
import org.bremersee.peregrinus.model.Leaf;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface LeafAdapter {

  @NotNull
  String[] getSupportedKeys();

  @NotNull
  LeafEntitySettings buildLeafEntitySettings(
      @NotNull LeafEntity leafEntity,
      @NotNull String userId);

  Mono<Leaf> buildLeaf(
      @NotNull LeafEntity leafEntity,
      @NotNull LeafEntitySettings leafEntitySettings);

  Mono<Boolean> renameLeaf(
      @NotNull LeafEntity leafEntity,
      @NotNull String name,
      @NotNull String userId);

}
