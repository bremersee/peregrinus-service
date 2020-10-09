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
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.service.GeocodeService;
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
@RequestMapping(path = "/api/protected/geocode")
@Validated
public class GeocodeController extends AbstractController {

  private GeocodeService geocodeService;

  public GeocodeController(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") GroupWebfluxControllerApi groupService,
      GeocodeService geocodeService) {
    super(groupService);
    this.geocodeService = geocodeService;
  }

  @PostMapping(
      path = "/query",
      consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<FeatureCollection> queryGeocode(@RequestBody GeocodeQueryRequest request) {
    return oneWithAuth(auth -> geocodeService
        .queryGeocode(request, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }
}
