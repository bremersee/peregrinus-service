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

package org.bremersee.peregrinus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.TimeZone;
import lombok.Getter;
import org.bremersee.geojson.GeoJsonObjectMapperModule;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.model.PeregrinusObjectMapperModule;
import org.bremersee.security.access.AclMapper;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.modelmapper.ModelMapper;

/**
 * @author Christian Bremer
 */
public abstract class TestConfig {

  @Getter
  private static final ObjectMapper objectMapper;

  @Getter
  private static final JaxbContextBuilder jaxbContextBuilder;

  @Getter
  private static final ModelMapper modelMapper;

  @Getter
  private static final AclMapper<AclEntity> aclMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModules(
        new Jdk8Module(),
        new JavaTimeModule(),
        new GeoJsonObjectMapperModule(),
        new PeregrinusObjectMapperModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    objectMapper.setDateFormat(new StdDateFormat());
    objectMapper.setTimeZone(TimeZone.getTimeZone("GMT"));
    objectMapper.setLocale(Locale.GERMANY);

    //MapperConfiguration modelMapperConfiguration = new MapperConfiguration();
    jaxbContextBuilder = JaxbContextBuilder.builder().processAll(
        ServiceLoader.load(JaxbContextDataProvider.class));
    modelMapper = null; // TODO modelMapperConfiguration.modelMapper();
    aclMapper = null; // TODO modelMapperConfiguration.aclMapper();
  }

  private TestConfig() {
  }
}
