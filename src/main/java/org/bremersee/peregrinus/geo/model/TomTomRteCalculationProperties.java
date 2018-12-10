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

package org.bremersee.peregrinus.geo.model;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.tomtom.model.Rectangle;
import org.bremersee.tomtom.model.RoutingRequest.Avoid;
import org.bremersee.tomtom.model.RoutingRequest.Hilliness;
import org.bremersee.tomtom.model.RoutingRequest.RouteType;
import org.bremersee.tomtom.model.RoutingRequest.TravelMode;
import org.bremersee.tomtom.model.RoutingRequest.Windingness;
import org.springframework.data.annotation.TypeAlias;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@TypeAlias("TomTomRteCalculationProperties")
public class TomTomRteCalculationProperties extends AbstractRteCalculationProperties {

  /**
   * The mode of travel for the requested route.
   */
  private TravelMode travelMode;

  private RouteType routeType;

  /**
   * Degree of hilliness for calculating a thrilling route.
   */
  private Hilliness hilliness;

  /**
   * Amount that a thrilling route should wind.
   */
  private Windingness windingness;

  /**
   * Specifies whether the routing engine should try to avoid specific types of road segment when
   * calculating the route. Can be specified multiple times.
   */
  private List<Avoid> avoid;

  /**
   * A list of shapes to avoid for planning routes. Supported shapes include {@code rectangles}. Can
   * contain one of each supported shapes element.
   */
  private List<Rectangle> avoidAreas; // TODO geometry

  /**
   * List of 3-character ISO 3166-1 alpha-3 country codes of countries in which all toll roads with
   * vignettes are to be avoided. Toll roads with vignettes in countries not in the list are
   * unaffected. It is an error to specify both avoidVignette and allowVignette.
   */
  private List<Locale> avoidVignette; // TODO mongo & json converter

  /**
   * List of 3-character ISO 3166-1 alpha-3 country codes of countries in which toll roads with
   * vignettes are allowed. Specifying allowVignette with some countries X is equivalent to
   * specifying avoidVignette with all countries but X. Specifying allowVignette with an empty list
   * is the same as avoiding all toll roads with vignettes. It is an error to specify both
   * avoidVignette and allowVignette.
   */
  private List<Locale> allowVignette; // TODO mongo & json converter

  private Date departAt;

  private Date arriveAt;

  @Override
  public String getProvider() {
    return "TomTom";
  }

}
