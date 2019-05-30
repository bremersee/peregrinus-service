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

package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Clock;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;

/**
 * The tree node.
 *
 * @param <S> the node settings type parameter
 * @author Christian Bremer
 */
@ApiModel(description = "The tree node.", discriminator = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type", visible = true)
@JsonSubTypes({
    @Type(value = Branch.class, name = "branch"),
    @Type(value = FeatureLeaf.class, name = "feature-leaf")
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class Node<S extends NodeSettings> implements Comparable<Node> {

  @ApiModelProperty("The node ID.")
  private String id;

  @ApiModelProperty("The date of creation.")
  private OffsetDateTime created;

  @ApiModelProperty("The user ID of the creator.")
  private String createdBy;

  @ApiModelProperty("The date of last modification.")
  private OffsetDateTime modified;

  @ApiModelProperty("The ID of the user who made the last modification.")
  private String modifiedBy;

  @ApiModelProperty("The access control list.")
  private AccessControlList acl;

  @ApiModelProperty("The node settings.")
  private S settings;

  @ApiModelProperty("The parent ID.")
  private String parentId;

  @ApiModelProperty(value = "The name.", required = true)
  @JsonProperty(value = "name", required = true)
  private String name;

  /**
   * Instantiates a new tree node.
   */
  Node() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    created = now;
    modified = now;
    acl = new AccessControlList();
  }

  /**
   * Instantiates a new tree node.
   *
   * @param id         the id
   * @param created    the created
   * @param createdBy  the created by
   * @param modified   the modified
   * @param modifiedBy the modified by
   * @param acl        the acl
   * @param settings   the settings
   * @param parentId   the parent id
   * @param name       the name
   */
  Node(
      String id,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      AccessControlList acl,
      S settings,
      String parentId,
      String name) {

    final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
    this.created = created == null ? now : created;
    this.modified = modified == null ? now : modified;
    setId(id);
    setCreated(created);
    setCreatedBy(createdBy);
    setModified(modified);
    setModifiedBy(modifiedBy);
    setAcl(acl);
    setSettings(settings);
    setParentId(parentId);
    setName(name);
  }

  /**
   * Sets created.
   *
   * @param created the created
   */
  public void setCreated(final OffsetDateTime created) {
    if (created != null) {
      this.created = created;
    }
  }

  /**
   * Sets modified.
   *
   * @param modified the modified
   */
  public void setModified(final OffsetDateTime modified) {
    if (modified != null) {
      this.modified = modified;
    }
  }

  /**
   * Sets acl.
   *
   * @param acl the acl
   */
  public void setAcl(final AccessControlList acl) {
    if (acl != null) {
      this.acl = acl;
    }
  }

  /**
   * Sets settings.
   *
   * @param settings the settings
   */
  public void setSettings(final S settings) {
    if (settings != null) {
      this.settings = settings;
    }
  }

  @Override
  public int compareTo(Node other) {
    if (this instanceof Branch && !(other instanceof Branch)) {
      return -1;
    }
    if (other instanceof Branch && !(this instanceof Branch)) {
      return 1;
    }
    return 0;
  }
}
