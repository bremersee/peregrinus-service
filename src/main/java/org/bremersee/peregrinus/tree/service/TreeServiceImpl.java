/*
 * Copyright 2018 the original author or authors.
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

import java.util.Date;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeature;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeatureSettings;
import org.bremersee.peregrinus.geo.repository.GeoJsonFeatureSettingsRepository;
import org.bremersee.peregrinus.tree.model.AbstractTreeNode;
import org.bremersee.peregrinus.tree.model.TreeBranch;
import org.bremersee.peregrinus.tree.model.TreeBranchSettings;
import org.bremersee.peregrinus.tree.model.GeoTreeLeaf;
import org.bremersee.peregrinus.tree.repository.TreeBranchRepository;
import org.bremersee.peregrinus.tree.repository.TreeBranchSettingsRepository;
import org.bremersee.peregrinus.tree.repository.GeoTreeLeafRepository;
import org.bremersee.peregrinus.tree.repository.TreeNodeRepository;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public class TreeServiceImpl implements TreeService {

  private TreeNodeRepository nodeRepository;

  private TreeBranchRepository branchRepository;

  //private GeoTreeLeafRepository geoTreeLeafRepository;

  private TreeBranchSettingsRepository branchSettingsRepository;

  private GeoJsonFeatureSettingsRepository featureSettingsRepository;

  public Mono<TreeBranch> loadPrivateTree(final String userId) {

    final String branchName = "u-" + userId;

    return branchRepository
        .findByNameAndParentIdIsNull(branchName)
        .switchIfEmpty(createBranch(userId, branchName, null))
        .flatMap(treeBranch -> loadBranch(treeBranch, userId));
  }

  private Mono<TreeBranch> createBranch(
      final String userId,
      final String name,
      final String parentId) {

    final Date currentDate = new Date();
    final TreeBranch branch = new TreeBranch();
    branch.setCreated(currentDate);
    branch.setCreatedBy(userId);
    branch.setModified(currentDate);
    branch.setModifiedBy(userId);
    branch.setName(name);
    branch.setParentId(parentId);
    return branchRepository.save(branch);
  }

  private Mono<TreeBranch> loadBranch(final TreeBranch branch, final String userId) {
    return addBranchSettings(branch, userId)
        .flatMap(parent -> addBranchChildren(parent, userId));
  }

  private Mono<TreeBranch> addBranchSettings(final TreeBranch branch, final String userId) {
    return branchSettingsRepository
        .findByNodeIdAndUserId(branch.getId(), userId)
        .switchIfEmpty(createBranchSettings(branch.getId(), userId))
        .map(branchSettings -> {
          branch.setSettings(branchSettings);
          return branch;
        });
  }

  private Mono<TreeBranchSettings> createBranchSettings(
      final String branchId,
      final String userId) {

    final TreeBranchSettings branchSettings = new TreeBranchSettings();
    branchSettings.setNodeId(branchId);
    branchSettings.setOpen(true);
    branchSettings.setUserId(userId);
    return branchSettingsRepository.save(branchSettings);
  }

  private Mono<TreeBranch> addBranchChildren(final TreeBranch parent, final String userId) {

    return nodeRepository
        .findByParentId(parent.getId())
        .flatMap(child -> processChild(child, userId))
        .collectSortedList()
        .map(children -> {
          parent.setChildren(children);
          return parent;
        })
        .switchIfEmpty(Mono.just(parent));
  }

  private Mono<AbstractTreeNode> processChild(
      final AbstractTreeNode child,
      final String userId) {

    if (child instanceof TreeBranch) {
      return loadBranch((TreeBranch) child, userId).cast(AbstractTreeNode.class);
    }
    if (child instanceof GeoTreeLeaf) {
      return featureSettingsRepository
          .findByFeatureIdAndUserId(((GeoTreeLeaf) child).getFeature().getId(), userId)
          .switchIfEmpty(createFeatureSettings(((GeoTreeLeaf) child).getFeature(), userId))
          .map(featureSettings -> {
            //noinspection unchecked
            ((GeoTreeLeaf) child).getFeature().getProperties().setSettings(featureSettings);
            return child;
          });
    }
    return Mono.just(child);
  }

  private Mono<AbstractGeoJsonFeatureSettings> createFeatureSettings(
      final AbstractGeoJsonFeature feature, final String userId) {

    final AbstractGeoJsonFeatureSettings featureSettings = feature
        .getProperties()
        .createDefaultSettings(feature.getId(), userId, true);
    return featureSettingsRepository.save(featureSettings);
  }

}
