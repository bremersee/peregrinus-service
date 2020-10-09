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

import static org.bremersee.peregrinus.service.AdapterHelper.getAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.Wpt;
import org.bremersee.peregrinus.service.adapter.GeocodeAdapter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class GeocodeServiceImpl implements GeocodeService {

  private Map<String, GeocodeAdapter> adapterMap = new HashMap<>();

  private FeatureService featureService;

  public GeocodeServiceImpl(
      List<GeocodeAdapter> adapters,
      FeatureService featureService) {

    if (adapters != null) {
      for (GeocodeAdapter adapter : adapters) {
        for (Class<? extends GeocodeQueryRequest> cls : adapter.getSupportedRequestClasses()) {
          adapterMap.put(cls.getName(), adapter);
        }
      }
    }
    this.featureService = featureService;
  }

  @Override
  public Mono<FeatureCollection> queryGeocode(
      GeocodeQueryRequest request,
      String userId,
      Set<String> roles,
      Set<String> groups) {

    final Flux<Wpt> webWpts = getAdapter(adapterMap, request)
        .queryGeocode(request, userId, roles, groups);
    final Flux<Wpt> localWpts = featureService.queryGeocode(request, userId, roles, groups);
    return Flux.concat(webWpts, localWpts)
        .collectList()
        .map(wpts -> {
          FeatureCollection fc = new FeatureCollection();
          List<Feature> features = new ArrayList<>(wpts); // TODO
          fc.setFeatures(features);
          fc.setBbox(GeometryUtils.getBoundingBox(wpts.stream().map(Wpt::getGeometry).collect(Collectors.toList())));
          return fc;
        });
  }

}
