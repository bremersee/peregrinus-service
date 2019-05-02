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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.peregrinus.entity.FeatureLeafEntitySettings;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.data.convert.ReadingConverter;

/**
 * @author Christian Bremer
 */
@ReadingConverter
@Slf4j
public class FeatureLeafEntitySettingsReadConverter
    extends AbstractEntityReadConverter<FeatureLeafEntitySettings> {

  private LeafEntitySettingsReadConverter leafSettingsConverter;

  FeatureLeafEntitySettingsReadConverter(
      ApplicationContext applicationContext) {
    super(applicationContext);
    leafSettingsConverter = new LeafEntitySettingsReadConverter(applicationContext);
  }

  @Override
  public FeatureLeafEntitySettings convert(Document document) {
    return (FeatureLeafEntitySettings) leafSettingsConverter.convert(document);
  }

}
