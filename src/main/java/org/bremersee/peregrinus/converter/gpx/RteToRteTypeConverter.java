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

import org.bremersee.garmin.gpx.v3.model.ext.DisplayColorT;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Rte;
import org.bremersee.peregrinus.content.model.RteProperties;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.content.model.RteSettings;
import org.bremersee.peregrinus.converter.InstantToXmlGregorianCalendarConverter;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.LineString;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
public class RteToRteTypeConverter extends AbstractFeatureConverter
    implements Converter<Rte, RteType> {

  private static final InstantToXmlGregorianCalendarConverter timeConverter
      = new InstantToXmlGregorianCalendarConverter();

  private final JaxbContextBuilder jaxbContextBuilder;

  RteToRteTypeConverter(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  @Override
  public RteType convert(Rte rte) {
    final RteType rteType = convertFeatureProperties(rte.getProperties(), RteType::new);
    final RteProperties properties = rte.getProperties();
    final RteSettings rteSettings = properties.getSettings();
    final RouteExtension routeExtension = new RouteExtension();
    if (rteSettings != null && rteSettings.getDisplayColor() != null) {
      routeExtension.setDisplayColor(rteSettings.getDisplayColor().getGarmin());
    } else {
      routeExtension.setDisplayColor(DisplayColorT.MAGENTA);
    }
    for (int n = 0; n < rte.getGeometry().getNumGeometries(); n++) {
      LineString lineString = (LineString) rte.getGeometry().getGeometryN(n);
      // TODO
    }


    rteType.getRtepts();


    return rteType;
  }

  private WptType convert(LineString lineString, RtePt rteSegment) {

    return null;
  }
}
