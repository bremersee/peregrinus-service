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

import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;
import org.bremersee.common.model.ThreeLetterCountryCode;
import org.bremersee.peregrinus.model.RteCalculationRequest;
import org.bremersee.peregrinus.model.RteSegCalcSettings;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class TomTomRteCalculationRequest extends RteCalculationRequest {

  private TravelMode travelMode;

  private RouteType routeType;

  private Windingness windingness;

  private Hilliness hilliness;

  private List<Avoid> avoid;

  private List<ThreeLetterCountryCode> avoidVignette;

  private MultiPolygon avoidAreas;

  @Builder
  public TomTomRteCalculationRequest(
      Collection<? extends Point> rtePts,
      HttpLanguageTag language,
      TravelMode travelMode,
      RouteType routeType,
      Windingness windingness,
      Hilliness hilliness,
      List<Avoid> avoid,
      List<ThreeLetterCountryCode> avoidVignette,
      MultiPolygon avoidAreas) {
    
    super(rtePts, language);
    this.travelMode = travelMode;
    this.routeType = routeType;
    this.windingness = windingness;
    this.hilliness = hilliness;
    this.avoid = avoid;
    this.avoidVignette = avoidVignette;
    this.avoidAreas = avoidAreas;
  }

  @Override
  public TomTomRteSegCalcSettings buildRteSegCalcSettings() {
    TomTomRteSegCalcSettings properties = new TomTomRteSegCalcSettings();
    properties.setAvoid(avoid);
    properties.setAvoidAreas(avoidAreas);
    properties.setAvoidVignette(avoidVignette);
    properties.setHilliness(hilliness);
    properties.setRouteType(routeType);
    properties.setTravelMode(travelMode);
    properties.setWindingness(windingness);
    return properties;
  }
}
