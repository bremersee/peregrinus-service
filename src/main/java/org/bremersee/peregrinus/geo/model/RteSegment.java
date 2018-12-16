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

package org.bremersee.peregrinus.geo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Christian Bremer
 */
@ToString
public class RteSegment {

  @Field("geometry")
  @JsonIgnore
  private Point point;

  @Getter
  @Setter
  private RteSegmentProperties properties;

  @Transient
  @JsonProperty(value = "type", required = true)
  @SuppressWarnings("unused")
  private String getType() {
    return "Feature";
  }

  @Transient
  @JsonProperty(value = "type", required = true)
  @SuppressWarnings("unused")
  private void setType(String type) {
    // ignored
  }

  @JsonProperty("geometry")
  public Point getPoint() {
    return point;
  }

  @JsonProperty("geometry")
  public void setPoint(Point point) {
    this.point = point;
  }
}
