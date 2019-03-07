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

import java.util.ArrayList;
import java.util.List;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Christian Bremer
 */
@Component
public class GpxToFeaturesConverter extends AbstractGpxConverter {

  private final WptTypeToWptConverter wptMapper;

  private final TrkTypeToTrkConverter trkMapper;

  private final RteTypeToRteConverter rteMapper;

  public GpxToFeaturesConverter(final JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
    wptMapper = new WptTypeToWptConverter(jaxbContextBuilder);
    trkMapper = new TrkTypeToTrkConverter(jaxbContextBuilder);
    rteMapper = new RteTypeToRteConverter(jaxbContextBuilder);
  }

  public List<Feature> convert(final Gpx gpx) {

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

    final List<Feature> features = new ArrayList<>();
    features.addAll(wptMapper.readWptTypes(gpx.getWpts()));
    features.addAll(trkMapper.readTrkTypes(gpx.getTrks()));
    features.addAll(rteMapper.readRtes(gpx.getRtes()));
    return features;
  }

}
