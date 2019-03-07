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
import java.util.Collection;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Feature;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.Trk;
import org.bremersee.peregrinus.content.model.Wpt;

/**
 * @author Christian Bremer
 */
public class FeaturesToGpxConverter {

  public Gpx convert(Collection<? extends Feature> features) {
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

    gpx.getRtes();
    gpx.getTrks();
    gpx.getWpts();

    for (Feature feature : features) {
      convertFeature(feature, gpx);
    }
    return gpx;
  }

  private void convertFeature(Feature feature, Gpx gpx) {
    if (feature instanceof Wpt) {
      convertWpt((Wpt) feature, gpx);
    } else if (feature instanceof Trk) {
      convertTrk((Trk) feature, gpx);
    } else if (feature instanceof Rte) {
      convertRte((Rte) feature, gpx);
    }
  }

  private void convertWpt(Wpt wpt, Gpx gpx) {
    final WptType wptType = new WptType();
    wptType.setAgeofdgpsdata(null);
    wptType.setCmt(wpt.getProperties().getPlainTextDescription());
    wptType.setDesc(wpt.getProperties().getPlainTextDescription());
    wptType.setDgpsid(null);
    wptType.setEle(wpt.getProperties().getEle());
    //wptType.setExtensions(null);
    wptType.setFix(null);
    wptType.setGeoidheight(null);
    wptType.setHdop(null);
    wptType.setLat(BigDecimal.valueOf(wpt.getGeometry().getY()));
    wptType.setLon(BigDecimal.valueOf(wpt.getGeometry().getX()));
    wptType.setMagvar(null);
    wptType.setName(wpt.getProperties().getName());
    wptType.setPdop(null);
    wptType.setSat(null);
    wptType.setSrc(null);
    wptType.setSym(null);
    wptType.setTime(null); // TODO
    wptType.setType(wpt.getProperties().getInternalType()); // TODO internalType
    wptType.setVdop(null);

    wptType.getLinks();

    gpx.getWpts().add(wptType);
  }

  private void convertTrk(Trk trk, Gpx gpx) {
  }

  private void convertRte(Rte rte, Gpx gpx) {
  }

}
