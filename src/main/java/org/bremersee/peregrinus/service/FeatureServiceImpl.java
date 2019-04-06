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

import static org.bremersee.security.access.PermissionConstants.WRITE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.repository.FeatureRepository;
import org.bremersee.peregrinus.service.adapter.FeatureAdapter;
import org.bremersee.security.access.AclMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class FeatureServiceImpl extends AbstractServiceImpl implements FeatureService {

  private final Map<String, FeatureAdapter> featureAdapterMap = new HashMap<>();

  private FeatureRepository featureRepository;

  public FeatureServiceImpl(
      AclMapper<AclEntity> aclMapper,
      FeatureRepository featureRepository,
      List<FeatureAdapter> featureAdapters) {
    super(aclMapper);
    this.featureRepository = featureRepository;
    for (final FeatureAdapter featureAdapter : featureAdapters) {
      for (final String key : featureAdapter.getSupportedKeys()) {
        featureAdapterMap.put(key, featureAdapter);
      }
    }
  }

  private FeatureAdapter getFeatureAdapter(final Object obj) {
    return AdapterHelper.getAdapter(featureAdapterMap, obj);
  }

  @Override
  public Mono<Feature> persistFeature(
      final Feature feature,
      final String userId,
      final Set<String> roles,
      final Set<String> groups) {

    if (StringUtils.hasText(feature.getId())) {
      return featureRepository
          .findFeatureById(feature.getId(), WRITE, true, userId, roles, groups)
          .switchIfEmpty(Mono.error(ServiceException.forbidden("Feature", feature.getId())))
          .flatMap(featureEntity -> persistFeature(feature, userId, featureEntity));
    }
    return persistFeature(feature, userId, null);
  }

  private Mono<Feature> persistFeature(
      final Feature feature,
      final String userId,
      final FeatureEntity featureEntity) {

    final FeatureAdapter adapter = getFeatureAdapter(feature);
    final FeatureEntity newFeatureEntity = adapter
        .buildFeatureEntity(feature, userId, featureEntity);
    return featureRepository
        .persistFeature(newFeatureEntity)
        .zipWhen(persistedFeatureEntity -> persistFeatureSettings(
            feature.getProperties().getSettings(), userId, persistedFeatureEntity))
        .flatMap(tuple -> adapter.buildFeature(tuple.getT1(), tuple.getT2()));
  }

  private Mono<FeatureEntitySettings> persistFeatureSettings(
      final FeatureSettings featureSettings,
      final String userId,
      final FeatureEntity featureEntity) {

    final FeatureAdapter adapter = getFeatureAdapter(featureEntity);
    return featureRepository
        .findFeatureEntitySettings(featureEntity.getId(), userId)
        .switchIfEmpty(Mono.just(adapter.buildFeatureEntitySettings(featureEntity, userId)))
        .map(featureEntitySettings -> adapter.updateFeatureEntitySettings(
            featureEntitySettings, featureSettings))
        .flatMap(featureRepository::persistFeatureSettings);
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
