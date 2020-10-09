package org.bremersee.peregrinus.service.adapter.gpx;

import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    Assertions.assertNull(actual);

    // no empty link
    Link link = new Link();
    actual = converter.convert(link);
    Assertions.assertNull(actual);

    link.setHref("http://localhost:8080/foo/bar");
    link.setText("Foo bar");
    link.setType("http");
    actual = converter.convert(link);
    Assertions.assertNotNull(actual);
    Assertions.assertEquals("http://localhost:8080/foo/bar", actual.getHref());
    Assertions.assertEquals("Foo bar", actual.getText());
    Assertions.assertEquals("http", actual.getType());
  }
}