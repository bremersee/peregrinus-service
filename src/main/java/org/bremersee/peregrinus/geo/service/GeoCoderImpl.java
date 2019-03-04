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

package org.bremersee.peregrinus.geo.service;

import org.bremersee.nominatim.client.ReactiveNominatimClient;
import org.bremersee.peregrinus.geo.mapper.google.GoogleMapper;
import org.bremersee.peregrinus.geo.mapper.google.GoogleMapperImpl;
import org.bremersee.peregrinus.geo.mapper.nominatim.NominatimMapper;
import org.bremersee.peregrinus.geo.mapper.nominatim.NominatimMapperImpl;
import org.bremersee.peregrinus.geo.mapper.tomtom.TomTomMapper;
import org.bremersee.peregrinus.geo.mapper.tomtom.TomTomMapperImpl;
import org.bremersee.peregrinus.geo.model.GeoCodingQueryRequest;
import org.bremersee.peregrinus.geo.model.GeoCodingResult;
import org.bremersee.peregrinus.geo.model.GeoProvider;
import org.bremersee.tomtom.client.ReactiveGeocodingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author Christian Bremer
 */
@Component
public class GeoCoderImpl implements GeoCoder {

  private NominatimMapper nominatimMapper = new NominatimMapperImpl();

  private TomTomMapper tomTomMapper = new TomTomMapperImpl();

  private GoogleMapper googleMapper = new GoogleMapperImpl();

  private ReactiveNominatimClient nominatimService;

  private ReactiveGeocodingClient tomTomGeocodingService;

  private org.bremersee.google.maps.client.ReactiveGeocodingClient googleGeocodingService;

  @Autowired
  public GeoCoderImpl(ReactiveNominatimClient nominatimService,
      ReactiveGeocodingClient tomTomGeocodingService,
      org.bremersee.google.maps.client.ReactiveGeocodingClient googleGeocodingService) {
    this.nominatimService = nominatimService;
    this.tomTomGeocodingService = tomTomGeocodingService;
    this.googleGeocodingService = googleGeocodingService;
  }

  @Override
  public Flux<GeoCodingResult> geocode(final GeoCodingQueryRequest request) {

    final GeoProvider geoProvider = request.getGeoProvider() != null
        ? request.getGeoProvider()
        : GeoProvider.NOMINATIM;

    switch (geoProvider) {

      case GOOGLE:
        return googleGeocodingService.geocode(googleMapper.mapToGeocodeRequest(request))
            .map(googleMapper::mapToGeoCodingResult)
            .filter(geoCodingResult -> geoCodingResult.getPosition() != null);

      case TOMTOM:
        return tomTomGeocodingService.geocode(tomTomMapper.mapToGeocodeRequest(request))
            .flatMapIterable(tomTomMapper::mapToGeoCodingResults)
            .filter(geoCodingResult -> geoCodingResult.getPosition() != null);

      default:
        return nominatimService
            .geocode(nominatimMapper.mapToSearchRequest(request))
            .map(nominatimMapper::mapToGeoCodingResult)
            .filter(geoCodingResult -> geoCodingResult.getPosition() != null);
    }
  }
}
