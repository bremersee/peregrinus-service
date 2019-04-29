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

package org.bremersee.peregrinus.model.tomtom;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.ThreeLetterCountryCode;
import org.bremersee.peregrinus.model.RteSegCalcSettings;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * The TomTom calculation settings of a route segment.
 *
 * @author Christian Bremer
 */
@ApiModel(description = "TomTom calculation settings of a route segment.")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TomTomRteSegCalcSettings extends RteSegCalcSettings {

  private TravelMode travelMode;

  private RouteType routeType;

  private Windingness windingness;

  private Hilliness hilliness;

  private List<Avoid> avoid;

  private List<ThreeLetterCountryCode> avoidVignette;

  private MultiPolygon avoidAreas;

}
