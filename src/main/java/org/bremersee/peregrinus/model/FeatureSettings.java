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

package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The GeoJSON feature settings.
 *
 * @author Christian Bremer
 */
@Schema(description = "Common settings of a GeoJSON feature.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
    @Type(value = WptSettings.class, name = Feature.WPT_TYPE),
    @Type(value = TrkSettings.class, name = Feature.TRK_TYPE),
    @Type(value = RteSettings.class, name = Feature.RTE_TYPE)
})
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class FeatureSettings {

  @Schema(description = "The settings ID.")
  private String id;

  @Schema(description = "The feature ID.")
  private String featureId;

  @Schema(description = "The user ID.")
  private String userId;

  /**
   * Instantiates new GeoJSON feature settings.
   *
   * @param id        the id
   * @param featureId the feature id
   * @param userId    the user id
   */
  public FeatureSettings(String id, String featureId, String userId) {
    this.id = id;
    this.featureId = featureId;
    this.userId = userId;
  }
}
