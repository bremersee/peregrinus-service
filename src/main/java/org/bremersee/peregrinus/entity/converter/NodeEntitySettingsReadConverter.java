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

import static org.bremersee.peregrinus.entity.TypeAliases.BRANCH_SETTINGS;

import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.bremersee.peregrinus.entity.NodeEntitySettings;
import org.bremersee.peregrinus.entity.TypeAliases;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.convert.ReadingConverter;

/**
 * The node entity settings read converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class NodeEntitySettingsReadConverter
    extends AbstractNodeEntitySettingsReadConverter<NodeEntitySettings> {

  private LeafEntitySettingsReadConverter leafSettingsConverter;

  /**
   * Instantiates a new node entity settings read converter.
   *
   * @param applicationContext the application context
   */
  NodeEntitySettingsReadConverter(
      ApplicationContext applicationContext) {
    super(applicationContext);
    leafSettingsConverter = new LeafEntitySettingsReadConverter(applicationContext);
  }

  @Override
  public NodeEntitySettings convert(Document document) {
    final String typeAlias = document.getString("_class");
    if (BRANCH_SETTINGS.equalsIgnoreCase(typeAlias)) {
      BranchEntitySettings entity = new BranchEntitySettings();
      entity.setOpen(document.getBoolean("open", false));
      convert(document, entity);
      return entity;
    }
    if (TypeAliases.isLeafSettings(typeAlias)) {
      return leafSettingsConverter.convert(document);
    }
    throw new ConverterNotFoundException(
        TypeDescriptor.valueOf(Document.class),
        TypeDescriptor.valueOf(NodeEntitySettings.class));
  }

}
