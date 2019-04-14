package org.bremersee.peregrinus.garmin;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import org.bremersee.common.model.Link;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.peregrinus.model.DisplayColor;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.TrkProperties;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

/**
 * The trk converter test.
 *
 * @author Christian Bremer
 */
public class TrkConverterTest {

  private static JaxbContextBuilder jaxbContextBuilder;

  private static TrkToTrkTypeConverter trkConverter;

  private static TrkTypeToTrkConverter trkTypeConverter;

  @BeforeClass
  public static void setup() {
    jaxbContextBuilder = JaxbContextBuilder.builder().processAll(
        ServiceLoader.load(JaxbContextDataProvider.class));
    trkConverter = new TrkToTrkTypeConverter(jaxbContextBuilder);
    trkTypeConverter = new TrkTypeToTrkConverter(jaxbContextBuilder);
  }

  /**
   * Tests convert.
   *
   * @throws Exception the exception
   */
  @Test
  public void convert() throws Exception {

    MultiLineString multiLineString = (MultiLineString) GeometryUtils.fromWKT(
        "MULTILINESTRING ("
            + "(0.000222 -1.0333, "
            + "-2.000009 -3.0001, "
            + "-4.000005 -5.000007), "
            + "(1.659999 -31.500006, "
            + "10.999902 3.0000004, "
            + "10.900004 1.100001, "
            + "0.000003 0.000032))");

    List<List<BigDecimal>> eleLines = new ArrayList<>();
    for (int n = 0; n < multiLineString.getNumGeometries(); n++) {
      List<BigDecimal> eleLine = new ArrayList<>();
      LineString lineString = (LineString) multiLineString.getGeometryN(n);
      //noinspection unused
      for (Coordinate coordinate : lineString.getCoordinates()) {
        eleLine.add(BigDecimal.valueOf(Math.random()));
      }
      eleLines.add(eleLine);
    }

    OffsetDateTime start = OffsetDateTime.ofInstant(
        new Date(System.currentTimeMillis() - 1000L * 60L).toInstant(),
        ZoneId.of("Z"));
    List<List<OffsetDateTime>> timeLines = new ArrayList<>();
    for (int n = 0; n < multiLineString.getNumGeometries(); n++) {
      List<OffsetDateTime> timeLine = new ArrayList<>();
      LineString lineString = (LineString) multiLineString.getGeometryN(n);
      //noinspection unused
      for (Coordinate coordinate : lineString.getCoordinates()) {
        timeLine.add(OffsetDateTime.now(Clock.systemUTC()));
      }
      timeLines.add(timeLine);
    }
    OffsetDateTime stop = OffsetDateTime.ofInstant(
        new Date(System.currentTimeMillis() + 1000L * 60L).toInstant(),
        ZoneId.of("Z"));
    timeLines.get(0).set(0, start);
    List<OffsetDateTime> lastLine = timeLines.get(timeLines.size() - 1);
    lastLine.set(lastLine.size() - 1, stop);

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

    Trk trk = new Trk();
    trk.setGeometry(multiLineString);
    trk.setBbox(GeometryUtils.getBoundingBox(multiLineString));
    trk.setProperties(new TrkProperties());
    trk.getProperties().setEleLines(eleLines);
    trk.getProperties().setLinks(Arrays.asList(link0, link1, link2, link3));
    trk.getProperties().setName("Test TRK");
    trk.getProperties().setPlainTextDescription("One\n---\nTwo");
    trk.getProperties().setDepartureTime(start);
    trk.getProperties().setArrivalTime(stop);
    trk.getProperties().setTimeLines(timeLines);
    trk.getProperties().getSettings().setDisplayColor(DisplayColor.BLUE);

    TrkType trkType = trkConverter.convert(trk);

    Gpx gpx = new Gpx();
    gpx.getTrks().add(trkType);
    jaxbContextBuilder.buildMarshaller().marshal(gpx, System.out);

    Trk actual = trkTypeConverter.convert(trkType);
    Assert.assertNotNull(actual);
    Assert.assertNotNull(actual.getProperties());

    Assert.assertNotNull(actual.getGeometry());
    Assert.assertTrue(GeometryUtils.equals(trk.getGeometry(), actual.getGeometry()));

    Assert.assertEquals(trk.getProperties().getEleLines(), actual.getProperties().getEleLines());
    Assert.assertEquals(trk.getProperties().getLinks(), actual.getProperties().getLinks());
    Assert.assertEquals(trk.getProperties().getName(), actual.getProperties().getName());
    Assert.assertEquals(
        trk.getProperties().getPlainTextDescription(),
        actual.getProperties().getPlainTextDescription());
    Assert.assertEquals(trk.getProperties().getDepartureTime(), actual.getProperties().getDepartureTime());
    Assert.assertEquals(trk.getProperties().getArrivalTime(), actual.getProperties().getArrivalTime());
    Assert.assertEquals(trk.getProperties().getTimeLines(), actual.getProperties().getTimeLines());
  }
}