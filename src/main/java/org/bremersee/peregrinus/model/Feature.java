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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;

/**
 * @author Christian Bremer
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
})
@JsonTypeName("Feature")
@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class Feature<G extends Geometry, P extends FeatureProperties> {

  private String id;

  //@ApiModelProperty(dataType = "org.bremersee.geojson.model.Geometry")
  private G geometry;

  private double[] bbox;

  private P properties;

  public Feature(String id, G geometry, double[] bbox, P properties) {
    setId(id);
    setGeometry(geometry);
    setBbox(bbox);
    setProperties(properties);
  }

  public void setProperties(final P properties) {
    if (properties != null) {
      this.properties = properties;
    }
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, geometry, properties);
    result = 31 * result + Arrays.hashCode(bbox);
    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Feature)) {
      return false;
    }
    Feature<?, ?> feature = (Feature<?, ?>) o;
    return Objects.equals(id, feature.id) &&
        GeometryUtils.equals(geometry, feature.geometry) &&
        Arrays.equals(bbox, feature.bbox) &&
        Objects.equals(properties, feature.properties);
  }

}
