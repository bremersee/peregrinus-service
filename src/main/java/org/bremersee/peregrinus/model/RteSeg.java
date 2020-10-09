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

package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The route segment.
 *
 * @author Christian Bremer
 */
@Schema(description = "Route segment.")
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class RteSeg {

  @Schema(description = "The route points.")
  private List<RtePt> rtePts;

  @Schema(description = "The calculation settings of this route segment.")
  private RteSegCalcSettings calculationSettings;

  @Schema(description = "Travel time in seconds.")
  private BigInteger travelTimeInSeconds;

  @Schema(description = "Length in meters.")
  private BigInteger lengthInMeters;

  /**
   * Instantiates a new route segment.
   *
   * @param rtePts              the rte pts
   * @param calculationSettings the calculation settings
   * @param travelTimeInSeconds the travel time in seconds
   * @param lengthInMeters      the length in meters
   */
  @Builder
  public RteSeg(
      List<RtePt> rtePts,
      RteSegCalcSettings calculationSettings,
      BigInteger travelTimeInSeconds,
      BigInteger lengthInMeters) {
    this.rtePts = rtePts;
    this.calculationSettings = calculationSettings;
    this.travelTimeInSeconds = travelTimeInSeconds;
    this.lengthInMeters = lengthInMeters;
  }

  /**
   * Gets route points.
   *
   * @return the route points
   */
  public List<RtePt> getRtePts() {
    if (rtePts == null) {
      rtePts = new ArrayList<>();
    }
    return rtePts;
  }

  /**
   * Has departure route point.
   *
   * @return the boolean
   */
  public boolean hasDepartureRtePtPresent() {
    return findDepartureRtePt() != null;
  }

  /**
   * Find departure route point.
   *
   * @return the rte pt
   */
  public RtePt findDepartureRtePt() {
    if (rtePts == null || rtePts.isEmpty()) {
      return null;
    }
    return rtePts.get(0);
  }

  /**
   * Has arrival route point.
   *
   * @return the boolean
   */
  public boolean hasArrivalRtePtPresent() {
    return findArrivalRtePt() != null;
  }

  /**
   * Find arrival route point.
   *
   * @return the rte pt
   */
  public RtePt findArrivalRtePt() {
    if (rtePts == null || rtePts.isEmpty()) {
      return null;
    }
    return rtePts.get(rtePts.size() - 1);
  }
}
