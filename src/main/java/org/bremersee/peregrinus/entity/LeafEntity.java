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

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The tree leaf entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "directory")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LeafEntity extends NodeEntity {

  /**
   * Instantiates a new tree leaf entity.
   *
   * @param id         the id
   * @param created    the created
   * @param createdBy  the created by
   * @param modified   the modified
   * @param modifiedBy the modified by
   * @param parentId   the parent id
   * @param acl        the acl
   */
  LeafEntity(
      String id,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String parentId,
      AclEntity acl) {
    super(id, created, createdBy, modified, modifiedBy, parentId, acl);
  }

}
