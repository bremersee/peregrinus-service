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

import java.nio.charset.StandardCharsets;
import org.bremersee.comparator.ComparatorItemTransformer;
import org.bremersee.comparator.ComparatorItemTransformerImpl;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.groupman.api.GroupControllerApi;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.FeatureLeaf;
import org.bremersee.peregrinus.model.gpx.GpxImportSettings;
import org.bremersee.peregrinus.service.GeometryCommand;
import org.bremersee.peregrinus.service.OpenBranchCommand;
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

  private final ComparatorItemTransformer sortTransformer = new ComparatorItemTransformerImpl();

  private final TreeService treeService;

  public TreeController(
      GroupControllerApi groupService,
      TreeService treeService) {
    super(groupService);
    this.treeService = treeService;
  }

  private ComparatorItem buildComparatorItem(final String sort) {
    final ComparatorItem item = new ComparatorItem();
    if (StringUtils.hasText(sort)) {
      item.setNextComparatorItem(
          sortTransformer.fromString(sort, false, StandardCharsets.UTF_8.name()));
    } else {
      item.setNextComparatorItem(
          sortTransformer.fromString(
              "name,asc|modified,desc", false, StandardCharsets.UTF_8.name()));
    }
    return item;
  }

  @PostMapping(
      params = {"name"},
      consumes = {MediaType.ALL_VALUE},
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
      @RequestParam(value = "omitGeometries", defaultValue = "false") Boolean omitGeometries,
      @RequestParam(value = "pub", defaultValue = "false") Boolean includePublic,
      @RequestParam(value = "sort", defaultValue = "name,asc|modified,desc") String sort) {
    return manyWithAuth(auth -> treeService
        .loadBranches(
            openAll,
            omitGeometries,
            includePublic,
            buildComparatorItem(sort),
            auth.getUserId(),
            auth.getRoles(),
            auth.getGroups()));
  }

  @PutMapping(
      path = "/{nodeId}",
      params = {"name"},
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Mono<Boolean> renameNode(
      @PathVariable("nodeId") String nodeId,
      @RequestParam(value = "name") @Length(min = 1) String name) {
    return oneWithAuth(auth -> treeService
        .renameNode(nodeId, name, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

  @GetMapping(path = "/{branchId}/open", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Mono<Branch> openBranch(
      @PathVariable("branchId") String branchId,
      @RequestParam(value = "open", defaultValue = "current") String openCommand,
      @RequestParam(value = "geometry", defaultValue = "retain") String geometryCommand,
      @RequestParam(value = "sort", defaultValue = "name,asc|modified,desc") String sort) {
    return oneWithAuth(auth -> treeService
        .openBranch(
            branchId,
            OpenBranchCommand.fromValue(openCommand),
            GeometryCommand.fromValue(geometryCommand),
            buildComparatorItem(sort),
            auth.getUserId(),
            auth.getRoles(),
            auth.getGroups()));
  }

  @PutMapping(path = "/{branchId}/close")
  public Mono<Void> closeBranch(
      @PathVariable("branchId") String branchId) {
    return oneWithUserId(userId -> treeService.closeBranch(branchId, userId));
  }

  @PutMapping(path = "/{nodeId}/display-on-map/{displayedOnMap}")
  // TODO only leaf, return leaf (optional)
  public Mono<Void> displayNodeOnMap(
      @PathVariable("nodeId") String nodeId,
      @PathVariable("displayedOnMap") boolean displayedOnMap) {
    return oneWithAuth(auth -> treeService
        .displayNodeOnMap(
            nodeId, displayedOnMap, auth.getUserId(), auth.getRoles(), auth.getGroups()));
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

  @PostMapping(
      path = "/{branchId}/features/import/gpx",
      consumes = {MediaType.APPLICATION_XML_VALUE},
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public Flux<FeatureLeaf> importGpx(
      @PathVariable("branchId") String branchId,
      @RequestParam(name = "waypoints", defaultValue = "true") Boolean waypoints,
      @RequestBody Gpx gpx) {
    final GpxImportSettings settings = new GpxImportSettings();
    settings.setImportRouteWaypoints(waypoints);
    return manyWithAuth(auth -> treeService
        .importGpx(branchId, gpx, settings, auth.getUserId(), auth.getRoles(), auth.getGroups()));
  }

}
