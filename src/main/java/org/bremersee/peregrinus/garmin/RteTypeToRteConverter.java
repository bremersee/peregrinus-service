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

package org.bremersee.peregrinus.garmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bremersee.garmin.gpx.v3.model.ext.AutoroutePointT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.garmin.gpx.v3.model.ext.RoutePointExtension;
import org.bremersee.garmin.trip.v1.model.ext.Trip;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.DisplayColor;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.RteSeg;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/**
 * The rte type to rte converter.
 *
 * @author Christian Bremer
 */
class RteTypeToRteConverter extends AbstractGpxConverter {

  private final JaxbContextBuilder jaxbContextBuilder;

  /**
   * Instantiates a new rte type to rte converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  RteTypeToRteConverter(final JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  Rte convert(final RteType rteType) {
    final Rte rte = new Rte();
    rte.setProperties(convertCommonGpxType(rteType, RteProperties::new));
    rte.getProperties().getSettings().setDisplayColor(getDisplayColor(rteType));

    final List<LineString> lineStrings = new ArrayList<>();
    final List<RteSeg> rteSegments = new ArrayList<>();
    for (int i = 1; i < rteType.getRtepts().size(); i++) {
      final WptType from = rteType.getRtepts().get(i - 1);
      final WptType to = rteType.getRtepts().get(i);
      final Point fromPoint = GeometryUtils.createPoint(to.getLon(), to.getLat());
      final Point toPoint = GeometryUtils.createPoint(to.getLon(), to.getLat());

      final List<Coordinate> lineStringCoordinates = new ArrayList<>();
      final RteSeg rteSeg = new RteSeg();

      lineStringCoordinates.add(fromPoint.getCoordinate());
      rteSeg.getRtePts().add(
          RtePt
              .builder()
              .name(from.getName())
              .position(GeometryUtils.createPoint(from.getLon(), from.getLat()))
              .build());

      getRoutePointExtension(from).ifPresent(routePointExtension -> {
        Point previous = fromPoint;
        for (AutoroutePointT apt : routePointExtension.getRpts()) {
          final Point pt = GeometryUtils.createPoint(apt.getLon(), apt.getLat());
          if (!GeometryUtils.equals(previous, pt)) {
            lineStringCoordinates.add(pt.getCoordinate());
            previous = pt;
          }
        }
      });

      if (!GeometryUtils.equals(
          toPoint,
          GeometryUtils.createPoint(lineStringCoordinates.get(lineStringCoordinates.size() - 1)))) {
        lineStringCoordinates.add(toPoint.getCoordinate());
      }
      rteSeg.getRtePts().add(
          RtePt
              .builder()
              .name(to.getName())
              .position(toPoint)
              .build());

      rteSegments.add(rteSeg);
      lineStrings.add(GeometryUtils.createLineString(lineStringCoordinates));
    }
    rte.getProperties().setRteSegments(rteSegments);
    rte.setGeometry(GeometryUtils.createMultiLineString(lineStrings));
    return rte;
  }

  private Optional<RouteExtension> getRouteExtension(final RteType rteType) {
    return GpxJaxbContextHelper.findFirstExtension(
        RouteExtension.class, true, rteType.getExtensions(),
        jaxbContextBuilder.buildUnmarshaller());
  }

  private DisplayColor getDisplayColor(final RteType rteType) {
    return getRouteExtension(rteType)
        .map(RouteExtension::getDisplayColor)
        .map(displayColorT -> DisplayColor.findByGarminDisplayColor(
            displayColorT, DisplayColor.MAGENTA))
        .orElse(DisplayColor.MAGENTA);
  }

  private Optional<Trip> getTripExtension(final RteType rteType) {
    return GpxJaxbContextHelper.findFirstExtension(
        Trip.class,
        true,
        rteType.getExtensions(),
        jaxbContextBuilder.buildUnmarshaller());
  }

  @SuppressWarnings("unused")
  private String getTransportationMode(final RteType rteType) {
    return getTripExtension(rteType).map(Trip::getTransportationMode).orElse(null);
  }

  private Optional<RoutePointExtension> getRoutePointExtension(final WptType rtePtType) {
    return GpxJaxbContextHelper
        .findFirstExtension(
            RoutePointExtension.class,
            true,
            rtePtType.getExtensions(),
            jaxbContextBuilder.buildUnmarshaller());
  }

}
