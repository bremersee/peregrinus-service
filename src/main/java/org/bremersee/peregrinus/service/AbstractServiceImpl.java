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

import lombok.Getter;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclMapper;

/**
 * @author Christian Bremer
 */
public abstract class AbstractServiceImpl {

  @Getter
  private AclMapper<AclEntity> aclMapper;

  public AbstractServiceImpl(
      AclMapper<AclEntity> aclMapper) {
    this.aclMapper = aclMapper;
  }

/*
  Mono<String> userId() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getName)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()));
  }

  Mono<Set<String>> roles() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(this::toRoles)
        .switchIfEmpty(Mono.just(Collections.emptySet()));
  }

  Mono<Set<String>> groups() {
    return groupService.getMembershipIds()
        .switchIfEmpty(Mono.just(Collections.emptySet()));
  }

  Mono<Tuple3<String, Set<String>, Set<String>>> authentication() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .zipWith(groupService.getMembershipIds())
        .flatMap(tuple -> Mono.just(Tuples.of(
            tuple.getT1().getName(),
            toRoles(tuple.getT1()),
            tuple.getT2())))
        .switchIfEmpty(Mono.error(ServiceException.forbidden()));
  }

  private Set<String> toRoles(Authentication authentication) {
    return authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }
  */



}
