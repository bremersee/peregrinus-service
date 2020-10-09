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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bremersee.geojson.AbstractGeoJsonFeatureCollection;

/**
 * The GeoJSON feature collection.
 *
 * @author Christian Bremer
 */
@Schema(description = "A GeoJSON feature collection.")
public class FeatureCollection
    extends AbstractGeoJsonFeatureCollection<Feature> {

  /**
   * Instantiates a new feature collection.
   */
  public FeatureCollection() {
  }

  @JsonProperty("features")
  @JsonDeserialize(contentUsing = FeatureDeserializer.class)
  @Override
  public void setFeatures(List<Feature> features) {
    super.setFeatures(features == null
        ? null
        : features.stream().filter(Objects::nonNull).collect(Collectors.toList()));
  }

}
