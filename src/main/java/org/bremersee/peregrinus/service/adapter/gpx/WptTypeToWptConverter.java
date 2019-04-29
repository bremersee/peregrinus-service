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

package org.bremersee.peregrinus.service.adapter.gpx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.WptProperties;
import org.bremersee.xml.JaxbContextBuilder;

/**
 * The gpx wpt type to wpt converter.
 *
 * @author Christian Bremer
 */
class WptTypeToWptConverter extends AbstractGpxConverter {

  private static final AddressTypeToAddressConverter addressConverter
      = new AddressTypeToAddressConverter();

  private static final PhoneNumberTypeToPhoneNumberConverter phoneNumberConverter
      = new PhoneNumberTypeToPhoneNumberConverter();

  @Getter(AccessLevel.PROTECTED)
  private final JaxbContextBuilder jaxbContextBuilder;

  WptTypeToWptConverter(final JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  /**
   * Convert wpt.
   *
   * @param wptType the wpt type
   * @return the wpt
   */
  Wpt convert(final WptType wptType) {
    Wpt wpt = new Wpt();
    wpt.setProperties(convertCommonGpxType(wptType, WptProperties::new));
    wpt.setGeometry(GeometryUtils.createPointWGS84(wptType.getLat(), wptType.getLon()));
    wpt.setBbox(GeometryUtils.getBoundingBox(wpt.getGeometry()));
    wpt.getProperties().setAddress(getAddress(wptType));
    wpt.getProperties().setEle(wptType.getEle());
    wpt.getProperties().setPhoneNumbers(getPhoneNumbers(wptType));
    return wpt;
  }

  private Optional<WaypointExtension> getWaypointExtension(WptType wptType) {
    return GpxJaxbContextHelper.findFirstExtension(
        WaypointExtension.class,
        true,
        wptType.getExtensions(),
        jaxbContextBuilder.buildUnmarshaller());
  }

  private Address getAddress(WptType wptType) {
    return getWaypointExtension(wptType)
        .map(WaypointExtension::getAddress)
        .map(addressConverter::convert)
        .orElse(null);
  }

  private List<PhoneNumber> getPhoneNumbers(WptType wptType) {
    return getWaypointExtension(wptType)
        .map(WaypointExtension::getPhoneNumbers)
        .map(this::getPhoneNumbers)
        .orElse(null);
  }

  private List<PhoneNumber> getPhoneNumbers(List<PhoneNumberT> phoneNumbers) {
    return phoneNumbers
        .stream()
        .map(phoneNumberConverter::convert)
        .collect(Collectors.toList());
  }

}
