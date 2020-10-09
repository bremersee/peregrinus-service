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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The branch settings.
 *
 * @author Christian Bremer
 */
@Schema(description = "The branch settings.")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public final class BranchSettings extends NodeSettings {

  @Getter
  @Schema(description = "Specifies whether the branch is open or not.")
  private Boolean open = true;

  /**
   * Instantiates new branch settings.
   *
   * @param id     the id
   * @param nodeId the node id
   * @param userId the user id
   * @param open   the open
   */
  @Builder
  public BranchSettings(String id, String nodeId, String userId, Boolean open) {
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
    this.open = Boolean.TRUE.equals(open);
  }

}
