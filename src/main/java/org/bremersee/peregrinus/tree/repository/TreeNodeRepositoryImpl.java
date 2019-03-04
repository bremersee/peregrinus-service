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
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.tree.model.AbstractTreeNode;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public class TreeNodeRepositoryImpl extends AbstractRepositoryImpl
    implements TreeNodeRepositoryCustom {

  public TreeNodeRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    super(mongoOperations);
  }

  @Override
  public Mono<AbstractTreeNode> findById(
      String nodeId,
      String permission,
      String userId,
      Collection<String> roles,
      Collection<String> groups) {

    return super.findById(
        AbstractTreeNode.class, nodeId, permission, true, userId, roles, groups);
  }

  @Override
  public Flux<AbstractTreeNode> findByParentId(
      String parentId,
      String userId,
      Collection<String> roles,
      Collection<String> groups) {

    return super.findByParentId(
        AbstractTreeNode.class, parentId, PermissionConstants.READ, true, userId, roles, groups);
  }
}
