/*
 * Copyright 2017 the original author or authors.
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

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;

/**
 * The tree branch.
 *
 * @author Christian Bremer
 */
@Schema(description = "The tree branch.")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Branch extends Node<BranchSettings> {

  @Schema(description = "The children.")
  private List<Node> children;

  /**
   * Instantiates a new tree branch.
   */
  public Branch() {
    setSettings(new BranchSettings());
  }

  /**
   * Instantiates a new tree branch.
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
   * @param children   the children
   */
  @Builder
  public Branch(
      String id,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      AccessControlList acl,
      BranchSettings settings,
      String parentId,
      String name,
      List<Node> children) {
    super(id, created, createdBy, modified, modifiedBy, acl, settings, parentId, name);
    this.children = children;
  }

  /**
   * Gets children.
   *
   * @return the children
   */
  public List<Node> getChildren() {
    if (children == null) {
      children = new ArrayList<>();
    }
    return children;
  }

}
