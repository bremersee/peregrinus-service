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

import org.bremersee.security.OAuth2Properties;
import org.bremersee.security.authentication.KeycloakReactiveJwtConverter;
import org.bremersee.security.authentication.PasswordFlowReactiveAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

/**
 * The security support configuration.
 *
 * @author Christian Bremer
 */
@Configuration
@EnableConfigurationProperties({OAuth2Properties.class})
public class SecuritySupportConfiguration {

  private OAuth2Properties properties;

  /**
   * Instantiates a new security support configuration.
   *
   * @param properties the properties
   */
  @Autowired
  public SecuritySupportConfiguration(OAuth2Properties properties) {
    this.properties = properties;
  }

  /**
   * Creates keycloak jwt converter bean.
   *
   * @return the keycloak jwt converter
   */
  @Bean
  public KeycloakReactiveJwtConverter keycloakJwtConverter() {
    return new KeycloakReactiveJwtConverter();
  }

  /**
   * Creates an authentication manager with OAuth2 password flow.
   *
   * @param jwtDecoder           the jwt decoder
   * @param keycloakJwtConverter the keycloak jwt converter
   * @return the authentication manager with OAuth2 password flow
   */
  @Bean
  public PasswordFlowReactiveAuthenticationManager passwordFlowReactiveAuthenticationManager(
      ReactiveJwtDecoder jwtDecoder,
      KeycloakReactiveJwtConverter keycloakJwtConverter) {

    final PasswordFlowReactiveAuthenticationManager manager
        = new PasswordFlowReactiveAuthenticationManager(properties, jwtDecoder);
    manager.setJwtAuthenticationConverter(keycloakJwtConverter);
    return manager;
  }

}
