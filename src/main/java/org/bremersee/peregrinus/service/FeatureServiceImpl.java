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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.FeatureEntitySettings;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.model.gpx.GpxExportSettings;
import org.bremersee.peregrinus.repository.FeatureRepository;
import org.bremersee.peregrinus.service.adapter.FeatureAdapter;
import org.bremersee.security.access.AclMapper;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class FeatureServiceImpl extends AbstractServiceImpl implements FeatureService {

  private final Map<String, FeatureAdapter> featureAdapterMap = new HashMap<>();

  private FeatureRepository featureRepository;

  private ConverterService converterService;

  public FeatureServiceImpl(
      AclMapper<AclEntity> aclMapper,
      FeatureRepository featureRepository,
      List<FeatureAdapter> featureAdapters,
      ConverterService converterService) {
    super(aclMapper);
    this.featureRepository = featureRepository;
    this.converterService = converterService;
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
        .flatMap(tuple -> adapter.buildFeature(tuple.getT1(), tuple.getT2(), false));
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
  public Mono<Feature> findFeatureById(String id, String userId, boolean omitGeometry) {

    return featureRepository.findFeatureById(id)
        .zipWhen(featureEntity -> featureRepository
            .findFeatureEntitySettings(featureEntity.getId(), userId)
            .switchIfEmpty(featureRepository
                .persistFeatureSettings(getFeatureAdapter(featureEntity)
                    .buildFeatureEntitySettings(featureEntity, userId))))
        .flatMap(tuple -> getFeatureAdapter(tuple.getT1())
            .buildFeature(tuple.getT1(), tuple.getT2(), omitGeometry));
  }

  @Override
  public Flux<Feature> findFeaturesById(
      Set<String> featureIds,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    final List<String> ids = new ArrayList<>(featureIds);
    return featureRepository
        .findFeaturesByIds(ids, PermissionConstants.READ, true, userId, roles, groups)
        .flatMap(featureEntity -> mapFeatureEntity(featureEntity, userId));
  }

  @Override
  public Mono<Boolean> renameFeature(
      String id,
      String name,
      String userId) {
    return featureRepository.renameFeature(id, name, userId);
  }

  @Override
  public Mono<Gpx> exportGpx(
      Set<String> featureIds,
      GpxExportSettings exportSettings,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    return findFeaturesById(featureIds, userId, roles, groups)
        .collectList()
        .map(features -> converterService.convertFeaturesToGpx(features, exportSettings));
  }

  @Override
  public Flux<Wpt> queryGeocode(
      GeocodeQueryRequest request,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    return featureRepository.queryGeocode(request, userId, roles, groups)
        .flatMap(wptEntity -> mapFeatureEntity(wptEntity, userId))
        .cast(Wpt.class);
  }

  private Mono<Feature> mapFeatureEntity(final FeatureEntity featureEntity, final String userId) {
    return featureRepository
        .findFeatureEntitySettings(featureEntity.getId(), userId)
        .switchIfEmpty(featureRepository
            .persistFeatureSettings(getFeatureAdapter(featureEntity)
                .buildFeatureEntitySettings(featureEntity, userId)))
        .map(featureEntitySettings -> Tuples.of(featureEntity, featureEntitySettings))
        .flatMap(tuple -> getFeatureAdapter(tuple.getT1())
            .buildFeature(tuple.getT1(), tuple.getT2(), false));
  }

}
