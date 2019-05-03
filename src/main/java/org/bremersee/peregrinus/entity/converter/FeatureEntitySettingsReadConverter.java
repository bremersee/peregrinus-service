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

import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.entity.RteEntitySettings;
import org.bremersee.peregrinus.entity.TrkEntitySettings;
import org.bremersee.peregrinus.entity.TypeAliases;
import org.bremersee.peregrinus.entity.WptEntitySettings;
import org.bremersee.peregrinus.model.DisplayColor;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.convert.ReadingConverter;

/**
 * The feature entity settings read converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class FeatureEntitySettingsReadConverter
    extends AbstractEntityReadConverter<FeatureEntitySettings> {

  /**
   * Instantiates a new feature entity settings read converter.
   *
   * @param applicationContext the application context
   */
  FeatureEntitySettingsReadConverter(ApplicationContext applicationContext) {
    super(applicationContext);
  }

  @Override
  public FeatureEntitySettings convert(Document document) {

    final String typeAlias = document.getString("_class");
    if (TypeAliases.RTE_SETTINGS.equalsIgnoreCase(typeAlias)) {
      RteEntitySettings entity = new RteEntitySettings();
      convert(document, entity);
      entity.setDisplayColor(DisplayColor.fromValue(document.getString("displayColor")));
      return entity;
    }
    if (TypeAliases.TRK_SETTINGS.equalsIgnoreCase(typeAlias)) {
      TrkEntitySettings entity = new TrkEntitySettings();
      convert(document, entity);
      entity.setDisplayColor(DisplayColor.fromValue(document.getString("displayColor")));
      return entity;
    }
    if (TypeAliases.WPT_SETTINGS.equalsIgnoreCase(typeAlias)) {
      WptEntitySettings entity = new WptEntitySettings();
      convert(document, entity);
      return entity;
    }
    throw new ConverterNotFoundException(
        TypeDescriptor.valueOf(Document.class),
        TypeDescriptor.valueOf(FeatureEntitySettings.class));
  }

  /**
   * Convert mongo document to feature entity settings.
   *
   * @param document the document
   * @param entity   the feature entity settings
   */
  void convert(Document document, FeatureEntitySettings entity) {
    entity.setId(document.getObjectId("_id").toString());
    entity.setFeatureId(document.getString("featureId"));
    entity.setUserId(document.getString("userId"));
  }
}
