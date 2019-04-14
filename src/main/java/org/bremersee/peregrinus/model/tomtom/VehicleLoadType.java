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
public enum VehicleLoadType {
  /**
   * Us hazmat class 1 vehicle load type.
   */
  US_HAZMAT_CLASS_1("USHazmatClass1", "Explosives (US)"),

  /**
   * The Us hazmat class 2.
   */
  US_HAZMAT_CLASS_2("USHazmatClass2", "Compressed gas"),

  /**
   * The Us hazmat class 3.
   */
  US_HAZMAT_CLASS_3("USHazmatClass3", "Flammable liquids"),

  /**
   * The Us hazmat class 4.
   */
  US_HAZMAT_CLASS_4("USHazmatClass4", "Flammable solids"),

  /**
   * Us hazmat class 5 vehicle load type.
   */
  US_HAZMAT_CLASS_5("USHazmatClass5", "Oxidizers"),

  /**
   * Us hazmat class 6 vehicle load type.
   */
  US_HAZMAT_CLASS_6("USHazmatClass6", "Poisons"),

  /**
   * Us hazmat class 7 vehicle load type.
   */
  US_HAZMAT_CLASS_7("USHazmatClass7", "Radioactive"),

  /**
   * Us hazmat class 8 vehicle load type.
   */
  US_HAZMAT_CLASS_8("USHazmatClass8", "Corrosives"),

  /**
   * Us hazmat class 9 vehicle load type.
   */
  US_HAZMAT_CLASS_9("USHazmatClass9", "Miscellaneous"),

  /**
   * Other hazmat explosive vehicle load type.
   */
  OTHER_HAZMAT_EXPLOSIVE("otherHazmatExplosive", "Explosives"),

  /**
   * Other hazmat general vehicle load type.
   */
  OTHER_HAZMAT_GENERAL("otherHazmatGeneral", "Miscellaneous"),

  /**
   * The Other hazmat harmful to water.
   */
  OTHER_HAZMAT_HARMFUL_TO_WATER("otherHazmatHarmfulToWater", "Harmful to water");

  @Getter
  private String value;

  @Getter
  private String description;

  VehicleLoadType(String value, String description) {
    this.value = value;
    this.description = description;
  }

  /**
   * From value vehicle load type.
   *
   * @param value the value
   * @return the vehicle load type
   */
  public static VehicleLoadType fromValue(String value) {
    for (VehicleLoadType vehicleLoadType : VehicleLoadType.values()) {
      if (vehicleLoadType.getValue().equalsIgnoreCase(value)) {
        return vehicleLoadType;
      }
    }
    return null;
  }
}
