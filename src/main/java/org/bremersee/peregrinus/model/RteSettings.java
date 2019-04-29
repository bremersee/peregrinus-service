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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The route settings.
 *
 * @author Christian Bremer
 */
@ApiModel(description = "The route settings.")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RteSettings extends FeatureSettings {

  private DisplayColor displayColor;

  /**
   * Instantiates new route settings.
   */
  @SuppressWarnings("WeakerAccess")
  public RteSettings() {
    displayColor = DisplayColor.MAGENTA;
  }

  /**
   * Instantiates new route settings.
   *
   * @param id           the id
   * @param featureId    the feature id
   * @param userId       the user id
   * @param displayColor the display color
   */
  @Builder
  @SuppressWarnings("unused")
  public RteSettings(
      String id,
      String featureId,
      String userId,
      DisplayColor displayColor) {

    super(id, featureId, userId);
    setDisplayColor(displayColor);
  }

  /**
   * Sets display color.
   *
   * @param displayColor the display color
   */
  public void setDisplayColor(DisplayColor displayColor) {
    if (displayColor != null) {
      this.displayColor = displayColor;
    }
  }
}
