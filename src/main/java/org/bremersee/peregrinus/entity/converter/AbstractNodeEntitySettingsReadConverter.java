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

import org.bremersee.peregrinus.entity.NodeEntitySettings;
import org.bson.Document;
import org.springframework.context.ApplicationContext;

/**
 * @author Christian Bremer
 */
abstract class AbstractNodeEntitySettingsReadConverter<T extends NodeEntitySettings>
    extends AbstractEntityReadConverter<T> {

  AbstractNodeEntitySettingsReadConverter(
      ApplicationContext applicationContext) {
    super(applicationContext);
  }

  void convert(Document document, NodeEntitySettings entity) {
    entity.setId(document.getObjectId("_id").toString());
    entity.setNodeId(document.getString("nodeId"));
    entity.setUserId(document.getString("userId"));
  }

}
