/*
 * Copyright 2018 the original author or authors.
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

package org.bremersee.peregrinus.geo.converter.gpx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.LongAndShort;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.bremersee.garmin.gpx.v3.model.ext.CategoriesT;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.garmin.gpx.v3.model.ext.TrackExtension;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.LinkType;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.gpx.model.TrksegType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeature;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeatureProperties;
import org.bremersee.peregrinus.geo.model.Rte;
import org.bremersee.peregrinus.geo.model.RteProperties;
import org.bremersee.peregrinus.geo.model.RteSegment;
import org.bremersee.peregrinus.geo.model.Trk;
import org.bremersee.peregrinus.geo.model.TrkProperties;
import org.bremersee.peregrinus.geo.model.Wpt;
import org.bremersee.peregrinus.geo.model.WptProperties;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@Component
@SuppressWarnings("WeakerAccess")
public class GpxConverterImpl implements GpxConverter {

  private final JaxbContextBuilder jaxbContextBuilder;

  @Autowired
  public GpxConverterImpl(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  protected Unmarshaller getUnmarshaller() {
    return jaxbContextBuilder.buildUnmarshaller();
  }

  /*
  protected Point createPoint(BigDecimal lon, BigDecimal lat) {
    return createPoint(lon != null ? lon.doubleValue() : 0., lat != null ? lat.doubleValue() : 0.);
  }

  protected Point createPoint(double lon, double lat) {
    return new Point(lon, lat);
  }
  */

  @Override
  public GpxConverterResult parseGpx(final Gpx gpx) {

    /*
    MetadataType metadata = gpx.getMetadata();
    PersonType person = metadata.getAuthor(); // name, email, link
    BoundsType bounds = metadata.getBounds(); // min/max Lat/Lon
    CopyrightType copyright = metadata.getCopyright(); // author, license, year
    metadata.getDesc(); // description
    metadata.getKeywords(); // keywords
    metadata.getLinks(); // link list
    metadata.getName(); // name
    metadata.getTime(); // time: 2018-10-27T14:40:01Z
    gpx.getCreator(); // creator="Garmin Desktop App"
    gpx.getVersion(); //version="1.1"
    */

    final List<AbstractGeoJsonFeature> features = new ArrayList<>(parseWpts(gpx.getWpts()));
    features.addAll(parseTrks(gpx.getTrks()));
    features.addAll(parseRtes(gpx.getRtes()));

    final GpxConverterResult result = new GpxConverterResult();
    result.setFeatures(features);
    return result;
  }

  protected Address parseAddress(final AddressT addressType) {
    if (addressType == null) {
      return null;
    }
    final Address address = new Address();
    if (StringUtils.hasText(addressType.getCity())) {
      address.setLocality(new LongAndShort().longName(addressType.getCity()));
    }
    if (StringUtils.hasText(addressType.getCountry())) {
      address.setCountry(new LongAndShort().longName(addressType.getCountry()));
    }
    address.setPostalCode(addressType.getPostalCode());
    if (StringUtils.hasText(addressType.getState())) {
      address.setLocality(new LongAndShort().longName(addressType.getState()));
    }
    if (addressType.getStreetAddresses() != null && !addressType.getStreetAddresses().isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (final String line : addressType.getStreetAddresses()) {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append(line);
      }
      address.setRoute(sb.toString());
    }
    return address;
  }

  protected List<String> parseCategories(final CategoriesT categoriesType) {
    if (categoriesType == null
        || categoriesType.getCategories() == null
        || categoriesType.getCategories().isEmpty()) {
      return null;
    }
    return categoriesType.getCategories();
  }

  protected String parseDescription(final String desc, final String cmt) {
    final String a = StringUtils.hasText(desc) ? desc : "";
    final String b = StringUtils.hasText(cmt) ? cmt : "";
    final StringBuilder sb = new StringBuilder();
    sb.append(a);
    if (!b.equals(a) && b.length() > 0) {
      if (a.length() > 0) {
        sb.append("\n---\n");
      }
      sb.append(b);
    }
    return sb.length() > 0 ? sb.toString() : null;
  }

  protected Optional<Link> parseLink(final LinkType linkType) {
    if (linkType == null || !StringUtils.hasText(linkType.getHref())) {
      return Optional.empty();
    }
    return Optional.of(
        new Link()
            .href(linkType.getHref())
            .type(linkType.getType())
            .text(linkType.getText()));
  }

  protected List<Link> parseLinks(final List<? extends LinkType> linkTypes) {

    if (linkTypes == null || linkTypes.isEmpty()) {
      return null;
    }
    final List<Link> links = new ArrayList<>(linkTypes.size());
    for (LinkType linkType : linkTypes) {
      Optional<Link> link = parseLink(linkType);
      link.ifPresent(links::add);
    }
    return links;
  }

  protected Optional<PhoneNumber> parsePhoneNumber(final PhoneNumberT phoneNumberType) {
    if (phoneNumberType == null || !StringUtils.hasText(phoneNumberType.getValue())) {
      return Optional.empty();
    }
    return Optional.of(
        new PhoneNumber()
            .value(phoneNumberType.getValue())
            .category(phoneNumberType.getCategory()));
  }

  protected List<PhoneNumber> parsePhoneNumbers(List<? extends PhoneNumberT> phoneNumberTypes) {

    if (phoneNumberTypes == null || phoneNumberTypes.isEmpty()) {
      return null;
    }
    final List<PhoneNumber> phoneNumbers = new ArrayList<>();
    for (PhoneNumberT phoneNumberType : phoneNumberTypes) {
      Optional<PhoneNumber> link = parsePhoneNumber(phoneNumberType);
      link.ifPresent(phoneNumbers::add);
    }
    return phoneNumbers;
  }

  protected Date parseXmlGregorianCalendar(final XMLGregorianCalendar cal) {
    if (cal == null) {
      return null;
    }
    return cal.toGregorianCalendar().getTime();
  }

  protected void parse(
      @NotNull final AbstractGeoJsonFeatureProperties geoJsonProperties,
      final String name,
      final String desc,
      final String cmt,
      final List<? extends LinkType> links) {

    geoJsonProperties.setCreated(new Date());
    geoJsonProperties.setModified(geoJsonProperties.getCreated());

    geoJsonProperties.setName(name);
    geoJsonProperties.setPlainTextDescription(parseDescription(desc, cmt));
    geoJsonProperties.setMarkdownDescription(geoJsonProperties.getPlainTextDescription());
    geoJsonProperties.setLinks(parseLinks(links));
  }

  protected List<Wpt> parseWpts(final List<WptType> wpts) {
    final List<Wpt> wptList = new ArrayList<>();
    if (wpts == null) {
      return wptList;
    }
    for (final WptType wptType : wpts) {
      if (wptType != null) {
        wptList.add(parseWpt(wptType));
      }
    }
    return wptList;
  }

  protected Wpt parseWpt(final WptType wptType) {

    final Wpt wpt = new Wpt();
    wpt.setGeometry(GeometryUtils.createPointWGS84(wptType.getLat(), wptType.getLon()));
    wpt.setBbox(GeometryUtils.getBoundingBox(wpt.getGeometry()));

    final WptProperties geoJsonProperties = new WptProperties();
    wpt.setProperties(geoJsonProperties);

    parse(
        geoJsonProperties,
        wptType.getName(),
        wptType.getDesc(),
        wptType.getCmt(),
        wptType.getLinks());

    final Optional<WaypointExtension> wptExt = GpxJaxbContextHelper.findFirstExtension(
        WaypointExtension.class,
        true,
        wptType.getExtensions(),
        getUnmarshaller());

    geoJsonProperties.setAddress(wptExt.map(ext -> parseAddress(ext.getAddress())).orElse(null));
    geoJsonProperties.setEle(wptType.getEle());
    geoJsonProperties.setPhoneNumbers(
        wptExt.map(ext -> parsePhoneNumbers(ext.getPhoneNumbers())).orElse(null));

    if (GarminType.PHOTO.equals(wptType.getType())) {
      GpxJaxbContextHelper.findFirstExtension(
          CreationTimeExtension.class,
          true,
          wptType.getExtensions(),
          getUnmarshaller()).ifPresent(ext -> {
        geoJsonProperties.setStartTime(parseXmlGregorianCalendar(ext.getCreationTime()));
        geoJsonProperties.setStopTime(parseXmlGregorianCalendar(ext.getCreationTime()));
      });
    }

    return wpt;
  }

  protected List<Trk> parseTrks(final List<TrkType> trks) {
    final List<Trk> trkList = new ArrayList<>();
    if (trks != null) {
      for (final TrkType trkType : trks) {
        if (trkType != null) {
          trkList.add(parseTrk(trkType));
        }
      }
    }
    return trkList;
  }

  protected Trk parseTrk(final TrkType trkType) {
    final Optional<TrackExtension> trkExt = GpxJaxbContextHelper.findFirstExtension(
        TrackExtension.class,
        true,
        trkType.getExtensions(),
        getUnmarshaller());

    final Trk trk = new Trk();
    final TrkProperties geoJsonProperties = new TrkProperties();
    trk.setProperties(geoJsonProperties);

    parse(
        geoJsonProperties,
        trkType.getName(),
        trkType.getDesc(),
        trkType.getCmt(),
        trkType.getLinks());

    // TODO parse
    //garminData.setGarminDisplayColor(trkExt.map(TrackExtension::getDisplayColor).orElse(null));

    parseTrkSegments(trkType.getTrksegs(), trk);

    return trk;
  }

  @SuppressWarnings("Duplicates")
  private void parseTrkSegments(List<TrksegType> trkSegments, final Trk trk) {
    if (trkSegments == null || trkSegments.isEmpty()) {
      return;
    }
    final List<LineString> geoLines = new ArrayList<>(trkSegments.size());
    final List<List<BigDecimal>> eleLines = new ArrayList<>(trkSegments.size());
    final List<List<Date>> timeLines = new ArrayList<>(trkSegments.size());
    for (final TrksegType trksegType : trkSegments) {
      final LineString geoLine = parseTrkPoints(trksegType.getTrkpts(), eleLines, timeLines);
      if (geoLine != null) {
        geoLines.add(geoLine);
      }
    }
    if (!geoLines.isEmpty()) {
      trk.setGeometry(GeometryUtils.createMultiLineString(geoLines));
      trk.getProperties().setEleLines(eleLines);
      trk.getProperties().setTimeLines(timeLines);
      final Date start = timeLines.get(0).get(0);
      final List<Date> lastTimeLine = timeLines.get(timeLines.size() - 1);
      final Date stop = lastTimeLine.get(lastTimeLine.size() - 1);
      trk.getProperties().setStartTime(start);
      trk.getProperties().setStopTime(stop);
    }
  }

  @SuppressWarnings("Duplicates")
  private LineString parseTrkPoints(
      final List<WptType> wpts,
      final List<List<BigDecimal>> eleLines,
      final List<List<Date>> timeLines) {

    if (wpts == null || wpts.size() < 2) {
      return null;
    }
    final List<Coordinate> points = new ArrayList<>(wpts.size());
    final List<BigDecimal> eleLine = new ArrayList<>(wpts.size());
    final List<Date> timeLine = new ArrayList<>(wpts.size());
    BigDecimal lastEle = findFirstEle(wpts);
    Date lastTime = findFirstTime(wpts);
    for (final WptType wpt : wpts) {
      if (wpt != null && wpt.getLon() != null && wpt.getLat() != null) {
        final Date time = parseXmlGregorianCalendar(wpt.getTime());
        lastTime = time != null ? time : lastTime;
        lastEle = wpt.getEle() != null ? wpt.getEle() : lastEle;
        points.add(GeometryUtils.createCoordinate(wpt.getLon(), wpt.getLat()));
        eleLine.add(lastEle);
        timeLine.add(lastTime);
      }
    }
    if (points.size() < 2) {
      return null;
    }
    eleLines.add(eleLine);
    timeLines.add(timeLine);
    return GeometryUtils.createLinearRing(points);
  }

  private BigDecimal findFirstEle(final List<WptType> wpts) {
    if (wpts != null) {
      for (final WptType wpt : wpts) {
        if (wpt != null) {
          final BigDecimal ele = wpt.getEle();
          if (ele != null) {
            return ele;
          }
        }
      }
    }
    return new BigDecimal("0");
  }

  private Date findFirstTime(final List<WptType> wpts) {
    if (wpts != null) {
      for (final WptType wpt : wpts) {
        if (wpt != null) {
          final Date time = parseXmlGregorianCalendar(wpt.getTime());
          if (time != null) {
            return time;
          }
        }
      }
    }
    return new Date((0L));
  }

  protected List<Rte> parseRtes(final List<RteType> rteTypes) {
    final List<Rte> rteList = new ArrayList<>();
    if (rteTypes != null) {
      for (final RteType rteType : rteTypes) {
        if (rteType != null) {
          rteList.add(parseRte(rteType));
        }
      }
    }
    return rteList;
  }

  protected Rte parseRte(final RteType rteType) {

    // display color
    final Optional<RouteExtension> rteTypeExt = GpxJaxbContextHelper.findFirstExtension(
        RouteExtension.class,
        true,
        rteType.getExtensions(),
        getUnmarshaller());

    final Rte rte = new Rte();
    final RteProperties geoJsonProperties = new RteProperties();
    rte.setProperties(geoJsonProperties);

    parse(
        geoJsonProperties,
        rteType.getName(),
        rteType.getDesc(),
        rteType.getCmt(),
        rteType.getLinks());

    List<WptType> rtePts = rteType.getRtepts();

    return rte;
  }

  private List<RteSegment> parseRtePts(final List<WptType> rtePts) {
    final List<RteSegment> rteSegments = new ArrayList<>();

    return rteSegments;
  }

  private RteSegment parseRtePt(final WptType rtePt) {
    final RteSegment rteSegment = new RteSegment();

    return rteSegment;
  }

}
