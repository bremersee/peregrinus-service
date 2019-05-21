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
 * The GeoJSON feature leaf settings entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "directory-settings")
@TypeAlias(TypeAliases.FEATURE_LEAF_SETTINGS)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class FeatureLeafEntitySettings extends LeafEntitySettings {

  /**
   * The constant DISPLAYED_ON_MAP_PATH.
   */
  public static final String DISPLAYED_ON_MAP_PATH = "displayedOnMap";

  @Getter
  private Boolean displayedOnMap = false;

  /**
   * Instantiates a new GeoJSON feature leaf settings entity.
   *
   * @param id             the id
   * @param nodeId         the node id
   * @param userId         the user id
   * @param displayedOnMap the displayed on map
   */
  @Builder
  @SuppressWarnings("unused")
  public FeatureLeafEntitySettings(
      String id,
      String nodeId,
      String userId,
      Boolean displayedOnMap) {
    super(id, nodeId, userId);
    setDisplayedOnMap(displayedOnMap);
  }

  /**
   * Sets displayed on map.
   *
   * @param displayedOnMap the displayed on map
   */
  public void setDisplayedOnMap(Boolean displayedOnMap) {
    if (displayedOnMap != null) {
      this.displayedOnMap = displayedOnMap;
    }
  }
}
