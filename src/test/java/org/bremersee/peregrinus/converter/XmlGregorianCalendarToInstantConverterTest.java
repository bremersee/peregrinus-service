package org.bremersee.peregrinus.converter;

import java.time.Instant;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;

/**
 * The xml gregorian calendar to instant converter test.
 *
 * @author Christian Bremer
 */
public class XmlGregorianCalendarToInstantConverterTest {

  private static final XmlGregorianCalendarToInstantConverter converter
      = new XmlGregorianCalendarToInstantConverter();

  /**
   * Tests convert.
   *
   * @throws Exception the exception
   */
  @Test
  public void convert() throws Exception {
    Instant actual = converter.convert(null);
    Assert.assertNull(actual);

    Instant expected = Instant.now();
    GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    cal.setTime(Date.from(expected));
    XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    actual = converter.convert(xmlCal);
    Assert.assertNotNull(actual);
    Assert.assertEquals(expected, actual);
  }
}