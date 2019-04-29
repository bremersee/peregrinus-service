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

import java.util.LinkedHashSet;
import java.util.Set;
import org.bremersee.garmin.model.WptSymbol;
import org.bremersee.garmin.trip.v1.model.ext.TripTransportationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointCalculationMode;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointElevationMode;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.groupman.api.GroupControllerApi;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.StringList;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.bremersee.peregrinus.service.FeatureService;
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
@RequestMapping(path = "/api")
public class FeatureController extends AbstractController {

  private FeatureService featureService;

  public FeatureController(
      GroupControllerApi groupService,
      FeatureService featureService) {
    super(groupService);
    this.featureService = featureService;
  }

  @PostMapping(path = "/protected/features/export/gpx",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public Mono<Gpx> exportGpx(
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
      @RequestBody StringList featureIds) {

    final Set<String> ids = new LinkedHashSet<>(featureIds);
    final GpxExportSettings settings = GpxExportSettings
        .builder()
        .author(author)
        .calculationMode(ViaPointCalculationMode.valueOf(calculationMode))
        .description(description)
        .elevationMode(ViaPointElevationMode.valueOf(elevationMode))
        .exportRouteAsTrack(track)
        .exportRouteWaypoints(waypoints)
        .name(name)
        .percentWaypoints(percentWaypoints)
        .routeWaypointSymbol(WptSymbol.valueOf(waypointSymbol))
        .transportationMode(TripTransportationMode.valueOf(transportationMode))
        .build();
    return oneWithAuth(auth -> featureService
        .exportGpx(ids, settings, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

}
