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

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.comparator.ComparatorBuilder;
import org.bremersee.comparator.ValueComparator;
import org.bremersee.comparator.model.ComparatorField;
import org.bremersee.comparator.spring.ComparatorSpringUtils;
import org.bremersee.exception.ServiceException;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.bremersee.peregrinus.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.entity.FeatureLeafEntitySettings;
import org.bremersee.peregrinus.entity.LeafEntity;
import org.bremersee.peregrinus.entity.LeafEntitySettings;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.BranchSettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.FeatureLeaf;
import org.bremersee.peregrinus.model.Node;
import org.bremersee.peregrinus.model.gpx.GpxImportSettings;
import org.bremersee.peregrinus.repository.TreeRepository;
import org.bremersee.peregrinus.service.adapter.LeafAdapter;
import org.bremersee.security.access.AclBuilder;
import org.bremersee.security.access.AclMapper;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class TreeServiceImpl extends AbstractServiceImpl implements TreeService {

  private final Map<String, LeafAdapter> leafAdapterMap = new HashMap<>();

  private TreeRepository treeRepository;

  private FeatureService featureService;

  private ConverterService converterService;

  public TreeServiceImpl(
      AclMapper<AclEntity> aclMapper,
      TreeRepository treeRepository,
      List<LeafAdapter> leafAdapters,
      FeatureService featureService,
      ConverterService converterService) {
    super(aclMapper);
    this.treeRepository = treeRepository;
    this.featureService = featureService;
    this.converterService = converterService;
    for (final LeafAdapter leafAdapter : leafAdapters) {
      for (final String key : leafAdapter.getSupportedKeys()) {
        leafAdapterMap.put(key, leafAdapter);
      }
    }
  }

  private Comparator<Object> treeComparator(Sort sort) {
    Sort sortOrder = sort != null ? sort : DEFAULT_SORT;
    ComparatorBuilder comparatorBuilder = ComparatorBuilder.builder();
    for (ComparatorField comparatorField : ComparatorSpringUtils.fromSort(sortOrder)) {
      if ("type".equalsIgnoreCase(comparatorField.getField())) {
        comparatorBuilder.add(new TreeComparator(comparatorField.isAsc()));
      } else {
        comparatorBuilder.add(new ValueComparator(comparatorField));
      }
    }
    return comparatorBuilder.build();
  }

  private LeafAdapter getLeafAdapter(final Object obj) {
    return AdapterHelper.getAdapter(leafAdapterMap, obj);
  }

  @Override
  public Mono<Branch> createBranch(
      final String name,
      final String userId) {

    return treeRepository
        .persistNode(BranchEntity
            .builder()
            .acl(getAclMapper().defaultAcl(userId))
            .createdBy(userId)
            .modifiedBy(userId)
            .name(name)
            .build())
        .zipWhen(branchEntity -> treeRepository
            .persistNodeSettings(BranchEntitySettings
                .builder()
                .nodeId(branchEntity.getId())
                .open(true)
                .userId(userId)
                .build()))
        .map(tuple -> buildBranchWithoutChildren(tuple.getT1(), tuple.getT2()));
  }

  @Override
  public Mono<Branch> createBranch(
      final String name,
      final String parentId,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    log.info("msg=[Creating branch.] name=[{}] parentId=[{}]", name, parentId);
    return treeRepository
        .findBranchById(parentId, WRITE, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .map(parentBranchEntity -> BranchEntity
            .builder()
            .acl(AclBuilder
                .builder()
                .from(parentBranchEntity.getAcl())
                .owner(userId)
                .build(AclEntity::new))
            .name(name)
            .parentId(parentId)
            .createdBy(userId)
            .modifiedBy(userId)
            .build())
        .flatMap(treeRepository::persistNode)
        .zipWhen(branchEntity -> treeRepository.persistNodeSettings(
            BranchEntitySettings
                .builder()
                .nodeId(branchEntity.getId())
                .open(true)
                .userId(userId)
                .build()))
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
  public Flux<FeatureLeaf> createFeatureLeafs(
      final String parentId,
      final FeatureCollection featureCollection,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return treeRepository.findBranchById(parentId, WRITE, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("Branch", parentId)))
        .flatMapMany(branchEntity -> createFeatureLeafs(
            branchEntity, featureCollection, userId, roles, groups));
  }

  private Flux<FeatureLeaf> createFeatureLeafs(
      final BranchEntity branchEntity,
      final FeatureCollection featureCollection,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (featureCollection.getFeatures() == null || featureCollection.getFeatures().isEmpty()) {
      return Flux.empty();
    }
    return Flux.concat(featureCollection
        .getFeatures()
        .stream()
        .map(feature -> createFeatureLeaf(branchEntity, feature, userId, roles, groups))
        .collect(Collectors.toList()));
  }

  private Mono<FeatureLeaf> createFeatureLeaf(
      final BranchEntity branchEntity,
      final Feature feature,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    feature.setId(null);
    final AccessControlList acl = AclBuilder.builder()
        .from(getAclMapper().map(branchEntity.getAcl()))
        .owner(userId)
        .addUser(userId, PermissionConstants.ALL)
        .buildAccessControlList();
    feature.getProperties().setAcl(acl);
    final LeafAdapter adapter = getLeafAdapter(FeatureLeafEntity.class);
    return featureService.persistFeature(feature, userId, roles, groups)
        .map(persistedFeature -> FeatureLeafEntity.builder()
            .acl(getAclMapper().map(acl))
            .createdBy(userId)
            .featureId(persistedFeature.getId())
            .modifiedBy(userId)
            .parentId(branchEntity.getId())
            .build())
        .flatMap(treeRepository::persistNode)
        .zipWhen(featureLeafEntity -> {
          final FeatureLeafEntitySettings settings = (FeatureLeafEntitySettings) adapter
              .buildLeafEntitySettings(featureLeafEntity, userId, true);
          return treeRepository.persistNodeSettings(settings);
        })
        .flatMap(tuple -> getLeafAdapter(
            tuple.getT1()).buildLeaf(tuple.getT1(), tuple.getT2(), false))
        .cast(FeatureLeaf.class);
  }

  @Override
  public Flux<FeatureLeaf> importGpx(
      final String parentId,
      final Gpx gpx,
      final GpxImportSettings importSettings,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return createFeatureLeafs(
        parentId,
        converterService.convertGpxToFeatures(gpx, importSettings),
        userId,
        roles,
        groups);
  }

  @Override
  public Flux<Branch> loadBranches(
      boolean openAll,
      boolean omitGeometries,
      boolean includePublic,
      Sort sort,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    final OpenBranchCommand openBranchCommand = openAll
        ? OpenBranchCommand.ALL
        : OpenBranchCommand.RETAIN;
    return treeRepository.findRootBranches(READ, includePublic, userId, roles, groups)
        .flatMap(branchEntity -> processBranchEntity(
            branchEntity,
            openBranchCommand,
            omitGeometries ? GeometryCommand.OMIT : GeometryCommand.RETAIN,
            sort,
            userId,
            roles,
            groups))
        .sort(treeComparator(sort));
  }

  @Override
  public Mono<Branch> openBranch(
      String branchId,
      OpenBranchCommand openBranchCommand,
      GeometryCommand geometryCommand,
      Sort sort,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    return treeRepository.findBranchById(branchId, READ, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden()))
        .flatMap(branchEntity -> processBranchEntity(
            branchEntity,
            openBranchCommand,
            geometryCommand,
            sort,
            userId,
            roles,
            groups));
  }

  @Override
  public Mono<Void> closeBranch(
      String branchId,
      String userId) {

    return treeRepository.closeBranch(branchId, userId);
  }

  private Mono<Branch> processBranchEntity(
      final BranchEntity branchEntity,
      final OpenBranchCommand openBranchCommand,
      final GeometryCommand geometryCommand,
      final Sort sort,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return treeRepository.findNodeSettings(branchEntity.getId(), userId)
        .cast(BranchEntitySettings.class)
        .switchIfEmpty(treeRepository.persistNodeSettings(
            BranchEntitySettings.builder()
                .nodeId(branchEntity.getId())
                .open(openBranchCommand != OpenBranchCommand.RETAIN)
                .userId(userId)
                .build()))
        .flatMap(branchEntitySettings -> {
          if (openBranchCommand.isCurrentAndBranchNotOpen(branchEntitySettings)) {
            branchEntitySettings.setOpen(true);
            return treeRepository.openBranch(branchEntitySettings.getId())
                .flatMap(result -> Mono.just(branchEntitySettings));
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
            geometryCommand,
            sort,
            userId,
            roles, groups));
  }

  private Mono<Branch> processBranch(
      final Branch branch,
      final OpenBranchCommand openBranchCommand,
      final GeometryCommand geometryCommand,
      final Sort sort,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (GeometryCommand.DISPLAY == geometryCommand || openBranchCommand.openBranch(branch)) {
      return treeRepository
          .findNodesByParentId(branch.getId(), READ, true, userId, roles, groups)
          .flatMap(nodeEntity -> processChild(
              nodeEntity,
              openBranchCommand,
              geometryCommand,
              sort,
              userId,
              roles,
              groups))
          .collectSortedList(treeComparator(sort))
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
      final GeometryCommand geometryCommand,
      final Sort sort,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (nodeEntity instanceof BranchEntity) {
      final BranchEntity branchEntity = (BranchEntity) nodeEntity;
      return processBranchEntity(
          branchEntity, openBranchCommand, geometryCommand, sort, userId, roles, groups)
          .cast(Node.class);
    }
    if (nodeEntity instanceof LeafEntity) {
      final boolean omitGeometry = GeometryCommand.OMIT == geometryCommand;
      final boolean displayedOnMap = GeometryCommand.DISPLAY == geometryCommand;
      final LeafEntity leafEntity = (LeafEntity) nodeEntity;
      return treeRepository.findNodeSettings(leafEntity.getId(), userId)
          .switchIfEmpty(treeRepository.persistNodeSettings(getLeafAdapter(leafEntity)
              .buildLeafEntitySettings(leafEntity, userId, displayedOnMap)))
          .cast(LeafEntitySettings.class)
          .flatMap(leafEntitySettings -> {
            if (displayedOnMap
                && !getLeafAdapter(leafEntity).isDisplayedOnMap(leafEntitySettings)) {
              return getLeafAdapter(leafEntity).setDisplayedOnMap(leafEntitySettings, true);
            }
            return Mono.just(leafEntitySettings);
          })
          .flatMap(leafEntitySettings -> getLeafAdapter(leafEntity).buildLeaf(
              leafEntity, leafEntitySettings, omitGeometry));
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
          nodeEntity.setModified(OffsetDateTime.now(Clock.systemUTC()));
          nodeEntity.setModifiedBy(userId);
          if (nodeEntity instanceof BranchEntity) {
            ((BranchEntity) nodeEntity).setName(name);
            return treeRepository.persistNode(nodeEntity).map(e -> true);
          } else if (nodeEntity instanceof LeafEntity) {
            return treeRepository.persistNode(nodeEntity)
                .flatMap(updatedNodeEntity -> getLeafAdapter(updatedNodeEntity)
                    .renameLeaf((LeafEntity) updatedNodeEntity, name, userId));
          } else {
            return Mono.error(ServiceException.internalServerError("Node is not branch or leaf."));
          }
        });
  }

  @Override
  public Mono<Void> displayNodeOnMap(
      final String nodeId,
      final Boolean displayedOnMap,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return treeRepository
        .findNodeById(nodeId, READ, true, userId, roles, groups)
        .flatMap(nodeEntity -> displayNodeOnMap(nodeEntity, displayedOnMap, userId, roles, groups));
  }

  private Mono<Void> displayNodeOnMap(
      final NodeEntity nodeEntity,
      final Boolean displayedOnMap,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (nodeEntity instanceof BranchEntity) {
      return treeRepository
          .findNodesByParentId(nodeEntity.getId(), READ, true, userId, roles, groups)
          .flatMap(nodeChild -> displayNodeOnMap(nodeChild, displayedOnMap, userId, roles, groups))
          .count()
          .flatMap(value -> Mono.empty());
    } else if (nodeEntity instanceof FeatureLeafEntity) {
      return treeRepository.updateFeatureLeafSettings(nodeEntity.getId(), userId, displayedOnMap)
          .flatMap(result -> Mono.empty());
    }
    return Mono.empty();
  }

  private Mono<Boolean> isNodeChildOf(final String nodeId, final String parentId) {
    return treeRepository.findNodesByParentId(parentId)
        .collectList()
        .flatMap(nodeEntities -> {
          for (NodeEntity nodeEntity : nodeEntities) {
            if (nodeEntity.getId().equals(nodeId)) {
              return Mono.just(true);
            } else if (nodeEntity instanceof BranchEntity) {
              return isNodeChildOf(nodeId, nodeEntity.getId());
            }
          }
          return Mono.just(false);
        });

  }

  /*
  public Mono<Branch> moveNode(
      final String nodeId,
      final String targetBranchId,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (nodeId.equals(targetBranchId)) {
      return treeRepository
          .findBranchById(targetBranchId, READ, true, userId, roles, groups)
          .switchIfEmpty(Mono.error(ServiceException.forbidden("Branch", targetBranchId)))
          .flatMap(branchEntity -> processBranchEntity(
              branchEntity, OpenBranchCommand.RETAIN, userId, roles, groups));
    }

    return isNodeChildOf(targetBranchId, nodeId)
        .flatMap(isChild -> {
          if (isChild) {
            return Mono.error(ServiceException.badRequest("Target is child of source.")); // TODO
          }
          return treeRepository
              .findNodeById(nodeId, DELETE, true, userId, roles, groups)
              .switchIfEmpty(Mono.error(ServiceException.forbidden("Node", nodeId)))
              .flatMap(nodeEntity -> moveNode(nodeEntity, targetBranchId, userId, roles, groups));
        });

  }

  private Mono<Branch> moveNode(
      final NodeEntity nodeEntity,
      final String targetBranchId,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    return treeRepository.findBranchById(targetBranchId, WRITE, true, userId, roles, groups)
        .switchIfEmpty(Mono.error(ServiceException.forbidden("Branch", targetBranchId)))
        .zipWhen(branchEntity -> {
          nodeEntity.setParentId(branchEntity.getId());
          return treeRepository.persistNode(nodeEntity);
        })
        .flatMap(tuple -> processBranchEntity(
            tuple.getT1(), OpenBranchCommand.RETAIN, userId, roles, groups));
  }
  */

}
