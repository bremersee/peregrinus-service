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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Christian Bremer
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class FeatureLeafSettings extends LeafSettings {

  @Getter
  private Boolean displayedOnMap = false;

  @Builder
  public FeatureLeafSettings(String id, String nodeId, String userId, Boolean displayedOnMap) {
    super(id, nodeId, userId);
    setDisplayedOnMap(displayedOnMap);
  }

  public void setDisplayedOnMap(final Boolean displayedOnMap) {
    if (displayedOnMap != null) {
      this.displayedOnMap = displayedOnMap;
    }
  }
}
