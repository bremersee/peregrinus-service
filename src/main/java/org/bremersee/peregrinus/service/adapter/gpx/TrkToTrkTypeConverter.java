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

package org.bremersee.peregrinus.service.adapter.gpx;

import static org.bremersee.xml.ConverterUtils.offsetDateTimeToXmlCalendarUtc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.bremersee.garmin.gpx.v3.model.ext.TrackExtension;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.gpx.model.TrksegType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

/**
 * The trk (track) to gpx trk (track) type converter.
 *
 * @author Christian Bremer
 */
class TrkToTrkTypeConverter extends AbstractFeatureConverter {

  private String[] gpxNameSpaces;

  private final JaxbContextBuilder jaxbContextBuilder;

  /**
   * Instantiates a new trk to trk type converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  TrkToTrkTypeConverter(final JaxbContextBuilder jaxbContextBuilder, String... gpxNameSpaces) {
    this.jaxbContextBuilder = jaxbContextBuilder;
    this.gpxNameSpaces = gpxNameSpaces == null || gpxNameSpaces.length == 0
        ? GarminJaxbContextDataProvider.GPX_NAMESPACES
        : gpxNameSpaces;
  }

  /**
   * Convert trk.
   *
   * @param trk the trk
   * @return the trk type
   */
  TrkType convert(final Trk trk) {
    final TrkType trkType = convertFeatureProperties(trk.getProperties(), TrkType::new);
    trkType.setExtensions(getTrackExtension(trkType, trk));

    final MultiLineString multiLineString = trk.getGeometry();
    final List<List<BigDecimal>> eleLines = trk.getProperties().getEleLines();
    final List<List<OffsetDateTime>> timeLines = trk.getProperties().getTimeLines();
    for (int n = 0; n < multiLineString.getNumGeometries(); n++) {
      final LineString lineString = (LineString) multiLineString.getGeometryN(n);
      trkType.getTrksegs().add(convert(lineString, eleLines.get(n), timeLines.get(n)));
    }
    return trkType;
  }

  private TrksegType convert(
      final LineString lineString,
      final List<BigDecimal> eleLine,
      final List<OffsetDateTime> timeLine) {

    final TrksegType trkseg = new TrksegType();
    for (int n = 0; n < lineString.getCoordinates().length; n++) {
      final Coordinate coordinate = lineString.getCoordinateN(n);
      final BigDecimal ele = eleLine.get(n);
      final OffsetDateTime time = timeLine.get(n);
      final WptType wptType = new WptType();
      wptType.setLon(BigDecimal.valueOf(coordinate.getX()));
      wptType.setLat(BigDecimal.valueOf(coordinate.getY()));
      wptType.setEle(ele);
      wptType.setTime(offsetDateTimeToXmlCalendarUtc(time));
      trkseg.getTrkpts().add(wptType);
    }
    return trkseg;
  }

  private ExtensionsType getTrackExtension(final TrkType trkType, final Trk trk) {
    final TrackExtension trackExtension;
    if (trk.getProperties() != null
        && trk.getProperties().getSettings() != null
        && trk.getProperties().getSettings().getDisplayColor() != null) {
      trackExtension = new TrackExtension();
      trackExtension.setDisplayColor(
          trk.getProperties().getSettings().getDisplayColor().getGarmin());
    } else {
      trackExtension = null;
    }
    return ExtensionsTypeBuilder
        .builder(trkType.getExtensions())
        .addElement(trackExtension, jaxbContextBuilder.buildJaxbContext(gpxNameSpaces))
        .build(true);
  }
}
