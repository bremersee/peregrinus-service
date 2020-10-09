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
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.geojson.model.LatitudeLongitude;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.config.TomTomProperties;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteCalcRequest;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.RteSeg;
import org.bremersee.peregrinus.model.RteSettings;
import org.bremersee.peregrinus.model.tomtom.Avoid;
import org.bremersee.peregrinus.model.tomtom.RouteType;
import org.bremersee.peregrinus.model.tomtom.TomTomRteCalcRequest;
import org.bremersee.peregrinus.service.adapter.RouteAdapter;
import org.bremersee.peregrinus.service.adapter.tomtom.exception.RoutingExceptionMessageParser;
import org.bremersee.peregrinus.service.adapter.tomtom.model.AvoidAreas;
import org.bremersee.peregrinus.service.adapter.tomtom.model.Language;
import org.bremersee.peregrinus.service.adapter.tomtom.model.Rectangle;
import org.bremersee.peregrinus.service.adapter.tomtom.model.Route;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteInstruction;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteInstruction.InstructionType;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteLeg;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteRequestBody;
import org.bremersee.peregrinus.service.adapter.tomtom.model.RouteResponse;
import org.bremersee.web.ErrorDetectors;
import org.bremersee.web.reactive.function.client.MessageAwareWebClientErrorDecoder;
import org.bremersee.web.reactive.function.client.WebClientErrorDecoder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class TomTomRouteAdapter implements RouteAdapter {

  private static final DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendInstant(0)
      .toFormatter();

  private TomTomProperties properties;

  private WebClient.Builder webClientBuilder;

  private WebClientErrorDecoder<? extends Throwable> webClientErrorDecoder;

  public TomTomRouteAdapter(
      TomTomProperties properties,
      WebClient.Builder webClientBuilder) {

    this.properties = properties;
    this.webClientBuilder = webClientBuilder.clone();
    this.webClientErrorDecoder = new MessageAwareWebClientErrorDecoder(
        new RoutingExceptionMessageParser());
  }

  @Override
  public @NotNull Class<? extends GeocodeQueryRequest>[] getSupportedRequestClasses() {
    //noinspection unchecked
    return new Class[]{
        TomTomRteCalcRequest.class
    };
  }

  @Override
  public Mono<Rte> calculateRoute(
      final RteCalcRequest request,
      final String userId,
      final Set<String> roles) {

    final TomTomRteCalcRequest tomTomRequest = (TomTomRteCalcRequest) request;
    final String baseUri = properties.getRoutingUri() + buildPath(tomTomRequest.getRtePts());
    final MultiValueMap<String, String> params = buildParameters(tomTomRequest);
    params.set("key", properties.getKey());

    final WebClient webClient = webClientBuilder
        .baseUrl(baseUri)
        .build();
    final WebClient.RequestHeadersSpec spec;
    if (HttpMethod.POST.equals(getHttpMethod(tomTomRequest))) {
      spec = webClient
          .post()
          .uri(uriBuilder -> uriBuilder.queryParams(params).build())
          .body(BodyInserters.fromObject(buildRequestBody(tomTomRequest)));

    } else {
      spec = webClient
          .get()
          .uri(uriBuilder -> uriBuilder.queryParams(params).build());
    }
    return spec
        .header("User-Agent", properties.getUserAgent())
        .retrieve()
        .onStatus(ErrorDetectors.DEFAULT, webClientErrorDecoder)
        .bodyToMono(RouteResponse.class)
        .filter(Objects::nonNull)
        .flatMap(routeResponse -> mapFirst(request, routeResponse, userId));
  }

  private String buildPath(final Collection<? extends Point> locations) {
    final String locationsStr = locations
        .stream()
        .filter(Objects::nonNull)
        .map(point -> BigDecimal.valueOf(point.getY()).toPlainString()
            + "," + BigDecimal.valueOf(point.getX()).toPlainString())
        .collect(Collectors.joining(":"));
    return "/" + locationsStr + "/json";
  }

  private HttpMethod getHttpMethod(final TomTomRteCalcRequest request) {
    if (request.getAvoidAreas() != null
        || (request.getAvoidVignette() != null && !request.getAvoidVignette().isEmpty())) {
      return HttpMethod.POST;
    }
    return HttpMethod.GET;
  }

  private MultiValueMap<String, String> buildParameters(final TomTomRteCalcRequest request) {
    final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.set("routeRepresentation", "polyline");
    map.set("instructionsType", "text");
    if (request.getLanguage() != null) {
      final Language language = Language.fromValue(request.getLanguage()); // TODO
      map.set("language", language.toString());
    }
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
    return map;
  }

  private RouteRequestBody buildRequestBody(TomTomRteCalcRequest request) {
    final RouteRequestBody body = new RouteRequestBody();
    body.setAvoidVignette(request.getAvoidVignette());
    if (request.getAvoidAreas() != null) {
      final AvoidAreas avoidAreas = new AvoidAreas();
      for (int n = 0; n < request.getAvoidAreas().getNumGeometries(); n++) {
        final double[] bbox = GeometryUtils.getBoundingBox(request.getAvoidAreas().getGeometryN(n));
        final Coordinate sw = GeometryUtils.getSouthWest(bbox);
        final Coordinate ne = GeometryUtils.getNorthEast(bbox);
        avoidAreas
            .getRectangles()
            .add(new Rectangle(new LatitudeLongitude(sw), new LatitudeLongitude(ne)));
      }
      body.setAvoidAreas(avoidAreas);
    }
    return body;
  }

  private Flux<Rte> mapMany(
      final RteCalcRequest request,
      final RouteResponse response,
      final String userId) {
    if (response.getRoutes() == null || response.getRoutes().isEmpty()) {

      return Flux.empty();
    }
    return Flux.fromStream(response
        .getRoutes()
        .stream()
        .filter(Objects::nonNull)
        .map(route -> mapRoute(request, route, userId)));
  }

  private Mono<Rte> mapFirst(
      final RteCalcRequest request,
      final RouteResponse response,
      final String userId) {

    if (response.getRoutes() == null || response.getRoutes().isEmpty()) {
      return Mono.empty();
    }
    return Mono.justOrEmpty(response
        .getRoutes()
        .stream()
        .filter(this::isValidRoute)
        .findFirst()
        .map(route -> mapRoute(request, route, userId)));
  }

  private boolean isValidRoute(final Route route) {
    return route != null
        && areValidLegs(route.getLegs())
        && route.getGuidance() != null
        && areValidInstructions(route.getGuidance().getInstructions());
  }

  private boolean areValidLegs(final List<RouteLeg> legs) {
    return legs != null && !legs.isEmpty();
  }

  private boolean areValidInstructions(final List<RouteInstruction> instructions) {
    return instructions != null
        && instructions.get(0).getInstructionType() == InstructionType.LOCATION_DEPARTURE
        && instructions.get(instructions.size() - 1).getInstructionType()
        == InstructionType.LOCATION_ARRIVAL;
  }

  private Rte mapRoute(
      final RteCalcRequest request,
      final Route route,
      final String userId) {

    final List<LineString> lineStrings = route
        .getLegs()
        .stream()
        .map(RouteLeg::getPoints)
        .map(latLngList -> latLngList
            .stream()
            .map(LatitudeLongitude::toCoordinate)
            .collect(Collectors.toList()))
        .map(GeometryUtils::createLineString)
        .collect(Collectors.toList());

    final List<RteSeg> rteSegments = new ArrayList<>();
    String departureName = null;
    String arrivalName = null;
    BigInteger routeOffsetInMeters = BigInteger.valueOf(0);
    BigInteger travelTimeInSeconds = BigInteger.valueOf(0);
    RteSeg rteSeg = new RteSeg();
    rteSeg.setCalculationSettings(request.buildRteSegCalcSettings());
    for (final RouteInstruction routeInstruction : route.getGuidance().getInstructions()) {
      final RtePt rtePt = RtePt
          .builder()
          .position(routeInstruction.getPoint().toPoint())
          .name(routeInstruction.findName())
          .build();
      rteSeg.getRtePts().add(rtePt);
      arrivalName = rtePt.getName();
      if (departureName == null) {
        departureName = rtePt.getName();
      }

      final InstructionType instructionType = routeInstruction.getInstructionType();
      if (InstructionType.LOCATION_WAYPOINT == instructionType
          || InstructionType.LOCATION_ARRIVAL == instructionType) {

        if (routeInstruction.getRouteOffsetInMeters() != null) {
          rteSeg.setLengthInMeters(
              routeInstruction.getRouteOffsetInMeters().subtract(routeOffsetInMeters));
        }
        if (routeInstruction.getTravelTimeInSeconds() != null) {
          rteSeg.setTravelTimeInSeconds(
              routeInstruction.getTravelTimeInSeconds().subtract(travelTimeInSeconds));
        }
        rteSegments.add(rteSeg);

        routeOffsetInMeters = routeInstruction.getRouteOffsetInMeters();
        travelTimeInSeconds = routeInstruction.getTravelTimeInSeconds();
        rteSeg = new RteSeg();
        rteSeg.setCalculationSettings(request.buildRteSegCalcSettings());
      }
    }

    return Rte
        .builder()
        .geometry(GeometryUtils.createMultiLineString(lineStrings))
        .properties(RteProperties
            .builder()
            //.arrivalTime(route.getSummary() != null ? route.getSummary().getArrivalTime() : null)
            .createdBy(userId)
            //.departureTime(
            //    route.getSummary() != null ? route.getSummary().getDepartureTime() : null)
            .modifiedBy(userId)
            .name(departureName + " -> " + arrivalName)
            .settings(RteSettings.builder().build())
            .rteSegments(rteSegments)
            .build())
        .build();
  }

}
