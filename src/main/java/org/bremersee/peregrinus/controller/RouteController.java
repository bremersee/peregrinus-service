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

import org.bremersee.groupman.api.GroupWebfluxControllerApi;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteCalcRequest;
import org.bremersee.peregrinus.service.RouteService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@RestController
@RequestMapping(path = "/api/protected/route")
@Validated
public class RouteController extends AbstractController {

  private RouteService routeService;

  public RouteController(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") GroupWebfluxControllerApi groupService,
      RouteService routeService) {
    super(groupService);
    this.routeService = routeService;
  }

  @PostMapping(
      consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<Rte> calculateRoute(@RequestBody RteCalcRequest request) {
    return oneWithUserIdAndRoles(auth -> routeService
        .calculateRoute(request, auth.getUserId(), auth.getRoles()));
  }
}
