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

package org.bremersee.peregrinus.service.adapter;

import java.util.Set;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.RteAddRtePtRequest;
import org.bremersee.peregrinus.model.RteCalculationRequest;
import org.bremersee.peregrinus.model.RteChangeRtePtCalculationPropertiesRequest;
import org.bremersee.peregrinus.model.RteChangeRtePtIndexRequest;
import org.bremersee.peregrinus.model.RteChangeRtePtLocationRequest;
import org.bremersee.peregrinus.model.RteRemoveRtePtRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
public interface RouteAdapter {

  @NotNull
  Class<? extends GeocodeQueryRequest>[] getSupportedRequestClasses();

  Mono<Rte> calculateRoute(RteCalculationRequest request, String userId, Set<String> roles);

}
