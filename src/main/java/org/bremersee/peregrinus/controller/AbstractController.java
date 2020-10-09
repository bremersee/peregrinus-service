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

package org.bremersee.peregrinus.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.reactivestreams.Publisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Slf4j
public abstract class AbstractController {

  private final GroupWebfluxControllerApi groupService;

  public AbstractController(GroupWebfluxControllerApi groupService) {
    Assert.notNull(groupService, "Group service must be present.");
    this.groupService = groupService;
    log.info("Using group service {}", groupService.getClass().getName());
  }

  <R> Mono<R> oneWithUserId(Function<String, ? extends Mono<R>> function) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getName)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .flatMap(function);
  }

  <R> Mono<R> oneWithUserIdAndRoles(Function<Auth, ? extends Mono<R>> function) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .map(authentication -> new Auth(
            authentication.getName(),
            authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()),
            new HashSet<>()))
        .flatMap(function);
  }

  <R> Mono<R> oneWithAuth(Function<Auth, ? extends Mono<R>> function) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .zipWith(groupService.getMembershipIds())
        .map(tuple -> new Auth(
            tuple.getT1().getName(),
            toRoles(tuple.getT1()),
            tuple.getT2()))
        .flatMap(function);
  }

  <R> Flux<R> manyWithUserId(Function<String, ? extends Publisher<R>> function) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getName)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .flatMapMany(function);
  }

  <R> Flux<R> manyWithAuth(Function<Auth, ? extends Publisher<R>> function) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .zipWith(groupService.getMembershipIds())
        .map(tuple -> new Auth(
            tuple.getT1().getName(),
            toRoles(tuple.getT1()),
            tuple.getT2()))
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .flatMapMany(function);
  }

  private Set<String> toRoles(Authentication authentication) {
    return authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }

}
