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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bremersee.garmin.gpx.v3.model.ext.RouteExtension;
import org.bremersee.garmin.trip.v1.model.ext.Trip;
import org.bremersee.gpx.ExtensionsTypeBuilder;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.RteType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RtePt;
import org.bremersee.xml.JaxbContextBuilder;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * The rte to rte type converter.
 *
 * @author Christian Bremer
 */
public class RteToRteTypeConverter extends AbstractFeatureConverter {

  private final JaxbContextBuilder jaxbContextBuilder;

  private final RtePtToRtePtTypeConverter rtePtConverter;

  /**
   * Instantiates a new rte to rte type converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  RteToRteTypeConverter(final JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
    this.rtePtConverter = new RtePtToRtePtTypeConverter(jaxbContextBuilder);
  }

  /**
   * Convert tuple.
   *
   * @param rte the rte
   * @return the tuple
   */
  Tuple2<RteType, List<WptType>> convert(final Rte rte) {
    final Tuple2<List<WptType>, List<WptType>> points = getRteptsAndWpts(rte);
    final RteType rteType = convertFeatureProperties(rte.getProperties(), RteType::new);
    rteType.setExtensions(getRteTypeExtension(rte));
    rteType.getRtepts().addAll(points.getT1());
    return Tuples.of(rteType, points.getT2());
  }

  private ExtensionsType getRteTypeExtension(final Rte rte) {

    final RouteExtension routeExtension = new RouteExtension();
    routeExtension.setDisplayColor(rte.getProperties().getSettings().getDisplayColor().getGarmin());
    routeExtension.setIsAutoNamed(true);

    final Trip trip = new Trip();
    trip.setTransportationMode("Automotive"); // TODO

    return ExtensionsTypeBuilder
        .builder()
        .addElement(routeExtension, jaxbContextBuilder.buildJaxbContext())
        .addElement(trip, jaxbContextBuilder.buildJaxbContext())
        .build(true);
  }

  private Tuple2<List<WptType>, List<WptType>> getRteptsAndWpts(final Rte rte) {

    final List<WptType> rtePtTypes = new ArrayList<>();
    final List<WptType> wptTypes = new ArrayList<>();
    final MultiLineString routes = rte.getGeometry();
    final List<RtePt> rtePts = rte.getProperties().getRtePts();
    final int size = routes.getNumGeometries();
    for (int n = 0; n <= size; n++) {
      final LineString line = n < size ? (LineString) routes.getGeometryN(n) : null;
      final RtePt rtePt = rtePts.get(n);
      final WptType rtePtType = rtePtConverter.convert(Tuples.of(rtePt, Optional.ofNullable(line)));
      final WptType wptType = rtePtConverter.convert(rtePt);
      rtePtTypes.add(rtePtType);
      wptTypes.add(wptType);
    }

    return Tuples.of(rtePtTypes, wptTypes);
  }
}
