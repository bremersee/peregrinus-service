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

package org.bremersee.peregrinus.geo.mapper.gpx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;
import org.bremersee.garmin.gpx.v3.model.ext.TrackExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.gpx.model.TrksegType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.geo.model.DisplayColor;
import org.bremersee.peregrinus.geo.model.Trk;
import org.bremersee.peregrinus.geo.model.TrkProperties;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

/**
 * @author Christian Bremer
 */
class TrkMapper extends AbstractGpxMapper {

  TrkMapper(JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  List<Trk> readTrkTypes(final List<TrkType> trks) {
    final List<Trk> trkList = new ArrayList<>();
    if (trks != null) {
      for (final TrkType trkType : trks) {
        if (trkType != null) {
          trkList.add(readTrkType(trkType));
        }
      }
    }
    return trkList;
  }

  private Trk readTrkType(final TrkType trkType) {

    final Trk trk = new Trk();
    trk.setProperties(readCommonData(
        TrkProperties::new,
        trkType.getName(),
        trkType.getDesc(),
        trkType.getCmt(),
        trkType.getLinks()));

    final Optional<TrackExtension> trkExt = GpxJaxbContextHelper.findFirstExtension(
        TrackExtension.class,
        true,
        trkType.getExtensions(),
        getUnmarshaller());

    final DisplayColorT displayColor = trkExt.map(TrackExtension::getDisplayColor).orElse(null);
    trk.getProperties().setDisplayColor(DisplayColor.findByGarminDisplayColor(
        displayColor,
        DisplayColor.DARK_GRAY));

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
      trk.setBbox(GeometryUtils.getBoundingBox(trk.getGeometry()));
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
        final XMLGregorianCalendar cal = wpt.getTime();
        final Date time = cal != null ? cal.toGregorianCalendar().getTime() : null;
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
          final XMLGregorianCalendar cal = wpt.getTime();
          final Date time = cal != null ? cal.toGregorianCalendar().getTime() : null;
          if (time != null) {
            return time;
          }
        }
      }
    }
    return new Date((0L));
  }

}
