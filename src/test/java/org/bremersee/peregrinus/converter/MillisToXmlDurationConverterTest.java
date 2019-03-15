package org.bremersee.peregrinus.converter;

import java.util.Date;
import java.util.Random;
import javax.xml.datatype.Duration;
import org.junit.Assert;
import org.junit.Test;

/**
 * The millis to xml duration converter test.
 *
 * @author Christian Bremer
 */
public class MillisToXmlDurationConverterTest {

  private static final MillisToXmlDurationConverter converter = new MillisToXmlDurationConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    Duration actual = converter.convert(null);
    Assert.assertNull(actual);

    long expected = Math.abs(new Random().nextInt());
    actual = converter.convert(expected);
    Assert.assertNotNull(actual);
    Date tmp = new Date(0L);
    actual.addTo(tmp);
    Assert.assertEquals(expected, tmp.getTime());
  }
}