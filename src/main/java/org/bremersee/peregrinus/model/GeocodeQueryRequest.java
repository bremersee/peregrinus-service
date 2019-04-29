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

import static org.bremersee.peregrinus.model.ProviderConstants.GOOGLE;
import static org.bremersee.peregrinus.model.ProviderConstants.NOMINATIM;
import static org.bremersee.peregrinus.model.ProviderConstants.TOMTOM;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;
import org.bremersee.common.model.TwoLetterCountryCodes;
import org.bremersee.peregrinus.model.google.GoogleGeocodeQueryRequest;
import org.bremersee.peregrinus.model.nominatim.NominatimGeocodeQueryRequest;
import org.bremersee.peregrinus.model.tomtom.TomTomGeocodeQueryRequest;

/**
 * @author Christian Bremer
 */
@ApiModel(description = "Geocoding request.", discriminator = "provider")
@JsonTypeInfo(use = Id.NAME, property = "provider")
@JsonSubTypes({
    @Type(value = GoogleGeocodeQueryRequest.class, name = GOOGLE),
    @Type(value = NominatimGeocodeQueryRequest.class, name = NOMINATIM),
    @Type(value = TomTomGeocodeQueryRequest.class, name = TOMTOM)
})
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class GeocodeQueryRequest extends GeocodeRequest {

  @JsonProperty(value = "query", required = true)
  private String query;

  public GeocodeQueryRequest(
      HttpLanguageTag language,
      double[] boundingBox,
      TwoLetterCountryCodes countryCodes,
      Integer limit,
      String query) {

    super(language, boundingBox, countryCodes, limit);
    setQuery(query);
  }
}
