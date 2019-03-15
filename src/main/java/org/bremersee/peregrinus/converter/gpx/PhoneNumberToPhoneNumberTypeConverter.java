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

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.model.CommonPhoneNumberT;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple2;

/**
 * The phone number to garmin phone number type converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class PhoneNumberToPhoneNumberTypeConverter {

  /**
   * Convert t.
   *
   * @param <T>              the garmin phone number type (can be
   *                         {@code org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT}
   *                         or {@code org.bremersee.garmin.waypoint.v1.model.ext.PhoneNumberT})
   * @param phoneNumberTuple the phone number tuple
   * @return the garmin phone number
   */
  <T extends CommonPhoneNumberT> T convert(
      final Tuple2<Optional<PhoneNumber>, Supplier<T>> phoneNumberTuple) {
    if (phoneNumberTuple == null
        || !phoneNumberTuple.getT1().isPresent()
        || !StringUtils.hasText(phoneNumberTuple.getT1().get().getValue())) {
      return null;
    }
    final PhoneNumber phoneNumber = phoneNumberTuple.getT1().get();
    final T phoneNumberType = phoneNumberTuple.getT2().get();
    phoneNumberType.setCategory(phoneNumber.getCategory());
    phoneNumberType.setValue(phoneNumber.getValue());
    return phoneNumberType;
  }
}
