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

import static org.bremersee.xml.ConverterUtils.offsetDateTimeToXmlCalendarUtc;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Pt;
import org.bremersee.xml.JaxbContextBuilder;

/**
 * The pt (point) to pt (point) type converter.
 *
 * @param <T> the point type parameter
 * @author Christian Bremer
 */
abstract class PtToPtTypeConverter<T extends Pt>
    extends AbstractFeatureConverter {

  private static final AddressToAddressTypeConverter addressConverter
      = new AddressToAddressTypeConverter();

  private static final PhoneNumberToPhoneNumberTypeConverter phoneNumberConverter
      = new PhoneNumberToPhoneNumberTypeConverter();

  @Getter(AccessLevel.PACKAGE)
  private String[] gpxNameSpaces;

  @Getter(AccessLevel.PACKAGE)
  private final JaxbContextBuilder jaxbContextBuilder;

  PtToPtTypeConverter(final JaxbContextBuilder jaxbContextBuilder, String... gpxNameSpaces) {
    this.gpxNameSpaces = gpxNameSpaces == null || gpxNameSpaces.length == 0
        ? GarminJaxbContextDataProvider.GPX_NAMESPACES
        : gpxNameSpaces;
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  /**
   * Convert wpt type.
   *
   * @param pt the pt
   * @return the wpt type
   */
  WptType convert(final T pt) {
    final WptType wptType = super.convertFeatureProperties(pt.getProperties(), WptType::new);
    wptType.setTime(offsetDateTimeToXmlCalendarUtc(pt.getProperties().getModified()));
    wptType.setSym("Flag, Blue");
    wptType.setLat(BigDecimal.valueOf(
        GeometryUtils.getLatitudeWGS84(pt.getGeometry().getCoordinate())));
    wptType.setLon(BigDecimal.valueOf(
        GeometryUtils.getLongitudeWGS84(pt.getGeometry().getCoordinate())));
    wptType.setEle(pt.getProperties().getEle());
    wptType.setExtensions(getWptTypeExtensions(wptType, pt));
    return wptType;
  }

  private ExtensionsType getWptTypeExtensions(final WptType wptType, final T pt) {
    if (pt.getProperties().getAddress() == null
        && (pt.getProperties().getPhoneNumbers() == null
        || pt.getProperties().getPhoneNumbers().isEmpty())) {
      return wptType.getExtensions();
    }
    final WaypointExtension waypointExtension3 = new WaypointExtension();
    waypointExtension3.setAddress(addressConverter.convert(
        pt.getProperties().getAddress(), AddressT::new));
    waypointExtension3.getPhoneNumbers().addAll(
        pt.getProperties().getPhoneNumbers()
            .stream()
            .filter(Objects::nonNull)
            .map(number -> phoneNumberConverter.convert(
                number, PhoneNumberT::new))
            .collect(Collectors.toList()));

    final org.bremersee.garmin.waypoint.v1.model.ext.WaypointExtension waypointExtension1
        = new org.bremersee.garmin.waypoint.v1.model.ext.WaypointExtension();
    waypointExtension1.setAddress(addressConverter.convert(
        pt.getProperties().getAddress(),
        org.bremersee.garmin.waypoint.v1.model.ext.AddressT::new));
    waypointExtension1.getPhoneNumbers().addAll(
        pt.getProperties().getPhoneNumbers()
            .stream()
            .filter(Objects::nonNull)
            .map(
                number -> phoneNumberConverter.convert(
                    number,
                    org.bremersee.garmin.waypoint.v1.model.ext.PhoneNumberT::new))
            .collect(Collectors.toList()));

    final CreationTimeExtension timeExtension = new CreationTimeExtension();
    timeExtension.setCreationTime(offsetDateTimeToXmlCalendarUtc(pt.getProperties().getCreated()));

    return ExtensionsTypeBuilder.builder(wptType.getExtensions())
        .addElement(waypointExtension3, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .addElement(waypointExtension1, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .addElement(timeExtension, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .build(true);
  }

}