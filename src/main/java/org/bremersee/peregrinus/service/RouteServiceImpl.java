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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteCalculationRequest;
import org.bremersee.peregrinus.service.adapter.RouteAdapter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
public class RouteServiceImpl implements RouteService {

  private Map<String, RouteAdapter> adapterMap = new HashMap<>();

  public RouteServiceImpl(List<RouteAdapter> adapters) {
    if (adapters != null) {
      for (RouteAdapter adapter : adapters) {
        for (Class<? extends GeocodeQueryRequest> cls : adapter.getSupportedRequestClasses()) {
          adapterMap.put(cls.getName(), adapter);
        }
      }
    }
  }

  @Override
  public Mono<Rte> calculateRoute(RteCalculationRequest request, String userId, Set<String> roles) {
    return getAdapter(adapterMap, request).calculateRoute(request, userId, roles);
  }
}
