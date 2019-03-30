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

import org.bremersee.common.model.AccessControlList;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclMapper;
import org.bremersee.security.access.AclMapperImpl;
import org.bremersee.security.access.PermissionConstants;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christian Bremer
 */
@Configuration
@SuppressWarnings("Duplicates")
public class ModelMapperConfiguration {

  @Bean
  public AclMapper<AclEntity> aclMapper() {
    return new AclMapperImpl<>(
        AclEntity::new,
        PermissionConstants.ALL,
        true,
        false);
  }

  @Bean
  public ModelMapper modelMapper() {
    final ModelMapper modelMapper = new ModelMapper();
    modelMapper
        .typeMap(AccessControlList.class, AclEntity.class)
        .setConverter(context -> aclMapper().map(context.getSource()));
    modelMapper
        .typeMap(AclEntity.class, AccessControlList.class)
        .setConverter(context -> aclMapper().map(context.getSource()));
    modelMapper
        .typeMap(String.class, Feature.class)
        .setConverter(context -> null);

    /*
    final Provider<Set> linkedHashSetProvider = request -> new LinkedHashSet<>();
    modelMapper
        .typeMap(AuthorizationSetDto.class, AceEntity.class)
        .addMappings(mapping -> {
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetDto::getGroups, AceEntity::setGroups);
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetDto::getRoles, AceEntity::setRoles);
          mapping
              .with(linkedHashSetProvider)
              .map(AuthorizationSetDto::getUsers, AceEntity::setUsers);
        });
    modelMapper
        .typeMap(AceEntity.class, AuthorizationSetDto.class)
        .addMappings(mapping -> {
          mapping
              .with(linkedHashSetProvider)
              .map(AceEntity::getGroups, AuthorizationSetDto::setGroups);
          mapping
              .with(linkedHashSetProvider)
              .map(AceEntity::getRoles, AuthorizationSetDto::setRoles);
          mapping
              .with(linkedHashSetProvider)
              .map(AceEntity::getUsers, AuthorizationSetDto::setUsers);
        });
    */
    return modelMapper;
  }

}
