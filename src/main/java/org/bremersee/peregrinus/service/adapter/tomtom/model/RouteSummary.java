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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.model.LatitudeLongitude;
import org.bremersee.plain.model.UnknownAware;

/**
 * A summary of a route, or a route leg.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("WeakerAccess")
public class RouteSummary extends UnknownAware {

  private BigInteger lengthInMeters;

  private BigInteger travelTimeInSeconds;

  private BigInteger trafficDelayInSeconds;

  /**
   * The estimated departure time for the route or leg.
   */
  private OffsetDateTime departureTime;

  /**
   * The estimated arrival time for the route or leg.
   */
  private OffsetDateTime arrivalTime;

  private BigInteger noTrafficTravelTimeInSeconds;

  private BigInteger historicTrafficTravelTimeInSeconds;

  private BigInteger liveTrafficIncidentsTravelTimeInSeconds;

  private BigDecimal fuelConsumptionInLiters;

  private BigDecimal batteryConsumptionInkWh;

  private BigInteger deviationDistance;

  private BigInteger deviationTime;

  private LatitudeLongitude deviationPoint;

}
