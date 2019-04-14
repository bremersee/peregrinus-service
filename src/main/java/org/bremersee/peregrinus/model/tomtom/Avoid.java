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

package org.bremersee.peregrinus.model.tomtom;

import lombok.Getter;

/**
 * @author Christian Bremer
 */
public enum Avoid {

  /**
   * Toll roads avoid.
   */
  TOLL_ROADS("tollRoads"),

  /**
   * Motorways avoid.
   */
  MOTORWAYS("motorways"),

  /**
   * Ferries avoid.
   */
  FERRIES("ferries"),

  /**
   * Unpaved roads avoid.
   */
  UNPAVED_ROADS("unpavedRoads"),

  /**
   * Carpools avoid.
   */
  CARPOOLS("carpools"),

  /**
   * Already used roads avoid.
   */
  ALREADY_USED_ROADS("alreadyUsedRoads");

  @Getter
  private String value;

  Avoid(String value) {
    this.value = value;
  }

  /**
   * From value avoid.
   *
   * @param value the value
   * @return the avoid
   */
  public static Avoid fromValue(String value) {
    for (Avoid avoid : Avoid
        .values()) {
      if (avoid.getValue().equalsIgnoreCase(value)) {
        return avoid;
      }
    }
    return null;
  }
}
