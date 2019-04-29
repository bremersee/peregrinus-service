package org.bremersee.peregrinus.service.adapter.gpx;

import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.junit.Assert;
import org.junit.Test;

/**
 * The link to link type converter test.
 *
 * @author Christian Bremer
 */
public class LinkToLinkTypeConverterTest {

  private static final LinkToLinkTypeConverter converter = new LinkToLinkTypeConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    LinkType actual = converter.convert(null);
    Assert.assertNull(actual);

    // no empty link
    Link link = new Link();
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