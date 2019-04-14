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

import lombok.Builder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;

/**
 * @author Christian Bremer
 */
public class Rte extends Feature {

  public Rte() {
    setProperties(new RteProperties());
  }

  @Builder
  public Rte(String id, MultiLineString geometry, double[] bbox,
      RteProperties properties) {
    super(id, geometry, bbox, properties);
  }

  @Override
  public MultiLineString getGeometry() {
    return (MultiLineString) super.getGeometry();
  }

  @Override
  public void setGeometry(Geometry geometry) {
    if (geometry == null || geometry instanceof MultiLineString) {
      super.setGeometry(geometry);
    } else {
      throw new IllegalArgumentException("Geometry must be of type 'MultiLineString'.");
    }
  }

  @Override
  public RteProperties getProperties() {
    return (RteProperties) super.getProperties();
  }

  @Override
  public void setProperties(FeatureProperties<? extends FeatureSettings> properties) {
    if (properties == null || properties instanceof RteProperties) {
      super.setProperties(properties != null ? properties : new RtePtProperties());
    } else {
      throw new IllegalArgumentException("Properties must be of type 'RteProperties'.");
    }
  }

}
