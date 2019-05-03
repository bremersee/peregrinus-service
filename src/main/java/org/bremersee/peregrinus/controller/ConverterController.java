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

package org.bremersee.peregrinus.controller;

import org.bremersee.garmin.model.WptSymbol;
import org.bremersee.garmin.trip.v1.model.ext.TripTransportationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointCalculationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointElevationMode;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.bremersee.peregrinus.model.gpx.GpxImportSettings;
import org.bremersee.peregrinus.service.ConverterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@RestController
@RequestMapping(path = "/api/public/convert")
public class ConverterController {

  private ConverterService converterService;

  public ConverterController(ConverterService converterService) {
    this.converterService = converterService;
  }

  @PostMapping(path = "/geojson-to-gpx",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public Mono<Gpx> convertToGpx(
      @RequestParam(name = "author", required = false) String author,
      @RequestParam(name = "calculationMode", defaultValue = "FasterTime") String calculationMode,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "elevationMode", defaultValue = "Standard") String elevationMode,
      @RequestParam(name = "track", defaultValue = "true") Boolean track,
      @RequestParam(name = "waypoints", defaultValue = "true") Boolean waypoints,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "percentWaypoints", required = false) Integer percentWaypoints,
      @RequestParam(name = "waypointSymbol", defaultValue = "Flag, Blue") String waypointSymbol,
      @RequestParam(name = "transportationMode", defaultValue = "Automotive")
          String transportationMode,
      @RequestBody FeatureCollection featureCollection) {

    final GpxExportSettings settings = GpxExportSettings
        .builder()
        .author(author)
        .calculationMode(ViaPointCalculationMode.fromValue(calculationMode))
        .description(description)
        .elevationMode(ViaPointElevationMode.fromValue(elevationMode))
        .exportRouteAsTrack(track)
        .exportRouteWaypoints(waypoints)
        .name(name)
        .percentWaypoints(percentWaypoints)
        .routeWaypointSymbol(WptSymbol.fromValue(waypointSymbol))
        .transportationMode(TripTransportationMode.fromValue(transportationMode))
        .build();
    return Mono.just(converterService.convertFeaturesToGpx(
        featureCollection.getFeatures(), settings));
  }

  @PostMapping(path = "/gpx-to-geojson",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Mono<FeatureCollection> convert(
      @RequestParam(name = "waypoints", defaultValue = "true") Boolean waypoints,
      @RequestBody Gpx gpx) {

    final GpxImportSettings settings = new GpxImportSettings();
    settings.setImportRouteWaypoints(waypoints);
    return Mono.just(converterService.convertGpxToFeatures(gpx, settings));
  }

}
