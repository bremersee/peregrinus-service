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

import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.garmin.ImportSettings;
import org.bremersee.peregrinus.service.adapter.garmin.FeaturesToGpxConverter;
import org.bremersee.peregrinus.service.adapter.garmin.GpxToFeaturesConverter;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.garmin.ExportSettings;
import org.bremersee.xml.JaxbContextBuilder;
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
@RequestMapping(path = "/api/public/garmin")
public class GarminController {

  private final FeaturesToGpxConverter toGpxConverter;

  private final GpxToFeaturesConverter toFeaturesConverter;

  public GarminController(final JaxbContextBuilder jaxbContextBuilder) {
    this.toGpxConverter = new FeaturesToGpxConverter(jaxbContextBuilder);
    this.toFeaturesConverter = new GpxToFeaturesConverter(jaxbContextBuilder);
  }

  @PostMapping(path = "/geojson-to-gpx",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public Mono<Gpx> convertToGpx(
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "percentWaypoints", required = false) Integer percentWaypoints,
      @RequestParam(name = "transportationMode", required = false) String transportationMode,
      @RequestParam(name = "calculationMode", required = false) String calculationMode,
      @RequestParam(name = "elevationMode", required = false) String elevationMode,
      @RequestBody FeatureCollection featureCollection) {

    return Mono.just(toGpxConverter.convert(
        featureCollection.getFeatures(), new ExportSettings()));
  }

  @PostMapping(path = "/gpx-to-geojson",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Mono<FeatureCollection> convert(
      @RequestParam(name = "removeRteWpts", defaultValue = "true") Boolean removeRteWpts,
      @RequestBody Gpx gpx) {

    return Mono.just(toFeaturesConverter.convert(gpx, new ImportSettings()));
  }
}
