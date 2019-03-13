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

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.Address;
import org.bremersee.garmin.model.CommonAddressT;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AddressToAddressTypeConverter {

  <T extends CommonAddressT> T convert(
      final Tuple2<Address, Supplier<T>> address) {

    if (address == null) {
      return null;
    }
    final T addressType = address.getT2().get();
    addressType.setCity(address.getT1().getCity());
    addressType.setCountry(address.getT1().getCountry());
    addressType.setPostalCode(address.getT1().getPostalCode());
    addressType.setState(address.getT1().getState());
    if (StringUtils.hasText(address.getT1().getStreet())) {
      if (StringUtils.hasText(address.getT1().getStreetNumber())) {
        addressType.getStreetAddresses()
            .add(address.getT1().getStreet() + " " + address.getT1().getStreetNumber());
      } else {
        addressType.getStreetAddresses().add(address.getT1().getStreet());
      }
    }
    return addressType;
  }
}
