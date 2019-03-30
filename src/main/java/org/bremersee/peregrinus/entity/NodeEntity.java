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

package org.bremersee.peregrinus.entity;

import java.time.Clock;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
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

  public static final String ID_PATH = "id";

  public static final String ACL_PATH = "acl";

  public static final String PARENT_ID_PATH = "parentId";

  @Id
  private String id;

  private OffsetDateTime created;

  private String createdBy;

  private OffsetDateTime modified;

  private String modifiedBy;

  @Indexed
  private String parentId;

  private AclEntity acl;

  NodeEntity() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    this.created = now;
    this.modified = now;
    this.acl = AclBuilder.builder().defaults(PermissionConstants.ALL).build(AclEntity::new);
  }

  NodeEntity(
      String id,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String parentId,
      AclEntity acl) {

    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    this.created = created == null ? now : created;
    this.modified = modified == null ? now : modified;
    setId(id);
    setCreated(created);
    setCreatedBy(createdBy);
    setModified(modified);
    setModifiedBy(modifiedBy);
    setAcl(acl);
    setParentId(parentId);
  }

  public void setCreated(final OffsetDateTime created) {
    if (created != null) {
      this.created = created;
    }
  }

  public void setModified(final OffsetDateTime modified) {
    if (modified != null) {
      this.modified = modified;
    }
  }

  public void setAcl(final AclEntity acl) {
    if (acl != null) {
      this.acl = acl;
    }
  }

}
