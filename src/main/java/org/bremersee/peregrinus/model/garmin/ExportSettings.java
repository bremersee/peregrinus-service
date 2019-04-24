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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bremersee.garmin.model.WptSymbol;
import org.bremersee.garmin.trip.v1.model.ext.TripTransportationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointCalculationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointElevationMode;

/**
 * @author Christian Bremer
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.PUBLIC_ONLY,
    isGetterVisibility = Visibility.PUBLIC_ONLY,
    setterVisibility = Visibility.PUBLIC_ONLY)
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ExportSettings {

  private String author;

  private String name;

  private String description;

  private Boolean exportRouteAsTrack = Boolean.TRUE;

  private Boolean exportRouteWaypoints = Boolean.TRUE;

  private WptSymbol routeWaypointSymbol = WptSymbol.FLAG_BLUE;

  // values 100, 50, 33, 25, 20, 15, 14, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
  private Integer percentWaypoints = 100; // enum?

  private TripTransportationMode transportationMode = TripTransportationMode.AUTOMOTIVE;

  private ViaPointCalculationMode calculationMode = ViaPointCalculationMode.FASTER_TIME;

  private ViaPointElevationMode elevationMode = ViaPointElevationMode.STANDARD;

  @Builder
  public ExportSettings(
      String author,
      String name,
      String description,
      Boolean exportRouteAsTrack,
      Boolean exportRouteWaypoints,
      WptSymbol routeWaypointSymbol,
      Integer percentWaypoints,
      TripTransportationMode transportationMode,
      ViaPointCalculationMode calculationMode,
      ViaPointElevationMode elevationMode) {

    setAuthor(author);
    setName(name);
    setDescription(description);
    setExportRouteAsTrack(exportRouteAsTrack);
    setExportRouteWaypoints(exportRouteWaypoints);
    setRouteWaypointSymbol(routeWaypointSymbol);
    setPercentWaypoints(percentWaypoints);
    setTransportationMode(transportationMode);
    setCalculationMode(calculationMode);
    setElevationMode(elevationMode);
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getExportRouteAsTrack() {
    return exportRouteAsTrack;
  }

  public void setExportRouteAsTrack(Boolean exportRouteAsTrack) {
    this.exportRouteAsTrack = !Boolean.FALSE.equals(exportRouteAsTrack);
  }

  public Boolean getExportRouteWaypoints() {
    return exportRouteWaypoints;
  }

  public void setExportRouteWaypoints(Boolean exportRouteWaypoints) {
    this.exportRouteWaypoints = !Boolean.FALSE.equals(exportRouteWaypoints);
  }

  public WptSymbol getRouteWaypointSymbol() {
    return routeWaypointSymbol;
  }

  public void setRouteWaypointSymbol(WptSymbol routeWaypointSymbol) {
    this.routeWaypointSymbol = routeWaypointSymbol != null
        ? routeWaypointSymbol
        : WptSymbol.FLAG_BLUE;
  }

  public Integer getPercentWaypoints() {
    return percentWaypoints;
  }

  public void setPercentWaypoints(Integer percentWaypoints) {
    if (percentWaypoints == null || percentWaypoints > 100) {
      this.percentWaypoints = 100;
    } else if (percentWaypoints < 0) {
      this.percentWaypoints = 0;
    } else {
      this.percentWaypoints = percentWaypoints;
    }
  }

  public TripTransportationMode getTransportationMode() {
    return transportationMode;
  }

  public void setTransportationMode(TripTransportationMode transportationMode) {
    this.transportationMode = transportationMode != null
        ? transportationMode
        : TripTransportationMode.AUTOMOTIVE;
  }

  public ViaPointCalculationMode getCalculationMode() {
    return calculationMode;
  }

  public void setCalculationMode(ViaPointCalculationMode calculationMode) {
    this.calculationMode = calculationMode != null
        ? calculationMode
        : ViaPointCalculationMode.FASTER_TIME;
  }

  public ViaPointElevationMode getElevationMode() {
    return elevationMode;
  }

  public void setElevationMode(ViaPointElevationMode elevationMode) {
    this.elevationMode = elevationMode != null ? elevationMode : ViaPointElevationMode.STANDARD;
  }
}
