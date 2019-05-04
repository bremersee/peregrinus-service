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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The track entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "feature")
@TypeAlias(TypeAliases.TRK)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrkEntity extends FeatureEntity<MultiLineString, TrkEntityProperties> {

  /**
   * Instantiates a new track entity.
   */
  public TrkEntity() {
    setProperties(new TrkEntityProperties());
  }

  /**
   * Instantiates a new track entity.
   *
   * @param id         the id
   * @param geometry   the geometry
   * @param bbox       the bbox
   * @param properties the properties
   */
  @Builder
  @SuppressWarnings("unused")
  public TrkEntity(String id, MultiLineString geometry, double[] bbox,
      TrkEntityProperties properties) {
    super(id, geometry, bbox, properties);
  }
}
