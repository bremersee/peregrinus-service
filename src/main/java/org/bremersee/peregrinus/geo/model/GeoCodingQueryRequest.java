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

package org.bremersee.peregrinus.geo.model;

import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.utils.GeometryUtils;
import org.locationtech.jts.geom.Polygon;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
public class GeoCodingQueryRequest extends AbstractGeoCodingRequest {

  private String query;

  private Integer limit = 6;

  /**
   * Preferred language.
   */
  private Locale language;

  /**
   * Limit search results to a specific country (or a list of countries).
   */
  private List<Locale> countries;

  private Polygon boundingBox;

  public double[] toBoundingBox() {
    return GeometryUtils.getBoundingBox(boundingBox);
  }

}
