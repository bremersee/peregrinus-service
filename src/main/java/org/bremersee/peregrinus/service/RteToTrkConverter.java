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

package org.bremersee.peregrinus.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.TrkProperties;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

/**
 * @author Christian Bremer
 */
public class RteToTrkConverter implements Function<Rte, Trk> {

  @Override
  public Trk apply(Rte rte) {
    return Trk
        .builder()
        .bbox(rte.getBbox())
        .geometry(rte.getGeometry())
        .properties(TrkProperties
            .builder()
            .acl(rte.getProperties().getAcl())
            .createdBy(rte.getProperties().getCreatedBy())
            .eleLines(getEleLines(rte))
            .links(rte.getProperties().getLinks())
            .markdownDescription(rte.getProperties().getMarkdownDescription())
            .modifiedBy(rte.getProperties().getModifiedBy())
            .name(rte.getProperties().getName() + " (trk)")
            .plainTextDescription(rte.getProperties().getPlainTextDescription())
            .timeLines(getTimeLines(rte))
            .build())
        .build();
  }

  private List<List<BigDecimal>> getEleLines(Rte rte) {
    final List<List<BigDecimal>> eleLines = new ArrayList<>();
    for (int i = 0; i < rte.getGeometry().getNumGeometries(); i++) {
      final LineString lineString = (LineString) rte.getGeometry().getGeometryN(i);
      eleLines.add(
          Arrays
              .stream(lineString.getCoordinates())
              .map(coordinate -> BigDecimal.valueOf(0.))
              .collect(Collectors.toList()));
    }
    return eleLines;
  }

  private List<List<OffsetDateTime>> getTimeLines(Rte rte) {
    final double lengthPerSecond = getLengthPerSecond(rte);
    final List<List<OffsetDateTime>> timeLines = new ArrayList<>();
    long timeInMillis = System.currentTimeMillis();
    for (int i = 0; i < rte.getGeometry().getNumGeometries(); i++) {
      final List<OffsetDateTime> timeLine = new ArrayList<>();
      Coordinate previous = null;
      for (Coordinate current : rte.getGeometry().getGeometryN(i).getCoordinates()) {
        if (previous != null) {
          double len = GeometryUtils.createLineString(Arrays.asList(previous, current)).getLength();
          double secs = getTimeInSeconds(len, lengthPerSecond);
          timeInMillis = timeInMillis + Math.round(secs * 1000.);
        }
        timeLine.add(OffsetDateTime.ofInstant(new Date(timeInMillis).toInstant(), ZoneOffset.UTC));
        previous = current;
      }
      timeLines.add(timeLine);
    }
    return timeLines;
  }

  private double getLengthPerSecond(Rte rte) {
    final BigInteger time = rte.getProperties().calculateTravelTimeInSeconds();
    if (time == null || time.longValue() == 0) {
      // TODO return something that makes sense
      return 0.1;
    }
    final double len = rte.getGeometry().getLength();
    return len / Math.abs(time.doubleValue());
  }

  private double getTimeInSeconds(double len, double lengthPerSecond) {
    return len / lengthPerSecond;
  }

}
