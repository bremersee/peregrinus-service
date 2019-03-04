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

package org.bremersee.peregrinus.tree.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.bremersee.exception.ServiceException;
import org.bremersee.groupman.api.GroupControllerApi;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeature;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeatureSettings;
import org.bremersee.peregrinus.geo.repository.GeoJsonFeatureSettingsRepository;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.tree.model.AbstractNode;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.model.BranchSettings;
import org.bremersee.peregrinus.tree.model.GeoLeaf;
import org.bremersee.peregrinus.tree.repository.BranchRepository;
import org.bremersee.peregrinus.tree.repository.BranchSettingsRepository;
import org.bremersee.peregrinus.tree.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class TreeServiceImpl implements TreeService {

  @Autowired
  private NodeRepository nodeRepository;

  @Autowired
  private BranchRepository branchRepository;

  //private GeoTreeLeafRepository geoTreeLeafRepository;

  @Autowired
  private BranchSettingsRepository branchSettingsRepository;

  @Autowired
  private GeoJsonFeatureSettingsRepository featureSettingsRepository;

  @Autowired
  private GroupControllerApi groupService;

  @Override
  public Mono<Branch> createBranch(
      final String name,
      final String parentId,
      final AccessControl accessControl,
      final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    if (StringUtils.hasText(parentId)) {
      return groupService.getMembershipIds()
          .flatMap(groups -> createChildBranch(
              name, parentId, userId, accessControl, roles, groups));
    }
    final AccessControl newAccessControl = new AccessControl(accessControl)
        .owner(userId)
        .addUser(userId, PermissionConstants.ALL);
    return branchRepository.save(new Branch(name, null, newAccessControl));
  }

  private Mono<Branch> createChildBranch(
      final String name,
      final String parentId,
      final String userId,
      final AccessControl accessControl,
      final Collection<String> roles,
      final Collection<String> groups) {

    return branchRepository.findById(parentId, PermissionConstants.WRITE, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("TreeBranch", parentId)))
        .map(AbstractNode::getAccessControl)
        .flatMap(existAccessControl -> {
          final AccessControl newAccessControl;
          if (accessControl == null
              || !existAccessControl.hasPermission(
              PermissionConstants.ADMINISTRATION, userId, roles, groups)) {
            newAccessControl = existAccessControl;
          } else {
            newAccessControl = new AccessControl(accessControl)
                .owner(userId)
                .addUser(userId, PermissionConstants.ALL);
          }
          return branchRepository.save(new Branch(name, parentId, newAccessControl));
        });
  }

  @Override
  public Mono<Void> renameNode(
      final String nodeId,
      final String name,
      final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMap(groups -> renameNode(nodeId, name, userId, roles, groups));
  }

  private Mono<Void> renameNode(
      final String nodeId,
      final String name,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    return nodeRepository.findById(nodeId, PermissionConstants.WRITE, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("TreeNode", nodeId)))
        .flatMap(node -> {
          if (node instanceof Branch) {
            Branch branch = (Branch) node;
            branch.setName(name);
            return branchRepository.save(branch).flatMap(b -> Mono.empty());
          }
          // TODO leaf
          return Mono.empty();
        });
  }

  @Override
  public Mono<Void> updateAccessControl(
      final String nodeId,
      final boolean recursive,
      final AccessControl accessControl,
      final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMap(groups -> updateAccessControl(
            nodeId, recursive, accessControl, userId, roles, groups));
  }

  private Mono<Void> updateAccessControl(
      final String nodeId,
      final boolean recursive,
      final AccessControl accessControl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final AccessControl newAccessControl = new AccessControl(accessControl)
        .owner(userId)
        .addUser(userId, PermissionConstants.ALL);

    return nodeRepository
        .findById(nodeId, PermissionConstants.ADMINISTRATION, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("TreeNode", nodeId)))
        .flatMap(node -> {
          return Mono.empty();
        });
  }

  @Override
  public Mono<Void> deleteNode(
      final String nodeId,
      final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMap(groups -> deleteNode(nodeId, userId, roles, groups));
  }

  private Mono<Void> deleteNode(
      final String nodeId,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    return nodeRepository.findById(nodeId, PermissionConstants.DELETE, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("TreeNode", nodeId)))
        .flatMap(this::delete);
  }

  private Mono<Void> delete(final AbstractNode node) {
    if (node instanceof Branch) {
      return nodeRepository.findByParentId(node.getId())
          .flatMap(this::delete)
          .count()
          .flatMap(size -> nodeRepository.delete(node));
    } else {
      // TODO delete leaf content
      return nodeRepository.delete(node);
    }
  }

  @Override
  public Flux<Branch> loadBranches(
      final boolean openAll,
      final boolean includePublic,
      final Authentication authentication) {

    final OpenBranchCommand openBranchCommand = openAll
        ? OpenBranchCommand.ALL
        : OpenBranchCommand.RETAIN;
    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMapMany(groups -> branchRepository
            .findByParentId(null, PermissionConstants.READ, includePublic, userId, roles, groups)
            .flatMap(branch -> loadBranch(branch, openBranchCommand, userId, roles, groups)));
  }

  @Override
  public Mono<Branch> openBranch(
      final String branchId,
      final boolean openAll,
      final Authentication authentication) {
    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    final OpenBranchCommand openBranchCommand = openAll
        ? OpenBranchCommand.ALL
        : OpenBranchCommand.CURRENT;
    return groupService.getMembershipIds()
        .flatMap(groups -> branchRepository.findById(
            branchId, PermissionConstants.READ, userId, roles, groups))
        .switchIfEmpty(Mono.error(ServiceException.forbidden("TreeBranch", branchId)))
        .flatMap(
            treeBranch -> groupService.getMembershipIds()
                .flatMap(
                    groups -> loadBranch(treeBranch, openBranchCommand, userId, roles, groups)));
  }

  @Override
  public Mono<Void> closeBranch(final String branchId, final Authentication authentication) {
    final String userId = authentication.getName();
    return branchSettingsRepository
        .findByNodeIdAndUserId(branchId, userId)
        .map(branch -> {
          branch.setOpen(false);
          return branchSettingsRepository.save(branch);
        })
        .flatMap(branch -> Mono.empty());
  }

  private Mono<Branch> loadBranch(
      final Branch branch,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    return prepareBranch(branch, openBranchCommand, userId)
        .flatMap(preparedBranch -> addChildren(
            preparedBranch, openBranchCommand.getCommandForChildren(), userId, roles, groups));
  }

  private Mono<Branch> prepareBranch(
      final Branch branch,
      final OpenBranchCommand openBranchCommand,
      final String userId) {
    return branchSettingsRepository
        .findByNodeIdAndUserId(branch.getId(), userId)
        .switchIfEmpty(createBranchSettings(branch.getId(), userId))
        .flatMap(branchSettings -> {
          if (openBranchCommand.isBranchToBeOpen() && !branchSettings.isOpen()) {
            branchSettings.setOpen(true);
            if (OpenBranchCommand.CURRENT.equals(openBranchCommand)) {
              return branchSettingsRepository.save(branchSettings);
            }
          }
          return Mono.just(branchSettings);
        })
        .map(branchSettings -> {
          branch.setSettings(branchSettings);
          return branch;
        });
  }

  private Mono<Branch> addChildren(
      final Branch parent,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    if (!parent.getSettings().isOpen()) {
      return Mono.just(parent);
    }
    return nodeRepository
        .findByParentId(parent.getId(), userId, roles, groups)
        .flatMap(child -> processChild(child, openBranchCommand, userId, roles, groups))
        .collectSortedList()
        .map(children -> {
          parent.setChildren(children);
          return parent;
        })
        .switchIfEmpty(Mono.just(parent));
  }

  private Mono<AbstractNode> processChild(
      final AbstractNode child,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    if (child instanceof Branch) {
      return loadBranch((Branch) child, openBranchCommand, userId, roles, groups)
          .cast(AbstractNode.class);
    }
    // TODO generisch
    if (child instanceof GeoLeaf) {
      return featureSettingsRepository
          .findByFeatureIdAndUserId(((GeoLeaf) child).getFeature().getId(), userId)
          .switchIfEmpty(createFeatureSettings(((GeoLeaf) child).getFeature(), userId))
          .map(featureSettings -> {
            //noinspection unchecked
            ((GeoLeaf) child).getFeature().getProperties().setSettings(featureSettings);
            return child;
          });
    }
    return Mono.just(child);
  }

  private Mono<BranchSettings> createBranchSettings(
      final String branchId,
      final String userId) {
    return branchSettingsRepository.save(new BranchSettings(branchId, userId));
  }

  private Mono<AbstractGeoJsonFeatureSettings> createFeatureSettings(
      final AbstractGeoJsonFeature feature, final String userId) {

    final AbstractGeoJsonFeatureSettings featureSettings = feature
        .getProperties()
        .createDefaultSettings(feature.getId(), userId, true);
    return featureSettingsRepository.save(featureSettings);
  }

}
