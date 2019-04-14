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
public enum Hilliness {

  /**
   * Low hilliness.
   */
  LOW("low"),

  /**
   * Normal hilliness.
   */
  NORMAL("normal"),

  /**
   * High hilliness.
   */
  HIGH("high");

  @Getter
  private String value;

  Hilliness(String value) {
    this.value = value;
  }

  /**
   * From value hilliness.
   *
   * @param value the value
   * @return the hilliness
   */
  public static Hilliness fromValue(String value) {
    for (Hilliness hilliness : Hilliness.values()) {
      if (hilliness.getValue().equalsIgnoreCase(value)) {
        return hilliness;
      }
    }
    return null;
  }
}
