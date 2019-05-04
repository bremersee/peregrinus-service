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
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;

/**
 * The GeoJSON feature properties entity.
 *
 * @author Christian Bremer
 */
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

  @TextIndexed(weight = 4f)
  private String name;

  @TextIndexed
  private String plainTextDescription;

  @TextIndexed(weight = 2f)
  private String markdownDescription;

  private List<Link> links;

  private AddressEntity address;

  @Indexed
  private OffsetDateTime departureTime;

  @Indexed
  private OffsetDateTime arrivalTime;

  /**
   * Instantiates a new GeoJSON feature properties entity.
   */
  FeatureEntityProperties() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    created = now;
    modified = now;
    acl = AclBuilder.builder().defaults(PermissionConstants.ALL).build(AclEntity::new);
    links = new ArrayList<>();
  }

  /**
   * Instantiates a new GeoJSON feature properties entity.
   *
   * @param acl                  the acl
   * @param created              the created
   * @param createdBy            the created by
   * @param modified             the modified
   * @param modifiedBy           the modified by
   * @param name                 the name
   * @param plainTextDescription the plain text description
   * @param markdownDescription  the markdown description
   * @param links                the links
   * @param address              the address
   * @param departureTime        the departure time
   * @param arrivalTime          the arrival time
   */
  FeatureEntityProperties(
      AclEntity acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      List<Link> links,
      AddressEntity address,
      OffsetDateTime departureTime,
      OffsetDateTime arrivalTime) {

    setAcl(acl);
    setCreated(created);
    setCreatedBy(createdBy);
    setModified(modified);
    setModifiedBy(modifiedBy);
    setName(name);
    setPlainTextDescription(plainTextDescription);
    setMarkdownDescription(markdownDescription);
    setLinks(links);
    setAddress(address);
    setDepartureTime(departureTime);
    setArrivalTime(arrivalTime);
  }

  /**
   * Sets acl.
   *
   * @param acl the acl
   */
  public void setAcl(AclEntity acl) {
    if (acl != null) {
      this.acl = acl;
    }
  }

  /**
   * Sets created.
   *
   * @param created the created
   */
  public void setCreated(OffsetDateTime created) {
    if (created != null) {
      this.created = created;
    }
  }

  /**
   * Sets modified.
   *
   * @param modified the modified
   */
  public void setModified(OffsetDateTime modified) {
    if (modified != null) {
      this.modified = modified;
    }
  }

  /**
   * Sets links.
   *
   * @param links the links
   */
  public void setLinks(List<Link> links) {
    if (links == null) {
      this.links = new ArrayList<>();
    } else {
      this.links = links;
    }
  }

}
