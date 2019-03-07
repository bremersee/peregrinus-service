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

import org.bremersee.common.model.Address;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
public class AddressTypeToAddressConverter implements Converter<AddressT, Address> {

  @Override
  public Address convert(AddressT addressType) {
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
}
