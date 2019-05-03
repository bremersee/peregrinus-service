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
import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.bremersee.peregrinus.entity.converter.EntityConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * The persistence configuration.
 *
 * @author Christian Bremer
 */
@Configuration
@EnableReactiveMongoRepositories
@Slf4j
public class PersistenceConfiguration {

  private ApplicationContext applicationContext;

  /**
   * Instantiates a new persistence configuration.
   *
   * @param applicationContext the application context
   */
  public PersistenceConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Mongo custom conversions.
   *
   * @return the mongo custom conversions
   */
  @Primary
  @Bean
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>(
        GeoJsonConverters.getConvertersToRegister(null));
    converters.addAll(EntityConverters.getConvertersToRegister(applicationContext));
    return new MongoCustomConversions(converters);
  }

}
