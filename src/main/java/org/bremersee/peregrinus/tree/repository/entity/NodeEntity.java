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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christian Bremer
 */
@Document(collection = "directory")
@TypeAlias("Node")
@Getter
@Setter
@EqualsAndHashCode
@ToString
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

  private AccessControlEntity accessControl = new AccessControlEntity();

  NodeEntity() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    this.created = now;
    this.modified = now;
  }

  NodeEntity(
      String id,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String parentId,
      AccessControlEntity accessControl) {
    this.id = id;
    this.created = created;
    this.createdBy = createdBy;
    this.modified = modified;
    this.modifiedBy = modifiedBy;
    this.parentId = parentId;
    this.accessControl = accessControl;
  }

}
