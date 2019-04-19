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

package org.bremersee.peregrinus.garmin;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.springframework.validation.annotation.Validated;

/**
 * The garmin to features converter.
 *
 * @author Christian Bremer
 */
@Validated
public class GpxToFeaturesConverter {

  private final WptTypeToWptConverter wptTypeConverter;

  private final TrkTypeToTrkConverter trkTypeConverter;

  private final RteTypeToRteConverter rteTypeConverter;

  /**
   * Instantiates a new garmin to features converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  public GpxToFeaturesConverter(final JaxbContextBuilder jaxbContextBuilder) {
    wptTypeConverter = new WptTypeToWptConverter(jaxbContextBuilder);
    trkTypeConverter = new TrkTypeToTrkConverter(jaxbContextBuilder);
    rteTypeConverter = new RteTypeToRteConverter(jaxbContextBuilder);
  }

  /**
   * Convert garmin.
   *
   * @param gpx           the garmin
   * @param removeRteWpts remove route waypoints
   * @return the list
   */
  @NotNull
  public FeatureCollection convert(final Gpx gpx, final Boolean removeRteWpts) {

    if (gpx == null) {
      return new FeatureCollection();
    }

    final List<Feature> rtes = gpx.getRtes()
        .stream()
        .filter(Objects::nonNull)
        .map(rteType -> rteTypeConverter.convert(rteType))
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

    final double[] boundingBox = GeometryUtils.getBoundingBox(
        features
            .stream()
            .map((Function<Feature, Geometry>) Feature::getGeometry)
            .collect(Collectors.toList()));

    return new FeatureCollection(features, boundingBox);
  }

  private boolean isExcluded(
      final boolean removeRteWpts,
      final WptType wptType,
      final List<Feature> rtes) {

    // TODO
    return false;
//    return removeRteWpts && rtes
//        .stream()
//        .filter(f -> f instanceof Rte)
//        .map(f -> ((Rte) f).getProperties().getRtePts())
//        .anyMatch(rtePts -> contains(rtePts, wptType));
  }

  private boolean contains(final Collection<RtePt> rtePts, final WptType wptType) {
    return rtePts.stream().anyMatch(rtePt -> areEqual(rtePt, wptType));
  }

  private boolean areEqual(final RtePt rtePt, final WptType wptType) {
//    String an = rtePt.getProperties().getName() != null ? rtePt.getProperties().getName() : "";
//    String bn = wptType.getName() != null ? wptType.getName() : "";
//    Coordinate ap = rtePt.getGeometry().getCoordinate();
//    Coordinate bp = GeometryUtils.createCoordinateWGS84(wptType.getLat(), wptType.getLon());
//    return an.equals(bn) && ap.equals2D(bp, 0.000000000000001);
    return false;
  }

}
