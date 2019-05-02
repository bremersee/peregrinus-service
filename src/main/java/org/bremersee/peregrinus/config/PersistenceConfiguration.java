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

package org.bremersee.peregrinus.config;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.bremersee.peregrinus.entity.converter.EntityConverters;
import org.bremersee.peregrinus.entity.converter.OffsetDateTimeReadConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@Configuration
@EnableReactiveMongoRepositories
@Slf4j
public class PersistenceConfiguration {

  private ApplicationContext applicationContext;

  public PersistenceConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Primary
  @Bean
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>(
        GeoJsonConverters.getConvertersToRegister(null));
    converters.add(new LocaleWriteConverter());
    converters.add(new LocaleReadConverter());
    converters.add(new OffsetDateTimeWriteConverter());
    converters.add(new OffsetDateTimeReadConverter());
    converters.addAll(EntityConverters.getConvertersToRegister(applicationContext));
    return new MongoCustomConversions(converters);
  }

  @WritingConverter
  public static class LocaleWriteConverter implements Converter<Locale, String> {

    @Override
    public String convert(Locale locale) {
      return locale != null ? locale.toString() : null;
    }
  }

  @ReadingConverter
  public static class LocaleReadConverter implements Converter<String, Locale> {

    @Override
    public Locale convert(String s) {
      if (StringUtils.hasText(s)) {
        String[] parts = s.split(Pattern.quote("_"));
        switch (parts.length) {
          case 1:
            return new Locale(parts[0]);
          case 2:
            return new Locale(parts[0], parts[1]);
          default:
            return new Locale(parts[0], parts[1], parts[2]);
        }
      }
      return null;
    }
  }

  @WritingConverter
  public static class OffsetDateTimeWriteConverter
      implements Converter<OffsetDateTime, Date> {

    @Override
    public Date convert(OffsetDateTime offsetDateTime) {
      if (offsetDateTime == null) {
        return null;
      }
      return Date.from(offsetDateTime.toInstant());
    }
  }

//  @ReadingConverter
//  public static class NodeEntityReadConverter implements Converter<Document, NodeEntity> {
//
//    private ApplicationContext applicationContext;
//
//    public NodeEntityReadConverter(ApplicationContext applicationContext) {
//      this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public NodeEntity convert(Document source) {
//
//      System.out.println("++++++++ " + applicationContext.getBean(MongoConverter.class));
//
//      MappingMongoConverter converter = (MappingMongoConverter)applicationContext.getBean(MongoConverter.class);
//
//      return BranchEntity
//          .builder()
//          .acl(converter.read(AclEntity.class, source.get("acl", Document.class)))
//          .created(new DateToOffsetDateTimeConverter().convert(source.getDate("created")))
//          .createdBy(source.getString("createdBy"))
//          .id(source.getObjectId("_id").toString())
//          .modified(new DateToOffsetDateTimeConverter().convert(source.getDate("modified")))
//          .modifiedBy(source.getString("modifiedBy"))
//          .name(source.getString("name"))
//          .parentId(source.getString("parentId"))
//          .build();
//    }
//  }
}
