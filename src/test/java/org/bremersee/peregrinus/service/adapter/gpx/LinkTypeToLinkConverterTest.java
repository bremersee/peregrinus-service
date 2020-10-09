package org.bremersee.peregrinus.service.adapter.gpx;

import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    Assertions.assertNull(actual);

    LinkType link = new LinkType();
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