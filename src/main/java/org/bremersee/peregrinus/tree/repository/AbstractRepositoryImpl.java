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

import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.peregrinus.security.access.MongoRepositoryUtils;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public abstract class AbstractRepositoryImpl {

  @Getter(AccessLevel.PACKAGE)
  private ReactiveMongoOperations mongoOperations;

  public AbstractRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  public <T> Mono<T> persist(T entity) {
    return getMongoOperations().save(entity);
  }

  <T> Mono<T> findById(
      Class<T> clazz,
      String id,
      String permission,
      boolean includePublic,
      String userId,
      Collection<String> roles,
      Collection<String> groups) {

    final List<Criteria> criteriaList = MongoRepositoryUtils.buildCriteriaList(
        permission, includePublic, userId, roles, groups);
    final Criteria one = Criteria.where("id").is(id);
    final Criteria two = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    final Criteria oneAndTwo = new Criteria().andOperator(one, two);
    return mongoOperations.findOne(Query.query(oneAndTwo), clazz);
  }

  <T> Flux<T> findByParentId(
      Class<T> clazz,
      String parentId,
      String permission,
      boolean includePublic,
      String userId,
      Collection<String> roles,
      Collection<String> groups) {

    final List<Criteria> criteriaList = MongoRepositoryUtils.buildCriteriaList(
        permission, includePublic, userId, roles, groups);
    final Criteria one = StringUtils.hasText(parentId)
        ? Criteria.where("parentId").is(parentId)
        : new Criteria().orOperator(
            Criteria.where("parentId").exists(false),
            Criteria.where("parentId").is(null));
    final Criteria two = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    final Criteria oneAndTwo = new Criteria().andOperator(one, two);
    return mongoOperations.find(Query.query(oneAndTwo), clazz);
  }

}
