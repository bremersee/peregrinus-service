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

package org.bremersee.peregrinus.security.access;

import java.util.Collection;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.bremersee.security.core.AuthorityConstants;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Validated
public interface AccessControl {

  String getOwner();

  void setOwner(String owner);

  @NotNull
  AuthorizationSet getAdministration();

  @NotNull
  AuthorizationSet getCreate();

  @NotNull
  AuthorizationSet getDelete();

  @NotNull
  AuthorizationSet getRead();

  @NotNull
  AuthorizationSet getWrite();

  default Optional<AuthorizationSet> findAuthorizationSet(final Object permission) {
    final String permissionStr = String.valueOf(permission);
    switch (permissionStr) {
      case PermissionConstants.ADMINISTRATION:
        return Optional.of(getAdministration());
      case PermissionConstants.CREATE:
        return Optional.of(getCreate());
      case PermissionConstants.DELETE:
        return Optional.of(getDelete());
      case PermissionConstants.READ:
        return Optional.of(getRead());
      case PermissionConstants.WRITE:
        return Optional.of(getWrite());
      default:
        return Optional.empty();
    }
  }

  default AccessControl ensureAdminAccess() {
    return ensureAdminAccess(AuthorityConstants.ADMIN_ROLE_NAME);
  }

  default AccessControl ensureAdminAccess(final String adminRole) {
    addRole(adminRole, PermissionConstants.ALL);
    return this;
  }

  default AccessControl removeAdminAccess() {
    return removeAdminAccess(AuthorityConstants.ADMIN_ROLE_NAME);
  }

  default AccessControl removeAdminAccess(final String adminRole) {
    removeRole(adminRole, PermissionConstants.ALL);
    return this;
  }

  default AccessControl owner(String owner) {
    if (StringUtils.hasText(owner)) {
      setOwner(owner);
    }
    return addUser(owner, PermissionConstants.ALL);
  }

  default AccessControl addUser(String user, String... permissions) {
    if (StringUtils.hasText(user) && permissions != null) {
      for (final String permission : permissions) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getUsers().add(user));
      }
    }
    return this;
  }

  default AccessControl addRole(String role, String... permissions) {
    if (StringUtils.hasText(role) && permissions != null) {
      for (final String permission : permissions) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getRoles().add(role));
      }
    }
    return this;
  }

  default AccessControl addGroup(String group, String... permissions) {
    if (StringUtils.hasText(group) && permissions != null) {
      for (final String permission : permissions) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getGroups().add(group));
      }
    }
    return this;
  }

  default AccessControl removeUser(String user, String... permissions) {
    if (StringUtils.hasText(user) && permissions != null) {
      for (final String permission : permissions) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getUsers().remove(user));
      }
    }
    return this;
  }

  default AccessControl removeRole(String role, String... permissions) {
    if (StringUtils.hasText(role) && permissions != null) {
      for (final String permission : permissions) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getRoles().remove(role));
      }
    }
    return this;
  }

  default AccessControl removeGroup(String group, String... permissions) {
    if (StringUtils.hasText(group) && permissions != null) {
      for (final String permission : permissions) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getGroups().remove(group));
      }
    }
    return this;
  }

  default boolean hasPermission(
      Object permission,
      String user,
      Collection<String> roles,
      Collection<String> groups) {

    if (permission == null) {
      return false;
    }
    if (user != null && user.equals(getOwner())) {
      return true;
    }
    final AuthorizationSet set = findAuthorizationSet(permission).orElse(null);
    if (set == null) {
      return false;
    }
    if (set.isGuest()) {
      return true;
    }
    if (set.getUsers().contains(user)) {
      return true;
    }
    if (roles != null && roles.stream().anyMatch(role -> set.getRoles().contains(role))) {
      return true;
    }
    return groups != null && groups.stream().anyMatch(group -> set.getGroups().contains(group));
  }

}
