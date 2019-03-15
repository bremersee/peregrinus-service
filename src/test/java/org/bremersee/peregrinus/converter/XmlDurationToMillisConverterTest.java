package org.bremersee.peregrinus.converter;

import java.util.Random;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.junit.Assert;
import org.junit.Test;

/**
 * The xml duration to millis converter test.
 *
 * @author Christian Bremer
 */
public class XmlDurationToMillisConverterTest {

  private static final XmlDurationToMillisConverter converter = new XmlDurationToMillisConverter();

  /**
   * Tests convert.
   *
   * @throws Exception the exception
   */
  @Test
  public void convert() throws Exception {
    Long actual = converter.convert(null);
    Assert.assertNull(actual);

    long expected = Math.abs(new Random().nextInt());
    Duration duration = DatatypeFactory.newInstance().newDuration(expected);
    actual = converter.convert(duration);
    Assert.assertNotNull(actual);
    Assert.assertEquals(expected, actual.longValue());
  }
}