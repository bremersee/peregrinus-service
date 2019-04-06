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

package org.bremersee.peregrinus.service.adapter.nominatim;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.TwoLetterCountryCode;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.config.NominatimProperties;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.NominatimGeocodeQueryRequest;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.WptProperties;
import org.bremersee.peregrinus.service.adapter.GeocodeAdapter;
import org.bremersee.peregrinus.service.adapter.nominatim.model.GeocodeResult;
import org.bremersee.web.ErrorDetectors;
import org.bremersee.web.reactive.function.client.WebClientErrorDecoder;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;

/**
 * @author Christian Bremer
 */
@Component
public class NominatimGeocodeAdapter implements GeocodeAdapter {

  private NominatimProperties properties;

  private WebClient.Builder webClientBuilder;

  private WebClientErrorDecoder<? extends Throwable> webClientErrorDecoder;

  public NominatimGeocodeAdapter(
      NominatimProperties properties,
      Builder webClientBuilder,
      WebClientErrorDecoder<? extends Throwable> webClientErrorDecoder) {
    this.properties = properties;
    this.webClientBuilder = webClientBuilder;
    this.webClientErrorDecoder = webClientErrorDecoder;
  }

  @Override
  public @NotNull Class<? extends GeocodeQueryRequest>[] getSupportedRequestClasses() {
    //noinspection unchecked
    return new Class[]{NominatimGeocodeQueryRequest.class};
  }

  @Override
  public Flux<Wpt> queryGeocode(
      GeocodeQueryRequest request,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    final NominatimGeocodeQueryRequest nominatimRequest = (NominatimGeocodeQueryRequest) request;
    final MultiValueMap<String, String> params = buildParameters(nominatimRequest);
    return webClientBuilder
        .uriBuilderFactory(null)
        .baseUrl(properties.getSearchUri())
        .build()
        .get()
        .uri(uriBuilder -> uriBuilder.queryParams(params).build())
        .header(HttpHeaders.USER_AGENT, properties.getUserAgent())
        .retrieve()
        .onStatus(ErrorDetectors.DEFAULT, webClientErrorDecoder)
        .bodyToFlux(GeocodeResult.class)
        .filter(GeocodeResult::hasLatLon)
        .map(this::map);
  }

  /**
   * Build parameters multi value map.
   *
   * @return the multi value map
   */
  private MultiValueMap<String, String> buildParameters(NominatimGeocodeQueryRequest request) {

    final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.set("format", "jsonv2");
    if (request.getLanguage() != null) {
      map.set("accept-language", request.getLanguage().toString());
    } else {
      map.set("accept-language", "en");
    }
    if (request.getBoundingBox() != null && request.getBoundingBox().length == 4) {
      map.set(
          "viewbox",
          BigDecimal.valueOf(request.getBoundingBox()[0]).toPlainString()
              + "," + BigDecimal.valueOf(request.getBoundingBox()[1]).toPlainString()
              + "," + BigDecimal.valueOf(request.getBoundingBox()[2]).toPlainString()
              + "," + BigDecimal.valueOf(request.getBoundingBox()[3]).toPlainString());
    }
    if (request.getCountryCodes() != null && !request.getCountryCodes().isEmpty()) {
      map.set("countrycodes", StringUtils.collectionToCommaDelimitedString(
          request.getCountryCodes()
              .stream()
              .filter(Objects::nonNull)
              .map(TwoLetterCountryCode::toString)
              .collect(Collectors.toSet())));
    }
    map.set("limit", String.valueOf(request.getLimit()));

    map.set("bounded", Boolean.TRUE.equals(request.getBounded()) ? "1" : "0");
    if (request.getExcludePlaceIds() != null && !request.getExcludePlaceIds().isEmpty()) {
      map.set(
          "exclude_place_ids",
          StringUtils.collectionToCommaDelimitedString(request.getExcludePlaceIds()));
    }
    map.set("dedupe", Boolean.FALSE.equals(request.getDedupe()) ? "0" : "1");
    map.set("debug", Boolean.TRUE.equals(request.getDebug()) ? "1" : "0");

    map.set("addressdetails", Boolean.FALSE.equals(request.getAddressDetails()) ? "0" : "1");
    if (StringUtils.hasText(request.getEmail())) {
      map.set(
          "email",
          UriUtils.encodeQueryParam(request.getEmail(), StandardCharsets.UTF_8));
    }
    if (request.getPolygon() == null || Boolean.TRUE.equals(request.getPolygon())) {
      map.set("polygon_geojson", "1");
    }
    map.set("extratags", Boolean.FALSE.equals(request.getExtraTags()) ? "0" : "1");
    map.set("namedetails", Boolean.FALSE.equals(request.getNameDetails()) ? "0" : "1");

    map.set(
        "q",
        UriUtils.encodeQueryParam(request.getQuery(), StandardCharsets.UTF_8));
    return map;
  }

  private Wpt map(GeocodeResult result) {
    return Wpt.builder()
        .geometry(GeometryUtils.createPoint(result.lonToDouble(), result.latToDouble()))
        .properties(WptProperties.builder()
            .address(map(result.getAddress()))
            .area(result.getGeoJson() instanceof Polygon ? (Polygon) result.getGeoJson() : null)
            .name(result.getDisplayName())
            .osmCategory(result.getCategory())
            .osmId(result.getOsmId())
            .osmPlaceId(result.getPlaceId())
            .osmType(result.getOsmType())
            .build())
        .build();
  }

  private Address map(org.bremersee.peregrinus.service.adapter.nominatim.model.Address source) {
    if (source == null) {
      return null;
    }
    final String countryCode = source.getCountryCode() != null
        ? source.getCountryCode().toUpperCase()
        : null;
    return new Address()
        .city(source.getCity())
        .country(source.getCountry())
        .countryCode(TwoLetterCountryCode.fromValue(countryCode))
        .postalCode(source.getPostcode())
        .state(source.getState())
        .suburb(source.getSuburb());
  }
}
