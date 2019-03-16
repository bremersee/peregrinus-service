package org.bremersee.peregrinus.converter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;

/**
 * The instant to xml gregorian calendar converter test.
 *
 * @author Christian Bremer
 */
public class InstantToXmlGregorianCalendarConverterTest {

  private static final OffsetDateTimeToXmlGregorianCalendarConverter converter
      = new OffsetDateTimeToXmlGregorianCalendarConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    XMLGregorianCalendar actual = converter.convert(null);
    Assert.assertNull(actual);

    Instant expected = Instant.now(Clock.system(ZoneId.of("Z")));
    actual = converter.convert(expected);
    Assert.assertNotNull(actual);
    Assert.assertEquals(Date.from(expected), actual.toGregorianCalendar().getTime());
  }
}