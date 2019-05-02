/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.peregrinus.entity.converter;

import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bson.Document;
import org.springframework.context.ApplicationContext;

/**
 * @author Christian Bremer
 */
abstract class AbstractNodeEntityReadConverter<T extends NodeEntity>
    extends AbstractEntityReadConverter<T> {

  AbstractNodeEntityReadConverter(
      ApplicationContext applicationContext) {
    super(applicationContext);
  }

  void convert(Document document, NodeEntity entity) {
    entity.setAcl(getMongoConverter().read(AclEntity.class, document));
    entity.setCreated(convertToOffsetDateTime(document.getDate("created")));
    entity.setCreatedBy(document.getString("createdBy"));
    entity.setId(document.getObjectId("_id").toString());
    entity.setModified(convertToOffsetDateTime(document.getDate("modified")));
    entity.setModifiedBy(document.getString("modifiedBy"));
    entity.setParentId(document.getString("parentId"));
  }

}
