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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.xml.JaxbContextBuilder;

/**
 * @author Christian Bremer
 */
class WptConverter extends AbstractGpxConverter {

  WptConverter(JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  List<Wpt> readWptTypes(final List<WptType> wpts) {
    final List<Wpt> wptList = new ArrayList<>();
    if (wpts == null) {
      return wptList;
    }
    for (final WptType wptType : wpts) {
      if (wptType != null) {
        wptList.add(readWptType(wptType));
      }
    }
    return wptList;
  }

  Wpt readWptType(final WptType wptType) {

    final Wpt wpt = new Wpt();
    wpt.setProperties(readCommonData(
        WptProperties::new,
        wptType.getName(),
        wptType.getDesc(),
        wptType.getCmt(),
        wptType.getLinks()));

    wpt.setGeometry(GeometryUtils.createPointWGS84(wptType.getLat(), wptType.getLon()));
    wpt.setBbox(GeometryUtils.getBoundingBox(wpt.getGeometry()));

    final Optional<WaypointExtension> wptExt = GpxJaxbContextHelper.findFirstExtension(
        WaypointExtension.class,
        true,
        wptType.getExtensions(),
        getUnmarshaller());

    wpt.getProperties()
        .setAddress(wptExt.map(ext -> readGarminAddress(ext.getAddress())).orElse(null));
    wpt.getProperties().setEle(wptType.getEle());
    wpt.getProperties().setPhoneNumbers(
        wptExt.map(ext -> readGarminPhoneNumbers(ext.getPhoneNumbers())).orElse(null));

    final XMLGregorianCalendar cal = GpxJaxbContextHelper
        .findFirstExtension(
            CreationTimeExtension.class,
            true,
            wptType.getExtensions(),
            getUnmarshaller())
        .map(CreationTimeExtension::getCreationTime)
        .orElse(wptType.getTime());
    final Instant time = cal != null ? cal.toGregorianCalendar().getTime().toInstant() : null;
    wpt.getProperties().setTime(time);

    if (GarminType.PHOTO.equals(wptType.getType())) {
      // TODO wanted images
    }

    return wpt;
  }

}
