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

package org.bremersee.peregrinus.tree.repository.adapter;

import java.util.Collection;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.model.BranchSettings;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.model.NodeSettings;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntity;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntitySettings;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntity;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntitySettings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
@Component
public class BranchAdapter extends AbstractNodeAdapter implements NodeAdapter {

  public BranchAdapter(ModelMapper modelMapper) {
    super(modelMapper);
  }

  @Override
  public Class<?>[] getSupportedClasses() {
    return new Class[]{
        BranchEntity.class,
        BranchEntitySettings.class,
        Branch.class,
        BranchSettings.class
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public Mono<Tuple2<BranchEntity, BranchEntitySettings>> mapNode(
      final Node node,
      final String userId) {
    return Mono.just(super.mapNode(node, userId, BranchEntity::new, BranchEntitySettings::new));
  }

  @Override
  public Mono<BranchEntitySettings> mapNodeSettings(
      final NodeSettings nodeSettings,
      final String userId) {
    return Mono.just(super.mapNodeSettings(nodeSettings, userId, BranchEntitySettings::new));
  }

  @Override
  public Mono<Branch> mapNodeEntity(
      final NodeEntity nodeEntity,
      final NodeEntitySettings nodeEntitySettings) {

    final BranchEntity branchEntity = (BranchEntity) nodeEntity;
    final Branch branch = super.mapNodeEntity(
        nodeEntity, nodeEntitySettings, Branch::new, BranchSettings::new);
    branch.setName(branchEntity.getName());
    return Mono.just(branch);
  }

  @Override
  public Mono<BranchSettings> mapNodeEntitySettings(
      final NodeEntitySettings nodeSettings) {
    return Mono.just(super.mapNodeEntitySettings(nodeSettings, BranchSettings::new));
  }

  @Override
  public Mono<NodeEntity> updateName(
      final NodeEntity nodeEntity,
      final String name,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {
    // the 'real' update is done in the repository
    ((BranchEntity) nodeEntity).setName(name);
    return Mono.just(nodeEntity);
  }

  @Override
  public Mono<NodeEntity> updateAccessControl(
      final NodeEntity nodeEntity,
      final AccessControl accessControl,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {
    // the 'real' update is done in the repository
    nodeEntity.setAccessControl(new AccessControlEntity(accessControl.ensureAdminAccess()));
    return Mono.just(nodeEntity);
  }

  @Override
  public Mono<NodeEntity> removeNode(
      final NodeEntity nodeEntity,
      final String userId,
      final Collection<String> roles,
      final Collection<String> groups) {
    return Mono.just(nodeEntity);
  }

  @Override
  public Mono<BranchEntitySettings> defaultSettings(
      final NodeEntity nodeEntity,
      final String userId) {
    final BranchEntitySettings settings = new BranchEntitySettings();
    settings.setNodeId(nodeEntity.getId());
    settings.setUserId(userId);
    return Mono.just(settings);
  }

}
