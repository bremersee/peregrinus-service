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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.RestApiExceptionParser;
import org.bremersee.groupman.api.GroupControllerApi;
import org.bremersee.groupman.client.GroupControllerClient;
import org.bremersee.groupman.mock.GroupControllerMock;
import org.bremersee.security.reactive.function.client.JwtAuthenticationTokenAppender;
import org.bremersee.web.reactive.function.client.DefaultWebClientErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Christian Bremer
 */
@Configuration
@EnableConfigurationProperties({PeregrinusProperties.class})
@Slf4j
public class PeregrinusConfiguration {

  private PeregrinusProperties peregrinusProperties;

  @Autowired
  public PeregrinusConfiguration(
      PeregrinusProperties peregrinusProperties) {
    this.peregrinusProperties = peregrinusProperties;
  }

  @Bean
  public GroupControllerApi groupService(final RestApiExceptionParser restApiExceptionParser) {
    if (StringUtils.hasText(peregrinusProperties.getGroupmanBaseUri())) {
      log.info("msg=[Creating http groupman client.] baseUri=[{}]",
          peregrinusProperties.getGroupmanBaseUri());
      final WebClient webClient = WebClient
          .builder()
          .baseUrl(peregrinusProperties.getGroupmanBaseUri())
          .filter(new JwtAuthenticationTokenAppender())
          .build();
      return new GroupControllerClient(
          webClient,
          new DefaultWebClientErrorDecoder(restApiExceptionParser));
    } else {
      log.info("msg=[Creating groupman mock client.]");
      return new GroupControllerMock();
    }
  }

}
