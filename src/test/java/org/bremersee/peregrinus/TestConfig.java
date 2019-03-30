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

import java.util.ServiceLoader;
import lombok.Getter;
import org.bremersee.peregrinus.config.ModelMapperConfiguration;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclMapper;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.modelmapper.ModelMapper;

/**
 * @author Christian Bremer
 */
public abstract class TestConfig {

  @Getter
  private static final JaxbContextBuilder jaxbContextBuilder;

  @Getter
  private static final ModelMapper modelMapper;

  @Getter
  private static final AclMapper<AclEntity> aclMapper;

  static {
    ModelMapperConfiguration modelMapperConfiguration = new ModelMapperConfiguration();
    jaxbContextBuilder = JaxbContextBuilder.builder().processAll(
        ServiceLoader.load(JaxbContextDataProvider.class));
    modelMapper = modelMapperConfiguration.modelMapper();
    aclMapper = modelMapperConfiguration.aclMapper();
  }

  private TestConfig() {
  }
}
