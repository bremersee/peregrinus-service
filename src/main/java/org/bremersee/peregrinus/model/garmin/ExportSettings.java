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

package org.bremersee.peregrinus.model.garmin;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.garmin.model.WptSymbol;
import org.bremersee.garmin.trip.v1.model.ext.TripTransportationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointCalculationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointElevationMode;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ExportSettings {

  private String name;

  private String description;

  private Boolean exportRouteAsTrack = Boolean.TRUE; // TODO

  private Boolean exportRouteWaypoints = Boolean.TRUE;

  private WptSymbol routeWaypointSymbol = WptSymbol.FLAG_BLUE;

  // values 100, 50, 33, 25, 20, 15, 14, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
  private Integer percentWaypoints = 100; // enum?

  private TripTransportationMode transportationMode = TripTransportationMode.AUTOMOTIVE;

  private ViaPointCalculationMode calculationMode = ViaPointCalculationMode.FASTER_TIME;

  private ViaPointElevationMode elevationMode = ViaPointElevationMode.STANDARD;

}
