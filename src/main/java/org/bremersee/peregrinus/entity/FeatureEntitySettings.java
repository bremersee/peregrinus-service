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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christian Bremer
 */
@Document(collection = "feature-settings")
@TypeAlias("FeatureSettings")
@CompoundIndexes({
    @CompoundIndex(name = "uk_feature_user", def = "{'featureId': 1, 'userId': 1}", unique = true)
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class FeatureEntitySettings {

  @Id
  private String id;

  @Indexed
  private String featureId;

  @Indexed
  private String userId;

  public FeatureEntitySettings(String id, String featureId, String userId) {
    this.id = id;
    this.featureId = featureId;
    this.userId = userId;
  }
}
