/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.peregrinus;

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
import org.bremersee.peregrinus.content.repository.entity.WptEntity;
import org.bremersee.peregrinus.content.repository.entity.WptEntityProperties;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.model.BranchSettings;
import org.bremersee.peregrinus.tree.model.FeatureLeaf;
import org.bremersee.peregrinus.tree.model.FeatureLeafSettings;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntity;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntitySettings;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;

/**
 * @author Christian Bremer
 */
public abstract class TestData {

  public static final String ANNA = "anna";

  public static final String ANNAS_FRIEND = "stephan";

  public static AccessControlDto accessControlDto(String owner) {
    AccessControlDto accessControl = new AccessControlDto();
    accessControl.setOwner(owner);
    accessControl.addUser(owner, PermissionConstants.ALL);
    accessControl.addGroup("friends", PermissionConstants.READ, PermissionConstants.WRITE);
    accessControl.addRole("ROLE_AUDIT", PermissionConstants.READ, PermissionConstants.DELETE);
    return accessControl;
  }

  public static AccessControlEntity accessControlEntity(AccessControl accessControl) {
    return new AccessControlEntity(accessControl.ensureAdminAccess());
  }

  public static Address address() {
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

  public static List<Link> links() {
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

  public static List<PhoneNumber> phoneNumbers() {
    final PhoneNumber phoneNumber0 = new PhoneNumber();
    phoneNumber0.setValue("0123456789");
    final PhoneNumber phoneNumber1 = new PhoneNumber();
    phoneNumber1.setValue("0234567891");
    phoneNumber1.setCategory("Mobile");
    return Arrays.asList(phoneNumber0, phoneNumber1);
  }

  public static Wpt wpt() {
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

  public static WptEntity wptEntity() {
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

  public static FeatureLeaf wptLeaf() {
    final Wpt wpt = wpt();
    return FeatureLeaf.builder()
        .accessControl(wpt.getProperties().getAccessControl())
        .createdBy(wpt.getProperties().getSettings().getUserId())
        .feature(wpt)
        .id("3")
        .modifiedBy(wpt.getProperties().getSettings().getUserId())
        .name(wpt.getProperties().getName())
        .parentId("1")
        .settings(FeatureLeafSettings.builder()
            .id("33")
            .displayedOnMap(true)
            .nodeId("3")
            .userId(wpt.getProperties().getSettings().getUserId())
            .build())
        .build();
  }

  public static MultiLineString trkMultiLineString() {
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

  public static List<List<BigDecimal>> trkEleLines() {
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

  public static List<List<OffsetDateTime>> trkTimeLines() {
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

  public static OffsetDateTime trkStartTime() {
    final List<List<OffsetDateTime>> timeLines = trkTimeLines();
    return timeLines.get(0).get(0);
  }

  public static OffsetDateTime trkStopTime() {
    final List<List<OffsetDateTime>> timeLines = trkTimeLines();
    final List<OffsetDateTime> last = timeLines.get(timeLines.size() - 1);
    return last.get(last.size() - 1);
  }

  public static TrkProperties trkProperties() {
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

  public static Trk trk() {
    return Trk.builder()
        .bbox(GeometryUtils.getBoundingBox(trkMultiLineString()))
        .geometry(trkMultiLineString())
        .id("444")
        .properties(trkProperties())
        .build();
  }

  public static FeatureLeaf trkLeaf() {
    final Trk trk = trk();
    return FeatureLeaf.builder()
        .accessControl(trk.getProperties().getAccessControl())
        .createdBy(trk.getProperties().getSettings().getUserId())
        .feature(trk)
        .id("4")
        .modifiedBy(trk.getProperties().getSettings().getUserId())
        .name(trk.getProperties().getName())
        .parentId("1")
        .settings(FeatureLeafSettings.builder()
            .id("44")
            .displayedOnMap(false)
            .nodeId("4")
            .userId(trk.getProperties().getSettings().getUserId())
            .build())
        .build();
  }

  public static MultiLineString rteMultiLineString() {
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

  public static List<RtePt> rtePts() {
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

  public static RteProperties rteProperties() {
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

  public static Rte rte() {
    return Rte.builder()
        .bbox(GeometryUtils.getBoundingBox(rteMultiLineString()))
        .geometry(rteMultiLineString())
        .id("555")
        .properties(rteProperties())
        .build();
  }

  public static FeatureLeaf rteLeaf() {
    final Rte rte = rte();
    return FeatureLeaf.builder()
        .accessControl(rte.getProperties().getAccessControl())
        .createdBy(rte.getProperties().getSettings().getUserId())
        .feature(rte)
        .id("5")
        .modifiedBy(rte.getProperties().getSettings().getUserId())
        .name(rte.getProperties().getName())
        .parentId("1")
        .settings(FeatureLeafSettings.builder()
            .id("55")
            .displayedOnMap(false)
            .nodeId("5")
            .userId(rte.getProperties().getSettings().getUserId())
            .build())
        .build();
  }

  public static Branch childBranch() {
    return Branch.builder()
        .accessControl(accessControlDto(ANNA))
        .createdBy(ANNA)
        .id("2")
        .modifiedBy(ANNA)
        .name("Child branch")
        .parentId("1")
        .settings(BranchSettings.builder().id("22").nodeId("2").open(false).userId(ANNA).build())
        .build();
  }

  public static Branch rootBranch() {
    return Branch.builder()
        .accessControl(accessControlDto(ANNA))
        .children(Arrays.asList(wptLeaf(), childBranch()))
        .createdBy(ANNAS_FRIEND)
        .id("1")
        .modifiedBy(ANNA)
        .name("rootBranch")
        .settings(BranchSettings.builder().id("11").nodeId("1").userId(ANNA).build())
        .build();
  }

  public static BranchEntity rootBranchEntity() {
    return BranchEntity.builder()
        .accessControl(accessControlEntity(accessControlDto(ANNAS_FRIEND)))
        .createdBy(ANNA)
        .modifiedBy(ANNAS_FRIEND)
        .name("child")
        .parentId("1")
        .build();
  }

  public static BranchEntitySettings rootBranchEntitySettings() {
    return BranchEntitySettings.builder()
        .id("11")
        .nodeId("1")
        .userId(ANNAS_FRIEND)
        .open(Boolean.FALSE)
        .build();
  }

  private TestData() {
  }
}
