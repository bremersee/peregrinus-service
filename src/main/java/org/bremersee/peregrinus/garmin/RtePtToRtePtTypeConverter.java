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

package org.bremersee.peregrinus.garmin;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.garmin.gpx.v3.model.ext.AutoroutePointT;
import org.bremersee.garmin.gpx.v3.model.ext.RoutePointExtension;
import org.bremersee.garmin.trip.v1.model.ext.ViaPoint;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import reactor.util.function.Tuple2;

/**
 * The rte pt to rte pt type converter.
 *
 * @author Christian Bremer
 */
class RtePtToRtePtTypeConverter { // extends PtToPtTypeConverter<RtePt> {

  /**
   * Instantiates a new rte pt to rte pt type converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  RtePtToRtePtTypeConverter(final JaxbContextBuilder jaxbContextBuilder, String... gpxNameSpaces) {
    //super(jaxbContextBuilder, gpxNameSpaces);
  }

  /**
   * Convert rte.
   *
   * @param rtePtWithPoints the rte pt with points
   * @return the wpt type
   */
  WptType convert(final Tuple2<RtePt, Optional<LineString>> rtePtWithPoints) {
    final WptType wpt = null; //super.convert(rtePtWithPoints.getT1());
    wpt.setType(null);
    wpt.setEle(null);
    if (rtePtWithPoints.getT2().isPresent()) {
      wpt.setExtensions(getRtePtExtensions(rtePtWithPoints.getT1(), rtePtWithPoints.getT2().get()));
    }
    return wpt;
  }

  private ExtensionsType getRtePtExtensions(RtePt rtePt, LineString lineString) {

    final ViaPoint viaPoint = new ViaPoint(); // TODO
    viaPoint.setArrivalTime(null);
    viaPoint.setCalculationMode("FasterTime");
    viaPoint.setDepartureTime(null);
    viaPoint.setElevationMode("Standard");
    viaPoint.setNamedRoad(null);
    viaPoint.setStopDuration(null);

    final RoutePointExtension routePointExtension = new RoutePointExtension();
    routePointExtension.getRpts().addAll(getRoutePoints(lineString));
    routePointExtension.setSubclass(null); // TODO

    return null;
//    return ExtensionsTypeBuilder
//        .builder()
//        .addElement(viaPoint, getJaxbContextBuilder().buildJaxbContext(getGpxNameSpaces()))
//        .addElement(
//            routePointExtension, getJaxbContextBuilder().buildJaxbContext(getGpxNameSpaces()))
//        .build(true);
  }

  private List<AutoroutePointT> getRoutePoints(LineString lineString) {
    return Arrays.stream(lineString.getCoordinates())
        .skip(1)
        .map(this::getAutoroutePoint)
        .collect(Collectors.toList());
  }

  private AutoroutePointT getAutoroutePoint(Coordinate coordinate) {
    AutoroutePointT point = new AutoroutePointT();
    point.setLat(BigDecimal.valueOf(GeometryUtils.getLatitudeWGS84(coordinate)));
    point.setLon(BigDecimal.valueOf(GeometryUtils.getLongitudeWGS84(coordinate)));
    point.setSubclass(null); // TODO
    return point;
  }

}
