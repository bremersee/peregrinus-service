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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import java.util.ArrayList;
import java.util.List;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.bremersee.peregrinus.geo.repository.converter.LocaleToStringConverter;
import org.bremersee.peregrinus.geo.repository.converter.StringToLocaleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@Configuration
public class PersistenceConfiguration { //} extends AbstractMongoConfiguration { // reactive component?

  @Autowired
  private MongoProperties mongoProperties;

  MongoReactiveAutoConfiguration c;

  MongoReactiveDataAutoConfiguration m;

  /*
  @Autowired
  private MongoClientFactory mongoClientFactory;

  @Autowired
  private MongoClientFactoryBean mongoClientFactoryBean;
  */

  //@Autowired
  private MongoClient mongoClient;

  /*
  @Override
  public MongoClient mongoClient() {
    if (StringUtils.hasText(mongoProperties.getUri())) {
      return new MongoClient(mongoProperties.getUri(), MongoClientOptions.builder().build());
    }
    return new MongoClient(mongoProperties.getHost(), mongoProperties.getPort());
  }

  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDatabase();
  }
  */

  @Primary
  @Bean
  public CustomConversions customConversions() {
    final List<Object> converters = new ArrayList<>(
        GeoJsonConverters.getConvertersToRegister(null));
    converters.add(new LocaleToStringConverter());
    converters.add(new StringToLocaleConverter());
    return new MongoCustomConversions(converters);
  }

}
