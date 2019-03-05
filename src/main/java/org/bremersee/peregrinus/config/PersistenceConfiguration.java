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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
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
public class PersistenceConfiguration {

  @Primary
  @Bean
  public CustomConversions customConversions() {
    final List<Object> converters = new ArrayList<>(
        GeoJsonConverters.getConvertersToRegister(null));
    converters.add(new LocaleToStringConverter());
    converters.add(new StringToLocaleConverter());
    return new MongoCustomConversions(converters);
  }

  @WritingConverter
  public static class LocaleToStringConverter implements Converter<Locale, String> {

    @Override
    public String convert(Locale locale) {
      return locale != null ? locale.toString() : null;
    }
  }

  @ReadingConverter
  public static class StringToLocaleConverter implements Converter<String, Locale> {

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
}
