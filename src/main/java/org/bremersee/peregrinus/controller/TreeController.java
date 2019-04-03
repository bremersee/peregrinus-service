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

import org.bremersee.groupman.api.GroupControllerApi;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.FeatureLeaf;
import org.bremersee.peregrinus.service.TreeService;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@RestController
@RequestMapping(path = "/api/protected/tree")
@Validated
public class TreeController extends AbstractController {

  private final TreeService treeService;

  public TreeController(
      GroupControllerApi groupService,
      TreeService treeService) {
    super(groupService);
    this.treeService = treeService;
  }

  @PostMapping(
      params = {"name"},
      consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<Branch> createBranch(
      @RequestParam(value = "name") @Length(min = 1) String name,
      @RequestParam(value = "parentId", required = false) String parentId) {
    if (StringUtils.hasText(parentId)) {
      return oneWithAuth(auth -> treeService
          .createBranch(name, parentId, auth.getUserId(), auth.getRoles(), auth.getGroups()));
    }
    return oneWithUserId(userId -> treeService.createBranch(name, userId));
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Flux<Branch> loadBranches(
      @RequestParam(value = "openAll", defaultValue = "false") Boolean openAll,
      @RequestParam(value = "pub", defaultValue = "false") Boolean includePublic) {
    return manyWithAuth(auth -> treeService
        .loadBranches(openAll, includePublic, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

  @PutMapping(path = "/{nodeId}", params = {"name"})
  public Mono<Boolean> renameNode(
      @PathVariable("nodeId") String nodeId,
      @RequestParam(value = "name") @Length(min = 1) String name) {
    return oneWithAuth(auth -> treeService
        .renameNode(nodeId, name, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

  /*
  @PutMapping(path = "/{nodeId}/access-control")
  public Mono<Boolean> updateAccessControl(
      @PathVariable("nodeId") String nodeId,
      @RequestBody AccessControlList acl) {
    return null;
  }

  @DeleteMapping(path = "/{nodeId}")
  public Mono<Boolean> removeNode(
      @PathVariable("nodeId") String nodeId) {
    return null;
  }
  */

  @GetMapping(path = "/{branchId}/open", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<Branch> openBranch(
      @PathVariable("branchId") String branchId,
      @RequestParam(value = "open-all", defaultValue = "false") Boolean openAll) {
    return oneWithAuth(auth -> treeService
        .openBranch(branchId, openAll, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

  @PutMapping(path = "/{branchId}/close")
  public Mono<Void> closeBranch(
      @PathVariable("branchId") String branchId) {
    return oneWithUserId(userId -> treeService.closeBranch(branchId, userId));
  }

  @PostMapping(
      path = "/{branchId}/feature",
      consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<FeatureLeaf> createFeatureLeaf(
      @PathVariable("branchId") String branchId,
      @RequestBody Feature feature) {
    return oneWithAuth(auth -> treeService
        .createFeatureLeaf(
            branchId, feature, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

  @PostMapping(
      path = "/{branchId}/features",
      consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Flux<FeatureLeaf> createFeatureLeafs(
      @PathVariable("branchId") String branchId,
      @RequestBody FeatureCollection featureCollection) {
    return manyWithAuth(auth -> treeService
        .createFeatureLeafs(
            branchId, featureCollection, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

}
