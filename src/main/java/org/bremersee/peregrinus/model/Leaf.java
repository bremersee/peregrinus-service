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

package org.bremersee.peregrinus.model;

import io.swagger.annotations.ApiModel;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;

/**
 * The tree leaf.
 *
 * @param <S> the leaf settings type parameter
 * @author Christian Bremer
 */
@ApiModel(description = "The tree leaf.")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class Leaf<S extends LeafSettings> extends Node<S> {

  /**
   * Instantiates a new tree leaf.
   *
   * @param id         the id
   * @param created    the created
   * @param createdBy  the created by
   * @param modified   the modified
   * @param modifiedBy the modified by
   * @param acl        the acl
   * @param settings   the settings
   * @param parentId   the parent id
   * @param name       the name
   */
  @SuppressWarnings("WeakerAccess")
  public Leaf(
      String id,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      AccessControlList acl,
      S settings,
      String parentId,
      String name) {
    super(id, created, createdBy, modified, modifiedBy, acl, settings, parentId, name);
  }

}
