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
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
public class AddressToAddressTypeConverter implements Converter<Address, AddressT> {

  @Override
  public AddressT convert(Address address) {
    if (address == null) {
      return null;
    }
    final AddressT addressType = new AddressT();
    addressType.setCity(address.getCity());
    addressType.setCountry(address.getCountry());
    addressType.setPostalCode(address.getPostalCode());
    addressType.setState(address.getState());
    if (StringUtils.hasText(address.getStreet())) {
      if (StringUtils.hasText(address.getStreetNumber())) {
        addressType.getStreetAddresses().add(address.getStreet() + " " + address.getStreetNumber());
      } else {
        addressType.getStreetAddresses().add(address.getStreet());
      }
    }
    return addressType;
  }
}
