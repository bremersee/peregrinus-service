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
import org.bremersee.common.model.AccessControlList;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.common.model.TwoLetterCountryCode;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.model.DisplayColor;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteProperties;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.peregrinus.model.RtePtProperties;
import org.bremersee.peregrinus.model.RteSettings;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.TrkProperties;
import org.bremersee.peregrinus.model.TrkSettings;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.WptProperties;
import org.bremersee.peregrinus.model.WptSettings;
import org.bremersee.peregrinus.entity.RteEntity;
import org.bremersee.peregrinus.entity.RteEntityProperties;
import org.bremersee.peregrinus.entity.RteEntitySettings;
import org.bremersee.peregrinus.entity.TrkEntity;
import org.bremersee.peregrinus.entity.TrkEntityProperties;
import org.bremersee.peregrinus.entity.WptEntity;
import org.bremersee.peregrinus.entity.WptEntityProperties;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.BranchSettings;
import org.bremersee.peregrinus.model.FeatureLeaf;
import org.bremersee.peregrinus.model.FeatureLeafSettings;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.bremersee.peregrinus.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.entity.FeatureLeafEntitySettings;
import org.bremersee.security.access.Ace;
import org.bremersee.security.access.Acl;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
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

  private static final String rootBranchId = "b0";
  private static final String rootBranchSettingsId = "bs0";

  private static final String childBranchId = "b0.1";
  private static final String childBranchSettingsId = "bs0.1";

  private static final String wptId = "f0";
  private static final String wptSettingsId = "fs0";
  private static final String wptLeafId = "fl0";

  private static final String trkId = "f1";
  private static final String trkSettingsId = "fs1";
  private static final String trkLeafId = "fl1";

  private static final String rteId = "f2";
  private static final String rteSettingsId = "fs2";
  private static final String rteLeafId = "fl2";

  public static Branch rootBranch() {
    return Branch.builder()
        .acl(accessControlDto(ANNA))
        .children(Arrays.asList(wptLeaf(), childBranch()))
        .createdBy(ANNAS_FRIEND)
        .id(rootBranchId)
        .modifiedBy(ANNA)
        .name("Root")
        .settings(BranchSettings.builder()
            .id(rootBranchSettingsId)
            .nodeId(rootBranchId)
            .userId(ANNA)
            .build())
        .build();
  }

  public static BranchEntity rootBranchEntity() {
    final Branch root = rootBranch();
    return BranchEntity.builder()
        .acl(accessControlEntity(root.getAcl()))
        .created(root.getCreated())
        .createdBy(root.getCreatedBy())
        .modified(root.getModified())
        .modifiedBy(root.getModifiedBy())
        .name(root.getName())
        .parentId(root.getParentId())
        .build();
  }

  public static BranchEntitySettings rootBranchEntitySettings() {
    final Branch root = rootBranch();
    return BranchEntitySettings.builder()
        .id(root.getSettings().getId())
        .nodeId(root.getId())
        .userId(root.getSettings().getUserId())
        .open(root.getSettings().getOpen())
        .build();
  }

  public static Branch childBranch() {
    return Branch.builder()
        .acl(accessControlDto(ANNA))
        .createdBy(ANNA)
        .id(childBranchId)
        .modifiedBy(ANNA)
        .name("Child branch")
        .parentId(rootBranchId)
        .settings(BranchSettings.builder()
            .id(childBranchSettingsId)
            .nodeId(childBranchId)
            .open(false)
            .userId(ANNA)
            .build())
        .build();
  }

  public static Wpt wpt() {
    return Wpt.builder()
        .geometry(GeometryUtils.createPointWGS84(52.1, 10.2))
        .id(wptId)
        .properties(WptProperties.builder()
            .acl(accessControlDto(ANNAS_FRIEND))
            .address(address())
            .ele(BigDecimal.valueOf(123.4))
            .internalComments("An internal comment")
            .links(links())
            .markdownDescription("# My Test Waypoint\n\nThis way point is not very interesting.")
            .name("My test wpt")
            .phoneNumbers(phoneNumbers())
            .plainTextDescription("Imported from garmin ...")
            .settings(WptSettings.builder()
                .id(wptSettingsId)
                .featureId(wptId)
                .userId(ANNAS_FRIEND)
                .build())
            .build())
        .build();
  }

  public static WptEntity wptEntity() {
    final Wpt wpt = wpt();
    return WptEntity.builder()
        .geometry(wpt.getGeometry())
        .id(wpt.getId())
        .properties(WptEntityProperties.builder()
            .acl(accessControlEntity(wpt.getProperties().getAcl()))
            .address(wpt.getProperties().getAddress())
            .created(wpt.getProperties().getCreated())
            .ele(wpt.getProperties().getEle())
            .internalComments(wpt.getProperties().getInternalComments())
            .links(wpt.getProperties().getLinks())
            .markdownDescription(wpt.getProperties().getMarkdownDescription())
            .modified(wpt.getProperties().getModified())
            .name(wpt.getProperties().getName())
            .phoneNumbers(wpt.getProperties().getPhoneNumbers())
            .plainTextDescription(wpt.getProperties().getPlainTextDescription())
            .build())
        .build();
  }

  public static FeatureLeaf wptLeaf() {
    final Wpt wpt = wpt();
    return FeatureLeaf.builder()
        .acl(wpt.getProperties().getAcl())
        .createdBy(wpt.getProperties().getSettings().getUserId())
        .feature(wpt)
        .id(wptLeafId)
        .modifiedBy(wpt.getProperties().getSettings().getUserId())
        .name(wpt.getProperties().getName())
        .parentId(rootBranchId)
        .settings(FeatureLeafSettings.builder()
            .id(wpt.getProperties().getSettings().getId())
            .displayedOnMap(true)
            .nodeId(wptLeafId)
            .userId(wpt.getProperties().getSettings().getUserId())
            .build())
        .build();
  }

  public static FeatureLeafEntity wptLeafEntity() {
    final Wpt wpt = wpt();
    final FeatureLeaf featureLeaf = wptLeaf();
    return FeatureLeafEntity.builder()
        .acl(accessControlEntity(featureLeaf.getAcl()))
        .createdBy(wpt.getProperties().getSettings().getUserId())
        .featureId(wpt.getId())
        .id(featureLeaf.getId())
        .modifiedBy(wpt.getProperties().getSettings().getUserId())
        .parentId(featureLeaf.getParentId())
        .build();
  }

  public static FeatureLeafEntitySettings wptLeafEntitySettings() {
    final FeatureLeaf wptLeaf = wptLeaf();
    return FeatureLeafEntitySettings.builder()
        .displayedOnMap(wptLeaf.getSettings().getDisplayedOnMap())
        .id(wptLeaf.getSettings().getId())
        .nodeId(wptLeaf.getSettings().getNodeId())
        .userId(wptLeaf.getSettings().getUserId())
        .build();
  }

  public static Trk trk() {
    return Trk.builder()
        .bbox(GeometryUtils.getBoundingBox(trkMultiLineString()))
        .geometry(trkMultiLineString())
        .id(trkId)
        .properties(TrkProperties.builder()
            .acl(accessControlDto(ANNA))
            .eleLines(trkEleLines())
            .internalComments("Try this track!")
            .name("My test track")
            .settings(TrkSettings.builder()
                .displayColor(DisplayColor.DARK_RED)
                .featureId(trkId)
                .id(trkSettingsId)
                .userId(ANNA)
                .build())
            .startTime(trkStartTime())
            .stopTime(trkStopTime())
            .timeLines(trkTimeLines())
            .build())
        .build();
  }

  public static TrkEntity trkEntity() {
    final Trk trk = trk();
    return TrkEntity.builder()
        .bbox(trk.getBbox())
        .geometry(trk.getGeometry())
        .id(trk.getId())
        .properties(TrkEntityProperties.builder()
            .acl(accessControlEntity(trk.getProperties().getAcl()))
            .created(trk.getProperties().getCreated())
            .eleLines(trk.getProperties().getEleLines())
            .internalComments(trk.getProperties().getInternalComments())
            .links(trk.getProperties().getLinks())
            .markdownDescription(trk.getProperties().getMarkdownDescription())
            .modified(trk.getProperties().getModified())
            .name(trk.getProperties().getName())
            .plainTextDescription(trk.getProperties().getPlainTextDescription())
            .startTime(trk.getProperties().getStartTime())
            .stopTime(trk.getProperties().getStopTime())
            .timeLines(trk.getProperties().getTimeLines())
            .build())
        .build();
  }

  public static FeatureLeaf trkLeaf() {
    final Trk trk = trk();
    return FeatureLeaf.builder()
        .acl(trk.getProperties().getAcl())
        .createdBy(trk.getProperties().getSettings().getUserId())
        .feature(trk)
        .id(trkLeafId)
        .modifiedBy(trk.getProperties().getSettings().getUserId())
        .name(trk.getProperties().getName())
        .parentId(childBranchId)
        .settings(FeatureLeafSettings.builder()
            .id(trkSettingsId)
            .displayedOnMap(false)
            .nodeId(trkLeafId)
            .userId(trk.getProperties().getSettings().getUserId())
            .build())
        .build();
  }

  public static FeatureLeafEntity trkLeafEntity() {
    final Trk trk = trk();
    final FeatureLeaf featureLeaf = trkLeaf();
    return FeatureLeafEntity.builder()
        .acl(accessControlEntity(featureLeaf.getAcl()))
        .createdBy(trk.getProperties().getSettings().getUserId())
        .featureId(trk.getId())
        .id(featureLeaf.getId())
        .modifiedBy(trk.getProperties().getSettings().getUserId())
        .parentId(featureLeaf.getParentId())
        .build();
  }

  public static FeatureLeafEntitySettings trkLeafEntitySettings() {
    FeatureLeaf trkLeaf = trkLeaf();
    return FeatureLeafEntitySettings.builder()
        .displayedOnMap(trkLeaf.getSettings().getDisplayedOnMap())
        .id(trkSettingsId)
        .nodeId(trkLeaf.getSettings().getNodeId())
        .userId(trkLeaf.getSettings().getUserId())
        .build();
  }

  public static Rte rte() {
    return Rte.builder()
        .bbox(GeometryUtils.getBoundingBox(rteMultiLineString()))
        .geometry(rteMultiLineString())
        .id(rteId)
        .properties(RteProperties.builder()
            .acl(accessControlDto(ANNA))
            .name("My test route")
            .rtePts(rtePts())
            .settings(RteSettings.builder()
                .displayColor(DisplayColor.LIGHT_GRAY)
                .featureId(rteId)
                .id(rteSettingsId)
                .userId(ANNA)
                .build())
            .build())
        .build();
  }

  public static RteEntity rteEntity() {
    final Rte rte = rte();
    return RteEntity.builder()
        .bbox(rte.getBbox())
        .geometry(rte.getGeometry())
        .id(rte.getId())
        .properties(RteEntityProperties.builder()
            .acl(accessControlEntity(rte.getProperties().getAcl()))
            .created(rte.getProperties().getCreated())
            .internalComments(rte.getProperties().getInternalComments())
            .links(rte.getProperties().getLinks())
            .markdownDescription(rte.getProperties().getMarkdownDescription())
            .modified(rte.getProperties().getModified())
            .name(rte.getProperties().getName())
            .plainTextDescription(rte.getProperties().getPlainTextDescription())
            .rtePts(rte.getProperties().getRtePts())
            .build())
        .build();
  }

  public static RteEntitySettings rteEntitySettings() {
    final Rte rte = rte();
    return RteEntitySettings.builder()
        .displayColor(rte.getProperties().getSettings().getDisplayColor())
        .featureId(rte.getId())
        .id(rte.getProperties().getSettings().getId())
        .userId(rte.getProperties().getSettings().getUserId())
        .build();
  }

  public static FeatureLeaf rteLeaf() {
    final Rte rte = rte();
    return FeatureLeaf.builder()
        .acl(rte.getProperties().getAcl())
        .createdBy(rte.getProperties().getSettings().getUserId())
        .feature(rte)
        .id(rteLeafId)
        .modifiedBy(rte.getProperties().getSettings().getUserId())
        .name(rte.getProperties().getName())
        .parentId(childBranchId)
        .settings(FeatureLeafSettings.builder()
            .id(rteSettingsId)
            .displayedOnMap(false)
            .nodeId(rteLeafId)
            .userId(rte.getProperties().getSettings().getUserId())
            .build())
        .build();
  }

  public static FeatureLeafEntity rteLeafEntity() {
    final Rte rte = rte();
    final FeatureLeaf featureLeaf = rteLeaf();
    return FeatureLeafEntity.builder()
        .acl(accessControlEntity(featureLeaf.getAcl()))
        .createdBy(rte.getProperties().getSettings().getUserId())
        .featureId(rte.getId())
        .id(featureLeaf.getId())
        .modifiedBy(rte.getProperties().getSettings().getUserId())
        .parentId(featureLeaf.getParentId())
        .build();
  }

  public static FeatureLeafEntitySettings rteLeafEntitySettings() {
    FeatureLeaf rteLeaf = rteLeaf();
    return FeatureLeafEntitySettings.builder()
        .displayedOnMap(rteLeaf.getSettings().getDisplayedOnMap())
        .id(rteSettingsId)
        .nodeId(rteLeaf.getSettings().getNodeId())
        .userId(rteLeaf.getSettings().getUserId())
        .build();
  }


  public static AccessControlList accessControlDto(String owner) {
    return AclBuilder.builder()
        .defaults(PermissionConstants.ALL)
        .owner(owner)
        .addUser(owner, PermissionConstants.ALL)
        .addGroup("friends", PermissionConstants.READ, PermissionConstants.WRITE)
        .addRole("ROLE_AUDIT", PermissionConstants.READ, PermissionConstants.DELETE)
        .buildAccessControlList();
  }

  public static AccessControlList accessControlDto(Acl<? extends Ace> acl) {
    return AclBuilder.builder()
        .from(acl)
        .defaults(PermissionConstants.ALL)
        .removeAdminAccess()
        .buildAccessControlList();
  }

  public static AclEntity accessControlEntity(AccessControlList acl) {
    return AclBuilder.builder()
        .from(acl)
        .defaults(PermissionConstants.ALL)
        .ensureAdminAccess()
        .build(AclEntity::new);
  }

  public static Address address() {
    final Address address = new Address();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.setCountryCode(TwoLetterCountryCode.DE);
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

  private TestData() {
  }
}
