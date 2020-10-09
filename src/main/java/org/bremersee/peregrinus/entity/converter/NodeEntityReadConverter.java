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

import static org.bremersee.peregrinus.entity.TypeAliases.BRANCH;

import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bremersee.peregrinus.entity.TypeAliases;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.convert.ReadingConverter;

/**
 * The node entity read converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class NodeEntityReadConverter extends AbstractNodeEntityReadConverter<NodeEntity> {

  private final LeafEntityReadConverter leafConverter;

  /**
   * Instantiates a new node entity read converter.
   *
   * @param applicationContext the application context
   */
  NodeEntityReadConverter(
      ApplicationContext applicationContext) {
    super(applicationContext);
    this.leafConverter = new LeafEntityReadConverter(applicationContext);
  }

  @Override
  public NodeEntity convert(Document document) {
    final String typeAlias = document.getString("_class");
    if (BRANCH.equalsIgnoreCase(typeAlias)) {
      BranchEntity entity = new BranchEntity();
      entity.setName(document.getString("name"));
      convert(document, entity);
      return entity;
    }
    if (TypeAliases.isLeaf(typeAlias)) {
      return leafConverter.convert(document);
    }
    throw new ConverterNotFoundException(
        TypeDescriptor.valueOf(Document.class),
        TypeDescriptor.valueOf(NodeEntity.class));
  }

}
