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

package org.bremersee.peregrinus.config;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bremersee.peregrinus.security.access.model.AuthorizationSetDto;
import org.bremersee.peregrinus.security.access.repository.entity.AuthorizationSetEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christian Bremer
 */
@Configuration
@SuppressWarnings("Duplicates")
public class ModelMapperConfiguration {

  @Bean
  public ModelMapper modelMapper() {
    final Provider<Set> linkedHashSetProvider = request -> new LinkedHashSet<>();
    final ModelMapper modelMapper = new ModelMapper();
    modelMapper
        .typeMap(AuthorizationSetDto.class, AuthorizationSetEntity.class)
        .addMappings(mapping -> {
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetDto::getGroups, AuthorizationSetEntity::setGroups);
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetDto::getRoles, AuthorizationSetEntity::setRoles);
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetDto::getUsers, AuthorizationSetEntity::setUsers);
        });
    modelMapper
        .typeMap(AuthorizationSetEntity.class, AuthorizationSetDto.class)
        .addMappings(mapping -> {
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetEntity::getGroups, AuthorizationSetDto::setGroups);
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetEntity::getRoles, AuthorizationSetDto::setRoles);
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetEntity::getUsers, AuthorizationSetDto::setUsers);
        });

    return modelMapper;
  }

}
