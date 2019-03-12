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

package org.bremersee.peregrinus.converter.gpx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.garmin.gpx.v3.model.ext.AutoroutePointT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.garmin.gpx.v3.model.ext.RoutePointExtension;
import org.bremersee.garmin.trip.v1.model.ext.Trip;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.DisplayColor;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.core.convert.converter.Converter;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * @author Christian Bremer
 */
class RteTypeToRteConverter extends AbstractGpxConverter
    implements Converter<Tuple2<RteType, List<WptType>>, Rte> {

  private final RtePtTypeToRtePtConverter rtePtTypeToRtePtConverter;

  private final JaxbContextBuilder jaxbContextBuilder;

  RteTypeToRteConverter(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
    this.rtePtTypeToRtePtConverter = new RtePtTypeToRtePtConverter(jaxbContextBuilder);
  }

  @Override
  public Rte convert(Tuple2<RteType, List<WptType>> rteTypesAndWptTypes) {

    final RteType rteType = rteTypesAndWptTypes.getT1();
    final List<WptType> wptTypes = rteTypesAndWptTypes.getT2();
    final String transportationMode = getTransportationMode(rteType);
    final List<Tuple2<RtePt, List<Coordinate>>> rtePtsWithCoordinates = getRtePtsWithCoordinates(
        rteType.getRtepts(),
        transportationMode,
        wptTypes);

    final Rte rte = new Rte();
    rte.setProperties(convertCommonGpxType(rteType, RteProperties::new));
    rte.getProperties().getSettings().setDisplayColor(getDisplayColor(rteType));
    rte.getProperties().setRtePts(
        rtePtsWithCoordinates
            .stream()
            .map(Tuple2::getT1)
            .collect(Collectors.toList()));
    rte.setGeometry(
        GeometryUtils.createMultiLineString(
            rtePtsWithCoordinates
                .stream()
                .map(Tuple2::getT2)
                .map(GeometryUtils::createLineString)
                .collect(Collectors.toList())));
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

  private String getTransportationMode(final RteType rteType) {
    return getTripExtension(rteType).map(Trip::getTransportationMode).orElse(null);
  }

  private List<Tuple2<RtePt, List<Coordinate>>> getRtePtsWithCoordinates(
      final List<WptType> rtePtTypes,
      final String transportationMode,
      final List<WptType> wptTypes) {

    final List<Tuple2<RtePt, List<Coordinate>>> tuples = rtePtTypes
        .stream()
        .map(rtePtType -> getRtePtWithCoordinates(rtePtType, transportationMode, wptTypes))
        .collect(Collectors.toList());
    for (int i = 0; i < tuples.size() - 1; i++) {
      List<Coordinate> coordinates = tuples.get(i).getT2();
      Coordinate endPt = tuples.get(i + 1).getT1().getGeometry().getCoordinate();
      if (!endPt.equals2D(coordinates.get(coordinates.size() - 1))) {
        coordinates.add(endPt);
      }
    }
    return tuples;
  }

  private Tuple2<RtePt, List<Coordinate>> getRtePtWithCoordinates(
      final WptType rtePtType,
      final String transportationMode,
      final List<WptType> wptTypes) {

    final RtePt tmpRtePt = findWptType(wptTypes, rtePtType)
        .map(wptType -> Tuples.of(wptType, transportationMode))
        .map(rtePtTypeToRtePtConverter::convert)
        .orElse(new RtePt());
    final RtePt rtePt = rtePtTypeToRtePtConverter
        .convert(rtePtType, () -> tmpRtePt, tmpRtePt::getProperties);
    final List<Coordinate> coordinates = new ArrayList<>();
    coordinates.add(rtePt.getGeometry().getCoordinate());
    int i = 0;
    for (Coordinate coordinate : getCoordinates(rtePtType)) {
      Coordinate previous = coordinates.get(i);
      if (!previous.equals2D(coordinate)) {
        coordinates.add(coordinate);
        i++;
      }
    }
    return Tuples.of(rtePt, coordinates);
  }

  private Optional<WptType> findWptType(final List<WptType> wptTypes, final WptType rtePtType) {
    return wptTypes
        .stream()
        .filter(wptType -> areEqual(wptType, rtePtType))
        .findAny();
  }

  private boolean areEqual(final WptType a, final WptType b) {
    return a != null && b != null && a.getName() != null && a.getName().equals(b.getName())
        && a.getLat() != null && a.getLat().equals(b.getLat())
        && a.getLon() != null && a.getLon().equals(b.getLon());
  }

  private List<Coordinate> getCoordinates(final WptType rtePtType) {
    return getRoutePointExtension(rtePtType)
        .map(RoutePointExtension::getRpts)
        .map(pts -> pts.stream().map(this::getCoordinate).collect(Collectors.toList()))
        .orElse(new ArrayList<>());
  }

  private Coordinate getCoordinate(AutoroutePointT pt) {
    return GeometryUtils.createCoordinateWGS84(pt.getLat(), pt.getLon());
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
