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

import lombok.Getter;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;

/**
 * @author Christian Bremer
 */
public enum DisplayColor {

  BLACK("Black", DisplayColorT.BLACK, 0),

  DARK_RED("DarkRed", DisplayColorT.DARK_RED, 0),

  DARK_GREEN("DarkGreen", DisplayColorT.DARK_GREEN, 0),

  DARK_YELLOW("DarkYellow", DisplayColorT.DARK_YELLOW, 0),

  DARK_BLUE("DarkBlue", DisplayColorT.DARK_BLUE, 0),

  DARK_MAGENTA("DarkMagenta", DisplayColorT.DARK_MAGENTA, 0),

  DARK_CYAN("DarkCyan", DisplayColorT.DARK_CYAN, 0),

  LIGHT_GRAY("LightGray", DisplayColorT.LIGHT_GRAY, 0),

  DARK_GRAY("DarkGray", DisplayColorT.DARK_GRAY, 0),

  RED("Red", DisplayColorT.RED, 0),

  GREEN("Green", DisplayColorT.GREEN, 0),

  YELLOW("Yellow", DisplayColorT.YELLOW, 0),

  BLUE("Blue", DisplayColorT.BLUE, 0),

  MAGENTA("Magenta", DisplayColorT.MAGENTA, 0),

  CYAN("Cyan", DisplayColorT.CYAN, 0),

  WHITE("White", DisplayColorT.WHITE, 0),

  TRANSPARENT("Transparent", DisplayColorT.TRANSPARENT, 0);

  @Getter
  private final String value;

  @Getter
  private DisplayColorT garmin;

  private int garminOrder;

  DisplayColor(String value, DisplayColorT garmin, int garminOrder) {
    this.value = value;
    this.garmin = garmin;
    this.garminOrder = garminOrder;
  }

  public static DisplayColor findByGarminDisplayColor(
      final DisplayColorT garmin,
      final DisplayColor defaultDisplayColor) {

    if (garmin != null) {
      for (DisplayColor displayColor : DisplayColor.values()) {
        if (displayColor.garminOrder == 0 && displayColor.garmin == garmin) {
          return displayColor;
        }
      }
    }
    return defaultDisplayColor != null ? defaultDisplayColor : MAGENTA;
  }

}
