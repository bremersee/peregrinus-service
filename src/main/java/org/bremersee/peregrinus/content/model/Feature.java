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

package org.bremersee.peregrinus.content.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
})
@JsonTypeName("Feature")
@Getter
@Setter
@ToString
public abstract class Feature<G extends Geometry, P extends FeatureProperties>
    implements Comparable<Feature> {

  @Id
  private String id;

  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  private G geometry;

  private double[] bbox;

  private P properties;

  abstract int orderValue();

  @SuppressWarnings("Duplicates")
  @Override
  public int compareTo(final Feature o) {
    if (this == o) {
      return 0;
    }
    if (o == null) {
      return -1;
    }
    int c = orderValue() - o.orderValue();
    if (c != 0) {
      return c;
    }
    if (getProperties() != null) {
      return getProperties().compareTo(o.getProperties());
    }
    return 0;
  }

}
