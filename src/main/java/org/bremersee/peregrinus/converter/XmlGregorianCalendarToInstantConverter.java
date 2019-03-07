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

import java.time.Instant;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
public class XmlGregorianCalendarToInstantConverter
    implements Converter<XMLGregorianCalendar, Instant> {

  @Override
  public Instant convert(XMLGregorianCalendar xmlGregorianCalendar) {
    if (xmlGregorianCalendar == null) {
      return null;
    }
    return xmlGregorianCalendar.toGregorianCalendar().getTime().toInstant();
  }

}
