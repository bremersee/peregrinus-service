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
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.model.CommonPhoneNumberT;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class PhoneNumberToPhoneNumberTypeConverter {

  <T extends CommonPhoneNumberT> T convert(final Tuple2<PhoneNumber, Supplier<T>> phoneNumber) {
    if (phoneNumber == null || !StringUtils.hasText(phoneNumber.getT1().getValue())) {
      return null;
    }
    final T phoneNumberType = phoneNumber.getT2().get();
    phoneNumberType.setCategory(phoneNumber.getT1().getCategory());
    phoneNumberType.setValue(phoneNumber.getT1().getValue());
    return phoneNumberType;
  }
}
