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

package org.bremersee.peregrinus.geo.mapper.gpx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bremersee.garmin.gpx.v3.model.ext.AutoroutePointT;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.garmin.gpx.v3.model.ext.RoutePointExtension;
import org.bremersee.garmin.trip.v1.model.ext.Trip;
import org.bremersee.garmin.trip.v1.model.ext.ViaPoint;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.DisplayColor;
import org.bremersee.peregrinus.geo.model.GarminImportRteCalculationProperties;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RteSegment;
import org.bremersee.peregrinus.content.model.RteSegmentProperties;
import org.bremersee.peregrinus.content.model.RteSettings;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

/**
 * @author Christian Bremer
 */
class RteMapper extends AbstractGpxConverter {

  private final WptMapper wptMapper;

  RteMapper(JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
    wptMapper = new WptMapper(jaxbContextBuilder);
  }

  List<Rte> readRtes(final List<RteType> rteTypes) {
    final List<Rte> rteList = new ArrayList<>();
    if (rteTypes != null) {
      for (final RteType rteType : rteTypes) {
        if (rteType != null) {
          final Rte rte = readRte(rteType);
          if (rte != null) {
            rteList.add(rte);
          }
        }
      }
    }
    return rteList;
  }

  private Rte readRte(final RteType rteType) {

    final Rte rte = new Rte();
    rte.setProperties(readCommonData(
        RteProperties::new,
        rteType.getName(),
        rteType.getDesc(),
        rteType.getCmt(),
        rteType.getLinks()));
    rte.getProperties().setSettings(new RteSettings());

    // display color
    final Optional<RouteExtension> rteTypeExt = GpxJaxbContextHelper.findFirstExtension(
        RouteExtension.class,
        true,
        rteType.getExtensions(),
        getUnmarshaller());

    final DisplayColorT displayColor = rteTypeExt.map(RouteExtension::getDisplayColor).orElse(null);
    rte
        .getProperties()
        .getSettings()
        .setDisplayColor(DisplayColor.findByGarminDisplayColor(
            displayColor,
            DisplayColor.MAGENTA));

    final Optional<Trip> tripExt = GpxJaxbContextHelper.findFirstExtension(
        Trip.class,
        true,
        rteType.getExtensions(),
        getUnmarshaller());

    final String transportationMode = tripExt.map(Trip::getTransportationMode).orElse(null);

    final List<RteSegmentWithCoordinates> rteSegmentWithCoordinates = readRtePts(
        rteType.getRtepts(), transportationMode);

    if (rteSegmentWithCoordinates.size() < 2) {
      return null;
    }

    final List<RteSegment> rteSegments = new ArrayList<>();
    final List<LineString> rteCoordinates = new ArrayList<>();
    RteSegment prevRteSegment = rteSegmentWithCoordinates.get(0).getRteSegment();
    List<Coordinate> prevRteSegmentCoordinates = rteSegmentWithCoordinates
        .get(0)
        .getCoordinates();

    for (int n = 1; n < rteSegmentWithCoordinates.size(); n++) {
      final RteSegment rteSegment = rteSegmentWithCoordinates.get(0).getRteSegment();
      final List<Coordinate> rteSegmentCoordinates = rteSegmentWithCoordinates
          .get(n)
          .getCoordinates();
      if (prevRteSegmentCoordinates.size() < 2
          || !prevRteSegmentCoordinates.get(prevRteSegmentCoordinates.size() - 1)
          .equals2D(rteSegment.getPoint().getCoordinate())) {
        prevRteSegmentCoordinates.add(rteSegment.getPoint().getCoordinate());
      }
      rteSegments.add(prevRteSegment);
      rteCoordinates.add(GeometryUtils.createLineString(prevRteSegmentCoordinates));
      prevRteSegment = rteSegment;
      prevRteSegmentCoordinates = rteSegmentCoordinates;
    }

    rteSegments.add(prevRteSegment);
    rte.getProperties().setRteSegments(rteSegments);
    rte.setGeometry(GeometryUtils.createMultiLineString(rteCoordinates));

    return rte;
  }

  private List<RteSegmentWithCoordinates> readRtePts(
      final List<WptType> rtePts,
      final String transportationMode) {

    final List<RteSegmentWithCoordinates> list = new ArrayList<>();
    if (rtePts != null) {
      for (final WptType rtePt : rtePts) {
        final RteSegmentWithCoordinates entry = readRtePt(rtePt, transportationMode);
        if (entry != null) {
          list.add(entry);
        }
      }
    }
    return list;
  }

  private RteSegmentWithCoordinates readRtePt(
      final WptType rtePt,
      final String transportationMode) {

    if (rtePt == null || rtePt.getLat() == null || rtePt.getLon() == null) {
      return null;
    }

    final Wpt wpt = wptMapper.readWptType(rtePt);

    final RteSegment rteSegment = new RteSegment();
    rteSegment.setPoint(wpt.getGeometry());

    final RteSegmentProperties rteSegmentProperties = new RteSegmentProperties();
    rteSegment.setProperties(rteSegmentProperties);
    rteSegmentProperties.setName(wpt.getProperties().getName());
    rteSegmentProperties.setTime(wpt.getProperties().getTime());

    final GarminImportRteCalculationProperties calculationProperties
        = new GarminImportRteCalculationProperties();
    rteSegmentProperties.setCalculationProperties(calculationProperties);
    calculationProperties.setTransportationMode(transportationMode);
    GpxJaxbContextHelper.findFirstExtension(
        ViaPoint.class,
        true,
        rtePt.getExtensions(),
        getUnmarshaller()).ifPresent(viaPoint -> {
      calculationProperties.setArrivalTime(
          viaPoint.getArrivalTime() != null
              ? viaPoint.getArrivalTime().toGregorianCalendar().getTime()
              : null);
      calculationProperties.setCalculationMode(viaPoint.getCalculationMode());
      calculationProperties.setDepartureTime(
          viaPoint.getDepartureTime() != null
              ? viaPoint.getDepartureTime().toGregorianCalendar().getTime().toInstant()
              : null);
      calculationProperties.setElevationMode(viaPoint.getElevationMode());
      calculationProperties.setNamedRoad(viaPoint.getNamedRoad());
      if (viaPoint.getStopDuration() != null) {
        final Date tmp = new Date(0L);
        viaPoint.getStopDuration().addTo(tmp);
        calculationProperties.setStopDurationMillis(tmp.getTime());
      }
    });

    final List<Coordinate> segmentCoordinates = new ArrayList<>();
    segmentCoordinates.add(wpt.getGeometry().getCoordinate());

    GpxJaxbContextHelper.findFirstExtension(
        RoutePointExtension.class,
        true,
        rtePt.getExtensions(),
        getUnmarshaller()).ifPresent(routePointExtension -> {
      if (routePointExtension.getRpts() != null) {
        for (AutoroutePointT pt : routePointExtension.getRpts()) {
          if (pt != null && pt.getLat() != null && pt.getLon() != null) {
            segmentCoordinates.add(
                GeometryUtils.createCoordinateWGS84(pt.getLat(), pt.getLon()));
          }
        }
      }
    });

    return new RteSegmentWithCoordinates(rteSegment, segmentCoordinates);
  }

  @Getter(AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class RteSegmentWithCoordinates {

    private RteSegment rteSegment;

    private List<Coordinate> coordinates;
  }

}
