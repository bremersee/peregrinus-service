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

import java.time.OffsetDateTime;
import java.util.Date;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * @author Christian Bremer
 */
abstract class AbstractEntityReadConverter<T> implements Converter<Document, T> {

  private final OffsetDateTimeReadConverter dateConverter = new OffsetDateTimeReadConverter();

  private ApplicationContext applicationContext;

  AbstractEntityReadConverter(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  MappingMongoConverter getMongoConverter() {
    return applicationContext.getBean(MappingMongoConverter.class);
  }

  OffsetDateTime convertToOffsetDateTime(Date date) {
    return dateConverter.convert(date);
  }

}