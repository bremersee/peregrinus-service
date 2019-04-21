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

package org.bremersee.peregrinus.service.adapter.garmin;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.BoundsType;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.MetadataType;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.garmin.ExportSettings;
import org.bremersee.peregrinus.service.ConverterService;
import org.bremersee.peregrinus.service.RteToTrkConverter;
import org.bremersee.xml.ConverterUtils;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import reactor.util.function.Tuple2;

/**
 * The features to garmin converter.
 *
 * @author Christian Bremer
 */
@Validated
public class FeaturesToGpxConverter {

  private final Function<Rte, Trk> rteToTrkConvert = new RteToTrkConverter();

  private final RteToRteTypeConverter rteConverter;

  private final TrkToTrkTypeConverter trkConverter;

  private final WptToWptTypeConverter wptConverter;

  private final String version;

  private final String creator;

  /**
   * Instantiates a new Features to garmin converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  public FeaturesToGpxConverter(
      final JaxbContextBuilder jaxbContextBuilder) {
    this(jaxbContextBuilder, null, null, null);
  }

  /**
   * Instantiates a new Features to garmin converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   * @param creator            the creator
   * @param version            the version
   */
  public FeaturesToGpxConverter(
      final JaxbContextBuilder jaxbContextBuilder,
      final String[] gpxNameSpaces,
      final String creator,
      final String version) {
    this.rteConverter = new RteToRteTypeConverter(jaxbContextBuilder, gpxNameSpaces);
    this.trkConverter = new TrkToTrkTypeConverter(jaxbContextBuilder, gpxNameSpaces);
    this.wptConverter = new WptToWptTypeConverter(jaxbContextBuilder, gpxNameSpaces);
    this.version = StringUtils.hasText(version) ? version.trim() : "1.1";
    this.creator = StringUtils.hasText(creator) ? creator.trim() : "peregrinus-garmin";
  }

  /**
   * Convert features to gpx.
   *
   * @param features       the features
   * @param exportSettings the export settings
   * @return the gpx
   */
  @NotNull
  public Gpx convert(
      @Nullable final Collection<? extends Feature> features,
      final ExportSettings exportSettings) {

    final Gpx gpx = new Gpx();
    gpx.setVersion(version);
    gpx.setCreator(creator);
    final BoundsType bounds;
    if (features == null) {
      bounds = null;
    } else {
      for (Feature feature : features) {
        if (feature instanceof Wpt) {
          gpx.getWpts().add(wptConverter.convert((Wpt) feature));
        } else if (feature instanceof Trk) {
          gpx.getTrks().add(trkConverter.convert((Trk) feature));
        } else if (feature instanceof Rte) {
          final Rte rte = (Rte)feature;
          if (Boolean.TRUE.equals(exportSettings.getExportRouteAsTrack())) {
            gpx.getTrks().add(trkConverter.convert(rteToTrkConvert.apply(rte)));
          }
          final Tuple2<RteType, List<WptType>> rteTuple = rteConverter
              .convert(rte, exportSettings);
          if (rteTuple != null) {
            gpx.getRtes().add(rteTuple.getT1());
            gpx.getWpts().addAll(
                rteTuple.getT2()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(wptType -> !contains(gpx, wptType))
                    .collect(Collectors.toList()));
          }
        }
      }
      final double[] boundingBox = GeometryUtils.getBoundingBox(
          features
              .stream()
              .map(Feature::getGeometry)
              .collect(Collectors.toList()));
      if (boundingBox == null) {
        bounds = null;
      } else {
        bounds = new BoundsType();
        final Coordinate sw = GeometryUtils.getSouthWest(boundingBox);
        bounds.setMinlon(BigDecimal.valueOf(sw.getX()));
        bounds.setMinlat(BigDecimal.valueOf(sw.getY()));
        final Coordinate ne = GeometryUtils.getNorthEast(boundingBox);
        bounds.setMaxlon(BigDecimal.valueOf(ne.getX()));
        bounds.setMaxlat(BigDecimal.valueOf(ne.getY()));
      }
    }

    final MetadataType metadata = new MetadataType();
    metadata.setTime(ConverterUtils.millisToXmlCalendar(System.currentTimeMillis()));
    metadata.setBounds(bounds);
    metadata.setName(exportSettings.getName());
    metadata.setDesc(exportSettings.getDescription());
    gpx.setMetadata(metadata);
    return gpx;
  }

  private boolean contains(final Gpx gpx, final WptType wptType) {
    return contains(gpx.getWpts(), wptType);
  }

  private boolean contains(final List<WptType> wptTypeList, final WptType wptType) {
    return wptTypeList.stream().anyMatch(wptType1 -> equals(wptType1, wptType));
  }

  private boolean equals(final WptType wptType1, final WptType wptType2) {
    return wptType1 != null && wptType2 != null
        && wptType1.getName() != null && wptType1.getName().equals(wptType2.getName())
        && wptType1.getLat() != null && wptType1.getLat().equals(wptType2.getLat())
        && wptType1.getLon() != null && wptType1.getLon().equals(wptType2.getLon());
  }

}
