package org.bremersee.peregrinus.service.adapter.gpx;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ServiceLoader;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.TestConfig;
import org.bremersee.peregrinus.config.GpxProperties;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.bremersee.peregrinus.model.gpx.GpxImportSettings;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Christian Bremer
 */
public class GpxConverterTest {

  private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

  private static ObjectMapper objectMapper;

  private static JaxbContextBuilder jaxbContextBuilder;

  private static GpxToFeaturesConverter gpxToFeaturesConverter;

  private static FeaturesToGpxConverter featuresToGpxConverter;

  @BeforeAll
  public static void setup() {
    objectMapper = TestConfig.getObjectMapper();
    jaxbContextBuilder = JaxbContextBuilder
        .builder()
        .processAll(ServiceLoader.load(JaxbContextDataProvider.class));
    gpxToFeaturesConverter = new GpxToFeaturesConverter(jaxbContextBuilder);
    featuresToGpxConverter = new FeaturesToGpxConverter(
        jaxbContextBuilder, new GpxProperties());
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

  @Test
  public void gpxRouteToFeatureCollection() throws Exception {
    Gpx gpx = loadGpx("route.gpx");
    FeatureCollection featureCollection = gpxToFeaturesConverter
        .convert(gpx, new GpxImportSettings());
    Assertions.assertNotNull(featureCollection);
    Assertions.assertNotNull(featureCollection.getFeatures());
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
    FeatureCollection featureCollection = gpxToFeaturesConverter
        .convert(gpx, new GpxImportSettings());
    Assertions.assertNotNull(featureCollection);
    Assertions.assertNotNull(featureCollection.getFeatures());
    String json = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(featureCollection);
    System.out.println("Track: GPX -> GeoJson");
    System.out.println(json);
    System.out.println("---------------------");
  }

  @Test
  public void gpxWaypointToFeatureCollection() throws Exception {
    Gpx gpx = loadGpx("waypoint.gpx");
    FeatureCollection featureCollection = gpxToFeaturesConverter
        .convert(gpx, new GpxImportSettings());
    Assertions.assertNotNull(featureCollection);
    Assertions.assertNotNull(featureCollection.getFeatures());
    String json = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(featureCollection);
    System.out.println("Waypoint: GPX -> GeoJson");
    System.out.println(json);
    System.out.println("------------------------");
  }

  @Disabled
  @Test
  public void rteFeaturesToGpx() throws Exception {
    FeatureCollection featureCollection = loadFeatureCollection("route.json");
    Gpx gpx = featuresToGpxConverter.convert(
        featureCollection.getFeatures(),
        new GpxExportSettings());
    System.out.println("Route: GeoJson -> GPX");
    jaxbContextBuilder
        .buildMarshaller(GarminJaxbContextDataProvider.GPX_NAMESPACES)
        .marshal(gpx, System.out);
    System.out.println("---------------------");
  }

  @Disabled
  @Test
  public void trkFeaturesToGpx() throws Exception {
    FeatureCollection featureCollection = loadFeatureCollection("track.json");
    Gpx gpx = featuresToGpxConverter.convert(
        featureCollection.getFeatures(),
        new GpxExportSettings());
    System.out.println("Track: GeoJson -> GPX");
    jaxbContextBuilder
        .buildMarshaller(GarminJaxbContextDataProvider.GPX_NAMESPACES)
        .marshal(gpx, System.out);
    System.out.println("---------------------");
  }

  @Disabled
  @Test
  public void wptFeaturesToGpx() throws Exception {
    FeatureCollection featureCollection = loadFeatureCollection("waypoint.json");
    Gpx gpx = featuresToGpxConverter.convert(
        featureCollection.getFeatures(),
        new GpxExportSettings());
    System.out.println("Wpt: GeoJson -> GPX");
    jaxbContextBuilder
        .buildMarshaller(GarminJaxbContextDataProvider.GPX_NAMESPACES)
        .marshal(gpx, System.out);
    System.out.println("-------------------");
  }
}