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

package org.bremersee.peregrinus.geo.converter.gpx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.gpx.GpxJaxbContextHelper;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.geo.model.DisplayColor;
import org.bremersee.peregrinus.geo.model.Rte;
import org.bremersee.peregrinus.geo.model.RtePoint;
import org.bremersee.peregrinus.geo.model.RtePointProperties;
import org.bremersee.peregrinus.geo.model.RteProperties;
import org.bremersee.peregrinus.geo.model.RteSegment;
import org.bremersee.peregrinus.geo.model.Wpt;
import org.bremersee.xml.JaxbContextBuilder;

/**
 * @author Christian Bremer
 */
class RteConverter extends AbstractGpxConverter {

  RteConverter(JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  protected List<Rte> parseRtes(final List<RteType> rteTypes) {
    final List<Rte> rteList = new ArrayList<>();
    if (rteTypes != null) {
      for (final RteType rteType : rteTypes) {
        if (rteType != null) {
          rteList.add(parseRte(rteType));
        }
      }
    }
    return rteList;
  }

  @SuppressWarnings("Duplicates")
  protected Rte parseRte(final RteType rteType) {

    final Rte rte = new Rte();
    rte.setProperties(readCommonData(
        RteProperties::new,
        rteType.getName(),
        rteType.getDesc(),
        rteType.getCmt(),
        rteType.getLinks()));

    // display color
    final Optional<RouteExtension> rteTypeExt = GpxJaxbContextHelper.findFirstExtension(
        RouteExtension.class,
        true,
        rteType.getExtensions(),
        getUnmarshaller());

    final DisplayColorT displayColor = rteTypeExt.map(RouteExtension::getDisplayColor).orElse(null);
    rte.getProperties().setDisplayColor(DisplayColor.findByGarminDisplayColor(
        displayColor,
        DisplayColor.MAGENTA));

    final List<WptType> rtePts = rteType.getRtepts();

    return rte;
  }

  private List<RteSegment> parseRtePts(final List<WptType> rtePts) {
    final List<RteSegment> rteSegments = new ArrayList<>();

    return rteSegments;
  }

  private RteSegment parseRtePt(final WptType rtePt) {
    final RteSegment rteSegment = new RteSegment();

    final Wpt wpt = new Wpt(); // TODO
    final RtePoint rtePoint = new RtePoint();
    rtePoint.setGeometry(wpt.getGeometry());

    final RtePointProperties rtePointProperties = new RtePointProperties();
    rtePointProperties.setName(wpt.getProperties().getName());
    rtePointProperties.setTime(
        rtePt.getTime() != null ? rtePt.getTime().toGregorianCalendar().getTime() : null);
    rtePoint.setProperties(rtePointProperties);

    rteSegment.setPoint(rtePoint);

    return rteSegment;
  }

}
