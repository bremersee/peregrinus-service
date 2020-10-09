package org.bremersee.peregrinus.service.adapter.gpx;

import static org.bremersee.xml.ConverterUtils.xmlCalendarToOffsetDateTimeUtc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.ServiceLoader;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.common.model.TwoLetterCountryCode;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.LinkType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.WptProperties;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * The wpt converter test.
 *
 * @author Christian Bremer
 */
public class WptConverterTest {

  private static JaxbContextBuilder jaxbContextBuilder;

  private static WptToWptTypeConverter wptConverter;

  private static WptTypeToWptConverter wptTypeConverter;

  @BeforeAll
  public static void setup() {
    jaxbContextBuilder = JaxbContextBuilder.builder().processAll(
        ServiceLoader.load(JaxbContextDataProvider.class));
    wptConverter = new WptToWptTypeConverter(jaxbContextBuilder);
    wptTypeConverter = new WptTypeToWptConverter(jaxbContextBuilder);
  }

  /**
   * Tests convert.
   *
   * @throws Exception the exception
   */
  @Disabled
  @Test
  public void convert() throws Exception {
    Address address = new Address();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.setCountryCode(TwoLetterCountryCode.DE);
    address.setStreet("Mengstraße");
    address.setStreetNumber("4");
    address.setFormattedAddress("Mengstraße 4, 23552 Lübeck");

    Link link0 = new Link();
    link0.setHref("http://example.org");
    Link link1 = new Link();
    link1.setHref("https://bremersee.org");
    link1.setType("https");
    Link link2 = new Link();
    link2.setHref("http://localhost:8080");
    link2.setText("A link");
    Link link3 = new Link();
    link3.setHref("ftp://files.net");
    link3.setText("File share");
    link3.setType("ftp");

    PhoneNumber phoneNumber0 = new PhoneNumber();
    phoneNumber0.setValue("0123456789");
    PhoneNumber phoneNumber1 = new PhoneNumber();
    phoneNumber1.setValue("0234567891");
    phoneNumber1.setCategory("Mobile");

    Wpt wpt = new Wpt();
    wpt.setGeometry(GeometryUtils.createPointWGS84(52.1, 10.2));
    wpt.setId("1");
    wpt.setProperties(new WptProperties());
    wpt.getProperties().setAddress(address);
    wpt.getProperties().setEle(BigDecimal.valueOf(12.3));
    wpt.getProperties().setLinks(Arrays.asList(link0, link1, link2, link3));
    wpt.getProperties().setName("Test WPT");
    wpt.getProperties().setPhoneNumbers(Arrays.asList(phoneNumber0, phoneNumber1));
    wpt.getProperties().setPlainTextDescription("Plain text description");

    WptType wptType = wptConverter.convert(wpt);
    Assertions.assertNotNull(wptType);

    Assertions.assertNotNull(wptType.getLat());
    Assertions.assertNotNull(wptType.getLon());
    Assertions.assertEquals(
        GeometryUtils.getLatitudeWGS84(wpt.getGeometry().getCoordinate()),
        wptType.getLat().doubleValue(),
        0.);
    Assertions.assertEquals(
        GeometryUtils.getLongitudeWGS84(wpt.getGeometry().getCoordinate()),
        wptType.getLon().doubleValue(),
        0.);

    Assertions.assertNotNull(wptType.getEle());
    Assertions.assertEquals(wpt.getProperties().getEle(), wptType.getEle());

    Assertions.assertNotNull(wptType.getTime());
    Assertions.assertEquals(
        wpt.getProperties().getModified(),
        xmlCalendarToOffsetDateTimeUtc(wptType.getTime()));

    Assertions.assertNotNull(wptType.getName());
    Assertions.assertEquals(wpt.getProperties().getName(), wptType.getName());

    Assertions.assertNotNull(wptType.getCmt());
    Assertions.assertEquals(wpt.getProperties().getPlainTextDescription(), wptType.getCmt());

    Assertions.assertNotNull(wptType.getDesc());
    Assertions.assertEquals(wpt.getProperties().getPlainTextDescription(), wptType.getDesc());

    for (LinkType linkType : wptType.getLinks()) {
      Assertions.assertNotNull(linkType);
      Assertions.assertNotNull(linkType.getHref());
    }

    Assertions.assertNotNull(wptType.getExtensions());
    Assertions.assertTrue(
        GpxJaxbContextHelper
            .findFirstExtension(
                WaypointExtension.class,
                false,
                wptType.getExtensions(),
                wptConverter.getJaxbContextBuilder().buildJaxbContext())
            .isPresent());
    Assertions.assertTrue(
        GpxJaxbContextHelper
            .findFirstExtension(
                CreationTimeExtension.class,
                false,
                wptType.getExtensions(),
                wptConverter.getJaxbContextBuilder().buildJaxbContext())
            .isPresent());

    Gpx gpx = new Gpx();
    gpx.getWpts().add(wptType);
    jaxbContextBuilder.buildMarshaller().marshal(gpx, System.out);

    Wpt actual = wptTypeConverter.convert(wptType);
    Assertions.assertNotNull(actual);
    Assertions.assertNotNull(actual.getProperties());

    Assertions.assertTrue(GeometryUtils.equals(wpt.getGeometry(), actual.getGeometry()));
    Assertions.assertEquals(
        wpt.getProperties().getAddress().getCity(),
        actual.getProperties().getAddress().getCity());
    Assertions.assertEquals(wpt.getProperties().getEle(), actual.getProperties().getEle());
    Assertions.assertEquals(wpt.getProperties().getLinks(), actual.getProperties().getLinks());
    Assertions.assertEquals(
        wpt.getProperties().getPhoneNumbers(),
        actual.getProperties().getPhoneNumbers());
    Assertions.assertEquals(
        wpt.getProperties().getPlainTextDescription(),
        actual.getProperties().getPlainTextDescription());
  }
}