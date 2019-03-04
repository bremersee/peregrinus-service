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
import java.time.Instant;
import java.time.ZoneId;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.geo.model.RteSettings;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Document(collection = "directory")
@TypeAlias("AbstractNode")
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @Type(value = RteSettings.class, name = "rte-settings")
})
@Getter
@Setter
@ToString
@Validated
public abstract class AbstractNode implements Comparable<AbstractNode> {

  @Id
  private String id;

  @Version
  private Long version;

  @Indexed
  private Instant created;

  @Indexed
  private String createdBy;

  @Indexed
  private Instant modified;

  @Indexed
  private String modifiedBy;

  @Indexed
  private String parentId;

  //@NotNull(message = "Access control must not be null.")
  private AccessControl accessControl = new AccessControl();

  public AbstractNode() {
    final Instant now = Instant.now(Clock.system(ZoneId.of("UTC")));
    this.created = now;
    this.modified = now;
  }

  public AbstractNode(
      @Nullable String parentId,
      @NotNull String owner) {
    this();
    this.accessControl.setOwner(owner);
    this.createdBy = owner;
    this.modifiedBy = owner;
    this.parentId = parentId;
  }

  public AbstractNode(
      @Nullable String parentId,
      @NotNull AccessControl accessControl) {
    this();
    Assert.hasText(accessControl.getOwner(), "Owner must be present.");
    this.accessControl = accessControl;
    this.createdBy = accessControl.getOwner();
    this.modifiedBy = accessControl.getOwner();
    this.parentId = parentId;
  }

  abstract int orderValue();

  public abstract String getName();

  @SuppressWarnings("Duplicates")
  @Override
  public int compareTo(final AbstractNode o) {
    if (this == o) {
      return 0;
    }
    if (o == null) {
      return -1;
    }
    int c = orderValue() - o.orderValue();
    if (c != 0) {
      return c;
    }

    /*
    if (this instanceof GeoLeaf
        && ((GeoLeaf) this).getFeature() != null
        && (o instanceof GeoLeaf)) {
      return ((GeoLeaf) this).getFeature().compareTo(((GeoLeaf) o).getFeature());
    }
    */

    final String n1 = StringUtils.hasText(getName()) ? getName() : "";
    final String n2 = StringUtils.hasText(o.getName()) ? o.getName() : "";
    return n1.compareToIgnoreCase(n2);
  }

}
