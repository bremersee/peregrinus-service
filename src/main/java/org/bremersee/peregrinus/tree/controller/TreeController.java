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

package org.bremersee.peregrinus.tree.controller;

import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.service.TreeService;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping(path = "/api/tree")
@Validated
public class TreeController {

  private TreeService treeService;

  public TreeController(final TreeService treeService) {
    this.treeService = treeService;
  }

  @PostMapping(
      params = {"name"},
      consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<Branch> createBranch(
      @RequestParam(value = "name") @Length(min = 1) String name,
      @RequestParam(value = "parentId", required = false) String parentId,
      @RequestBody(required = false) AccessControlDto accessControl,
      Authentication authentication) {
    return treeService.createBranch(name, parentId, accessControl, authentication);
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Flux<Branch> loadBranches(
      @RequestParam(value = "open-all", defaultValue = "false") Boolean openAll,
      @RequestParam(value = "pub", defaultValue = "false") Boolean includePublic,
      Authentication authentication) {
    return treeService.loadBranches(openAll, includePublic, authentication);
  }

  @PutMapping(path = "/{nodeId}", params = {"name"})
  public Mono<Boolean> renameNode(
      @PathVariable("nodeId") String nodeId,
      @RequestParam(value = "name") @Length(min = 1) String name,
      Authentication authentication) {
    return treeService.renameNode(nodeId, name, authentication);
  }

  @PutMapping(path = "/{nodeId}/access-control")
  public Mono<Boolean> updateAccessControl(
      @PathVariable("nodeId") String nodeId,
      @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive,
      @RequestBody AccessControlDto accessControl,
      Authentication authentication) {
    return treeService.updateAccessControl(nodeId, accessControl, recursive, authentication);
  }

  @DeleteMapping(path = "/{nodeId}")
  public Mono<Boolean> removeNode(
      @PathVariable("nodeId") String nodeId,
      Authentication authentication) {
    return treeService.removeNode(nodeId, authentication);
  }

  @GetMapping(path = "/{branchId}/open", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<Branch> openBranch(
      @PathVariable("branchId") String branchId,
      @RequestParam(value = "open-all", defaultValue = "false") Boolean openAll,
      Authentication authentication) {
    return treeService.openBranch(branchId, openAll, authentication);
  }

  @PutMapping(path = "/{branchId}/close")
  public Mono<Void> closeBranch(
      @PathVariable("branchId") String branchId,
      Authentication authentication) {
    return treeService.closeBranch(branchId, authentication);
  }

}
