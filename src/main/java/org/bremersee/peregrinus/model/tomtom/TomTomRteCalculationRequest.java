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

package org.bremersee.peregrinus.model.tomtom;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bremersee.common.model.ThreeLetterCountryCode;
import org.bremersee.peregrinus.model.RteCalculationRequest;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
public class TomTomRteCalculationRequest extends RteCalculationRequest {

  private TravelMode travelMode;

  private RouteType routeType;

  private Windingness windingness;

  private Hilliness hilliness;

  private List<Avoid> avoid;

  private List<ThreeLetterCountryCode> avoidVignette;

  private MultiPolygon avoidAreas;

}
