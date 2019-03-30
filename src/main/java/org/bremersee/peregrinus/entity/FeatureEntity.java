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

package org.bremersee.peregrinus.entity;

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christian Bremer
 */
@Document(collection = "feature")
@TypeAlias("Feature")
@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class FeatureEntity<G extends Geometry, P extends FeatureEntityProperties> {

  @Id
  private String id;

  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  private G geometry;

  private double[] bbox;

  private P properties;

  public FeatureEntity(String id, G geometry, double[] bbox, P properties) {
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
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FeatureEntity)) {
      return false;
    }
    FeatureEntity<?, ?> that = (FeatureEntity<?, ?>) o;
    return Objects.equals(id, that.id) &&
        GeometryUtils.equals(geometry, that.geometry) &&
        Arrays.equals(bbox, that.bbox) &&
        Objects.equals(properties, that.properties);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, geometry, properties);
    result = 31 * result + Arrays.hashCode(bbox);
    return result;
  }
}
