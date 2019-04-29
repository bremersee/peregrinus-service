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

package org.bremersee.peregrinus.service.adapter.nominatim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.UnknownAware;
import org.locationtech.jts.geom.Geometry;

/**
 * The nominatim search result.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class GeocodeResult extends UnknownAware {

  private Address address;

  @JsonProperty("boundingbox")
  private double[] boundingBox;

  private String category;

  @JsonProperty("class")
  private String clazz;

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("extratags")
  private ExtraTags extraTags;

  @JsonProperty("geojson")
  private Geometry geoJson;

  private String icon;

  private BigDecimal importance;

  private String lat;

  private String licence;

  private String lon;

  @JsonProperty("namedetails")
  private NameDetails nameDetails;

  @JsonProperty("osm_id")
  private String osmId;

  @JsonProperty("osm_type")
  private String osmType;

  @JsonProperty("place_id")
  private String placeId;

  @JsonProperty("place_rank")
  private String placeRank;

  private String type;

  /**
   * Returns {@code true} if lat and lon are present and valid numbers, otherwise {@code false}.
   *
   * @return {@code true} if lat and lon are present and valid numbers, otherwise {@code false}
   */
  public boolean hasLatLon() {
    if (lat == null || lon == null) {
      return false;
    }
    try {
      return !Double.isNaN(Double.parseDouble(lat)) && !Double.isNaN(Double.parseDouble(lon));

    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns lat as double.
   *
   * @return lat as double
   * @throws NullPointerException  if lat is null
   * @throws NumberFormatException if lat is not a number
   */
  @SuppressWarnings("unused")
  public double latToDouble() {
    return Double.parseDouble(lat);
  }

  /**
   * Returns lon as double.
   *
   * @return lon as double
   * @throws NullPointerException  if lon is null
   * @throws NumberFormatException if lon is not a number
   */
  @SuppressWarnings("unused")
  public double lonToDouble() {
    return Double.parseDouble(lon);
  }

}
