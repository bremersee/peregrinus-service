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

package org.bremersee.peregrinus.service.adapter.tomtom.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigInteger;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.model.LatitudeLongitude;
import org.bremersee.plain.model.UnknownAware;

/**
 * A set of attributes describing a maneuver, e.g. 'Turn right', 'Keep left', 'Take the ferry',
 * 'Take the motorway', 'Arrive'.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RouteInstruction extends UnknownAware {

  /**
   * Distance from the start of the route to the point of the instruction.
   */
  private BigInteger routeOffsetInMeters;

  /**
   * Estimated travel time up to the point corresponding to routeOffsetInMeters.
   */
  private BigInteger travelTimeInSeconds;

  /**
   * A location of the maneuver defined as a latitude longitude pair.
   */
  private LatitudeLongitude point;

  /**
   * Type of the instruction, e.g., turn or change of road form.
   */
  private InstructionType instructionType;

  /**
   * An aggregate for roadNumber elements.
   */
  private List<String> roadNumbers;

  /**
   * Road number of the next significant road segment after the maneuver, or of the road that has to
   * be followed.
   */
  private String roadNumber;

  /**
   * The number(s) of a highway exit taken by the current maneuver. If an exit has multiple exit
   * numbers, they will be separated by "," and possibly aggregated by "-", e.g. "10, 13-15".
   */
  private String exitNumber;

  /**
   * Street name of the next significant road segment after the maneuver, or of the street that
   * should be followed.
   */
  private String street;

  /**
   * Text on a signpost which is most relevant to the maneuver, or to the direction that should be
   * followed.
   */
  private String signpostText;

  /**
   * 3-character ISO 3166-1 alpha-3 country code.
   */
  private String countryCode; // 3-letters

  /**
   * Subdivision (e.g. state) of the country, represented by the second part of an ISO 3166-2 code.
   * This is only available for some countries, such as the US, Canada, and Mexico.
   */
  private String stateCode;

  /**
   * Type of the junction at which the maneuver takes place.
   *
   * <p>For larger roundabouts two separate instructions are generated for entering and leaving the
   * roundabout.
   */
  private String junctionType;

  /**
   * Indicates the direction of an instruction. If junctionType indicates a turn instruction:
   *
   * <ul>
   * <li> -180 = U-turn
   * <li> -179 .. -1 = left turn
   * <li> 0 = straight on (a '0 degree' turn)
   * <li> 1 .. 179 = right turn
   * </ul>
   *
   * <p>If junctionType indicates a bifurcation instruction:
   * <ul>
   * <li> {@literal <0} - keep left
   * <li> {@literal >0} - keep right
   * </ul>
   */
  private BigInteger turnAngleInDecimalDegrees;

  /**
   * Indicates which exit to take at a roundabout.
   */
  private BigInteger roundaboutExitNumber;

  /**
   * It is possible to optionally combine the instruction with the next one. This can be used to
   * build messages like "Turn left and then turn right".
   */
  private Boolean possibleCombineWithNext;

  /**
   * Indicates left-hand vs. right-hand side driving at the point of the maneuver.
   *
   * <p>Possible values:
   * <ul>
   * <li> LEFT
   * <li> RIGHT
   * </ul>
   */
  private String drivingSide;

  /**
   * A code identifying the maneuver (e.g. 'Turn right').
   *
   * See
   * <a href="https://developer.tomtom.com/routing-api/routing-api-documentation-routing/calculate-route#maneuverCodes">
   * maneuver codes</a>
   */
  private String maneuver;

  /**
   * A human-readable message for the maneuver.
   */
  private String message;

  /**
   * A human-readable message for the maneuver combined with the message from the next instruction.
   *
   * See
   * <a href="https://developer.tomtom.com/routing-api/routing-api-documentation-routing/calculate-route#combinedMessageExample">
   * example of a combined message</a>
   */
  private String combinedMessage;

  /**
   * The instruction type.
   */
  public enum InstructionType {

    /**
     * Unknown instruction type.
     */
    UNKNOWN,

    /**
     * Turn instruction type.
     */
    TURN,

    /**
     * Road change instruction type.
     */
    ROAD_CHANGE,

    /**
     * Location departure instruction type.
     */
    LOCATION_DEPARTURE,

    /**
     * Location arrival instruction type.
     */
    LOCATION_ARRIVAL,

    /**
     * Direction info instruction type.
     */
    DIRECTION_INFO,

    /**
     * Location waypoint instruction type.
     */
    LOCATION_WAYPOINT;

    @JsonValue
    @Override
    public String toString() {
      return name();
    }

    /**
     * From value instruction type.
     *
     * @param value the value
     * @return the instruction type
     */
    @JsonCreator
    public static InstructionType fromValue(String value) {
      for (InstructionType instructionType : InstructionType.values()) {
        if (instructionType.name().equalsIgnoreCase(value)) {
          return instructionType;
        }
      }
      return UNKNOWN;
    }
  }

}
