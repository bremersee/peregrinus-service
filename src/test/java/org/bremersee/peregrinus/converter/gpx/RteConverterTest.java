package org.bremersee.peregrinus.converter.gpx;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.TestConfig;
import org.bremersee.peregrinus.content.model.DisplayColor;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RtePtProperties;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import reactor.util.function.Tuple2;

/**
 * The rte converter test.
 *
 * @author Christian Bremer
 */
public class RteConverterTest {

  private static final RteToRteTypeConverter rteConverter = new RteToRteTypeConverter(
      TestConfig.getJaxbContextBuilder());

  private static final RteTypeToRteConverter rteTypeConverter = new RteTypeToRteConverter(
      TestConfig.getJaxbContextBuilder());

  /**
   * Tests convert.
   */
  @Test
  public void convert() throws Exception {
    Address address = new Address();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.setCountryCode(Locale.GERMANY.getCountry());
    address.setStreet("Mengstraße");
    address.setStreetNumber("4");
    address.setFormattedAddress("Mengstraße 4, 23552 Lübeck");

    MultiLineString multiLineString = (MultiLineString) GeometryUtils.fromWKT(
        "MULTILINESTRING ("
            + "(0.000222 -1.0333, "
            + "-2.000009 -3.0001, "
            + "-4.000005 -5.000007, "
            + "1.659999 -31.500006), "
            + "(1.659999 -31.500006, "
            + "10.999902 3.0000004, "
            + "10.900004 1.100001, "
            + "0.000003 0.000032))");

    RtePt pt0 = new RtePt();
    pt0.setGeometry((Point) GeometryUtils.fromWKT("POINT (0.000222 -1.0333)"));
    pt0.setProperties(new RtePtProperties());
    pt0.getProperties().setCalculationProperties(null);
    pt0.getProperties().setName("RtePt0");
    pt0.getProperties().setAddress(address);
    pt0.getProperties().setEle(BigDecimal.valueOf(123.45));
    pt0.getProperties().setPhoneNumbers(Collections.singletonList(new PhoneNumber().value("4711")));
    pt0.getProperties().setPlainTextDescription("Plain Test Description");

    RtePt pt1 = new RtePt();
    pt1.setGeometry((Point) GeometryUtils.fromWKT("POINT (1.659999 -31.500006)"));
    pt1.setProperties(new RtePtProperties());
    pt1.getProperties().setCalculationProperties(null);
    pt1.getProperties().setName("RtePt1");

    RtePt pt2 = new RtePt();
    pt2.setGeometry((Point) GeometryUtils.fromWKT("POINT (0.000003 0.000032)"));
    pt2.setProperties(new RtePtProperties());
    pt2.getProperties().setCalculationProperties(null);
    pt2.getProperties().setName("RtePt2");

    Rte rte = new Rte();
    rte.setBbox(GeometryUtils.getBoundingBox(multiLineString));
    rte.setGeometry(multiLineString);
    rte.setProperties(new RteProperties());
    rte.getProperties().setName("Rte Test");
    rte.getProperties().setRtePts(Arrays.asList(pt0, pt1, pt2));
    rte.getProperties().getSettings().setDisplayColor(DisplayColor.DARK_GREEN);

    Tuple2<RteType, List<WptType>> tuple = rteConverter.convert(rte);

    Gpx gpx = new Gpx();
    gpx.getWpts().addAll(tuple.getT2());
    gpx.getRtes().add(tuple.getT1());
    TestConfig.getJaxbContextBuilder().buildMarshaller().marshal(gpx, System.out);

    Rte actual = rteTypeConverter.convert(tuple);
    Assert.assertNotNull(actual);
    Assert.assertNotNull(actual.getProperties());

    Assert.assertTrue(GeometryUtils.equals(rte.getGeometry(), actual.getGeometry()));
  }
}