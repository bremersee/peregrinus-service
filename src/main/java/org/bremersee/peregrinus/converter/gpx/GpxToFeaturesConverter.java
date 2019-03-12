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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * @author Christian Bremer
 */
@Component
@Validated
public class GpxToFeaturesConverter extends AbstractGpxConverter
    implements Converter<Tuple2<Gpx, Boolean>, List<Feature>> {

  private final WptTypeToWptConverter wptTypeConverter;

  private final TrkTypeToTrkConverter trkTypeConverter;

  private final RteTypeToRteConverter rteTypeConverter;

  public GpxToFeaturesConverter(final JaxbContextBuilder jaxbContextBuilder) {
    wptTypeConverter = new WptTypeToWptConverter(jaxbContextBuilder);
    trkTypeConverter = new TrkTypeToTrkConverter(jaxbContextBuilder);
    rteTypeConverter = new RteTypeToRteConverter(jaxbContextBuilder);
  }

  @NotNull
  @Override
  public List<Feature> convert(@NotNull final Tuple2<Gpx, Boolean> gpxAndRemoveRouteWaypoints) {

    final Gpx gpx = gpxAndRemoveRouteWaypoints.getT1();
    final Boolean removeRteWpts = gpxAndRemoveRouteWaypoints.getT2();

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

    final List<Feature> rtes = gpx.getRtes()
        .stream()
        .filter(Objects::nonNull)
        .map(rteType -> rteTypeConverter.convert(Tuples.of(rteType, gpx.getWpts())))
        .collect(Collectors.toList());

    final List<Feature> trks = gpx.getTrks()
        .stream()
        .filter(Objects::nonNull)
        .map(trkTypeConverter::convert)
        .collect(Collectors.toList());

    final List<Feature> features = gpx.getWpts()
        .stream()
        .filter(Objects::nonNull)
        .filter(wptType -> !isExcluded(removeRteWpts, wptType, rtes))
        .map(wptTypeConverter::convert)
        .collect(Collectors.toList());

    features.addAll(trks);
    features.addAll(rtes);
    return features;
  }

  private boolean isExcluded(
      final boolean removeRteWpts,
      final WptType wptType,
      final List<Feature> rtes) {

    return removeRteWpts && rtes
        .stream()
        .filter(f -> f instanceof Rte)
        .map(f -> ((Rte) f).getProperties().getRtePts())
        .anyMatch(rtePts -> contains(rtePts, wptType));
  }

  private boolean contains(final Collection<RtePt> rtePts, final WptType wptType) {
    return rtePts.stream().anyMatch(rtePt -> areEqual(rtePt, wptType));
  }

  private boolean areEqual(final RtePt rtePt, final WptType wptType) {
    String an = rtePt.getProperties().getName() != null ? rtePt.getProperties().getName() : "";
    String bn = wptType.getName() != null ? wptType.getName() : "";
    Coordinate ap = rtePt.getGeometry().getCoordinate();
    Coordinate bp = GeometryUtils.createCoordinateWGS84(wptType.getLat(), wptType.getLon());
    return an.equals(bn) && ap.equals2D(bp, 0.000000000000001);
  }

}
