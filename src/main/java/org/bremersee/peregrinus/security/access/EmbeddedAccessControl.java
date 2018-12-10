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

package org.bremersee.peregrinus.security.access;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.security.access.AccessControl;
import org.bremersee.security.access.AuthorizationSet;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@TypeAlias("EmbeddedAccessControl")
public class EmbeddedAccessControl implements AccessControl {

  @Indexed
  private String owner;

  private EmbeddedAuthorizationSet administration = new EmbeddedAuthorizationSet();

  private EmbeddedAuthorizationSet create = new EmbeddedAuthorizationSet();

  private EmbeddedAuthorizationSet delete = new EmbeddedAuthorizationSet();

  private EmbeddedAuthorizationSet read = new EmbeddedAuthorizationSet();

  private EmbeddedAuthorizationSet write = new EmbeddedAuthorizationSet();

  @Override
  public Optional<AuthorizationSet> findAuthorizationSet(Object permission) {
    final String permissionStr = String.valueOf(permission);
    switch (permissionStr) {
      case PermissionConstants.ADMINISTRATION:
        return Optional.of(administration);
      case PermissionConstants.CREATE:
        return Optional.of(create);
      case PermissionConstants.DELETE:
        return Optional.of(delete);
      case PermissionConstants.READ:
        return Optional.of(read);
      case PermissionConstants.WRITE:
        return Optional.of(write);
      default:
        return Optional.empty();
    }
  }

}
