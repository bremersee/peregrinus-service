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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.xml.bind.Unmarshaller;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.bremersee.gpx.model.LinkType;
import org.bremersee.peregrinus.geo.model.FeatureProperties;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
abstract class AbstractGpxMapper {

  private final JaxbContextBuilder jaxbContextBuilder;

  public AbstractGpxMapper(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  Unmarshaller getUnmarshaller() {
    return jaxbContextBuilder.buildUnmarshaller();
  }

  <T extends FeatureProperties> T readCommonData(
      final Supplier<T> geoJsonPropertiesSupplier,
      final String name,
      final String desc,
      final String cmt,
      final List<? extends LinkType> links) {

    final T geoJsonProperties = geoJsonPropertiesSupplier.get();
    geoJsonProperties.setCreated(Instant.now(Clock.system(ZoneId.of("UTC"))));
    geoJsonProperties.setModified(geoJsonProperties.getCreated());

    geoJsonProperties.setName(name);
    geoJsonProperties.setPlainTextDescription(readDescriptionAndComment(desc, cmt));
    geoJsonProperties.setMarkdownDescription(geoJsonProperties.getPlainTextDescription());
    geoJsonProperties.setLinks(readGarminLinks(links));
    return geoJsonProperties;
  }

  private String readDescriptionAndComment(final String desc, final String cmt) {
    final String a = StringUtils.hasText(desc) ? desc : "";
    final String b = StringUtils.hasText(cmt) ? cmt : "";
    final StringBuilder sb = new StringBuilder();
    sb.append(a);
    if (!b.equals(a) && b.length() > 0) {
      if (a.length() > 0) {
        sb.append("\n---\n");
      }
      sb.append(b);
    }
    return sb.length() > 0 ? sb.toString() : null;
  }

  Address readGarminAddress(final AddressT addressType) {
    if (addressType == null) {
      return null;
    }
    final Address address = new Address();
    address.setCity(addressType.getCity());
    address.setCountry(addressType.getCountry());
    address.setPostalCode(addressType.getPostalCode());
    address.setState(addressType.getState());
    if (addressType.getStreetAddresses() != null && !addressType.getStreetAddresses().isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (final String line : addressType.getStreetAddresses()) {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append(line);
      }
      address.setStreet(sb.toString());
    }
    return address;
  }

  private Optional<Link> readGarminLink(final LinkType linkType) {
    if (linkType == null || !StringUtils.hasText(linkType.getHref())) {
      return Optional.empty();
    }
    return Optional.of(
        new Link()
            .href(linkType.getHref())
            .type(linkType.getType())
            .text(linkType.getText()));
  }

  private List<Link> readGarminLinks(final List<? extends LinkType> linkTypes) {

    if (linkTypes == null || linkTypes.isEmpty()) {
      return null;
    }
    final List<Link> links = new ArrayList<>(linkTypes.size());
    for (LinkType linkType : linkTypes) {
      Optional<Link> link = readGarminLink(linkType);
      link.ifPresent(links::add);
    }
    return links;
  }

  private Optional<PhoneNumber> readGarminPhoneNumber(final PhoneNumberT phoneNumberType) {
    if (phoneNumberType == null || !StringUtils.hasText(phoneNumberType.getValue())) {
      return Optional.empty();
    }
    return Optional.of(
        new PhoneNumber()
            .value(phoneNumberType.getValue())
            .category(phoneNumberType.getCategory()));
  }

  List<PhoneNumber> readGarminPhoneNumbers(List<? extends PhoneNumberT> phoneNumberTypes) {

    if (phoneNumberTypes == null || phoneNumberTypes.isEmpty()) {
      return null;
    }
    final List<PhoneNumber> phoneNumbers = new ArrayList<>();
    for (PhoneNumberT phoneNumberType : phoneNumberTypes) {
      Optional<PhoneNumber> link = readGarminPhoneNumber(phoneNumberType);
      link.ifPresent(phoneNumbers::add);
    }
    return phoneNumbers;
  }

}
