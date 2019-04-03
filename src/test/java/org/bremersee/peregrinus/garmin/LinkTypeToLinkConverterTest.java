package org.bremersee.peregrinus.garmin;

import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.junit.Assert;
import org.junit.Test;

/**
 * The link type to link converter test.
 *
 * @author Christian Bremer
 */
public class LinkTypeToLinkConverterTest {

  private static final LinkTypeToLinkConverter converter = new LinkTypeToLinkConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    Link actual = converter.convert(null);
    Assert.assertNull(actual);

    LinkType link = new LinkType();
    actual = converter.convert(link);
    Assert.assertNull(actual);

    link.setHref("http://localhost:8080/foo/bar");
    link.setText("Foo bar");
    link.setType("http");
    actual = converter.convert(link);
    Assert.assertNotNull(actual);
    Assert.assertEquals("http://localhost:8080/foo/bar", actual.getHref());
    Assert.assertEquals("Foo bar", actual.getText());
    Assert.assertEquals("http", actual.getType());
  }
}