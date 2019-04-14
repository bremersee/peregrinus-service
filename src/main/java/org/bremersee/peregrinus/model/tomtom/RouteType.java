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
public enum RouteType {

  /**
   * Fastest route type.
   */
  FASTEST("fastest"),

  /**
   * Shortest route type.
   */
  SHORTEST("shortest"),

  /**
   * Eco route type.
   */
  ECO("eco"),

  /**
   * Thrilling route type.
   */
  THRILLING("thrilling");

  @Getter
  private String value;

  RouteType(String value) {
    this.value = value;
  }

  /**
   * From value route type.
   *
   * @param value the value
   * @return the route type
   */
  public static RouteType fromValue(String value) {
    for (RouteType routeType : RouteType
        .values()) {
      if (routeType.getValue().equalsIgnoreCase(value)) {
        return routeType;
      }
    }
    return null;
  }
}
