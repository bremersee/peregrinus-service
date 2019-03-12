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

package org.bremersee.peregrinus.converter.gpx;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;
import org.bremersee.garmin.gpx.v3.model.ext.TrackExtension;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.gpx.model.TrksegType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Trk;
import org.bremersee.peregrinus.content.model.TrkProperties;
import org.bremersee.peregrinus.content.model.TrkSettings;
import org.bremersee.peregrinus.converter.InstantToXmlGregorianCalendarConverter;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
class TrkToTrkTypeConverter extends AbstractFeatureConverter
    implements Converter<Trk, TrkType> {

  private static final InstantToXmlGregorianCalendarConverter timeConverter
      = new InstantToXmlGregorianCalendarConverter();

  private final JaxbContextBuilder jaxbContextBuilder;

  TrkToTrkTypeConverter(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  @Override
  public TrkType convert(Trk trk) {
    final TrkType trkType = convertFeatureProperties(trk.getProperties(), TrkType::new);
    final TrkProperties properties = trk.getProperties();
    final TrkSettings trkSettings = properties.getSettings();
    final TrackExtension trackExtension = new TrackExtension();
    if (trkSettings != null && trkSettings.getDisplayColor() != null) {
      trackExtension.setDisplayColor(trkSettings.getDisplayColor().getGarmin());
    } else {
      trackExtension.setDisplayColor(DisplayColorT.DARK_GRAY);
    }
    trkType.setExtensions(
        ExtensionsTypeBuilder
            .builder(trkType.getExtensions())
            .addElement(trackExtension, jaxbContextBuilder.buildJaxbContext())
            .build(true));
    final MultiLineString multiLineString = trk.getGeometry();
    final List<List<BigDecimal>> eleLines = properties.getEleLines();
    final List<List<Instant>> timeLines = properties.getTimeLines();
    final List<TrksegType> trksegList = new ArrayList<>(multiLineString.getNumGeometries());
    for (int n = 0; n < multiLineString.getNumGeometries(); n++) {
      LineString lineString = (LineString) multiLineString.getGeometryN(n);
      trkType.getTrksegs().add(convert(lineString, eleLines.get(n), timeLines.get(n)));
    }
    return trkType;
  }

  private TrksegType convert(
      LineString lineString,
      List<BigDecimal> eleLine,
      List<Instant> timeLine) {
    TrksegType trkseg = new TrksegType();
    for (int n = 0; n < lineString.getCoordinates().length; n++) {
      Coordinate coordinate = lineString.getCoordinateN(n);
      BigDecimal ele = eleLine.get(n);
      Instant time = timeLine.get(n);
      WptType wptType = new WptType();
      wptType.setLon(BigDecimal.valueOf(coordinate.getX()));
      wptType.setLat(BigDecimal.valueOf(coordinate.getY()));
      wptType.setEle(ele);
      wptType.setTime(timeConverter.convert(time));
      trkseg.getTrkpts().add(wptType);
    }
    return trkseg;
  }
}
