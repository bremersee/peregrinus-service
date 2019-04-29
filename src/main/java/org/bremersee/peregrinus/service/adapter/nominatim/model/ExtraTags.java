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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.UnknownAware;

/**
 * The nominatim extra tags response object.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("WeakerAccess")
public class ExtraTags extends UnknownAware {

  @JsonProperty("wikidata")
  private String wikiData; // "Q323181"

  @JsonProperty("wikipedia")
  private String wikiPedia; // "de:Kommandantenhaus (Berlin)"

  @JsonProperty("wheelchair")
  private String wheelChair; // "yes"

}
