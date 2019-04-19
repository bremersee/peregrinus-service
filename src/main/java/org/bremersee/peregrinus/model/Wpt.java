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
import org.locationtech.jts.geom.Point;

/**
 * @author Christian Bremer
 */
public class Wpt extends Feature {

  public Wpt() {
    setProperties(new WptProperties());
  }

  @Builder
  public Wpt(String id, Point geometry, double[] bbox,
      WptProperties properties) {
    super(id, geometry, bbox, properties);
  }

  @Override
  public Point getGeometry() {
    return (Point) super.getGeometry();
  }

  @Override
  public void setGeometry(Geometry geometry) {
    if (geometry == null || geometry instanceof Point) {
      super.setGeometry(geometry);
    } else {
      throw new IllegalArgumentException("Geometry must be of type 'Point'.");
    }
  }

  @Override
  public WptProperties getProperties() {
    return (WptProperties) super.getProperties();
  }

  @Override
  public void setProperties(FeatureProperties<? extends FeatureSettings> properties) {
    if (properties == null || properties instanceof WptProperties) {
      super.setProperties(properties != null ? properties : new WptProperties());
    } else {
      throw new IllegalArgumentException("Properties must be of type 'WptProperties'.");
    }
  }

}
