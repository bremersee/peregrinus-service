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

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.model.BoundsType;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.LinkType;
import org.bremersee.gpx.model.MetadataType;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.TrkType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.bremersee.peregrinus.service.RteToTrkConverter;
import org.bremersee.xml.ConverterUtils;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple2;

/**
 * The features to gpx converter.
 *
 * @author Christian Bremer
 */
@Component
@Slf4j
public class FeaturesToGpxConverter {

  private final Function<Rte, Trk> rteToTrkConvert = new RteToTrkConverter();

  private final RteToRteTypeConverter rteConverter;

  private final TrkToTrkTypeConverter trkConverter;

  private final WptToWptTypeConverter wptConverter;

  private final String version;

  private final String creator;

  private final String link;

  private final String linkText;

  /**
   * Instantiates a new Features to gpx converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   * @param gpxNameSpaces      the gpx name spaces (can be {@code null})
   * @param creator            the creator (can be {@code null})
   * @param version            the version (can be {@code null})
   * @param link               the link (can be {@code null})
   * @param linkText           the link text (can be {@code null})
   */
  public FeaturesToGpxConverter(
      final JaxbContextBuilder jaxbContextBuilder,
      final @Value("${bremersee.gpx.name-spaces:}") String[] gpxNameSpaces,
      final @Value("${bremersee.gpx.creator:Peregrinus Web App}") String creator,
      final @Value("${bremersee.gpx.version:1.1}") String version,
      final @Value("${bremersee.gpx.link:https://peregrinus.bremersee.org}") String link,
      final @Value("${bremersee.gpx.link-text:Peregrinus Web App}") String linkText) {

    this.rteConverter = new RteToRteTypeConverter(jaxbContextBuilder, gpxNameSpaces);
    this.trkConverter = new TrkToTrkTypeConverter(jaxbContextBuilder, gpxNameSpaces);
    this.wptConverter = new WptToWptTypeConverter(jaxbContextBuilder, gpxNameSpaces);
    this.version = StringUtils.hasText(version) ? version.trim() : "1.1";
    this.creator = StringUtils.hasText(creator) ? creator.trim() : "Peregrinus Web App";
    this.link = isLink(link) ? link : "https://peregrinus.bremersee.org";
    this.linkText = StringUtils.hasText(linkText) ? linkText : "Peregrinus Web App";
  }

  private static boolean isLink(String link) {
    if (!StringUtils.hasText(link)) {
      return false;
    }
    try {
      new URL(link);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  /**
   * Convert features to gpx.
   *
   * @param features       the features
   * @param exportSettings the export settings
   * @return the gpx
   */
  public Gpx convert(
      final Collection<? extends Feature> features,
      final GpxExportSettings exportSettings) {

    final Gpx gpx = new Gpx();
    gpx.setVersion(version);
    gpx.setCreator(creator);

    final BoundsType bounds;
    if (features == null) {
      bounds = null;
    } else {
      final Set<String> names = new HashSet<>();
      for (Feature feature : features) {
        if (feature instanceof Rte) {
          final Rte rte = (Rte) feature;
          if (Boolean.TRUE.equals(exportSettings.getExportRouteAsTrack())) {
            final TrkType trkType = trkConverter.convert(rteToTrkConvert.apply(rte));
            trkType.setName(findUniqueName(trkType.getName(), names));
            gpx.getTrks().add(trkType);
          }
          final Tuple2<RteType, List<WptType>> rteTuple = rteConverter
              .convert(rte, exportSettings, names);
          final RteType rteType = rteTuple.getT1();
          rteType.setName(findUniqueName(rteType.getName(), names));
          gpx.getRtes().add(rteType);
          gpx.getWpts().addAll(rteTuple.getT2());
        } else if (feature instanceof Wpt) {
          final WptType wptType = wptConverter.convert((Wpt) feature);
          wptType.setName(findUniqueName(wptType.getName(), names));
          gpx.getWpts().add(wptType);
        } else if (feature instanceof Trk) {
          final TrkType trkType = trkConverter.convert((Trk) feature);
          trkType.setName(findUniqueName(trkType.getName(), names));
          gpx.getTrks().add(trkType);
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

    final LinkType linkType = new LinkType();
    linkType.setHref(link);
    linkType.setText(linkText);
    final MetadataType metadata = new MetadataType();
    metadata.setTime(ConverterUtils.millisToXmlCalendar(System.currentTimeMillis()));
    metadata.setBounds(bounds);
    metadata.setName(exportSettings.getName());
    metadata.setDesc(exportSettings.getDescription());
    metadata.getLinks().add(linkType);
    gpx.setMetadata(metadata);
    return gpx;
  }

  private String findUniqueName(final String name, final Set<String> names) {
    int counter = 0;
    String newName = name;
    while (names.contains(newName)) {
      counter++;
      newName = name + " (" + counter + ")";
    }
    names.add(newName);
    return newName;
  }

}
