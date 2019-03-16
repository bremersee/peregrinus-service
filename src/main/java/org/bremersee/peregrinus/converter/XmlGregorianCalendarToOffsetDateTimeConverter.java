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

import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.core.convert.converter.Converter;

/**
 * The xml gregorian calendar to offset date time converter.
 *
 * @author Christian Bremer
 */
public class XmlGregorianCalendarToOffsetDateTimeConverter
    implements Converter<XMLGregorianCalendar, OffsetDateTime> {

  @Override
  public OffsetDateTime convert(final XMLGregorianCalendar xmlGregorianCalendar) {
    if (xmlGregorianCalendar == null) {
      return null;
    }
    return OffsetDateTime.ofInstant(
        xmlGregorianCalendar.toGregorianCalendar().getTime().toInstant(),
        ZoneId.of("Z"));
  }

}
