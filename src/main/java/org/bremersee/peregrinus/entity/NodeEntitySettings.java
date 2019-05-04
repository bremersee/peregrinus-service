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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The tree node settings entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "directory-settings")
@CompoundIndexes({
    @CompoundIndex(name = "uk_node_user", def = "{'nodeId': 1, 'userId': 1 }", unique = true)
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class NodeEntitySettings {

  /**
   * The constant NODE_ID_PATH.
   */
  public static final String NODE_ID_PATH = "nodeId";

  /**
   * The constant USER_ID_PATH.
   */
  public static final String USER_ID_PATH = "userId";

  @Id
  private String id;

  @Indexed
  private String nodeId;

  @Indexed
  private String userId;

  /**
   * Instantiates a new tree node settings entity.
   *
   * @param id     the id
   * @param nodeId the node id
   * @param userId the user id
   */
  NodeEntitySettings(String id, String nodeId, String userId) {
    this.id = id;
    this.nodeId = nodeId;
    this.userId = userId;
  }

}
