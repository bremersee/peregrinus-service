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

package org.bremersee.peregrinus.model;

import static org.bremersee.peregrinus.model.ProviderConstants.TOMTOM;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;
import org.bremersee.peregrinus.model.tomtom.TomTomRteCalcRequest;
import org.locationtech.jts.geom.Point;

/**
 * The route calculation request.
 *
 * @author Christian Bremer
 */
@ApiModel(description = "Route calculation request.", discriminator = "provider")
@JsonTypeInfo(use = Id.NAME, property = "provider")
@JsonSubTypes({
    @Type(value = TomTomRteCalcRequest.class, name = TOMTOM)
})
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class RteCalcRequest {

  @ApiModelProperty(value = "The route points.", required = true)
  @JsonProperty(required = true)
  private List<Point> rtePts;

  @ApiModelProperty("The requested language.")
  private HttpLanguageTag language;

  /**
   * Instantiates a new route calculation request.
   *
   * @param rtePts   the route points
   * @param language the language
   */
  public RteCalcRequest(Collection<? extends Point> rtePts, HttpLanguageTag language) {
    if (rtePts != null) {
      this.rtePts = new ArrayList<>(rtePts);
    }
    this.language = language;
  }

  /**
   * Gets route points.
   *
   * @return the route points
   */
  public List<Point> getRtePts() {
    if (rtePts == null) {
      rtePts = new ArrayList<>();
    }
    return rtePts;
  }

  /**
   * Build route segment calculation settings.
   *
   * @return the route segment calculation settings
   */
  public abstract RteSegCalcSettings buildRteSegCalcSettings();

}
