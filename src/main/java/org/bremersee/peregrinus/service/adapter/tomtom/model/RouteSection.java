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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.UnknownAware;
import org.bremersee.peregrinus.model.tomtom.TravelMode;

/**
 * The route section.
 *
 * <p>Sections contain additional information about parts of a route. Each {@code section}
 * contains at least the elements {@code startPointIndex}, {@code endPointIndex} {@code
 * sectionType}*.
 *
 * <p>See
 * <a href="https://developer.tomtom.com/routing-api/routing-api-documentation-routing/calculate-route#responseSection">
 * Structure of route sections</a>
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("WeakerAccess")
public class RouteSection extends UnknownAware {

  /**
   * Index of the first point (offset 0) in the route this section applies to (only included for
   * routeRepresentation polyline).
   */
  private BigInteger startPointIndex;

  /**
   * Index of the last point (offset 0) in the route this section applies to (only included for
   * routeRepresentation polyline).
   */
  private BigInteger endPointIndex;

  /**
   * Contains the response section type.
   */
  private SectionType sectionType;

  /**
   * This attribute is either set to the value given to the request parameter travelMode, if this
   * travel mode is possible, or to other which indicates that the given mode of transport is not
   * possible in this section. This field can only be used within sections of type TRAVEL_MODE.
   */
  private TravelMode travelMode;

  /**
   * The section type.
   */
  public enum SectionType {

    /**
     * Unknown section type.
     */
    UNKNOWN,

    /**
     * Car train section type.
     */
    CAR_TRAIN,

    /**
     * Country section type.
     */
    COUNTRY,

    /**
     * Ferry section type.
     */
    FERRY,

    /**
     * Motorway section type.
     */
    MOTORWAY,

    /**
     * Pedestrian section type.
     */
    PEDESTRIAN,

    /**
     * Toll road section type.
     */
    TOLL_ROAD,

    /**
     * Toll vignette section type.
     */
    TOLL_VIGNETTE,

    /**
     * Traffic section type.
     */
    TRAFFIC,

    /**
     * Travel mode section type.
     */
    TRAVEL_MODE,

    /**
     * Tunnel section type.
     */
    TUNNEL;

    @JsonValue
    @Override
    public String toString() {
      return name();
    }

    /**
     * From value section type.
     *
     * @param value the value
     * @return the section type
     */
    @JsonCreator
    public static SectionType fromValue(String value) {
      for (SectionType sectionType : SectionType.values()) {
        if (sectionType.name().equalsIgnoreCase(value)) {
          return sectionType;
        }
      }
      return UNKNOWN;
    }
  }

}
