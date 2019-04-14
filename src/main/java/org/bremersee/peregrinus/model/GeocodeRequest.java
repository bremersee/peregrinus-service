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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;
import org.bremersee.common.model.TwoLetterCountryCodes;

/**
 * @author Christian Bremer
 */
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class GeocodeRequest {

  private static final int DEFAULT_LIMIT = 8;

  @Setter
  private HttpLanguageTag language;

  private double[] boundingBox;

  private TwoLetterCountryCodes countryCodes;

  private Integer limit = DEFAULT_LIMIT;

  public GeocodeRequest(
      HttpLanguageTag language,
      double[] boundingBox,
      TwoLetterCountryCodes countryCodes,
      Integer limit) {

    setLimit(limit);
    setLanguage(language);
    setCountryCodes(countryCodes);
    setBoundingBox(boundingBox);
  }

  @JsonProperty("boundingBox")
  public void setBoundingBox(double[] boundingBox) {
    if (boundingBox != null && boundingBox.length == 4) {
      this.boundingBox = boundingBox;
    } else {
      this.boundingBox = null;
    }
  }

  @JsonProperty("countryCodes")
  public void setCountryCodes(TwoLetterCountryCodes countryCodes) {
    if (countryCodes != null && !countryCodes.isEmpty()) {
      this.countryCodes = countryCodes;
    } else {
      this.countryCodes = null;
    }
  }

  @JsonProperty("limit")
  public void setLimit(Integer limit) {
    if (limit != null && limit > 0) {
      this.limit = limit;
    } else {
      this.limit = DEFAULT_LIMIT;
    }
  }
}
