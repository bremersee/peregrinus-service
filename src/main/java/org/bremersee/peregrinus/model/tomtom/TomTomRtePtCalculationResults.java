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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.model.RtePtCalculationResults;
import org.locationtech.jts.geom.Point;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TomTomRtePtCalculationResults extends RtePtCalculationResults {

  private Integer lengthInMeters;

  private Integer travelTimeInSeconds;

  private Integer trafficDelayInSeconds;

  /**
   * The estimated departure time for the route or leg.
   */
  private OffsetDateTime departureTime;

  /**
   * The estimated arrival time for the route or leg.
   */
  private OffsetDateTime arrivalTime;

  private Integer noTrafficTravelTimeInSeconds;

  private Integer historicTrafficTravelTimeInSeconds;

  private Integer liveTrafficIncidentsTravelTimeInSeconds;

  private BigDecimal fuelConsumptionInLiters;

  private BigDecimal batteryConsumptionInkWh;

  private Integer deviationDistance;

  private Integer deviationTime;

  private Point deviationPoint;

  @Builder
  public TomTomRtePtCalculationResults(Integer lengthInMeters, Integer travelTimeInSeconds,
      Integer trafficDelayInSeconds, OffsetDateTime departureTime,
      OffsetDateTime arrivalTime, Integer noTrafficTravelTimeInSeconds,
      Integer historicTrafficTravelTimeInSeconds,
      Integer liveTrafficIncidentsTravelTimeInSeconds, BigDecimal fuelConsumptionInLiters,
      BigDecimal batteryConsumptionInkWh, Integer deviationDistance, Integer deviationTime,
      Point deviationPoint) {
    this.lengthInMeters = lengthInMeters;
    this.travelTimeInSeconds = travelTimeInSeconds;
    this.trafficDelayInSeconds = trafficDelayInSeconds;
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
    this.noTrafficTravelTimeInSeconds = noTrafficTravelTimeInSeconds;
    this.historicTrafficTravelTimeInSeconds = historicTrafficTravelTimeInSeconds;
    this.liveTrafficIncidentsTravelTimeInSeconds = liveTrafficIncidentsTravelTimeInSeconds;
    this.fuelConsumptionInLiters = fuelConsumptionInLiters;
    this.batteryConsumptionInkWh = batteryConsumptionInkWh;
    this.deviationDistance = deviationDistance;
    this.deviationTime = deviationTime;
    this.deviationPoint = deviationPoint;
  }
}
