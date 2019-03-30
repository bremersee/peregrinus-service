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

import static org.bremersee.security.access.PermissionConstants.READ;
import static org.bremersee.security.access.PermissionConstants.WRITE;
import static org.springframework.util.Assert.notNull;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.bremersee.peregrinus.entity.LeafEntity;
import org.bremersee.peregrinus.entity.LeafEntitySettings;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.BranchSettings;
import org.bremersee.peregrinus.model.Node;
import org.bremersee.peregrinus.repository.TreeRepository;
import org.bremersee.peregrinus.service.adapter.LeafAdapter;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.AclMapper;
import org.hibernate.validator.constraints.Length;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Slf4j
public class TreeServiceImpl extends AbstractServiceImpl implements TreeService {

  private final Map<Class<?>, LeafAdapter> leafAdapterMap = new HashMap<>();

  private TreeRepository treeRepository;

  public TreeServiceImpl(
      AclMapper<AclEntity> aclMapper,
      TreeRepository treeRepository,
      List<LeafAdapter> leafAdapters) {
    super(aclMapper);
    this.treeRepository = treeRepository;
    for (final LeafAdapter leafAdapter : leafAdapters) {
      for (final Class<?> cls : leafAdapter.getSupportedClasses()) {
        leafAdapterMap.put(cls, leafAdapter);
      }
    }
  }

  private LeafAdapter getLeafAdapter(final Object obj) {

    notNull(obj, "Object must not be null.");
    final Class<?> cls;
    if (obj instanceof Class<?>) {
      cls = (Class<?>) obj;
    } else {
      cls = obj.getClass();
    }
    final LeafAdapter leafAdapter = leafAdapterMap.get(cls);
    if (leafAdapter == null) {
      final ServiceException se = ServiceException.internalServerError(
          "No leaf adapter found for " + cls.getName());
      log.error("Getting leaf adapter failed.", se);
      throw se;
    }
    return leafAdapter;
  }

  @Override
  public Mono<Branch> createBranch(
      @NotNull @Length(min = 1) final String name,
      @NotNull final String userId) {

    return treeRepository.persistNode(BranchEntity.builder()
        .acl(getAclMapper().defaultAcl(userId))
        .createdBy(userId)
        .modifiedBy(userId)
        .name(name)
        .build())
        .zipWhen(branchEntity -> treeRepository
            .persistNodeSettings(BranchEntitySettings.builder().build()))
        .map(tuple -> buildBranchWithoutChildren(tuple.getT1(), tuple.getT2()));
  }

  @Override
  public Mono<Branch> createBranch(
      @NotNull @Length(min = 1) final String name,
      @NotNull final String parentId,
      @NotNull final String userId,
      @NotNull final Set<String> roles,
      @NotNull final Set<String> groups) {

    return treeRepository.findBranchById(parentId, WRITE, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .map(parentBranchEntity -> BranchEntity.builder()
            .acl(AclBuilder.builder()
                .from(parentBranchEntity.getAcl())
                .owner(userId)
                .build(AclEntity::new))
            .build())
        .flatMap(treeRepository::persistNode)
        .zipWhen(branchEntity -> treeRepository.persistNodeSettings(
            BranchEntitySettings.builder().build()))
        .map(tuple -> buildBranchWithoutChildren(tuple.getT1(), tuple.getT2()));
  }

  private Branch buildBranchWithoutChildren(
      final BranchEntity branchEntity,
      final BranchEntitySettings branchEntitySettings) {

    return Branch.builder()
        .acl(getAclMapper().map(branchEntity.getAcl()))
        .created(branchEntity.getCreated())
        .createdBy(branchEntity.getCreatedBy())
        .id(branchEntity.getId())
        .modified(branchEntity.getModified())
        .modifiedBy(branchEntity.getModifiedBy())
        .name(branchEntity.getName())
        .parentId(branchEntity.getParentId())
        .settings(BranchSettings.builder()
            .id(branchEntitySettings.getId())
            .nodeId(branchEntity.getId())
            .open(branchEntitySettings.getOpen())
            .userId(branchEntitySettings.getUserId())
            .build())
        .build();
  }

  @Override
  public Flux<Branch> loadBranches(
      boolean openAll,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups) {

    final OpenBranchCommand openBranchCommand = openAll
        ? OpenBranchCommand.ALL
        : OpenBranchCommand.RETAIN;
    return treeRepository.findRootBranches(READ, includePublic, userId, roles, groups)
        .flatMap(branchEntity -> processBranchEntity(
            branchEntity, openBranchCommand, userId, roles, groups));
  }

  @Override
  public Mono<Branch> openBranch(
      @NotNull String branchId,
      boolean openAll,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups) {

    return treeRepository.findBranchById(branchId, READ, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .flatMap(branchEntity -> processBranchEntity(
            branchEntity, OpenBranchCommand.CURRENT, userId, roles, groups));
  }

  @Override
  public Mono<Void> closeBranch(
      @NotNull String branchId,
      @NotNull String userId) {

    return treeRepository.closeBranch(branchId, userId);
  }

  private Mono<Branch> processBranchEntity(
      final BranchEntity branchEntity,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return treeRepository.findNodeSettings(branchEntity.getId(), userId)
        .cast(BranchEntitySettings.class)
        .switchIfEmpty(treeRepository.persistNodeSettings(
            BranchEntitySettings.builder()
                .nodeId(branchEntity.getId())
                .open(true)
                .userId(userId)
                .build()))
        .flatMap(branchEntitySettings -> {
          if (openBranchCommand.isCurrentAndBranchNotOpen(branchEntitySettings)) {
            branchEntitySettings.setOpen(true);
            return treeRepository.openBranch(branchEntitySettings.getId())
                .flatMap(result -> Mono.just(branchEntitySettings));
            //return treeRepository.persistNodeSettings(branchEntitySettings);
          }
          return Mono.just(branchEntitySettings);
        })
        .map(branchEntitySettings -> Branch.builder()
            .acl(getAclMapper().map(branchEntity.getAcl()))
            .created(branchEntity.getCreated())
            .createdBy(branchEntity.getCreatedBy())
            .id(branchEntity.getId())
            .modified(branchEntity.getModified())
            .modifiedBy(branchEntity.getModifiedBy())
            .name(branchEntity.getName())
            .parentId(branchEntity.getParentId())
            .settings(BranchSettings.builder()
                .id(branchEntitySettings.getId())
                .nodeId(branchEntity.getId())
                .open(branchEntitySettings.getOpen())
                .userId(userId)
                .build())
            .build())
        .flatMap(branch -> processBranch(
            branch,
            openBranchCommand.getCommandForChildren(),
            userId,
            roles, groups));
  }

  private Mono<Branch> processBranch(
      final Branch branch,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (openBranchCommand.openBranch(branch)) {
      return treeRepository.findNodesByParentId(branch.getId(), READ, true, userId, roles, groups)
          .flatMap(nodeEntity -> processChild(nodeEntity, openBranchCommand, userId, roles, groups))
          .collectList()
          .flatMap(children -> {
            branch.setChildren(children);
            return Mono.just(branch);
          });

    }
    return Mono.just(branch);
  }

  private Mono<Node> processChild(
      final NodeEntity nodeEntity,
      final OpenBranchCommand openBranchCommand,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (nodeEntity instanceof BranchEntity) {
      final BranchEntity branchEntity = (BranchEntity) nodeEntity;
      return processBranchEntity(branchEntity, openBranchCommand, userId, roles, groups)
          .cast(Node.class);
    }
    if (nodeEntity instanceof LeafEntity) {
      final LeafEntity leafEntity = (LeafEntity) nodeEntity;
      return treeRepository.findNodeSettings(leafEntity.getId(), userId)
          .switchIfEmpty(treeRepository.persistNodeSettings(getLeafAdapter(leafEntity)
              .buildLeafEntitySettings(leafEntity, userId)))
          .cast(LeafEntitySettings.class)
          .flatMap(leafEntitySettings -> getLeafAdapter(leafEntity)
              .buildLeaf(leafEntity, leafEntitySettings));

    }
    return Mono.error(ServiceException.internalServerError("Node is not branch or leaf."));
  }

  @Override
  public Mono<Boolean> renameNode(
      final String nodeId,
      final String name,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return treeRepository.findNodeById(nodeId, WRITE, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("Node", nodeId)))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            nodeEntity.setModified(OffsetDateTime.now(Clock.systemUTC()));
            nodeEntity.setModifiedBy(userId);
            ((BranchEntity) nodeEntity).setName(name);
            return treeRepository.persistNode(nodeEntity).map(e -> true);
          } else if (nodeEntity instanceof LeafEntity) {
            return treeRepository.updateModified(userId)
                .flatMap(updatedNodeEntity -> getLeafAdapter(updatedNodeEntity)
                    .renameLeaf((LeafEntity) updatedNodeEntity, name, userId));
          } else {
            return Mono.error(ServiceException.internalServerError("Node is not branch or leaf."));
          }
        });
  }

}
