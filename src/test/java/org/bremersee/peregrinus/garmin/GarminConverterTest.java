package org.bremersee.peregrinus.garmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.TimeZone;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.bremersee.geojson.GeoJsonObjectMapperModule;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.PeregrinusObjectMapperModule;
import org.bremersee.peregrinus.model.garmin.ExportSettings;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Christian Bremer
 */
public class GarminConverterTest {

  private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

  private static ObjectMapper objectMapper;

  private static JaxbContextBuilder jaxbContextBuilder;

  private static GpxToFeaturesConverter gpxToFeaturesConverter;

  private static FeaturesToGpxConverter featuresToGpxConverter;

  @BeforeClass
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModules(
        new Jdk8Module(),
        new JavaTimeModule(),
        new GeoJsonObjectMapperModule(),
        new PeregrinusObjectMapperModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    objectMapper.setDateFormat(new StdDateFormat());
    objectMapper.setTimeZone(TimeZone.getTimeZone("GMT"));
    objectMapper.setLocale(Locale.GERMANY);

    jaxbContextBuilder = JaxbContextBuilder
        .builder()
        .processAll(ServiceLoader.load(JaxbContextDataProvider.class));
    gpxToFeaturesConverter = new GpxToFeaturesConverter(jaxbContextBuilder);
    featuresToGpxConverter = new FeaturesToGpxConverter(jaxbContextBuilder);
  }

  private Gpx loadGpx(String file) throws Exception {
    return (Gpx) jaxbContextBuilder
        .buildUnmarshaller()
        .unmarshal(resourceLoader.getResource(file).getInputStream());
  }

  private FeatureCollection loadFeatureCollection(String file) throws Exception {
    return objectMapper
        .readValue(resourceLoader.getResource(file).getInputStream(), FeatureCollection.class);
  }

  @Ignore // TODO
  @Test
  public void gpxRouteToFeatureCollection() throws Exception {
    Gpx gpx = loadGpx("route.gpx");
    FeatureCollection featureCollection = gpxToFeaturesConverter.convert(gpx, false);
    Assert.assertNotNull(featureCollection);
    Assert.assertNotNull(featureCollection.getFeatures());
    String json = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(featureCollection);
    System.out.println("Route: GPX -> GeoJson");
    System.out.println(json);
    System.out.println("---------------------");
  }

  @Test
  public void gpxTrackToFeatureCollection() throws Exception {
    Gpx gpx = loadGpx("track.gpx");
    FeatureCollection featureCollection = gpxToFeaturesConverter.convert(gpx, false);
    Assert.assertNotNull(featureCollection);
    Assert.assertNotNull(featureCollection.getFeatures());
    String json = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(featureCollection);
    System.out.println("Track: GPX -> GeoJson");
    System.out.println(json);
    System.out.println("---------------------");
  }

  @Ignore // TODO
  @Test
  public void gpxWaypointToFeatureCollection() throws Exception {
    Gpx gpx = loadGpx("waypoint.gpx");
    FeatureCollection featureCollection = gpxToFeaturesConverter.convert(gpx, false);
    Assert.assertNotNull(featureCollection);
    Assert.assertNotNull(featureCollection.getFeatures());
    String json = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(featureCollection);
    System.out.println("Waypoint: GPX -> GeoJson");
    System.out.println(json);
    System.out.println("------------------------");
  }

  @Ignore
  @Test
  public void rteFeaturesToGpx() throws Exception {
    FeatureCollection featureCollection = loadFeatureCollection("route.json");
    Gpx gpx = featuresToGpxConverter.convert(
        featureCollection.getFeatures(),
        new ExportSettings());
    System.out.println("Route: GeoJson -> GPX");
    jaxbContextBuilder
        .buildMarshaller(GarminJaxbContextDataProvider.GPX_NAMESPACES)
        .marshal(gpx, System.out);
    System.out.println("---------------------");
  }

  @Test
  public void trkFeaturesToGpx() throws Exception {
    FeatureCollection featureCollection = loadFeatureCollection("track.json");
    Gpx gpx = featuresToGpxConverter.convert(
        featureCollection.getFeatures(),
        new ExportSettings());
    System.out.println("Track: GeoJson -> GPX");
    jaxbContextBuilder
        .buildMarshaller(GarminJaxbContextDataProvider.GPX_NAMESPACES)
        .marshal(gpx, System.out);
    System.out.println("---------------------");
  }

  @Test
  public void wptFeaturesToGpx() throws Exception {
    FeatureCollection featureCollection = loadFeatureCollection("waypoint.json");
    Gpx gpx = featuresToGpxConverter.convert(
        featureCollection.getFeatures(),
        new ExportSettings());
    System.out.println("Wpt: GeoJson -> GPX");
    jaxbContextBuilder
        .buildMarshaller(GarminJaxbContextDataProvider.GPX_NAMESPACES)
        .marshal(gpx, new File("/Users/cbr/waypoint_written.xml"));
    System.out.println("-------------------");
  }
}