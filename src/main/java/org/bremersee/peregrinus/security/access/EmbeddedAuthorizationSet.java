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

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.security.access.AuthorizationSet;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@TypeAlias("EmbeddedAuthorizationSet")
public class EmbeddedAuthorizationSet implements AuthorizationSet {

  public EmbeddedAuthorizationSet(final AuthorizationSet authorizationSet) {
    if (authorizationSet != null) {
      guest = authorizationSet.isGuest();
      users.addAll(authorizationSet.getUsers());
      roles.addAll(authorizationSet.getRoles());
      groups.addAll(authorizationSet.getGroups());
    }
  }

  @Indexed
  private boolean guest;

  @Indexed
  private Set<String> users = new LinkedHashSet<>();

  @Indexed
  private Set<String> roles = new LinkedHashSet<>();

  @Indexed
  private Set<String> groups = new LinkedHashSet<>();

}
