package org.bremersee.peregrinus.service.adapter.gpx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.TestConfig;
import org.bremersee.peregrinus.model.DisplayColor;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.RteSeg;
import org.bremersee.peregrinus.model.RteSettings;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.MultiLineString;
import reactor.util.function.Tuple2;

/**
 * The rte converter test.
 *
 * @author Christian Bremer
 */
public class RteConverterTest {

  private static JaxbContextBuilder jaxbContextBuilder;

  private static RteToRteTypeConverter rteConverter;

  private static RteTypeToRteConverter rteTypeConverter;

  @BeforeClass
  public static void setup() {
    jaxbContextBuilder = JaxbContextBuilder.builder().processAll(
        ServiceLoader.load(JaxbContextDataProvider.class));
    rteConverter = new RteToRteTypeConverter(jaxbContextBuilder);
    rteTypeConverter = new RteTypeToRteConverter(jaxbContextBuilder);
  }

  /**
   * Tests convert.
   */
  @Test
  public void convert() throws Exception {
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

    RteSeg rteSeg0 = RteSeg
        .builder()
        .rtePts(Arrays.asList(
            RtePt
                .builder()
                .name("Departure")
                .position(GeometryUtils.createPoint(0.000222, -1.0333))
                .build(),
            RtePt
                .builder()
                .name("Waypoint1")
                .position(GeometryUtils.createPoint(1.659999, -31.500006))
                .build()))
        .build();

    RteSeg rteSeg1 = RteSeg
        .builder()
        .rtePts(Arrays.asList(
            RtePt
                .builder()
                .name("Waypoint1")
                .position(GeometryUtils.createPoint(1.659999, -31.500006))
                .build(),
            RtePt
                .builder()
                .name("Arrival")
                .position(GeometryUtils.createPoint(0.000003, 0.000032))
                .build()))
        .build();

    Rte rte = Rte
        .builder()
        .geometry(multiLineString)
        .properties(RteProperties
            .builder()
            .name("Rte Test")
            .rteSegments(Arrays.asList(rteSeg0, rteSeg1))
            .settings(RteSettings
                .builder()
                .displayColor(DisplayColor.DARK_GREEN)
                .build())
            .build())
        .build();

    GpxExportSettings settings = new GpxExportSettings();
    Tuple2<RteType, List<WptType>> tuple = rteConverter.convert(rte, settings, new HashSet<>());

    Gpx gpx = new Gpx();
    gpx.getWpts().addAll(tuple.getT2());
    gpx.getRtes().add(tuple.getT1());
    jaxbContextBuilder.buildMarshaller().marshal(gpx, System.out);

    Rte actual = rteTypeConverter.convert(tuple.getT1());
    Assert.assertNotNull(actual);
    Assert.assertNotNull(actual.getProperties());

    String json = TestConfig
        .getObjectMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(actual);
    System.out.println(json);
  }
}