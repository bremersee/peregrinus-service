package org.bremersee.peregrinus.tree.repository.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.config.ModelMapperConfiguration;
import org.bremersee.peregrinus.content.model.DisplayColor;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RtePtProperties;
import org.bremersee.peregrinus.content.model.RteSettings;
import org.bremersee.peregrinus.content.model.Trk;
import org.bremersee.peregrinus.content.model.TrkProperties;
import org.bremersee.peregrinus.content.model.TrkSettings;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.peregrinus.content.model.WptSettings;
import org.bremersee.peregrinus.content.repository.FeatureRepository;
import org.bremersee.peregrinus.content.repository.entity.WptEntity;
import org.bremersee.peregrinus.content.repository.entity.WptEntityProperties;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.bremersee.peregrinus.tree.model.FeatureLeaf;
import org.bremersee.peregrinus.tree.model.FeatureLeafSettings;
import org.bremersee.peregrinus.tree.repository.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.tree.repository.entity.FeatureLeafEntitySettings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Christian Bremer
 */
public class FeatureLeafAdapterTest {

  private static final String ANNA = "anna";

  private static final String ANNAS_FRIEND = "stephan";

  private static FeatureLeafAdapter adapter;

  private static AccessControlDto accessControlDto(String owner) {
    AccessControlDto accessControl = new AccessControlDto();
    accessControl.setOwner(owner);
    accessControl.addUser(owner, PermissionConstants.ALL);
    accessControl.addGroup("friends", PermissionConstants.READ, PermissionConstants.WRITE);
    accessControl.addRole("ROLE_AUDIT", PermissionConstants.READ, PermissionConstants.DELETE);
    return accessControl;
  }

  private static AccessControlEntity accessControlEntity(AccessControl accessControl) {
    return new AccessControlEntity(accessControl.ensureAdminAccess());
  }

  private static Address address() {
    final Address address = new Address();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.setCountryCode(Locale.GERMANY.getCountry());
    address.setStreet("Mengstraße");
    address.setStreetNumber("4");
    address.setFormattedAddress("Mengstraße 4, 23552 Lübeck");
    return address;
  }

  private static List<Link> links() {
    final Link link0 = new Link();
    link0.setHref("http://example.org");
    final Link link1 = new Link();
    link1.setHref("https://bremersee.org");
    link1.setType("https");
    final Link link2 = new Link();
    link2.setHref("http://localhost:8080");
    link2.setText("A link");
    final Link link3 = new Link();
    link3.setHref("ftp://files.net");
    link3.setText("File share");
    link3.setType("ftp");
    return Arrays.asList(link0, link1, link2, link3);
  }

  private static List<PhoneNumber> phoneNumbers() {
    final PhoneNumber phoneNumber0 = new PhoneNumber();
    phoneNumber0.setValue("0123456789");
    final PhoneNumber phoneNumber1 = new PhoneNumber();
    phoneNumber1.setValue("0234567891");
    phoneNumber1.setCategory("Mobile");
    return Arrays.asList(phoneNumber0, phoneNumber1);
  }

  private static Wpt wpt() {
    return Wpt.builder()
        .geometry(GeometryUtils.createPointWGS84(52.1, 10.2))
        .id("333")
        .properties(WptProperties.builder()
            .accessControl(accessControlDto(ANNAS_FRIEND))
            .address(address())
            .ele(BigDecimal.valueOf(123.4))
            .internalComments("An internal comment")
            .links(links())
            .markdownDescription("# My Test Waypoint\n\nThis way point is not very interesting.")
            .name("Child leaf")
            .phoneNumbers(phoneNumbers())
            .plainTextDescription("Imported from garmin ...")
            .settings(WptSettings.builder()
                .id("3333")
                .featureId("333")
                .userId(ANNAS_FRIEND)
                .build())
            .build())
        .build();
  }

  private static WptEntity wptEntity() {
    return WptEntity.builder()
        .geometry(GeometryUtils.createPointWGS84(52.1, 10.2))
        .id("333")
        .properties(WptEntityProperties.builder()
            .accessControl(accessControlEntity(accessControlDto(ANNAS_FRIEND).ensureAdminAccess()))
            .address(address())
            .ele(BigDecimal.valueOf(123.4))
            .internalComments("An internal comment")
            .links(links())
            .markdownDescription("# My Test Waypoint\n\nThis way point is not very interesting.")
            .name("Child leaf")
            .phoneNumbers(phoneNumbers())
            .plainTextDescription("Imported from garmin ...")
            .build())
        .build();
  }

  private static MultiLineString trkMultiLineString() {
    return (MultiLineString) GeometryUtils.fromWKT(
        "MULTILINESTRING ("
            + "(0.000222 -1.0333, "
            + "-2.000009 -3.0001, "
            + "-4.000005 -5.000007), "
            + "(1.659999 -31.500006, "
            + "10.999902 3.0000004, "
            + "10.900004 1.100001, "
            + "0.000003 0.000032))");
  }

  private static List<List<BigDecimal>> trkEleLines() {
    final MultiLineString multiLineString = trkMultiLineString();
    final List<List<BigDecimal>> eleLines = new ArrayList<>();
    for (int n = 0; n < multiLineString.getNumGeometries(); n++) {
      List<BigDecimal> eleLine = new ArrayList<>();
      LineString lineString = (LineString) multiLineString.getGeometryN(n);
      //noinspection unused
      for (Coordinate coordinate : lineString.getCoordinates()) {
        eleLine.add(BigDecimal.valueOf(Math.random()));
      }
      eleLines.add(eleLine);
    }
    return eleLines;
  }

  private static List<List<OffsetDateTime>> trkTimeLines() {
    final MultiLineString multiLineString = trkMultiLineString();
    final OffsetDateTime start = OffsetDateTime.ofInstant(
        new Date(System.currentTimeMillis() - 1000L * 60L).toInstant(),
        ZoneId.of("Z"));
    final List<List<OffsetDateTime>> timeLines = new ArrayList<>();
    for (int n = 0; n < multiLineString.getNumGeometries(); n++) {
      List<OffsetDateTime> timeLine = new ArrayList<>();
      LineString lineString = (LineString) multiLineString.getGeometryN(n);
      //noinspection unused
      for (Coordinate coordinate : lineString.getCoordinates()) {
        timeLine.add(OffsetDateTime.now(Clock.systemUTC()));
      }
      timeLines.add(timeLine);
    }
    final OffsetDateTime stop = OffsetDateTime.ofInstant(
        new Date(System.currentTimeMillis() + 1000L * 60L).toInstant(),
        ZoneId.of("Z"));
    timeLines.get(0).set(0, start);
    final List<OffsetDateTime> lastLine = timeLines.get(timeLines.size() - 1);
    lastLine.set(lastLine.size() - 1, stop);
    return timeLines;
  }

  private static OffsetDateTime trkStartTime() {
    final List<List<OffsetDateTime>> timeLines = trkTimeLines();
    return timeLines.get(0).get(0);
  }

  private static OffsetDateTime trkStopTime() {
    final List<List<OffsetDateTime>> timeLines = trkTimeLines();
    final List<OffsetDateTime> last = timeLines.get(timeLines.size() - 1);
    return last.get(last.size() - 1);
  }

  private static TrkProperties trkProperties() {
    return TrkProperties.builder()
        .accessControl(accessControlDto(ANNA))
        .eleLines(trkEleLines())
        .internalComments("Try this track!")
        .name("My test track")
        .settings(TrkSettings.builder()
            .displayColor(DisplayColor.DARK_RED)
            .featureId("444")
            .id("4444")
            .userId(ANNA)
            .build())
        .startTime(trkStartTime())
        .stopTime(trkStopTime())
        .timeLines(trkTimeLines())
        .build();
  }

  private static Trk trk() {
    return Trk.builder()
        .bbox(GeometryUtils.getBoundingBox(trkMultiLineString()))
        .geometry(trkMultiLineString())
        .id("444")
        .properties(trkProperties())
        .build();
  }

  private static MultiLineString rteMultiLineString() {
    return (MultiLineString) GeometryUtils.fromWKT(
        "MULTILINESTRING ("
            + "(0.000222 -1.0333, "
            + "-2.000009 -3.0001, "
            + "-4.000005 -5.000007, "
            + "1.659999 -31.500006), "
            + "(1.659999 -31.500006, "
            + "10.999902 3.0000004, "
            + "10.900004 1.100001, "
            + "0.000003 0.000032))");
  }

  private static List<RtePt> rtePts() {
    return Arrays.asList(
        RtePt.builder()
            .geometry((Point) GeometryUtils.fromWKT("POINT (0.000222 -1.0333)"))
            .properties(RtePtProperties.builder()
                .address(address())
                .ele(BigDecimal.valueOf(123.45))
                .lengthInMeters(500)
                .links(links())
                .name("RtePt0")
                .phoneNumbers(Collections.singletonList(new PhoneNumber().value("4711")))
                .build())
            .build(),
        RtePt.builder()
            .geometry((Point) GeometryUtils.fromWKT("POINT (1.659999 -31.500006)"))
            .properties(RtePtProperties.builder()
                .name("RtePt1")
                .build())
            .build(),
        RtePt.builder()
            .geometry((Point) GeometryUtils.fromWKT("POINT (0.000003 0.000032)"))
            .properties(RtePtProperties.builder()
                .name("RtePt2")
                .build())
            .build());
  }

  private static RteProperties rteProperties() {
    return RteProperties.builder()
        .accessControl(accessControlDto(ANNA))
        .name("My test route")
        .rtePts(rtePts())
        .settings(RteSettings.builder()
            .displayColor(DisplayColor.LIGHT_GRAY)
            .featureId("555")
            .id("5555")
            .userId(ANNA).build())
        .build();
  }

  private static Rte rte() {
    return Rte.builder()
        .bbox(GeometryUtils.getBoundingBox(rteMultiLineString()))
        .geometry(rteMultiLineString())
        .id("555")
        .properties(rteProperties())
        .build();
  }

  @BeforeClass
  public static void setup() {
    FeatureRepository featureRepository = mock(FeatureRepository.class);
    when(
        featureRepository.updateNameAndAccessControl(
            anyString(),
            anyString(),
            any(AccessControlDto.class)))
        .thenReturn(Mono.just(Boolean.TRUE));
    when(
        featureRepository.findById(
            eq(wpt().getId()),
            anyString()))
        .thenReturn(Mono.just(wpt()));

    adapter = new FeatureLeafAdapter(
        new ModelMapperConfiguration().modelMapper(),
        featureRepository);
  }

  @Test
  public void getSupportedClasses() {
    final List<Class<?>> classes = Arrays.asList(adapter.getSupportedClasses());
    assertTrue(classes.contains(FeatureLeaf.class));
    assertTrue(classes.contains(FeatureLeafSettings.class));
    assertTrue(classes.contains(FeatureLeafEntity.class));
    assertTrue(classes.contains(FeatureLeafEntitySettings.class));
  }

  @Test
  public void mapWptNode() {
    final Wpt wpt = wpt();
    final FeatureLeaf featureLeaf = FeatureLeaf.builder()
        .accessControl(wpt.getProperties().getAccessControl())
        .createdBy(wpt.getProperties().getSettings().getUserId())
        .feature(wpt)
        .id("1")
        .modifiedBy(wpt.getProperties().getSettings().getUserId())
        .name(wpt.getProperties().getName())
        .parentId("x")
        .settings(FeatureLeafSettings.builder()
            .id("123")
            .displayedOnMap(true)
            .nodeId("1")
            .userId(wpt.getProperties().getSettings().getUserId())
            .build())
        .build();
    StepVerifier
        .create(adapter.mapNode(featureLeaf, wpt.getProperties().getSettings().getUserId()))
        .assertNext(tuple -> {
          assertNotNull(tuple);
          assertNotNull(tuple.getT1());
          assertNotNull(tuple.getT2());

          System.out.println("### Feature leaf entity");
          System.out.println(tuple.getT1());


        })
        //.expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void mapNodeSettings() {
  }

  @Test
  public void mapNodeEntity() {
  }

  @Test
  public void getNodeName() {
  }

  @Test
  public void mapNodeEntitySettings() {
  }

  @Test
  public void updateName() {
  }

  @Test
  public void updateAccessControl() {
  }

  @Test
  public void removeNode() {
  }

  @Test
  public void defaultSettings() {
  }
}