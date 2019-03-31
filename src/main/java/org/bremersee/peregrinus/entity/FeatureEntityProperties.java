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

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Christian Bremer
 */
@TypeAlias("FeatureProperties")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class FeatureEntityProperties {

  private AclEntity acl;

  private OffsetDateTime created;

  private String createdBy;

  @Indexed
  private OffsetDateTime modified;

  private String modifiedBy;

  @Indexed
  private String name;

  private String plainTextDescription; // desc == cmt

  private String markdownDescription;

  private String internalComments;

  private List<Link> links;

  /**
   * Start time of tracks.
   */
  @Indexed
  private OffsetDateTime startTime;

  /**
   * Stop time of tracks.
   */
  @Indexed
  private OffsetDateTime stopTime;

  public FeatureEntityProperties() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    created = now;
    modified = now;
    acl = AclBuilder.builder().defaults(PermissionConstants.ALL).build(AclEntity::new);
    links = new ArrayList<>();
  }

  public FeatureEntityProperties(
      AclEntity acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime startTime,
      OffsetDateTime stopTime) {

    setAcl(acl);
    setCreated(created);
    setCreatedBy(createdBy);
    setModified(modified);
    setModifiedBy(modifiedBy);
    setName(name);
    setPlainTextDescription(plainTextDescription);
    setMarkdownDescription(markdownDescription);
    setInternalComments(internalComments);
    setLinks(links);
    setStartTime(startTime);
    setStopTime(stopTime);
  }

  public void setAcl(AclEntity acl) {
    if (acl != null) {
      this.acl = acl;
    }
  }

  public void setCreated(OffsetDateTime created) {
    if (created != null) {
      this.created = created;
    }
  }

  public void setModified(OffsetDateTime modified) {
    if (modified != null) {
      this.modified = modified;
    }
  }

  public void setLinks(List<Link> links) {
    if (links == null) {
      this.links = new ArrayList<>();
    } else {
      this.links = links;
    }
  }

}
