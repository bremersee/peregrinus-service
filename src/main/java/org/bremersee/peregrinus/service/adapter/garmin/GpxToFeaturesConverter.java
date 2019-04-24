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

package org.bremersee.peregrinus.service.adapter.garmin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.garmin.ImportSettings;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.stereotype.Component;

/**
 * The gpx to features converter.
 *
 * @author Christian Bremer
 */
@Component
public class GpxToFeaturesConverter {

  private final WptTypeToWptConverter wptTypeConverter;

  private final TrkTypeToTrkConverter trkTypeConverter;

  private final RteTypeToRteConverter rteTypeConverter;

  /**
   * Instantiates a new gpx to features converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  public GpxToFeaturesConverter(final JaxbContextBuilder jaxbContextBuilder) {
    wptTypeConverter = new WptTypeToWptConverter(jaxbContextBuilder);
    trkTypeConverter = new TrkTypeToTrkConverter(jaxbContextBuilder);
    rteTypeConverter = new RteTypeToRteConverter(jaxbContextBuilder);
  }

  /**
   * Convert gpx.
   *
   * @param gpx            the gpx
   * @param importSettings import settings
   * @return the list
   */
  public FeatureCollection convert(
      final Gpx gpx,
      final ImportSettings importSettings) {

    if (gpx == null) {
      return new FeatureCollection();
    }

    final List<Feature> features = gpx.getWpts()
        .stream()
        .filter(Objects::nonNull)
        .filter(wptType -> importWpt(wptType, gpx.getRtes(), importSettings))
        .map(wptTypeConverter::convert)
        .collect(Collectors.toList());

    features.addAll(gpx.getRtes()
        .stream()
        .filter(Objects::nonNull)
        .map(rteTypeConverter::convert)
        .collect(Collectors.toList()));

    features.addAll(gpx.getTrks()
        .stream()
        .filter(Objects::nonNull)
        .map(trkTypeConverter::convert)
        .collect(Collectors.toList()));

    final double[] boundingBox = GeometryUtils.getBoundingBox(
        features
            .stream()
            .map(Feature::getGeometry)
            .collect(Collectors.toList()));

    return new FeatureCollection(features, boundingBox);
  }

  private boolean importWpt(
      final WptType wptType,
      final List<RteType> rteTypes,
      final ImportSettings importSettings) {

    return Boolean.TRUE.equals(importSettings.getImportRouteWaypoints())
        || !wptIsRtePt(wptType, rteTypes);
  }

  private boolean wptIsRtePt(final WptType wptType, final List<RteType> rteTypes) {
    return rteTypes
        .stream()
        .anyMatch(rteType -> rteType
            .getRtepts()
            .stream()
            .anyMatch(rtePt -> rtePt.getName().equals(wptType.getName())
                && rtePt.getLat().equals(wptType.getLat())
                && rtePt.getLon().equals(wptType.getLon())));
  }

}
