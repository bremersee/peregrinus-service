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

import static org.bremersee.xml.ConverterUtils.millisToXmlCalendar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayModeT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.garmin.trip.v1.model.ext.Trip;
import org.bremersee.garmin.trip.v1.model.ext.ViaPoint;
import org.bremersee.garmin.trip.v1.model.ext.ViaPointCalculationMode;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.RteSeg;
import org.bremersee.peregrinus.model.garmin.ExportSettings;
import org.bremersee.xml.JaxbContextBuilder;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * The rte to rte type converter.
 *
 * @author Christian Bremer
 */
public class RteToRteTypeConverter extends AbstractFeatureConverter {

  private String[] gpxNameSpaces;

  private final JaxbContextBuilder jaxbContextBuilder;

  RteToRteTypeConverter(final JaxbContextBuilder jaxbContextBuilder, String... gpxNameSpaces) {
    this.gpxNameSpaces = gpxNameSpaces == null || gpxNameSpaces.length == 0
        ? GarminJaxbContextDataProvider.GPX_NAMESPACES
        : gpxNameSpaces;
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  Tuple2<RteType, List<WptType>> convert(
      final Rte rte,
      final ExportSettings exportSettings) {

    final int eachRtePtNumber = getEachRtePointValue(exportSettings);
    final List<WptType> wptTypes = new ArrayList<>();
    final RteType rteType = convertFeatureProperties(rte.getProperties(), RteType::new);
    int iSize = rte.getProperties().getRteSegments().size();
    int i = 0;
    for (RteSeg rteSeg : rte.getProperties().getRteSegments()) {
      int nSize = rteSeg.getRtePts().size();
      int n = 0;
      for (RtePt rtePt : rteSeg.getRtePts()) {
        if ((n < nSize - 1 && n % eachRtePtNumber == 0)
            || (n == nSize - 1 && i == iSize - 1)) {

          final WptType wptType = new WptType();
          wptType.setLat(BigDecimal.valueOf(rtePt.getPosition().getY()));
          wptType.setLon(BigDecimal.valueOf(rtePt.getPosition().getX()));
          wptType.setName(getRtePtName(iSize, i, nSize, n, rte.getProperties().getName()));
          wptType.setExtensions(getViaPoint(exportSettings));
          rteType.getRtepts().add(wptType);

          if (Boolean.TRUE.equals(exportSettings.getExportRouteWaypoints())) {
            final WptType wpt = new WptType();
            wpt.setLat(wptType.getLat());
            wpt.setLon(wptType.getLon());
            wpt.setName(wptType.getName());
            wpt.setSym(exportSettings.getRouteWaypointSymbol().toString());
            wpt.setType("user");
            wpt.setExtensions(getWptTypeExtensions());
            wptTypes.add(wpt);
          }
        }
        n++;
      }
      i++;
    }
    rteType.setExtensions(getRouteExtension(rte, exportSettings));
    return Tuples.of(rteType, wptTypes);
  }

  private int getEachRtePointValue(ExportSettings exportSettings) {
    if (exportSettings.getPercentWaypoints() == null || exportSettings.getPercentWaypoints() <= 0) {
      return Integer.MAX_VALUE;
    }
    if (100 / exportSettings.getPercentWaypoints() < 1) {
      return 1;
    }
    return 100 / exportSettings.getPercentWaypoints();
  }

  private String getRtePtName(int iSize, int i, int nSize, int n, String name) {
    int len = Integer.toString(iSize).length();
    final String iStr = String.format("%0" + len + "d", i);
    len = Integer.toString(nSize).length();
    final String nStr = String.format("%0" + len + "d", n);
    return name + " WPT(" + iStr + "_" + nStr + ")";
  }

  private ExtensionsType getRouteExtension(
      final Rte rte,
      final ExportSettings exportSettings) {

    final RouteExtension routeExtension = new RouteExtension();
    routeExtension.setDisplayColor(rte.getProperties().getSettings().getDisplayColor().getGarmin());
    routeExtension.setIsAutoNamed(true);

    if (ViaPointCalculationMode.DIRECT.equals(exportSettings.getCalculationMode())) {
      return ExtensionsTypeBuilder
          .builder()
          .addElement(routeExtension, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
          .build(true);
    }

    final Trip trip = new Trip();
    trip.setTransportationMode(exportSettings.getTransportationMode().toString());

    return ExtensionsTypeBuilder
        .builder()
        .addElement(routeExtension, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .addElement(trip, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .build(true);
  }

  private ExtensionsType getViaPoint(final ExportSettings exportSettings) {
    ViaPoint viaPoint = new ViaPoint();
    viaPoint.setArrivalTime(null);
    viaPoint.setCalculationMode(exportSettings.getCalculationMode().toString());
    viaPoint.setDepartureTime(null);
    viaPoint.setElevationMode(exportSettings.getElevationMode().toString());
    viaPoint.setNamedRoad(null);
    viaPoint.setStopDuration(null);
    return ExtensionsTypeBuilder
        .builder()
        .addElement(viaPoint, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .build(true);
  }

  private ExtensionsType getWptTypeExtensions() {
    final WaypointExtension waypointExtension3 = new WaypointExtension();
    waypointExtension3.setDisplayMode(DisplayModeT.SYMBOL_AND_NAME);

    final org.bremersee.garmin.waypoint.v1.model.ext.WaypointExtension waypointExtension1
        = new org.bremersee.garmin.waypoint.v1.model.ext.WaypointExtension();
    waypointExtension1.setDisplayMode(
        org.bremersee.garmin.waypoint.v1.model.ext.DisplayModeT.SYMBOL_AND_NAME);

    final CreationTimeExtension timeExtension = new CreationTimeExtension();
    timeExtension.setCreationTime(millisToXmlCalendar(System.currentTimeMillis()));

    return ExtensionsTypeBuilder.builder()
        .addElement(waypointExtension3, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .addElement(waypointExtension1, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .addElement(timeExtension, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .build(true);
  }
}
