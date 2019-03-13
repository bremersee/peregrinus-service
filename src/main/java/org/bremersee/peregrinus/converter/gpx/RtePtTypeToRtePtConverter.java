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

import java.time.Instant;
import java.util.Optional;
import org.bremersee.garmin.trip.v1.model.ext.NamedRoadT;
import org.bremersee.garmin.trip.v1.model.ext.ViaPoint;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RtePtProperties;
import org.bremersee.peregrinus.converter.XmlDurationToMillisConverter;
import org.bremersee.peregrinus.converter.XmlGregorianCalendarToInstantConverter;
import org.bremersee.peregrinus.geo.model.GarminImportRteCalculationProperties;
import org.bremersee.xml.JaxbContextBuilder;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
class RtePtTypeToRtePtConverter extends PtTypeToPtConverter {

  private static final XmlGregorianCalendarToInstantConverter timeConverter
      = new XmlGregorianCalendarToInstantConverter();

  private static final XmlDurationToMillisConverter durationConverter
      = new XmlDurationToMillisConverter();

  RtePtTypeToRtePtConverter(final JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  RtePt convert(final Tuple2<WptType, String> wptTypeAndTransportationMode) {
    final WptType wptType = wptTypeAndTransportationMode.getT1();
    final String transportationMode = wptTypeAndTransportationMode.getT2();
    final RtePt rtePt = convert(wptType, RtePt::new, RtePtProperties::new);
    rtePt
        .getProperties()
        .setCalculationProperties(getCalculationProperties(wptType, transportationMode));
    return rtePt;
  }

  private GarminImportRteCalculationProperties getCalculationProperties(
      final WptType wptType,
      final String transportationMode) {

    final GarminImportRteCalculationProperties calculationProperties
        = new GarminImportRteCalculationProperties();
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

  private Instant getDepartureTime(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getDepartureTime)
        .map(timeConverter::convert)
        .orElse(null);
  }

  private Instant getArrivalTime(final WptType wptType) {
    return getViaPointExtension(wptType)
        .map(ViaPoint::getArrivalTime)
        .map(timeConverter::convert)
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
        .map(durationConverter::convert).orElse(null);
  }
}
