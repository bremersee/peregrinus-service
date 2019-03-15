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

package org.bremersee.peregrinus.converter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.bremersee.exception.ServiceException;
import org.springframework.core.convert.converter.Converter;

/**
 * The millis to xml duration converter.
 *
 * @author Christian Bremer
 */
public class MillisToXmlDurationConverter implements Converter<Long, Duration> {

  @Override
  public Duration convert(final Long millis) {
    if (millis == null || millis < 0L) {
      return null;
    }
    try {
      return DatatypeFactory.newInstance().newDuration(millis);

    } catch (DatatypeConfigurationException e) {
      throw ServiceException.internalServerError("Creating xml duration failed.", e);
    }
  }
}
