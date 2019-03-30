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

package org.bremersee.peregrinus.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.AclEntity;
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

/**
 * @author Christian Bremer
 */
@Repository
public class TreeRepositoryImpl extends AbstractMongoRepository implements TreeRepository {

  public TreeRepositoryImpl(
      ReactiveMongoOperations mongoOperations,
      AclMapper<AclEntity> aclMapper) {
    super(mongoOperations, aclMapper);
  }

  @Override
  protected String aclPath() {
    return NodeEntity.ACL_PATH;
  }

  @Override
  public Mono<NodeEntity> findNodeById(
      @NotNull String id,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    final Query query = queryAnd(
        where(NodeEntity.ID_PATH).is(id), includePublic, userId, roles, groups, permission);
    return getMongoOperations()
        .findOne(query, NodeEntity.class);
  }

  @Override
  public <T extends NodeEntity> Mono<T> persistNode(@NotNull T node) {
    return getMongoOperations().save(node);
  }

  @Override
  public Flux<NodeEntity> findNodesByParentId(
      @NotNull String parentId,
      @NotNull String permission,
      boolean includePublic,
      @NotNull String userId,
      @NotNull Collection<String> roles,
      @NotNull Collection<String> groups) {

    final Criteria criteria = StringUtils.hasText(parentId)
        ? where(NodeEntity.PARENT_ID_PATH).is(parentId)
        : new Criteria().orOperator(
            where(NodeEntity.PARENT_ID_PATH).exists(false),
            where(NodeEntity.PARENT_ID_PATH).is(null));
    final Query query = queryAnd(criteria, includePublic, userId, roles, groups, permission);
    return getMongoOperations()
        .find(query, NodeEntity.class);
  }

  @Override
  public <T extends NodeEntitySettings> Mono<T> persistNodeSettings(@NotNull T nodeSettings) {
    return getMongoOperations().save(nodeSettings);
  }

  @Override
  public Mono<NodeEntitySettings> findNodeSettings(@NotNull String nodeId, @NotNull String userId) {
    return getMongoOperations()
        .findOne(query(nodeSettingsCriteria(nodeId, userId)), NodeEntitySettings.class);
  }

  @Override
  public Mono<Boolean> openBranch(@NotNull String settingsId) {
    final Update update = Update.update(BranchEntitySettings.OPEN_PATH, true);
    return getMongoOperations()
        .findAndModify(query(where("id").is(settingsId)), update, BranchEntitySettings.class)
        .map(result -> true)
        .switchIfEmpty(Mono.just(false));
  }

  @Override
  public Mono<Void> closeBranch(@NotNull String branchId, @NotNull String userId) {
    final Query query = query(nodeSettingsCriteria(branchId, userId));
    final Update update = Update.update(BranchEntitySettings.OPEN_PATH, false);
    return getMongoOperations()
        .findAndModify(query, update, BranchEntitySettings.class)
        .flatMap(result -> Mono.empty());
  }

  private Criteria nodeSettingsCriteria(final String nodeId, final String userId) {
    return new Criteria().andOperator(
        where(NodeEntitySettings.NODE_ID_PATH).is(nodeId),
        where(NodeEntitySettings.USER_ID_PATH).is(userId));
  }

}
