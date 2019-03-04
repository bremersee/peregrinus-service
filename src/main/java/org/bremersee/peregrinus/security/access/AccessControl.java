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

import java.util.Collection;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.security.core.AuthorityConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@TypeAlias("AccessControl")
public class AccessControl {

  @Indexed
  private String owner;

  private AuthorizationSet administration = new AuthorizationSet();

  private AuthorizationSet create = new AuthorizationSet();

  private AuthorizationSet delete = new AuthorizationSet();

  private AuthorizationSet read = new AuthorizationSet();

  private AuthorizationSet write = new AuthorizationSet();

  public AccessControl(final AccessControl accessControl) {
    if (accessControl != null) {
      owner = accessControl.getOwner();
      for (final String permission : PermissionConstants.ALL) {
        accessControl.findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> findAuthorizationSet(permission)
                .ifPresent(as -> {
                  as.getGroups().addAll(authorizationSet.getGroups());
                  as.getRoles().addAll(authorizationSet.getRoles());
                  as.getUsers().addAll(authorizationSet.getUsers());
                }));
      }
    }
  }

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

  public AccessControl ensureAdminAccess() {
    addRole(AuthorityConstants.ADMIN_ROLE_NAME, PermissionConstants.ALL);
    return this;
  }

  public AccessControl ensureAdminAccess(final String adminRole) {
    addRole(adminRole, PermissionConstants.ALL);
    return this;
  }

  public AccessControl owner(String owner) {
    if (StringUtils.hasText(owner)) {
      this.owner = owner;
    }
    return addUser(owner, PermissionConstants.ALL);
  }

  public AccessControl addUser(String user, String... permissionConstants) {
    if (StringUtils.hasText(user) && permissionConstants != null) {
      for (final String permission : permissionConstants) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getUsers().add(user));
      }
    }
    return this;
  }

  public AccessControl addRole(String role, String... permissionConstants) {
    if (StringUtils.hasText(role) && permissionConstants != null) {
      for (final String permission : permissionConstants) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getRoles().add(role));
      }
    }
    return this;
  }

  public AccessControl addGroup(String group, String... permissionConstants) {
    if (StringUtils.hasText(group) && permissionConstants != null) {
      for (final String permission : permissionConstants) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getGroups().add(group));
      }
    }
    return this;
  }

  public AccessControl removeUser(String user, String... permissionConstants) {
    if (StringUtils.hasText(user) && permissionConstants != null) {
      for (final String permission : permissionConstants) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getUsers().remove(user));
      }
    }
    return this;
  }

  public AccessControl removeRole(String role, String... permissionConstants) {
    if (StringUtils.hasText(role) && permissionConstants != null) {
      for (final String permission : permissionConstants) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getRoles().remove(role));
      }
    }
    return this;
  }

  public AccessControl removeGroup(String group, String... permissionConstants) {
    if (StringUtils.hasText(group) && permissionConstants != null) {
      for (final String permission : permissionConstants) {
        findAuthorizationSet(permission)
            .ifPresent(authorizationSet -> authorizationSet.getGroups().remove(group));
      }
    }
    return this;
  }

  public boolean hasPermission(
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
    final AuthorizationSet set = findAuthorizationSet(permission).orElse(new AuthorizationSet());
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
