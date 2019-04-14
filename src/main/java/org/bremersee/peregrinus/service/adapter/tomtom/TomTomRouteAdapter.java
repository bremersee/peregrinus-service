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

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.config.TomTomProperties;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.Pt;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteAddRtePtRequest;
import org.bremersee.peregrinus.model.RteCalculationProperties;
import org.bremersee.peregrinus.model.RteCalculationRequest;
import org.bremersee.peregrinus.model.RteChangeRtePtCalculationPropertiesRequest;
import org.bremersee.peregrinus.model.RteChangeRtePtIndexRequest;
import org.bremersee.peregrinus.model.RteChangeRtePtLocationRequest;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.RteRemoveRtePtRequest;
import org.bremersee.peregrinus.model.RteSettings;
import org.bremersee.peregrinus.model.tomtom.Avoid;
import org.bremersee.peregrinus.model.tomtom.RouteType;
import org.bremersee.peregrinus.model.tomtom.TomTomRteCalculationRequest;
import org.bremersee.peregrinus.service.adapter.RouteAdapter;
import org.bremersee.peregrinus.service.adapter.tomtom.exception.RoutingExceptionMessageParser;
import org.bremersee.peregrinus.service.adapter.tomtom.model.Route;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteLeg;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteResponse;
import org.bremersee.web.ErrorDetectors;
import org.bremersee.web.reactive.function.client.MessageAwareWebClientErrorDecoder;
import org.bremersee.web.reactive.function.client.WebClientErrorDecoder;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * @author Christian Bremer
 */
public class TomTomRouteAdapter implements RouteAdapter {

  private static final DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendInstant(0)
      .toFormatter();

  private TomTomProperties properties;

  private WebClient.Builder webClientBuilder;

  private WebClientErrorDecoder<? extends Throwable> webClientErrorDecoder;

  public TomTomRouteAdapter(TomTomProperties properties,
      Builder webClientBuilder) {
    this.properties = properties;
    this.webClientBuilder = webClientBuilder;
    this.webClientErrorDecoder = new MessageAwareWebClientErrorDecoder(
        new RoutingExceptionMessageParser());
  }

  @Override
  public @NotNull Class<? extends GeocodeQueryRequest>[] getSupportedRequestClasses() {
    //noinspection unchecked
    return new Class[]{
        TomTomRteCalculationRequest.class
    };
  }

  @Override
  public Mono<Rte> calculateRoute(
      final RteCalculationRequest request,
      final String userId,
      final Set<String> roles) {

    final TomTomRteCalculationRequest tomTomRequest = (TomTomRteCalculationRequest) request;
    final String baseUri = properties.getRoutingUri() + buildPath(tomTomRequest.getRtePts());
    final MultiValueMap<String, String> params = buildParameters(tomTomRequest);
    params.set("key", properties.getKey());

    return webClientBuilder
        .baseUrl(baseUri)
        .build()
        .get()
        .uri(uriBuilder -> uriBuilder.queryParams(params).build())
        .header("User-Agent", properties.getUserAgent())
        .retrieve()
        .onStatus(ErrorDetectors.DEFAULT, webClientErrorDecoder)
        .bodyToMono(RouteResponse.class)
        .filter(Objects::nonNull)
        .flatMap(routeResponse -> mapFirst(request, routeResponse));
  }

  private String buildPath(Collection<? extends Pt> locations) {
    final String locationsStr = locations
        .stream()
        .filter(Objects::nonNull)
        .filter(pt -> pt.getGeometry() != null)
        .map(Pt::getGeometry)
        .map(point -> BigDecimal.valueOf(point.getY()).toPlainString()
            + "," + BigDecimal.valueOf(point.getX()).toPlainString())
        .collect(Collectors.joining(":"));
    return "/" + locationsStr + "/json";
  }

  private HttpMethod getHttpMethod(TomTomRteCalculationRequest request) {
    if (request.getAvoidAreas() != null
        || (request.getAvoidVignette() != null && !request.getAvoidVignette().isEmpty())) {
      return HttpMethod.POST;
    }
    return HttpMethod.GET;
  }

  private MultiValueMap<String, String> buildParameters(TomTomRteCalculationRequest request) {
    final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

    if (request.getLanguage() != null) {
      map.set("language", request.getLanguage().toString());
    }
    if (request.getTime() != null) {
      if (request.getTimeIsDepartureTime() == null || request.getTimeIsDepartureTime()) {
        map.set("departAt", request.getTime().format(dtf));
      } else {
        map.set("arriveAt", request.getTime().format(dtf));
      }
    }

    map.set("routeRepresentation", "polyline");
    map.set("instructionsType", "text"); // use positions for garmin route export

    if (request.getTravelMode() != null) {
      map.set("travelMode", request.getTravelMode().getValue()); // car, bicycle
    }

    if (request.getRouteType() != null) {
      map.set("routeType", request.getRouteType().getValue()); // fastest, shortest, eco, thrilling
      if (RouteType.THRILLING.equals(request.getRouteType())) {
        if (request.getWindingness() != null) {
          map.set("windingness", request.getWindingness().getValue());
        }
        if (request.getHilliness() != null) {
          map.set("hilliness", request.getHilliness().getValue());
        }
      }
    }

    if (request.getAvoid() != null && !request.getAvoid().isEmpty()) {
      map.set("avoid", request.getAvoid()
          .stream()
          .map(Avoid::getValue)
          .collect(Collectors.joining(","))); // tollRoads, motorways, ferries, etc
    }
    // avoidVignette -> post
    // avoidAreas -> post

    return map;
  }

  private Flux<Rte> mapMany(
      final RteCalculationRequest request,
      final RouteResponse response) {
    if (response.getRoutes() == null || response.getRoutes().isEmpty()) {

      return Flux.empty();
    }
    return Flux.fromStream(response
        .getRoutes()
        .stream()
        .filter(Objects::nonNull)
        .map(route -> map(request, route)));
  }

  private Mono<Rte> mapFirst(
      final RteCalculationRequest request,
      final RouteResponse response) {
    if (response.getRoutes() == null || response.getRoutes().isEmpty()) {

      return Mono.empty();
    }
    return Mono.justOrEmpty(response
        .getRoutes()
        .stream()
        .filter(Objects::nonNull)
        .findFirst()
        .map(route -> map(request, route)));
  }

  private Rte map(
      final RteCalculationRequest request,
      final Route route) {

    route.getGuidance();

    RteCalculationProperties calcProps = new RteCalculationProperties();
    calcProps.setLanguage(request.getLanguage());
    calcProps.setTime(request.getTime());
    calcProps.setTimeIsDepartureTime(request.getTimeIsDepartureTime());

    Rte.builder()
        .geometry(null)
        .properties(RteProperties.builder()
            .arrivalTime(route.getSummary().getArrivalTime())
            .calculationProperties(calcProps)
            .createdBy(null) // TODO
            .departureTime(route.getSummary().getDepartureTime())
            .modifiedBy(null) // TODO
            .name(null) // TODO
            .rtePts(null) // TODO
            .settings(RteSettings.builder().build())
            .build());

    return null;
  }

  private Tuple2<List<RtePt>, MultiLineString> getRtePts(
      final RteCalculationRequest request,
      final Route route) {

    final List<RtePt> rtePts = new ArrayList<>();
    final List<LineString> lineStrings = new ArrayList<>();
    for (RouteLeg routeLeg : route.getLegs()) {
      final Tuple2<RtePt, LineString> tuple = getRtePt(request, routeLeg);
      rtePts.add(tuple.getT1());
      lineStrings.add(tuple.getT2());
    }
    final MultiLineString multiLineString = GeometryUtils.createMultiLineString(lineStrings);

    // TODO add last RtePt

    return Tuples.of(rtePts, multiLineString);
  }

  private Tuple2<RtePt, LineString> getRtePt(
      final RteCalculationRequest request,
      final RouteLeg route) {

    final RtePt rtePt = RtePt.builder()
        .build();

    final LineString lineString = GeometryUtils.createLineString(
        route.getPoints()
            .stream()
            .map(latLon -> GeometryUtils
                .createCoordinateWGS84(latLon.getLatitude(), latLon.getLongitude()))
            .collect(Collectors.toList()));

    return Tuples.of(rtePt, lineString);
  }


}
