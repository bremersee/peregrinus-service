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

import static org.bremersee.peregrinus.entity.TypeAliases.FEATURE_LEAF;

import org.bremersee.peregrinus.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.entity.LeafEntity;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.convert.ReadingConverter;

/**
 * The leaf entity read converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class LeafEntityReadConverter extends AbstractNodeEntityReadConverter<LeafEntity> {

  /**
   * Instantiates a new leaf entity read converter.
   *
   * @param applicationContext the application context
   */
  LeafEntityReadConverter(
      ApplicationContext applicationContext) {
    super(applicationContext);
  }

  @Override
  public LeafEntity convert(Document document) {
    final String typeAlias = document.getString("_class");
    if (FEATURE_LEAF.equalsIgnoreCase(typeAlias)) {
      FeatureLeafEntity entity = new FeatureLeafEntity();
      entity.setFeatureId(document.getString("featureId"));
      convert(document, entity);
      return entity;
    }
    throw new ConverterNotFoundException(
        TypeDescriptor.valueOf(Document.class),
        TypeDescriptor.valueOf(LeafEntity.class));
  }

}
