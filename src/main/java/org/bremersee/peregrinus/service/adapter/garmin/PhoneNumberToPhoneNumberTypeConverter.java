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

package org.bremersee.peregrinus.service.adapter.garmin;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.model.CommonPhoneNumberT;
import org.springframework.util.StringUtils;

/**
 * The phone number to garmin phone number type converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class PhoneNumberToPhoneNumberTypeConverter {

  /**
   * Converts a phone number into a garmin phone number.
   *
   * @param <T>                 the garmin phone number type (can be
   *                            {@code org.bremersee.garmin.garmin.v3.model.ext.PhoneNumberT}
   *                            or {@code org.bremersee.garmin.waypoint.v1.model.ext.PhoneNumberT})
   * @param phoneNumber         the phone number
   * @param phoneNumberSupplier the garmin phone number supplier
   * @return the garmin phone number
   */
  <T extends CommonPhoneNumberT> T convert(
      final PhoneNumber phoneNumber, Supplier<T> phoneNumberSupplier) {
    if (phoneNumber == null
        || !StringUtils.hasText(phoneNumber.getValue())) {
      return null;
    }
    final T phoneNumberType = phoneNumberSupplier.get();
    phoneNumberType.setCategory(phoneNumber.getCategory());
    phoneNumberType.setValue(phoneNumber.getValue());
    return phoneNumberType;
  }
}
