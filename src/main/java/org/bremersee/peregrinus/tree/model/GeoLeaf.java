/*
 * Copyright 2017 the original author or authors.
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

package org.bremersee.peregrinus.tree.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.content.model.Feature;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christian Bremer
 */
@Document(collection = "directory")
@TypeAlias("GeoLeaf")
@Getter
@Setter
@ToString(callSuper = true)
public class GeoLeaf extends Leaf {

  @DBRef
  private Feature feature; // TODO dbref not supported in reactive

  public GeoLeaf() {
  }

  int orderValue() {
    return 50;
  }

  @Override
  public String getName() {
    if (super.getName() == null && feature != null && feature.getProperties() != null) {
      super.setName(feature.getProperties().getName());
    }
    return super.getName() != null ? super.getName() : "unknown";
  }

}
