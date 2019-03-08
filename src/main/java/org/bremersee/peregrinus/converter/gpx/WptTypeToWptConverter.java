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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
class WptTypeToWptConverter extends AbstractGpxConverter implements Converter<WptType, Wpt> {

  private AddressTypeToAddressConverter addressConverter = new AddressTypeToAddressConverter();

  private PhoneNumberTypeToPhoneNumberConverter phoneNumberConverter
      = new PhoneNumberTypeToPhoneNumberConverter();

  WptTypeToWptConverter(JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  @Override
  public Wpt convert(final WptType wptType) {

    final Wpt wpt = new Wpt();
    wpt.setProperties(convertCommonGpxType(wptType, WptProperties::new));

    wpt.setGeometry(GeometryUtils.createPointWGS84(wptType.getLat(), wptType.getLon()));
    wpt.setBbox(GeometryUtils.getBoundingBox(wpt.getGeometry()));

    final Optional<WaypointExtension> wptExt = GpxJaxbContextHelper.findFirstExtension(
        WaypointExtension.class,
        true,
        wptType.getExtensions(),
        getUnmarshaller());

    wpt.getProperties().setAddress(
        wptExt.map(ext -> addressConverter.convert(ext.getAddress())).orElse(null));
    wpt.getProperties().setEle(wptType.getEle());
    wpt.getProperties().setPhoneNumbers(
        wptExt.map(ext -> ext.getPhoneNumbers()
            .stream()
            .filter(Objects::nonNull)
            .map(phoneNumberConverter::convert)
            .collect(Collectors.toList()))
            .orElse(null));

    final XMLGregorianCalendar cal = GpxJaxbContextHelper
        .findFirstExtension(
            CreationTimeExtension.class,
            true,
            wptType.getExtensions(),
            getUnmarshaller())
        .map(CreationTimeExtension::getCreationTime)
        .orElse(wptType.getTime());
    final Instant time = cal != null ? cal.toGregorianCalendar().getTime().toInstant() : null;
    wpt.getProperties().setTime(time);

    if (GarminType.PHOTO.equals(wptType.getType())) {
      // TODO wanted images
    }

    return wpt;
  }

}
