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

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.Address;
import org.bremersee.garmin.model.CommonAddressT;
import org.springframework.util.StringUtils;

/**
 * The address to garmin address type converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AddressToAddressTypeConverter {

  /**
   * Converts an address into a garmin address.
   *
   * @param <T>             the garmin address type (can be
   *                        {@code org.bremersee.garmin.garmin.v3.model.ext.AddressT}
   *                        or {@code org.bremersee.garmin.waypoint.v1.model.ext.AddressT})
   * @param address         the address
   * @param addressSupplier the address supplier
   * @return the t
   */
  <T extends CommonAddressT> T convert(final Address address, final Supplier<T> addressSupplier) {

    if (address == null) {
      return null;
    }
    final T addressType = addressSupplier.get();
    addressType.setCity(address.getCity());
    addressType.setCountry(address.getCountry());
    addressType.setPostalCode(address.getPostalCode());
    addressType.setState(address.getState());
    if (StringUtils.hasText(address.getStreet())) {
      if (StringUtils.hasText(address.getStreetNumber())) {
        addressType.getStreetAddresses()
            .add(address.getStreet() + " " + address.getStreetNumber());
      } else {
        addressType.getStreetAddresses().add(address.getStreet());
      }
    }
    return addressType;
  }
}
