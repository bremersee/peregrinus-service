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

package org.bremersee.peregrinus.geo.mapper.tomtom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bremersee.common.model.Address;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.geo.model.GeoCodingQueryRequest;
import org.bremersee.peregrinus.geo.model.GeoCodingResult;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RtePtProperties;
import org.bremersee.peregrinus.geo.model.TomTomRteCalculationProperties;
import org.bremersee.tomtom.model.BoundingBox;
import org.bremersee.tomtom.model.GeocodeRequest;
import org.bremersee.tomtom.model.GeocodeResponse;
import org.bremersee.tomtom.model.GeocodeResult;
import org.bremersee.tomtom.model.Language;
import org.bremersee.tomtom.model.LatLon;
import org.bremersee.tomtom.model.LatLonAware;
import org.bremersee.tomtom.model.LatitudeLongitude;
import org.bremersee.tomtom.model.Route;
import org.bremersee.tomtom.model.RouteLeg;
import org.bremersee.tomtom.model.RouteSummary;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * @author Christian Bremer
 */
public class TomTomMapperImpl implements TomTomMapper {

  @Override
  public GeocodeRequest mapToGeocodeRequest(final GeoCodingQueryRequest source) {
    return GeocodeRequest
        .builder()
        .boundingBox(mapToBoundingBox(source))
        .countrySet(source.getCountries())
        .language(Language.fromLocale(source.getLanguage()))
        .limit(source.getLimit())
        .query(source.getQuery())
        .build();
  }

  @Override
  public Iterable<GeoCodingResult> mapToGeoCodingResults(final GeocodeResponse source) {
    if (!source.hasResults()) {
      return Collections.emptyList();
    }
    return source.getResults()
        .stream()
        .map(this::mapToGeoCodingResult)
        .collect(Collectors.toList());
  }

  private GeoCodingResult mapToGeoCodingResult(final GeocodeResult source) {
    final GeoCodingResult destination = new GeoCodingResult();
    destination.setAddress(mapToAddress(source));
    destination.setBoundingBox(mapToBoundingBox(source));
    destination.setPosition(mapToPosition(source));
    return destination;
  }

  private Point mapToPosition(final GeocodeResult source) {
    if (source.getPosition() == null || !source.getPosition().hasValues()) {
      return null;
    }
    return GeometryUtils.createPointWGS84(
        source.getPosition().getLatitude(),
        source.getPosition().getLongitude());
  }

  private Polygon mapToBoundingBox(final GeocodeResult source) {
    if (!source.getBoundingBox().hasValues()) {
      return null;
    }
    final BoundingBox boundingBox = source.getBoundingBox();
    final LatLon btmRightPoint = boundingBox.getBtmRightPoint();
    final LatLon topLeftPoint = boundingBox.getTopLeftPoint();
    final Coordinate btmRight = GeometryUtils.createCoordinateWGS84(
        btmRightPoint.getLatitude(),
        btmRightPoint.getLongitude());
    final Coordinate topLeft = GeometryUtils.createCoordinateWGS84(
        topLeftPoint.getLatitude(),
        topLeftPoint.getLongitude());
    return GeometryUtils.getBoundingBoxAsPolygon2D(
        GeometryUtils.createLineString(Arrays.asList(btmRight, topLeft)));
  }

  @SuppressWarnings("Duplicates")
  private Address mapToAddress(final GeocodeResult source) {

    if (source.getAddress() == null) {
      return null;
    }

    final Address destination = new Address();
    destination.setStreet(source.getAddress().getStreetName());
    destination.setStreetNumber(source.getAddress().getStreetNumber());
    destination.setPostalCode(source.getAddress().getPostalCode());
    destination.setCity(source.getAddress().getMunicipality());
    destination.setSuburb(source.getAddress().getMunicipalitySubdivision());
    destination.setStreetNumber(source.getAddress().getCountrySubdivision());
    destination.setCountry(source.getAddress().getCountry());
    destination.setCountryCode(source.getAddress().getCountryCode());
    destination.setFormattedAddress(source.getAddress().getFreeformAddress());
    return destination;
  }

  private BoundingBox mapToBoundingBox(final GeoCodingQueryRequest source) {
    final double[] bbox = source.toBoundingBox();
    if (bbox == null) {
      return null;
    }
    final Coordinate btmRight = GeometryUtils.getSouthEast(bbox);
    final LatLon btmRightPoint = new LatLon(
        LatLonAware
            .builder()
            .longitude(btmRight.getX())
            .latitude(btmRight.getY())
            .build());
    final Coordinate topLeft = GeometryUtils.getNorthWest(bbox);
    final LatLon topLeftPoint = new LatLon(
        LatLonAware
            .builder()
            .longitude(topLeft.getX())
            .latitude(topLeft.getY())
            .build());
    final BoundingBox boundingBox = new BoundingBox();
    boundingBox.setBtmRightPoint(btmRightPoint);
    boundingBox.setTopLeftPoint(topLeftPoint);
    return boundingBox;
  }

  @Override
  public Rte mapToRte(
      final Route route,
      final TomTomRteCalculationProperties calculationProperties) { // TODO we need more info

    if (route == null || route.getLegs() == null || route.getLegs().isEmpty()) {
      return null;
    }

    final Rte rte = new Rte();
    final RteProperties rteProperties = new RteProperties();
    rte.setProperties(rteProperties);

    //rteProperties.setDisplayColor(DisplayColor.MAGENTA);
    // TODO name etc

    final List<LineString> lineStrings = new ArrayList<>();
    final List<RtePt> rteSegments = new ArrayList<>();
    for (final RouteLeg routeLeg : route.getLegs()) {
      if (routeLeg == null || routeLeg.getPoints() == null || routeLeg.getPoints().isEmpty()) {
        continue;
      }
      final LatitudeLongitude latLon = routeLeg.getPoints().get(0);
      if (latLon == null || !latLon.hasValues()) {
        continue;
      }
      final List<Coordinate> line = new ArrayList<>();
      final RtePt rteSegment = new RtePt();
      rteSegment.setGeometry(GeometryUtils
          .createPointWGS84(latLon.getLatitude(), latLon.getLongitude()));

      final RtePtProperties rteSegmentProperties = new RtePtProperties();
      rteSegment.setProperties(rteSegmentProperties);

      rteSegmentProperties.setCalculationProperties(calculationProperties);
      //rteSegmentProperties.setName(""); // TODO
      if (routeLeg.getSummary() != null) {
        final RouteSummary summary = routeLeg.getSummary();
        if (summary.getDepartureTime() != null) {
          //rteSegmentProperties.setTime(summary.getDepartureTime().toInstant());
          rteSegmentProperties.setHistoricTrafficTravelTimeInSeconds(
              summary.getHistoricTrafficTravelTimeInSeconds());
          rteSegmentProperties.setLengthInMeters(summary.getLengthInMeters());
          rteSegmentProperties.setLiveTrafficIncidentsTravelTimeInSeconds(
              summary.getLiveTrafficIncidentsTravelTimeInSeconds());
          rteSegmentProperties.setNoTrafficTravelTimeInSeconds(
              summary.getNoTrafficTravelTimeInSeconds());
          rteSegmentProperties.setTrafficDelayInSeconds(summary.getTrafficDelayInSeconds());
          rteSegmentProperties.setTravelTimeInSeconds(summary.getTravelTimeInSeconds());
        }
      }

      for (final LatitudeLongitude point : routeLeg.getPoints()) {
        if (point != null && point.hasValues()) {
          line.add(GeometryUtils.createCoordinateWGS84(point.getLatitude(), point.getLongitude()));
        }
      }

      rteSegments.add(rteSegment);
      lineStrings.add(GeometryUtils.createLineString(line));
    }

    if (rteSegments.isEmpty()) {
      return null;
    }

    final RouteLeg lastRouteLeg = route.getLegs().get(route.getLegs().size() - 1);
    final RtePt lastRteSegment = new RtePt();
    rteSegments.add(lastRteSegment);
    final RtePtProperties lastRteSegmentProperties = new RtePtProperties();
    lastRteSegment.setProperties(lastRteSegmentProperties);

    final LatitudeLongitude lastLatLon = lastRouteLeg
        .getPoints().get(lastRouteLeg.getPoints().size() - 1);
    lastRteSegment.setGeometry(
        GeometryUtils.createPointWGS84(lastLatLon.getLatitude(), lastLatLon.getLongitude()));

    lastRteSegmentProperties.setName(null); // TODO
    if (lastRouteLeg.getSummary() != null) {
      final RouteSummary lastSummary = lastRouteLeg.getSummary();
      if (lastSummary.getArrivalTime() != null) {
        //lastRteSegmentProperties.setTime(lastSummary.getArrivalTime().toInstant());
      }
    }

    rteProperties.setRtePts(rteSegments);
    rte.setGeometry(GeometryUtils.createMultiLineString(lineStrings));

    return rte;
  }

}
