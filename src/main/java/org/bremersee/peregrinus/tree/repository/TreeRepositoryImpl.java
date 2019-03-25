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

package org.bremersee.peregrinus.tree.repository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.peregrinus.security.access.AclEntity;
import org.bremersee.peregrinus.security.access.MongoRepositoryUtils;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.model.NodeSettings;
import org.bremersee.peregrinus.tree.repository.adapter.NodeAdapter;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntity;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntitySettings;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntity;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntitySettings;
import org.bremersee.security.access.AclMapper;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Repository
@Slf4j
public class TreeRepositoryImpl implements TreeRepository {

  private final Map<Class<?>, NodeAdapter> nodeAdapterMap = new HashMap<>();

  private ReactiveMongoOperations mongoOperations;

  private AclMapper<AclEntity> aclMapper;

  public TreeRepositoryImpl(
      final ReactiveMongoOperations mongoOperations,
      final AclMapper<AclEntity> aclMapper,
      final List<NodeAdapter> nodeAdapters) {

    Assert.notNull(mongoOperations, "Mongo operations must not be null.");
    Assert.notNull(aclMapper, "Acl mapper must not be null.");
    Assert.notNull(nodeAdapters, "Node adapters must not be null.");
    this.mongoOperations = mongoOperations;
    this.aclMapper = aclMapper;
    for (final NodeAdapter nodeAdapter : nodeAdapters) {
      for (final Class<?> cls : nodeAdapter.getSupportedClasses()) {
        nodeAdapterMap.put(cls, nodeAdapter);
      }
    }
  }

  @Override
  public <T extends Node> Mono<T> persistNode(
      final T node,
      final String userId) {

    node.setModifiedBy(userId);
    node.setModified(OffsetDateTime.now(Clock.systemUTC()));
    //noinspection unchecked
    return getNodeAdapter(node).mapNode(node, userId)
        .flatMap(this::persistNodeAndSettings)
        .flatMap(tuple -> (Mono<T>) getNodeAdapter(tuple.getT1())
            .mapNodeEntity(tuple.getT1(), tuple.getT2()));
  }

  private Mono<Tuple2<NodeEntity, NodeEntitySettings>> persistNodeAndSettings(
      final Tuple2<NodeEntity, NodeEntitySettings> tuple) {

    return mongoOperations.save(tuple.getT1()).flatMap(entity -> {
      tuple.getT2().setNodeId(entity.getId());
      return Mono.zip(Mono.just(entity), mongoOperations.save(tuple.getT2()));
    });
  }

  @Override
  public Mono<Node> findNodeById(
      final String id,
      final String permission,
      final boolean includePublic,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    return mongoOperations
        .findOne(Query.query(MongoRepositoryUtils.buildCriteria(
            Criteria.where("id").is(id),
            permission, includePublic, userId, roles, groups)), NodeEntity.class)
        .flatMap(nodeEntity -> Mono.zip(
            Mono.just(nodeEntity),
            findNodeSettings(nodeEntity, userId)))
        .flatMap(this::mapNode);
  }

  @Override
  public Flux<Node> findNodesByParentId(
      final String parentId,
      final String permission,
      final boolean includePublic,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Criteria criteria = StringUtils.hasText(parentId)
        ? Criteria.where("parentId").is(parentId)
        : new Criteria().orOperator(
            Criteria.where("parentId").exists(false),
            Criteria.where("parentId").is(null));
    return mongoOperations
        .find(Query.query(MongoRepositoryUtils.buildCriteria(
            criteria,
            permission, includePublic, userId, roles, groups)), NodeEntity.class)
        .flatMap(nodeEntity -> Mono.zip(
            Mono.just(nodeEntity),
            findNodeSettings(nodeEntity, userId)))
        .flatMap(this::mapNode);
  }

  @Override
  public Mono<Boolean> updateName(
      final String id,
      final String name,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Update update = new Update()
        .set("modified", OffsetDateTime.now(Clock.systemUTC()))
        .set("modifiedBy", userId)
        .set("name", name);
    return mongoOperations.findAndModify(
        Query.query(MongoRepositoryUtils.buildCriteria(
            Criteria.where("id").is(id),
            PermissionConstants.WRITE, true, userId, roles, groups)),
        update,
        NodeEntity.class)
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity)
            .updateName(nodeEntity, name, userId, roles, groups))
        .flatMap(nodeEntity -> Mono.just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> updateAccessControl(
      final String id,
      final AccessControlList acl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final AclEntity aclEntity = aclMapper.map(acl);
    final Update update = new Update()
        .set("modified", OffsetDateTime.now(Clock.systemUTC()))
        .set("modifiedBy", userId)
        .set("acl." + PermissionConstants.ADMINISTRATION, aclEntity.getAdministration())
        .set("acl." + PermissionConstants.CREATE, aclEntity.getCreate())
        .set("acl." + PermissionConstants.DELETE, aclEntity.getDelete())
        .set("acl." + PermissionConstants.READ, aclEntity.getRead())
        .set("acl." + PermissionConstants.WRITE, aclEntity.getWrite());
    return mongoOperations.findAndModify(
        Query.query(MongoRepositoryUtils.buildCriteria(
            Criteria.where("id").is(id),
            PermissionConstants.ADMINISTRATION, true, userId, roles, groups)),
        update,
        NodeEntity.class)
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity).updateAccessControl(
            nodeEntity, acl, userId, roles, groups))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return updateAccessControlRecursive(
                nodeEntity.getId(), acl, userId, roles, groups);
          }
          return Mono.just(Boolean.TRUE);
        });
  }

  private Mono<Boolean> updateAccessControlRecursive(
      final String parentId,
      final AccessControlList acl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    return mongoOperations
        .find(Query.query(MongoRepositoryUtils.buildCriteria(
            Criteria.where("parentId").is(parentId),
            PermissionConstants.ADMINISTRATION, true, userId, roles, groups)),
            NodeEntity.class)
        .flatMap(nodeEntity -> {
          final AclEntity aclEntity = aclMapper.map(acl);
          aclEntity.setOwner(nodeEntity.getAcl().getOwner());
          nodeEntity.setModified(OffsetDateTime.now(Clock.systemUTC()));
          nodeEntity.setModifiedBy(userId);
          nodeEntity.setAcl(aclEntity);
          return mongoOperations.save(nodeEntity);
        })
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity).updateAccessControl(
            nodeEntity, acl, userId, roles, groups))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return updateAccessControlRecursive(
                nodeEntity.getId(), acl, userId, roles, groups);
          }
          return Mono.just(Boolean.TRUE);
        })
        .count()
        .flatMap(c -> Mono.just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> removeNode(
      final String id,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    // TODO delete other settings
    return mongoOperations
        .findAndRemove(Query.query(MongoRepositoryUtils.buildCriteria(
            Criteria.where("id").is(id),
            PermissionConstants.DELETE, true, userId, roles, groups)),
            NodeEntity.class)
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity)
            .removeNode(nodeEntity, userId, roles, groups))
        .flatMap(nodeEntity -> mongoOperations.remove(
            Query.query(nodeSettingsCriteria(nodeEntity.getId(), userId)),
            NodeEntitySettings.class)
            .map(deleteResult -> nodeEntity))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return deleteNodeRecursive(nodeEntity.getId(), userId, roles, groups);
          }
          return Mono.just(Boolean.TRUE);
        });
  }

  private Mono<Boolean> deleteNodeRecursive(
      final String parentId,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Criteria one = Criteria.where("parentId").is(parentId);
    return mongoOperations.find(Query.query(one), NodeEntity.class)
        .flatMap(nodeEntity -> mongoOperations.remove(nodeEntity).map(deleteResult -> nodeEntity))
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity)
            .removeNode(nodeEntity, userId, roles, groups))
        .flatMap(nodeEntity -> mongoOperations.remove(
            Query.query(nodeSettingsCriteria(nodeEntity.getId(), userId)),
            NodeEntitySettings.class)
            .map(deleteResult -> nodeEntity))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return deleteNodeRecursive(nodeEntity.getId(), userId, roles, groups);
          }
          return Mono.just(Boolean.TRUE);
        })
        .count()
        .flatMap(c -> Mono.just(Boolean.TRUE));
  }

  @Override
  public <T extends NodeSettings> Mono<T> persistNodeSettings(
      final T settings,
      final String userId) {

    //noinspection unchecked
    return getNodeAdapter(settings).mapNodeSettings(settings, userId)
        .flatMap(entity -> mongoOperations.save(entity))
        .flatMap(entity -> (Mono<T>) getNodeAdapter(entity).mapNodeEntitySettings(entity));
  }

  @Override
  public Mono<Boolean> closeBranch(
      @NotNull final String branchId,
      @NotNull final String userId) {

    final Update update = new Update()
        .set("open", false);
    return mongoOperations
        .findAndModify(Query.query(nodeSettingsCriteria(branchId, userId)), update,
            BranchEntitySettings.class)
        .switchIfEmpty(Mono.just(
            BranchEntitySettings.builder()
                .nodeId(branchId)
                .userId(userId).open(false)
                .build()))
        .flatMap(branchEntitySettings -> mongoOperations.save(branchEntitySettings))
        .map(branchEntitySettings -> Boolean.TRUE);
  }

  private Mono<? extends NodeEntitySettings> findNodeSettings(
      final NodeEntity nodeEntity,
      final String userId) {
    return mongoOperations
        .findOne(Query.query(nodeSettingsCriteria(nodeEntity.getId(), userId)),
            NodeEntitySettings.class)
        .switchIfEmpty(getNodeAdapter(nodeEntity).defaultSettings(nodeEntity, userId));
  }

  private Mono<? extends Node> mapNode(
      final Tuple2<? extends NodeEntity, ? extends NodeEntitySettings> tuple) {
    return getNodeAdapter(tuple.getT1()).mapNodeEntity(tuple.getT1(), tuple.getT2());
  }

  private NodeAdapter getNodeAdapter(final Object obj) {
    Assert.notNull(obj, "Object must not be null.");
    final Class<?> cls;
    if (obj instanceof Class<?>) {
      cls = (Class<?>) obj;
    } else {
      cls = obj.getClass();
    }
    final NodeAdapter nodeAdapter = nodeAdapterMap.get(cls);
    if (nodeAdapter == null) {
      RuntimeException e = new RuntimeException("No node adapter found for " + cls.getName());
      log.error("Getting node adapter failed.", e);
      throw e;
    }
    return nodeAdapter;
  }

  private Criteria nodeSettingsCriteria(final String nodeId, final String userId) {
    return new Criteria().andOperator(
        Criteria.where("nodeId").is(nodeId),
        Criteria.where("userId").is(userId));
  }

}
