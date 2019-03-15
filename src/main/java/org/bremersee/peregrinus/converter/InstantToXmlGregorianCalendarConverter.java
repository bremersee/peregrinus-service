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

import java.sql.Date;
import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bremersee.exception.ServiceException;
import org.springframework.core.convert.converter.Converter;

/**
 * The instant to xml gregorian calendar converter.
 *
 * @author Christian Bremer
 */
public class InstantToXmlGregorianCalendarConverter
    implements Converter<Instant, XMLGregorianCalendar> {

  @Override
  public XMLGregorianCalendar convert(final Instant instant) {
    if (instant == null) {
      return null;
    }
    try {
      final GregorianCalendar source = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
      source.setTime(Date.from(instant));
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(source);

    } catch (DatatypeConfigurationException e) {
      throw ServiceException.internalServerError("Creating XMLGregorianCalendar failed.", e);
    }
  }
}
