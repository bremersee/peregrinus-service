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

import static org.bremersee.security.access.PermissionConstants.ADMINISTRATION;
import static org.bremersee.security.access.PermissionConstants.DELETE;
import static org.bremersee.security.access.PermissionConstants.WRITE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.util.Assert.notNull;
import static reactor.core.publisher.Mono.just;
import static reactor.core.publisher.Mono.zip;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.exception.ServiceException;
import org.bremersee.peregrinus.repository.AbstractMongoRepository;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.model.Node;
import org.bremersee.peregrinus.model.NodeSettings;
import org.bremersee.peregrinus.tree.repository.adapter.NodeAdapter;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bremersee.peregrinus.entity.NodeEntitySettings;
import org.bremersee.security.access.AclMapper;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Repository
@Slf4j
public class TreeRepositoryImpl extends AbstractMongoRepository implements TreeRepository {

  private final Map<Class<?>, NodeAdapter> nodeAdapterMap = new HashMap<>();

  public TreeRepositoryImpl(
      final ReactiveMongoOperations mongoOperations,
      final AclMapper<AclEntity> aclMapper,
      final List<NodeAdapter> nodeAdapters) {

    super(mongoOperations, aclMapper);
    notNull(nodeAdapters, "Node adapters must not be null.");
    for (final NodeAdapter nodeAdapter : nodeAdapters) {
      for (final Class<?> cls : nodeAdapter.getSupportedClasses()) {
        nodeAdapterMap.put(cls, nodeAdapter);
      }
    }
  }

  @Override
  protected String aclPath() {
    return "acl";
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

    return mongo().save(tuple.getT1()).flatMap(entity -> {
      tuple.getT2().setNodeId(entity.getId());
      return zip(just(entity), mongo().save(tuple.getT2()));
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

    final Query query = queryAnd(
        where("id").is(id), includePublic, userId, roles, groups, permission);
    return mongo()
        .findOne(query, NodeEntity.class)
        .flatMap(nodeEntity -> zip(just(nodeEntity), findNodeSettings(nodeEntity, userId)))
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
        ? where("parentId").is(parentId)
        : new Criteria().orOperator(
            where("parentId").exists(false),
            where("parentId").is(null));
    final Query query = queryAnd(criteria, includePublic, userId, roles, groups, permission);
    return mongo()
        .find(query, NodeEntity.class)
        .flatMap(nodeEntity -> zip(
            just(nodeEntity),
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
    final Query query = queryAnd(where("id").is(id), true, userId, roles, groups, WRITE);
    return mongo().findAndModify(query, update, NodeEntity.class)
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity)
            .updateName(nodeEntity, name, userId, roles, groups))
        .flatMap(nodeEntity -> just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> updateAccessControl(
      final String id,
      final AccessControlList acl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Update update = createUpdate(aclMapper().map(acl), userId);
    final Query query = queryAnd(where("id").is(id), true, userId, roles, groups, ADMINISTRATION);
    return mongo().findAndModify(query, update, NodeEntity.class)
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity).updateAccessControl(
            nodeEntity, acl, userId, roles, groups))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return updateAccessControlRecursive(
                nodeEntity.getId(), acl, userId, roles, groups);
          }
          return just(Boolean.TRUE);
        });
  }

  private Mono<Boolean> updateAccessControlRecursive(
      final String parentId,
      final AccessControlList acl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Query query = queryAnd(
        where("parentId").is(parentId), true, userId, roles, groups, ADMINISTRATION);
    return mongo()
        .find(query, NodeEntity.class)
        .flatMap(nodeEntity -> {
          final AclEntity aclEntity = aclMapper().map(acl);
          aclEntity.setOwner(nodeEntity.getAcl().getOwner());
          nodeEntity.setModified(OffsetDateTime.now(Clock.systemUTC()));
          nodeEntity.setModifiedBy(userId);
          nodeEntity.setAcl(aclEntity);
          return mongo().save(nodeEntity);
        })
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity).updateAccessControl(
            nodeEntity, acl, userId, roles, groups))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return updateAccessControlRecursive(
                nodeEntity.getId(), acl, userId, roles, groups);
          }
          return just(Boolean.TRUE);
        })
        .count()
        .flatMap(c -> just(Boolean.TRUE));
  }

  @Override
  public Mono<Boolean> removeNode(
      final String id,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    // TODO delete other settings
    final Query query = queryAnd(where("id").is(id), true, userId, roles, groups, DELETE);
    return mongo()
        .findAndRemove(query, NodeEntity.class)
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity)
            .removeNode(nodeEntity, userId, roles, groups))
        .flatMap(nodeEntity -> mongo()
            .remove(
                query(nodeSettingsCriteria(nodeEntity.getId(), userId)),
                NodeEntitySettings.class)
            .map(deleteResult -> nodeEntity))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return deleteNodeRecursive(nodeEntity.getId(), userId, roles, groups);
          }
          return just(Boolean.TRUE);
        });
  }

  private Mono<Boolean> deleteNodeRecursive(
      final String parentId,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {

    final Query query = query(where("parentId").is(parentId));
    return mongo().find(query, NodeEntity.class)
        .flatMap(nodeEntity -> mongo().remove(nodeEntity).map(deleteResult -> nodeEntity))
        .flatMap(nodeEntity -> getNodeAdapter(nodeEntity)
            .removeNode(nodeEntity, userId, roles, groups))
        .flatMap(nodeEntity -> mongo()
            .remove(
                query(nodeSettingsCriteria(nodeEntity.getId(), userId)),
                NodeEntitySettings.class)
            .map(deleteResult -> nodeEntity))
        .flatMap(nodeEntity -> {
          if (nodeEntity instanceof BranchEntity) {
            return deleteNodeRecursive(nodeEntity.getId(), userId, roles, groups);
          }
          return just(Boolean.TRUE);
        })
        .count()
        .flatMap(c -> just(Boolean.TRUE));
  }

  @Override
  public <T extends NodeSettings> Mono<T> persistNodeSettings(
      final T settings,
      final String userId) {

    //noinspection unchecked
    return getNodeAdapter(settings).mapNodeSettings(settings, userId)
        .flatMap(entity -> mongo().save(entity))
        .flatMap(entity -> (Mono<T>) getNodeAdapter(entity).mapNodeEntitySettings(entity));
  }

  @Override
  public Mono<Boolean> closeBranch(
      final String branchId,
      final String userId) {

    final Update update = Update.update("open", false);
    final Query query = query(nodeSettingsCriteria(branchId, userId));
    return mongo()
        .findAndModify(query, update, BranchEntitySettings.class)
        .switchIfEmpty(just(
            BranchEntitySettings.builder()
                .nodeId(branchId)
                .userId(userId).open(false)
                .build()))
        .flatMap(branchEntitySettings -> mongo().save(branchEntitySettings))
        .map(branchEntitySettings -> Boolean.TRUE);
  }

  private Mono<? extends NodeEntitySettings> findNodeSettings(
      final NodeEntity nodeEntity,
      final String userId) {

    final Query query = query(nodeSettingsCriteria(nodeEntity.getId(), userId));
    return mongo()
        .findOne(query, NodeEntitySettings.class)
        .switchIfEmpty(getNodeAdapter(nodeEntity).defaultSettings(nodeEntity, userId));
  }

  private Mono<? extends Node> mapNode(
      final Tuple2<? extends NodeEntity, ? extends NodeEntitySettings> tuple) {

    return getNodeAdapter(tuple.getT1()).mapNodeEntity(tuple.getT1(), tuple.getT2());
  }

  private NodeAdapter getNodeAdapter(final Object obj) {

    notNull(obj, "Object must not be null.");
    final Class<?> cls;
    if (obj instanceof Class<?>) {
      cls = (Class<?>) obj;
    } else {
      cls = obj.getClass();
    }
    final NodeAdapter nodeAdapter = nodeAdapterMap.get(cls);
    if (nodeAdapter == null) {
      final ServiceException se = ServiceException.internalServerError(
          "No node adapter found for " + cls.getName());
      log.error("Getting node adapter failed.", se);
      throw se;
    }
    return nodeAdapter;
  }

  private Criteria nodeSettingsCriteria(final String nodeId, final String userId) {
    return new Criteria().andOperator(
        where("nodeId").is(nodeId),
        where("userId").is(userId));
  }

}
