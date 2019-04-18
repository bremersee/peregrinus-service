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

import java.time.OffsetDateTime;
import java.util.Optional;
import org.bremersee.garmin.trip.v1.model.ext.NamedRoadT;
import org.bremersee.garmin.trip.v1.model.ext.ViaPoint;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.garmin.GarminRtePtCalculationProperties;
import org.bremersee.xml.ConverterUtils;
import org.bremersee.xml.JaxbContextBuilder;
import reactor.util.function.Tuple2;

/**
 * The rte pt type to rte pt converter.
 *
 * @author Christian Bremer
 */
class RtePtTypeToRtePtConverter extends PtTypeToPtConverter {

  /**
   * Instantiates a new rte pt type to rte pt converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  RtePtTypeToRtePtConverter(final JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  /**
   * Convert rte pt.
   *
   * @param wptTypeAndTransportationMode the wpt type and transportation mode
   * @return the rte pt
   */
  RtePt convert(final Tuple2<WptType, String> wptTypeAndTransportationMode) {
    final WptType wptType = wptTypeAndTransportationMode.getT1();
    final String transportationMode = wptTypeAndTransportationMode.getT2();
//    final RtePt rtePt = convert(wptType, RtePt::new, RtePtProperties::new);
//    rtePt
//        .getProperties()
//        .setCalculationProperties(getCalculationProperties(wptType, transportationMode));
//    return rtePt;
    return null;
  }

  private GarminRtePtCalculationProperties getCalculationProperties(
      final WptType wptType,
      final String transportationMode) {

    final GarminRtePtCalculationProperties calculationProperties
        = new GarminRtePtCalculationProperties();
    calculationProperties.setArrivalTime(getArrivalTime(wptType));
    calculationProperties.setCalculationMode(getCalculationMode(wptType));
    calculationProperties.setDepartureTime(getDepartureTime(wptType));
    calculationProperties.setElevationMode(getElevationMode(wptType));
    calculationProperties.setNamedRoad(getNamedRoad(wptType));
    calculationProperties.setStopDurationMillis(getStopDurationMillis(wptType));
    calculationProperties.setTransportationMode(transportationMode);
    return calculationProperties;
  }

  private Optional<ViaPoint> getViaPointExtension(final WptType wptType) {
    return GpxJaxbContextHelper.findFirstExtension(
        ViaPoint.class,
        true,
        wptType.getExtensions(),
        getJaxbContextBuilder().buildUnmarshaller());
  }

  private OffsetDateTime getDepartureTime(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getDepartureTime)
        .map(ConverterUtils::xmlCalendarToOffsetDateTimeUtc)
        .orElse(null);
  }

  private OffsetDateTime getArrivalTime(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getArrivalTime)
        .map(ConverterUtils::xmlCalendarToOffsetDateTimeUtc)
        .orElse(null);
  }

  private String getCalculationMode(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getCalculationMode)
        .orElse(null);
  }

  private String getElevationMode(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getElevationMode)
        .orElse(null);
  }

  private NamedRoadT getNamedRoad(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getNamedRoad)
        .orElse(null);
  }

  private Long getStopDurationMillis(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getStopDuration)
        .map(ConverterUtils::xmlDurationToMillis)
        .orElse(null);
  }
}
