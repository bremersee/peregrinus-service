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

package org.bremersee.peregrinus.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The tree branch settings entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "directory-settings")
@TypeAlias(TypeAliases.BRANCH_SETTINGS)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public final class BranchEntitySettings extends NodeEntitySettings {

  /**
   * The constant OPEN_PATH.
   */
  public static final String OPEN_PATH = "open";

  @Getter
  private Boolean open = true;

  /**
   * Instantiates a new tree branch settings entity.
   *
   * @param id     the id
   * @param nodeId the node id
   * @param userId the user id
   * @param open   the open
   */
  @Builder
  @SuppressWarnings("unused")
  public BranchEntitySettings(String id, String nodeId, String userId, Boolean open) {
    super(id, nodeId, userId);
    if (open != null) {
      this.open = open;
    }
  }

  /**
   * Sets open.
   *
   * @param open the open
   */
  public void setOpen(Boolean open) {
    if (open != null) {
      this.open = open;
    }
  }

}
