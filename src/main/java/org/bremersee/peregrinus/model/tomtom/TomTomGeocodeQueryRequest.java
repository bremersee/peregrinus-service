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

package org.bremersee.peregrinus.model.tomtom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;
import org.bremersee.common.model.TwoLetterCountryCodes;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.locationtech.jts.geom.Point;

/**
 * @author Christian Bremer
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@SuppressWarnings({"WeakerAccess", "unused"})
public class TomTomGeocodeQueryRequest extends GeocodeQueryRequest {

  @JsonIgnore
  private Integer offset;

  @Builder
  public TomTomGeocodeQueryRequest(
      HttpLanguageTag language,
      double[] boundingBox,
      TwoLetterCountryCodes countryCodes,
      Integer limit,
      String query,
      Integer offset) {

    super(language, boundingBox, countryCodes, limit, query);
    setOffset(offset);
  }

  /**
   * The bounding box (South-west and north-east).
   *
   * @return the bounding box
   */
  @Override
  public double[] getBoundingBox() {
    return super.getBoundingBox();
  }

  /**
   * Comma separated string of country codes. This will limit the search to the specified
   * countries.
   *
   * @return the country codes
   */
  @Override
  public TwoLetterCountryCodes getCountryCodes() {
    return super.getCountryCodes();
  }

  /**
   * Starting offset of the returned results within the full result set.
   *
   * @return the offset
   */
  @JsonProperty(value = "offset")
  public Integer getOffset() {
    return offset;
  }

  @JsonProperty(value = "offset")
  public void setOffset(Integer offset) {
    if (offset != null && offset >= 0) {
      this.offset = offset;
    }
  }

  /**
   * Latitude and longitude where results should be biased. NOTE: supplying a lat/lon without a
   * radius will return search results biased to that point.
   *
   * @return the center
   */
  @ApiModelProperty(hidden = true)
  @JsonIgnore
  public Point getCenter() {
    // TODO
    return null;
  }

  /**
   * If radius and center are set, the results will be constrained to the defined area. The radius
   * parameter is specified in meters.
   *
   * @return the radius in meters
   */
  @ApiModelProperty(hidden = true)
  @JsonIgnore
  public Integer getRadius() {
    // TODO
    return null;
  }

}
