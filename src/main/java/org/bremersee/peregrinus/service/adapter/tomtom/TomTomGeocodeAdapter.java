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

package org.bremersee.peregrinus.service.adapter.tomtom;

import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.LocaleHelper;
import org.bremersee.common.model.ThreeLetterCountryCode;
import org.bremersee.common.model.TwoLetterCountryCode;
import org.bremersee.peregrinus.config.TomTomProperties;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.TomTomGeocodeQueryRequest;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.WptProperties;
import org.bremersee.peregrinus.service.adapter.GeocodeAdapter;
import org.bremersee.peregrinus.service.adapter.tomtom.model.GeocodeResponse;
import org.bremersee.peregrinus.service.adapter.tomtom.model.GeocodeResult;
import org.bremersee.web.ErrorDetectors;
import org.bremersee.web.reactive.function.client.WebClientErrorDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.core.publisher.Flux;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class TomTomGeocodeAdapter implements GeocodeAdapter {

  private TomTomProperties properties;

  private WebClient.Builder webClientBuilder;

  private WebClientErrorDecoder<? extends Throwable> webClientErrorDecoder;

  public TomTomGeocodeAdapter(
      TomTomProperties properties,
      Builder webClientBuilder,
      WebClientErrorDecoder<? extends Throwable> webClientErrorDecoder) {
    this.properties = properties;
    this.webClientBuilder = webClientBuilder;
    this.webClientErrorDecoder = webClientErrorDecoder;
  }

  @Override
  public @NotNull Class<? extends GeocodeQueryRequest>[] getSupportedRequestClasses() {
    //noinspection unchecked
    return new Class[]{TomTomGeocodeQueryRequest.class};
  }

  @Override
  public Flux<Wpt> queryGeocode(
      GeocodeQueryRequest request,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    final TomTomGeocodeQueryRequest tomTomRequest = (TomTomGeocodeQueryRequest) request;
    final String baseUri = properties.getGeocodeUri() + tomTomRequest.buildPath();
    final MultiValueMap<String, String> params = tomTomRequest.buildParameters();
    params.set("key", properties.getKey());

    return webClientBuilder
        .baseUrl(baseUri)
        .build()
        .get()
        .uri(uriBuilder -> uriBuilder.queryParams(params).build())
        .header("User-Agent", properties.getUserAgent())
        .retrieve()
        .onStatus(ErrorDetectors.DEFAULT, webClientErrorDecoder)
        .bodyToMono(GeocodeResponse.class)
        .filter(GeocodeResponse::hasResults)
        .flatMapMany(this::map);
  }

  private Flux<Wpt> map(GeocodeResponse geocodeResponse) {
    return Flux.fromStream(
        geocodeResponse.getResults()
            .stream()
            .filter(Objects::nonNull)
            .filter(geocodeResult -> geocodeResult.getPosition() != null)
            .filter(geocodeResult -> geocodeResult.getPosition().hasValues())
            .map(this::map));
  }

  private Wpt map(GeocodeResult geocodeResult) {
    return Wpt.builder()
        .geometry(geocodeResult.getPosition().toPoint())
        .properties(WptProperties.builder()
            .address(map(geocodeResult.getAddress()))
            .name(findName(geocodeResult))
            .build())
        .build();
  }

  private String findName(GeocodeResult geocodeResult) {
    if (geocodeResult.getAddress() != null) {
      if (geocodeResult.getAddress().getFreeformAddress() != null) {
        return geocodeResult.getAddress().getFreeformAddress();
      }
    }
    return geocodeResult.getPosition().toLatLonString();
  }

  private Address map(org.bremersee.peregrinus.service.adapter.tomtom.model.Address source) {
    if (source == null) {
      return null;
    }
    return new Address()
        .city(findCity(source))
        .country(source.getCountry())
        .countryCode(findCountryCode(source))
        .formattedAddress(source.getFreeformAddress())
        .postalCode(source.getPostalCode())
        .state(source.getCountrySubdivision())
        .street(source.getStreetName())
        .streetNumber(source.getStreetNumber())
        .suburb(source.getMunicipalitySubdivision());
  }

  private String findCity(org.bremersee.peregrinus.service.adapter.tomtom.model.Address source) {
    String city = source.getMunicipality();
    if (city == null) {
      return source.getCountrySecondarySubdivision();
    }
    return city;
  }

  private TwoLetterCountryCode findCountryCode(
      org.bremersee.peregrinus.service.adapter.tomtom.model.Address source) {
    TwoLetterCountryCode countryCode = TwoLetterCountryCode.fromValue(source.getCountryCode());
    if (countryCode == null) {
      ThreeLetterCountryCode tmp = ThreeLetterCountryCode.fromValue(source.getCountryCodeISO3());
      if (tmp != null) {
        return LocaleHelper.toTwoLetterCountryCode(LocaleHelper.toLocale(tmp));
      }
    }
    return countryCode;
  }
}
