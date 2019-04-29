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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
 * The GeoJSON feature properties.
 *
 * @param <S> the settings type parameter
 * @author Christian Bremer
 */
@ApiModel(description = "Common properties of a GeoJSON feature.", discriminator = "_type")
@JsonTypeInfo(use = Id.NAME, property = "_type")
@JsonSubTypes({
    @Type(value = WptProperties.class, name = Feature.WPT_TYPE),
    @Type(value = TrkProperties.class, name = Feature.TRK_TYPE),
    @Type(value = RteProperties.class, name = Feature.RTE_TYPE)
})
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class FeatureProperties<S extends FeatureSettings> {

  @ApiModelProperty("The access control list.")
  private AccessControlList acl;

  @ApiModelProperty("The date of creation.")
  private OffsetDateTime created;

  @ApiModelProperty("The user ID of the creator.")
  private String createdBy;

  @ApiModelProperty("The date of last modification.")
  private OffsetDateTime modified;

  @ApiModelProperty("The ID of the user who made the last modification.")
  private String modifiedBy;

  @ApiModelProperty(value = "The name of the feature.", required = true)
  @JsonProperty(value = "name", required = true)
  private String name;

  @ApiModelProperty("The plain text description.")
  private String plainTextDescription;

  @ApiModelProperty("The markdown description.")
  private String markdownDescription;

  @ApiModelProperty("Links to other resources.")
  private List<Link> links;

  @ApiModelProperty(
      value = "The private settings.",
      dataType = "org.bremersee.peregrinus.model.FeatureLeafSettings")
  private S settings;

  /**
   * Instantiates new GeoJSON feature properties.
   */
  public FeatureProperties() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    created = now;
    modified = now;
    acl = new AccessControlList();
    links = new ArrayList<>();
  }

  /**
   * Instantiates new GeoJSON feature properties.
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
   * @param settings             the settings
   */
  public FeatureProperties(
      AccessControlList acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      List<Link> links,
      S settings) {

    setAcl(acl);
    setCreated(created);
    setCreatedBy(createdBy);
    setModified(modified);
    setModifiedBy(modifiedBy);
    setName(name);
    setPlainTextDescription(plainTextDescription);
    setMarkdownDescription(markdownDescription);
    setLinks(links);
    setSettings(settings);
  }

  /**
   * Sets acl.
   *
   * @param acl the acl
   */
  public void setAcl(AccessControlList acl) {
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

  /**
   * Sets settings.
   *
   * @param settings the settings
   */
  public void setSettings(S settings) {
    if (settings != null) {
      this.settings = settings;
    }
  }

  /**
   * No acl.
   */
  @SuppressWarnings({"unused"})
  protected void noAcl() {
    this.acl = null;
  }

  /**
   * No settings.
   */
  @SuppressWarnings({"unused"})
  protected void noSettings() {
    this.settings = null;
  }
}
