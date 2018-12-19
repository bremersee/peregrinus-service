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

package org.bremersee.peregrinus.geo.mapper.tomtom;

import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.geo.model.GeoCodingQueryRequest;
import org.bremersee.peregrinus.geo.model.GeoCodingResult;
import org.bremersee.peregrinus.geo.model.Rte;
import org.bremersee.peregrinus.geo.model.TomTomRteCalculationProperties;
import org.bremersee.tomtom.model.GeocodeRequest;
import org.bremersee.tomtom.model.GeocodeResponse;
import org.bremersee.tomtom.model.Route;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Validated
public interface TomTomMapper {

  @NotNull
  GeocodeRequest mapToGeocodeRequest(@NotNull GeoCodingQueryRequest source);

  @NotNull
  Iterable<GeoCodingResult> mapToGeoCodingResults(@NotNull GeocodeResponse source);

  Rte mapToRte(Route route, TomTomRteCalculationProperties calculationProperties);

}
