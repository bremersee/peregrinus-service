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

package org.bremersee.peregrinus.tree.model;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class Leaf<S extends LeafSettings> extends Node<S> {

  public Leaf(
      final String userId,
      final String parentId,
      final AccessControlDto accessControl,
      final S settings,
      @NotNull(message = "Name must not be null.") final String name) {

    super(userId, parentId, accessControl, settings);
    setName(name);
  }
}
