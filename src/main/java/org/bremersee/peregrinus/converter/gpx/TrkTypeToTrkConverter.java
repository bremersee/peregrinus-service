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

package org.bremersee.peregrinus.converter.gpx;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;
import org.bremersee.garmin.gpx.v3.model.ext.TrackExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.gpx.model.TrksegType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.DisplayColor;
import org.bremersee.peregrinus.content.model.Trk;
import org.bremersee.peregrinus.content.model.TrkProperties;
import org.bremersee.peregrinus.converter.XmlGregorianCalendarToOffsetDateTimeConverter;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

/**
 * The garmin trk (track) type to trk (track) converter.
 *
 * @author Christian Bremer
 */
class TrkTypeToTrkConverter extends AbstractGpxConverter {

  private static final XmlGregorianCalendarToOffsetDateTimeConverter timeConverter
      = new XmlGregorianCalendarToOffsetDateTimeConverter();

  private final JaxbContextBuilder jaxbContextBuilder;

  /**
   * Instantiates a new trk type to trk converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  TrkTypeToTrkConverter(final JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  /**
   * Convert trk type.
   *
   * @param trkType the trk type
   * @return the trk
   */
  Trk convert(final TrkType trkType) {

    final Tuple3<MultiLineString, List<List<BigDecimal>>, List<List<OffsetDateTime>>> tuple = convert(
        trkType.getTrksegs());

    final Trk trk = new Trk();
    trk.setGeometry(tuple.getT1());
    trk.setBbox(GeometryUtils.getBoundingBox(trk.getGeometry()));
    trk.setProperties(convertCommonGpxType(trkType, TrkProperties::new));
    trk.getProperties().setEleLines(tuple.getT2());
    trk.getProperties().setTimeLines(tuple.getT3());
    trk.getProperties().getSettings().setDisplayColor(getDisplayColor(trkType));
    trk.getProperties().setStartTime(getStartTime(trk.getProperties().getTimeLines()));
    trk.getProperties().setStopTime(getStopTime(trk.getProperties().getTimeLines()));
    return trk;
  }

  private OffsetDateTime getStartTime(final List<List<OffsetDateTime>> timeList) {
    return getTime(timeList, false);
  }

  private OffsetDateTime getStopTime(final List<List<OffsetDateTime>> timeList) {
    return getTime(timeList, true);
  }

  private OffsetDateTime getTime(final List<List<OffsetDateTime>> timeList, final boolean last) {
    if (timeList != null && !timeList.isEmpty()) {
      final int i0 = last ? timeList.size() - 1 : 0;
      final List<OffsetDateTime> list = timeList.get(i0);
      if (list != null && !list.isEmpty()) {
        final int i1 = last ? list.size() - 1 : 0;
        return list.get(i1);
      }
    }
    return null;
  }

  private Optional<TrackExtension> getTrackExtension(final TrkType trkType) {
    return GpxJaxbContextHelper.findFirstExtension(
        TrackExtension.class,
        true,
        trkType.getExtensions(),
        jaxbContextBuilder.buildUnmarshaller());
  }

  private DisplayColor getDisplayColor(final TrkType trkType) {
    return getTrackExtension(trkType)
        .map(TrackExtension::getDisplayColor)
        .map(this::getDisplayColor)
        .orElse(DisplayColor.DARK_GRAY);
  }

  private DisplayColor getDisplayColor(final DisplayColorT displayColorT) {
    return DisplayColor.findByGarminDisplayColor(displayColorT, DisplayColor.DARK_GRAY);
  }

  private Tuple3<MultiLineString, List<List<BigDecimal>>, List<List<OffsetDateTime>>> convert(
      final List<TrksegType> trkSegments) {

    final List<LineString> lineList = new ArrayList<>();
    final List<List<BigDecimal>> eleList = new ArrayList<>();
    final List<List<OffsetDateTime>> timeList = new ArrayList<>();
    trkSegments
        .stream()
        .filter(Objects::nonNull)
        .map(this::convert)
        .forEach(tuple -> {
          lineList.add(tuple.getT1());
          eleList.add(tuple.getT2());
          timeList.add(tuple.getT3());
        });
    return Tuples.of(GeometryUtils.createMultiLineString(lineList), eleList, timeList);
  }

  private Tuple3<LineString, List<BigDecimal>, List<OffsetDateTime>> convert(
      final TrksegType trkSegment) {

    final List<Coordinate> coordinates = new ArrayList<>();
    final List<BigDecimal> eleList = new ArrayList<>();
    final List<OffsetDateTime> timeList = new ArrayList<>();
    trkSegment.getTrkpts()
        .stream()
        .filter(Objects::nonNull)
        .map(this::convert)
        .forEach(tuple -> {
          coordinates.add(tuple.getT1());
          eleList.add(tuple.getT2());
          timeList.add(tuple.getT3());
        });
    return Tuples.of(GeometryUtils.createLineString(coordinates), eleList, timeList);
  }

  private Tuple3<Coordinate, BigDecimal, OffsetDateTime> convert(
      final WptType trkPt) {

    final Coordinate coordinate = GeometryUtils.createCoordinateWGS84(
        trkPt.getLat(), trkPt.getLon());
    final BigDecimal ele = trkPt.getEle() != null ? trkPt.getEle() : BigDecimal.valueOf(0);
    OffsetDateTime time = timeConverter.convert(trkPt.getTime());
    if (time == null) {
      time = OffsetDateTime.ofInstant(new Date(0).toInstant(), ZoneId.of("Z"));
    }
    return Tuples.of(coordinate, ele, time);
  }

}
