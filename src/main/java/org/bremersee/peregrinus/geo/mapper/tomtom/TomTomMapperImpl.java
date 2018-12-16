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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.geo.model.DisplayColor;
import org.bremersee.peregrinus.geo.model.Rte;
import org.bremersee.peregrinus.geo.model.RteProperties;
import org.bremersee.peregrinus.geo.model.RteSegment;
import org.bremersee.peregrinus.geo.model.RteSegmentProperties;
import org.bremersee.peregrinus.geo.model.TomTomRteCalculationProperties;
import org.bremersee.tomtom.model.LatitudeLongitude;
import org.bremersee.tomtom.model.Route;
import org.bremersee.tomtom.model.RouteLeg;
import org.bremersee.tomtom.model.RouteSummary;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

/**
 * @author Christian Bremer
 */
public class TomTomMapperImpl implements TomTomMapper {

  @Override
  public Rte readRoute(
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
    final List<RteSegment> rteSegments = new ArrayList<>();
    for (final RouteLeg routeLeg : route.getLegs()) {
      if (routeLeg == null || routeLeg.getPoints() == null || routeLeg.getPoints().isEmpty()) {
        continue;
      }
      final LatitudeLongitude latLon = routeLeg.getPoints().get(0);
      if (latLon == null || !latLon.hasValues()) {
        continue;
      }
      final List<Coordinate> line = new ArrayList<>();
      final RteSegment rteSegment = new RteSegment();
      rteSegment.setPoint(GeometryUtils
          .createPointWGS84(latLon.getLatitude(), latLon.getLongitude()));

      final RteSegmentProperties rteSegmentProperties = new RteSegmentProperties();
      rteSegment.setProperties(rteSegmentProperties);

      rteSegmentProperties.setCalculationProperties(calculationProperties);
      //rteSegmentProperties.setName(""); // TODO
      if (routeLeg.getSummary() != null) {
        final RouteSummary summary = routeLeg.getSummary();
        if (summary.getDepartureTime() != null) {
          rteSegmentProperties.setTime(Date.from(summary.getDepartureTime().toInstant()));
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
    final RteSegment lastRteSegment = new RteSegment();
    rteSegments.add(lastRteSegment);
    final RteSegmentProperties lastRteSegmentProperties = new RteSegmentProperties();
    lastRteSegment.setProperties(lastRteSegmentProperties);

    final LatitudeLongitude lastLatLon = lastRouteLeg
        .getPoints().get(lastRouteLeg.getPoints().size() - 1);
    lastRteSegment.setPoint(
        GeometryUtils.createPointWGS84(lastLatLon.getLatitude(), lastLatLon.getLongitude()));

    lastRteSegmentProperties.setName(null); // TODO
    if (lastRouteLeg.getSummary() != null) {
      final RouteSummary lastSummary = lastRouteLeg.getSummary();
      if (lastSummary.getArrivalTime() != null) {
        lastRteSegmentProperties.setTime(Date.from(lastSummary.getArrivalTime().toInstant()));
      }
    }

    rteProperties.setRteSegments(rteSegments);
    rte.setGeometry(GeometryUtils.createMultiLineString(lineStrings));

    return rte;
  }

}
