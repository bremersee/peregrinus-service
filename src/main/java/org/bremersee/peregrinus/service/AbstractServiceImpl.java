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

package org.bremersee.peregrinus.service;

import static org.springframework.util.Assert.notNull;

import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclMapper;

/**
 * @author Christian Bremer
 */
@Slf4j
public abstract class AbstractServiceImpl {

  static <T> T getAdapter(final Map<Class<?>, T> adapterMap, final Object obj) {

    notNull(obj, "Object must not be null.");
    final Class<?> cls;
    if (obj instanceof Class<?>) {
      cls = (Class<?>) obj;
    } else {
      cls = obj.getClass();
    }
    final T adapter = adapterMap.get(cls);
    if (adapter == null) {
      final ServiceException se = ServiceException.internalServerError(
          "No adapter found for " + cls.getName());
      log.error("Getting leaf adapter failed.", se);
      throw se;
    }
    return adapter;
  }

  @Getter
  private AclMapper<AclEntity> aclMapper;

  public AbstractServiceImpl(
      AclMapper<AclEntity> aclMapper) {
    this.aclMapper = aclMapper;
  }

}
