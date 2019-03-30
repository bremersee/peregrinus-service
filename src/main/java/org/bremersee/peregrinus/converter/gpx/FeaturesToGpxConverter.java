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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.util.function.Tuple2;

/**
 * The features to gpx converter.
 *
 * @author Christian Bremer
 */
@Component
@Validated
public class FeaturesToGpxConverter {

  private final RteToRteTypeConverter rteConverter;

  private final TrkToTrkTypeConverter trkConverter;

  private final WptToWptTypeConverter wptConverter;

  /**
   * Instantiates a new features to gpx converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  public FeaturesToGpxConverter(final JaxbContextBuilder jaxbContextBuilder) {
    this.rteConverter = new RteToRteTypeConverter(jaxbContextBuilder);
    this.trkConverter = new TrkToTrkTypeConverter(jaxbContextBuilder);
    this.wptConverter = new WptToWptTypeConverter(jaxbContextBuilder);
  }

  /**
   * Convert gpx.
   *
   * @param features the features
   * @return the gpx
   */
  @NotNull
  public Gpx convert(@NotNull final Collection<? extends Feature> features) {
    final Gpx gpx = new Gpx();

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

    for (Feature feature : features) {
      if (feature instanceof Wpt) {
        gpx.getWpts().add(wptConverter.convert((Wpt) feature));
      } else if (feature instanceof Trk) {
        gpx.getTrks().add(trkConverter.convert((Trk) feature));
      } else if (feature instanceof Rte) {
        final Tuple2<RteType, List<WptType>> rteTuple = rteConverter.convert((Rte) feature);
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
