package org.bremersee.peregrinus.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Test;

/**
 * The xml gregorian calendar to offset date time converter test.
 *
 * @author Christian Bremer
 */
public class XmlGregorianCalendarToOffsetDateTimeConverterTest {

  private static final XmlGregorianCalendarToOffsetDateTimeConverter converter
      = new XmlGregorianCalendarToOffsetDateTimeConverter();

  /**
   * Tests convert.
   *
   * @throws Exception the exception
   */
  @Test
  public void convert() throws Exception {
    OffsetDateTime actual = converter.convert(null);
    assertNull(actual);

    OffsetDateTime expected = OffsetDateTime.now(Clock.systemUTC());
    GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    cal.setTime(Date.from(expected.toInstant()));
    XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    actual = converter.convert(xmlCal);
    assertNotNull(actual);
    assertEquals(expected, actual);
  }
}