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

package org.bremersee.peregrinus.converter.gpx;

import java.util.Objects;
import java.util.stream.Collectors;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.peregrinus.converter.InstantToXmlGregorianCalendarConverter;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
class WptToWptTypeConverter extends AbstractFeatureConverter
    implements Converter<Wpt, WptType> {

  private final JaxbContextBuilder jaxbContextBuilder;

  private InstantToXmlGregorianCalendarConverter timeConverter
      = new InstantToXmlGregorianCalendarConverter();

  private AddressToAddressTypeConverter addressConverter = new AddressToAddressTypeConverter();

  private PhoneNumberToPhoneNumberTypeConverter phoneNumberConverter
      = new PhoneNumberToPhoneNumberTypeConverter();

  WptToWptTypeConverter(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  @Override
  public WptType convert(final Wpt wpt) {
    final WptType wptType = convertFeatureProperties(wpt.getProperties(), WptType::new);
    final WptProperties properties = wpt.getProperties();
    if (properties != null) {
      wptType.setType(properties.getInternalType()); // TODO internalType
      wptType.setTime(timeConverter.convert(properties.getTime()));
      wptType.setEle(wpt.getProperties().getEle());
      final WaypointExtension wptExt = new WaypointExtension();
      wptExt.setAddress(addressConverter.convert(properties.getAddress()));
      wptExt.getPhoneNumbers().addAll(
          properties.getPhoneNumbers()
              .stream()
              .filter(Objects::nonNull)
              .map(phoneNumberConverter::convert)
              .collect(Collectors.toList()));
      if (wptExt.getAddress() != null || !wptExt.getPhoneNumbers().isEmpty()) {
        wptType.setExtensions(
            ExtensionsTypeBuilder.builder(wptType.getExtensions())
                .addElement(wptExt, jaxbContextBuilder.buildJaxbContext())
                .build(true));
      }
    }
    return wptType;
  }
}
