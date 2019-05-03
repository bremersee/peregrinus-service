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

import org.bremersee.peregrinus.entity.BranchEntity;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.data.convert.ReadingConverter;

/**
 * The branch entity read converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class BranchEntityReadConverter extends AbstractEntityReadConverter<BranchEntity> {

  private NodeEntityReadConverter converter;

  /**
   * Instantiates a branch entity read converter.
   *
   * @param applicationContext the application context
   */
  BranchEntityReadConverter(ApplicationContext applicationContext) {
    super(applicationContext);
    converter = new NodeEntityReadConverter(applicationContext);
  }

  @Override
  public BranchEntity convert(Document document) {
    return (BranchEntity) converter.convert(document);
  }
}
