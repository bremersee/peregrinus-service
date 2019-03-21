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

package org.bremersee.peregrinus.security.access.repository.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@TypeAlias("AccessControl")
public class AccessControlEntity implements AccessControl {

  @Indexed
  private String owner;

  private AuthorizationSetEntity administration = new AuthorizationSetEntity();

  private AuthorizationSetEntity create = new AuthorizationSetEntity();

  private AuthorizationSetEntity delete = new AuthorizationSetEntity();

  private AuthorizationSetEntity read = new AuthorizationSetEntity();

  private AuthorizationSetEntity write = new AuthorizationSetEntity();

  public AccessControlEntity(final AccessControl accessControl) {
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

}
