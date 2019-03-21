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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Clock;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.AccessControl;

/**
 * @author Christian Bremer
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @Type(value = Branch.class, name = "branch"),
    @Type(value = FeatureLeaf.class, name = "feature-leaf")
})
@Getter
@Setter
@ToString
public abstract class Node<S extends NodeSettings> {

  private String id;

  private OffsetDateTime created;

  private String createdBy;

  private OffsetDateTime modified;

  private String modifiedBy;

  private AccessControl accessControl = new AccessControl();

  private S settings;

  private String parentId;

  private String name;

  public Node() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    created = now;
    modified = now;
  }

  public Node(
      final String userId,
      final String parentId,
      final AccessControl accessControl,
      final S settings) {
    this.createdBy = userId;
    this.modifiedBy = userId;
    this.parentId = parentId;
    this.accessControl = accessControl;
    this.settings = settings;
  }

}
