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

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(exclude = {"parent"})
@Document(collection = "directory")
@CompoundIndexes({
    @CompoundIndex(name = "uk_name_parent", def = "{'name': 1, 'parent': 1 }", unique = true)
})
public abstract class AbstractTreeNode {

  @Id
  private String id;

  @Version
  private Long version;

  //@CreatedDate
  @Indexed(sparse = true)
  private Date created = new Date();

  //@CreatedBy
  @Indexed(sparse = true)
  private String createdBy;

  //@LastModifiedDate
  @Indexed(sparse = true)
  private Date modified = new Date();

  //@LastModifiedBy
  @Indexed(sparse = true)
  private String modifiedBy;

  private String name;

  @DBRef(lazy = true)
  private TreeBranch parent;

  //private String parentId;

}
