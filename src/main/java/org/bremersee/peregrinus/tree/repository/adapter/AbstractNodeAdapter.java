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

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.peregrinus.tree.model.Node;
import org.bremersee.peregrinus.tree.model.NodeSettings;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntity;
import org.bremersee.peregrinus.tree.repository.entity.NodeEntitySettings;
import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * @author Christian Bremer
 */
@Validated
public abstract class AbstractNodeAdapter {

  @Getter(AccessLevel.PROTECTED)
  private ModelMapper modelMapper;

  public AbstractNodeAdapter(
      final ModelMapper modelMapper) {
    Assert.notNull(modelMapper, "Model mapper must not be null.");
    this.modelMapper = modelMapper;
  }

  <T1 extends NodeEntity, T2 extends NodeEntitySettings> Tuple2<T1, T2> mapNode(
      final Node node,
      final String userId,
      final Supplier<T1> nodeSupplier,
      final Supplier<T2> nodeSettingsSupplier) {

    final T1 t1 = nodeSupplier.get();
    modelMapper.map(node, t1);
    final T2 t2 = mapNodeSettings(node.getSettings(), userId, nodeSettingsSupplier);
    return Tuples.of(t1, t2);
  }

  <T extends Node<S>, S extends NodeSettings> T mapNodeEntity(
      final NodeEntity nodeEntity,
      final NodeEntitySettings nodeEntitySettings,
      final Supplier<T> nodeSupplier,
      final Supplier<S> nodeSettingsSupplier) {

    final S settings = nodeSettingsSupplier.get();
    modelMapper.map(nodeEntitySettings, settings);

    final T node = nodeSupplier.get();
    modelMapper.map(nodeEntity, node);
    node.setSettings(settings);
    return node;
  }

  <T extends NodeEntitySettings> T mapNodeSettings(
      final NodeSettings nodeSettings,
      final String userId,
      final Supplier<T> nodeEntitySettingsSupplier) {

    final T entity = nodeEntitySettingsSupplier.get();
    if (nodeSettings != null) {
      modelMapper.map(nodeSettings, entity);
    }
    entity.setUserId(userId);
    return entity;
  }

  <T extends NodeSettings> T mapNodeEntitySettings(
      final NodeEntitySettings nodeEntitySettings,
      final Supplier<T> nodeSettingsSupplier) {

    final T settings = nodeSettingsSupplier.get();
    modelMapper.map(nodeEntitySettings, settings);
    return settings;
  }

}
