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

package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.common.model.Link;

/**
 * @author Christian Bremer
 */
@ApiModel(
    value = "FeatureProperties",
    description = "Common properties of a GeoJSON feature.",
    discriminator = "_type",
    subTypes = {
        PtProperties.class,
        TrkProperties.class,
        RteProperties.class
    })
@JsonTypeInfo(use = Id.NAME, property = "_type")
@JsonSubTypes({
    @Type(value = WptProperties.class, name = Feature.WPT_TYPE),
    @Type(value = TrkProperties.class, name = Feature.TRK_TYPE),
    @Type(value = RteProperties.class, name = Feature.RTE_TYPE),
    @Type(value = RtePtProperties.class, name = Feature.RTE_PT_TYPE)
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class FeatureProperties<S extends FeatureSettings> {

  private AccessControlList acl;

  private OffsetDateTime created;

  private String createdBy;

  private OffsetDateTime modified;

  private String modifiedBy;

  @ApiModelProperty(value = "The name of the feature.", required = true)
  @JsonProperty(value = "name", required = true)
  private String name;

  private String plainTextDescription;

  private String markdownDescription;

  private String internalComments;

  private List<Link> links;

  private OffsetDateTime departureTime;

  private OffsetDateTime arrivalTime;

  @ApiModelProperty(
      value = "The private settings.",
      dataType = "org.bremersee.peregrinus.model.FeatureLeafSettings")
  private S settings;

  public FeatureProperties() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    created = now;
    modified = now;
    acl = new AccessControlList();
    links = new ArrayList<>();
  }

  public FeatureProperties(
      AccessControlList acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime departureTime,
      OffsetDateTime arrivalTime,
      S settings) {

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
    setDepartureTime(departureTime);
    setArrivalTime(arrivalTime);
    setSettings(settings);
  }

  public void setAcl(AccessControlList acl) {
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

  public void setSettings(S settings) {
    if (settings != null) {
      this.settings = settings;
    }
  }

  @SuppressWarnings("WeakerAccess")
  protected void noAcl() {
    this.acl = null;
  }

  @SuppressWarnings("WeakerAccess")
  protected void noSettings() {
    this.settings = null;
  }
}
