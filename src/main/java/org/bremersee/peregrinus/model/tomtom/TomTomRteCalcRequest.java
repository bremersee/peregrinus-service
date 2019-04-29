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

import io.swagger.annotations.ApiModel;
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
import org.bremersee.peregrinus.model.RteCalcRequest;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

/**
 * @author Christian Bremer
 */
@ApiModel(description = "TomTom route calculation request.")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class TomTomRteCalcRequest extends RteCalcRequest {

  private TravelMode travelMode;

  private RouteType routeType;

  private Windingness windingness;

  private Hilliness hilliness;

  private List<Avoid> avoid;

  private List<ThreeLetterCountryCode> avoidVignette;

  private MultiPolygon avoidAreas;

  @Builder
  public TomTomRteCalcRequest(
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
    final TomTomRteSegCalcSettings settings = new TomTomRteSegCalcSettings();
    settings.setAvoid(avoid);
    settings.setAvoidAreas(avoidAreas);
    settings.setAvoidVignette(avoidVignette);
    settings.setHilliness(hilliness);
    settings.setRouteType(routeType);
    settings.setTravelMode(travelMode);
    settings.setWindingness(windingness);
    return settings;
  }
}
