/*
 * Copyright 2017 the original author or authors.
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

import org.bremersee.exception.RestApiExceptionMapper;
import org.bremersee.exception.RestApiExceptionMapperImpl;
import org.bremersee.exception.RestApiExceptionMapperProperties;
import org.bremersee.exception.RestApiExceptionParser;
import org.bremersee.exception.RestApiExceptionParserImpl;
import org.bremersee.web.reactive.ApiExceptionHandler;
import org.bremersee.web.reactive.function.client.DefaultWebClientErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author Christian Bremer
 */
@Configuration
@EnableConfigurationProperties({RestApiExceptionMapperProperties.class})
public class ExceptionConfiguration {

  private final String applicationName;

  private final RestApiExceptionMapperProperties apiExceptionMapperProperties;

  @Autowired
  public ExceptionConfiguration(
      @Value("${spring.application.name:peregrinus}") String applicationName,
      RestApiExceptionMapperProperties apiExceptionMapperProperties) {
    this.applicationName = applicationName;
    this.apiExceptionMapperProperties = apiExceptionMapperProperties;
  }

  @Bean
  public RestApiExceptionMapper restApiExceptionMapper() {
    return new RestApiExceptionMapperImpl(apiExceptionMapperProperties, applicationName);
  }

  @Bean
  public RestApiExceptionParser restApiExceptionParser(
      Jackson2ObjectMapperBuilder objectMapperBuilder) {
    return new RestApiExceptionParserImpl(objectMapperBuilder);
  }

  @Primary
  @Bean("defaultWebClientErrorDecoder")
  public DefaultWebClientErrorDecoder defaultWebClientErrorDecoder(
      RestApiExceptionParser parser) {
    return new DefaultWebClientErrorDecoder(parser);
  }

  @Bean
  @Order(-2)
  public ApiExceptionHandler apiExceptionHandler(
      ErrorAttributes errorAttributes,
      ResourceProperties resourceProperties,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer,
      RestApiExceptionMapper restApiExceptionMapper) {

    return new ApiExceptionHandler(
        errorAttributes,
        resourceProperties,
        applicationContext,
        serverCodecConfigurer,
        restApiExceptionMapper);
  }

}
