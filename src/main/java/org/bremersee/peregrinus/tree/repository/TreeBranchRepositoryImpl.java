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
import org.bremersee.peregrinus.tree.model.Branch;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public class TreeBranchRepositoryImpl extends AbstractRepositoryImpl
    implements TreeBranchRepositoryCustom {

  public TreeBranchRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    super(mongoOperations);
  }

  @Override
  public Mono<Branch> findById(
      String branchId,
      String permission,
      String userId,
      Collection<String> roles,
      Collection<String> groups) {

    return super.findById(
        Branch.class, branchId, permission, true, userId, roles, groups);
  }

  @Override
  public Flux<Branch> findByParentId(
      String parentId,
      String permission,
      boolean includePublic,
      String userId,
      Collection<String> roles,
      Collection<String> groups) {

    return super.findByParentId(
        Branch.class, parentId, permission, includePublic, userId, roles, groups);
  }

}
