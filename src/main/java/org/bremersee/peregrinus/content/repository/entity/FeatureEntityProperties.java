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

package org.bremersee.peregrinus.content.repository.entity;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
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

  private AccessControlEntity accessControl;

  private OffsetDateTime created;

  @Indexed
  private OffsetDateTime modified;

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
    accessControl = new AccessControlEntity();
    links = new ArrayList<>();
  }

  public FeatureEntityProperties(
      AccessControlEntity accessControl,
      OffsetDateTime created,
      OffsetDateTime modified,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime startTime,
      OffsetDateTime stopTime) {

    setAccessControl(accessControl);
    setCreated(created);
    setModified(modified);
    setName(name);
    setPlainTextDescription(plainTextDescription);
    setMarkdownDescription(markdownDescription);
    setInternalComments(internalComments);
    setLinks(links);
    setStartTime(startTime);
    setStopTime(stopTime);
  }

  public void setAccessControl(AccessControlEntity accessControl) {
    if (accessControl != null) {
      this.accessControl = accessControl;
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
