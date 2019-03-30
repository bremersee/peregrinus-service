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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.repository.FeatureRepository;
import org.bremersee.peregrinus.service.adapter.FeatureAdapter;
import org.bremersee.security.access.AclMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class FeatureServiceImpl extends AbstractServiceImpl implements FeatureService {

  private final Map<Class<?>, FeatureAdapter> featureAdapterMap = new HashMap<>();

  private FeatureRepository featureRepository;

  public FeatureServiceImpl(
      AclMapper<AclEntity> aclMapper,
      FeatureRepository featureRepository,
      List<FeatureAdapter> featureAdapters) {
    super(aclMapper);
    this.featureRepository = featureRepository;
    for (final FeatureAdapter featureAdapter : featureAdapters) {
      for (final Class<?> cls : featureAdapter.getSupportedClasses()) {
        featureAdapterMap.put(cls, featureAdapter);
      }
    }
  }

  private FeatureAdapter getFeatureAdapter(final Object obj) {
    return getAdapter(featureAdapterMap, obj);
  }

  @Override
  public Mono<Feature> findFeatureById(String id, String userId) {

    return featureRepository.findFeatureById(id)
        .zipWhen(featureEntity -> featureRepository
            .findFeatureEntitySettings(featureEntity.getId(), userId)
            .switchIfEmpty(featureRepository
                .persistFeatureSettings(getFeatureAdapter(featureEntity)
                    .buildFeatureEntitySettings(featureEntity, userId))))
        .flatMap(tuple -> getFeatureAdapter(tuple.getT1())
            .buildFeature(tuple.getT1(), tuple.getT2()));
  }

  @Override
  public Mono<Boolean> renameFeature(String id, String name, @NotNull String userId) {
    return featureRepository.renameFeature(id, name, userId);
  }
}
