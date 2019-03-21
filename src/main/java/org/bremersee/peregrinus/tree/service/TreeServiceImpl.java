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

package org.bremersee.peregrinus.tree.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.groupman.api.GroupControllerApi;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.repository.TreeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class TreeServiceImpl implements TreeService {

  private TreeRepository treeRepository;

  private GroupControllerApi groupService;

  public TreeServiceImpl(
      final TreeRepository treeRepository,
      final GroupControllerApi groupService) {

    Assert.notNull(treeRepository, "Node repository must not be null.");
    Assert.notNull(groupService, "Group service must not be null.");
    this.treeRepository = treeRepository;
    this.groupService = groupService;
  }

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

    final AccessControl newAccessControl = new AccessControlDto(accessControl)
        .owner(userId)
        .addUser(userId, PermissionConstants.ALL);

    if (StringUtils.hasText(parentId)) {
      return groupService.getMembershipIds()
          .flatMap(groups -> createBranch(
              name, parentId, userId, newAccessControl, roles, groups));
    }
    final Branch branch = new Branch(userId, null, newAccessControl, name);
    return treeRepository.persistNode(branch, userId);
  }

  private Mono<Branch> createBranch(
      final String name,
      final String parentId,
      final String userId,
      final AccessControl accessControl,
      final Collection<String> roles,
      final Collection<String> groups) {

    return treeRepository
        .findBranchById(parentId, PermissionConstants.WRITE, true, userId, roles,
            groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("Branch", parentId)))
        .map(Node::getAccessControl)
        .flatMap(existAccessControl -> {
          final AccessControl newAccessControl;
          if (accessControl == null
              || !existAccessControl.hasPermission(
              PermissionConstants.ADMINISTRATION, userId, roles, groups)) {
            newAccessControl = existAccessControl;
          } else {
            newAccessControl = new AccessControlDto(accessControl)
                .owner(userId)
                .addUser(userId, PermissionConstants.ALL);
          }
          final Branch branch = new Branch(userId, null, newAccessControl, name);
          return treeRepository.persistNode(branch, userId);
        });
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
        .flatMapMany(groups -> treeRepository.findRootBranches(
            PermissionConstants.READ, includePublic, userId, roles, groups)
            .flatMap(branch -> processBranch(branch, openBranchCommand, userId, roles, groups)));
  }

  private Mono<Branch> processBranch(
      final Branch parent,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    if (openBranchCommand.isBranchToBeOpen() && !parent.getSettings().isOpen()) {
      parent.getSettings().setOpen(true);
      if (OpenBranchCommand.CURRENT.equals(openBranchCommand)) {
        return treeRepository.persistNodeSettings(parent.getSettings(), userId)
            .flatMap(branchSettings -> {
              parent.setSettings(branchSettings);
              return processBranch(
                  parent, openBranchCommand.getCommandForChildren(), userId, roles, groups);
            });
      }
    }
    if (!parent.getSettings().isOpen()) {
      return Mono.just(parent);
    }
    return treeRepository.findNodesByParentId(
        parent.getId(), PermissionConstants.READ, true, userId, roles, groups)
        .flatMap(child -> {
          if (child instanceof Branch) {
            return processBranch(
                (Branch) child, openBranchCommand.getCommandForChildren(), userId, roles, groups);
          } else {
            return Mono.just(child);
          }
        })
        .collectList()
        .map(children -> {
          parent.setChildren(children);
          return parent;
        });
  }

  @Override
  public Mono<Boolean> renameNode(
      final String nodeId,
      final String name,
      final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMap(groups -> treeRepository.updateName(nodeId, name, userId, roles, groups)
            .switchIfEmpty(Mono.error(ServiceException.forbidden("Node", nodeId))));
  }

  @Override
  public Mono<Boolean> updateAccessControl(
      final String nodeId,
      final AccessControl accessControl,
      boolean recursive,
      final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMap(groups -> treeRepository.updateAccessControl(
            nodeId, accessControl, recursive, userId, roles, groups)
            .switchIfEmpty(Mono.error(ServiceException.forbidden("Node", nodeId))));
  }

  @Override
  public Mono<Boolean> removeNode(
      @NotNull final String nodeId,
      @NotNull final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    return groupService.getMembershipIds()
        .flatMap(groups -> treeRepository.removeNode(nodeId, userId, roles, groups))
        .switchIfEmpty(Mono.error(ServiceException.forbidden("Node", nodeId)));
  }

  @Override
  public Mono<Branch> openBranch(
      @NotNull final String branchId,
      final boolean openAll,
      @NotNull final Authentication authentication) {

    final String userId = authentication.getName();
    final Set<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    final OpenBranchCommand openBranchCommand = openAll
        ? OpenBranchCommand.ALL
        : OpenBranchCommand.CURRENT;
    return groupService.getMembershipIds()
        .flatMap(groups -> treeRepository.findBranchById(
            branchId, PermissionConstants.READ, true, userId, roles, groups)
            .flatMap(branch -> processBranch(branch, openBranchCommand, userId, roles, groups)));
  }

  @Override
  public Mono<Void> closeBranch(
      @NotNull final String branchId,
      @NotNull final Authentication authentication) {

    return treeRepository.closeBranch(branchId, authentication.getName())
        .flatMap(result -> Mono.empty());
  }

}
