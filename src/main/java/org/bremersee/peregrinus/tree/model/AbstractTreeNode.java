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
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.EmbeddedAccessControl;
import org.bremersee.peregrinus.security.access.EmbeddedAuthorizationSet;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@ToString
@Document(collection = "directory")
@TypeAlias("AbstractTreeNode")
@CompoundIndexes({
    @CompoundIndex(name = "uk_name_parent", def = "{'name': 1, 'parentId': 1 }", unique = true)
})
public abstract class AbstractTreeNode implements Comparable<AbstractTreeNode> {

  @Id
  private String id;

  @Version
  private Long version;

  //@CreatedDate
  @Indexed
  private Date created = new Date();

  //@CreatedBy
  @Indexed
  private String createdBy;

  //@LastModifiedDate
  @Indexed
  private Date modified = new Date();

  //@LastModifiedBy
  @Indexed
  private String modifiedBy;

  private String name;

  @Indexed
  private String parentId;

  private EmbeddedAccessControl accessControl = new EmbeddedAccessControl();

  abstract int orderValue();

  @SuppressWarnings("Duplicates")
  @Override
  public int compareTo(final AbstractTreeNode o) {
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

    if (this instanceof GeoTreeLeaf
        && ((GeoTreeLeaf) this).getFeature() != null
        && (o instanceof GeoTreeLeaf)) {
      return ((GeoTreeLeaf) this).getFeature().compareTo(((GeoTreeLeaf) o).getFeature());
    }

    final String n1 = StringUtils.hasText(getName()) ? getName() : "";
    final String n2 = StringUtils.hasText(o.getName()) ? o.getName() : "";
    return n1.compareToIgnoreCase(n2);
  }
}
