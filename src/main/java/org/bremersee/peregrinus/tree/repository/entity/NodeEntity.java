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

package org.bremersee.peregrinus.tree.repository.entity;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Document(collection = "directory")
@TypeAlias("Node")
@Getter
@Setter
@ToString
@Validated
public abstract class NodeEntity {

  @Id
  private String id;

  @Indexed
  private OffsetDateTime created;

  @Indexed
  private String createdBy;

  @Indexed
  private OffsetDateTime modified;

  @Indexed
  private String modifiedBy;

  @Indexed
  private String parentId;

  private AccessControl accessControl = new AccessControl();

  public NodeEntity() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.system(ZoneId.of("Z")));
    this.created = now;
    this.modified = now;
  }

  public NodeEntity(
      @Nullable String parentId,
      @NotNull String owner) {
    this();
    this.accessControl.setOwner(owner);
    this.createdBy = owner;
    this.modifiedBy = owner;
    this.parentId = parentId;
  }

  public NodeEntity(
      @Nullable String parentId,
      @NotNull AccessControl accessControl) {
    this();
    Assert.hasText(accessControl.getOwner(), "Owner must be present.");
    this.accessControl = accessControl;
    this.createdBy = accessControl.getOwner();
    this.modifiedBy = accessControl.getOwner();
    this.parentId = parentId;
  }

}
