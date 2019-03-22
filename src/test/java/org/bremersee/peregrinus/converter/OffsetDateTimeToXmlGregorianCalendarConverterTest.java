package org.bremersee.peregrinus.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;

/**
 * The offset date time to xml gregorian calendar converter test.
 *
 * @author Christian Bremer
 */
public class OffsetDateTimeToXmlGregorianCalendarConverterTest {

  private static final OffsetDateTimeToXmlGregorianCalendarConverter converter
      = new OffsetDateTimeToXmlGregorianCalendarConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    XMLGregorianCalendar actual = converter.convert(null);
    assertNull(actual);

    OffsetDateTime expected = OffsetDateTime.now(Clock.system(ZoneId.of("Z")));
    actual = converter.convert(expected);
    assertNotNull(actual);
    assertEquals(Date.from(expected.toInstant()), actual.toGregorianCalendar().getTime());
  }
}