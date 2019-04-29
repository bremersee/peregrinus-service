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

package org.bremersee.peregrinus.service.adapter.tomtom.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.UnknownAware;

/**
 * The routing response.
 *
 * <p>See
 * <a href="https://developer.tomtom.com/routing-api/routing-api-documentation-routing/calculate-route#Response">
 * Response</a>
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RouteResponse extends UnknownAware {

  private String formatVersion;

  private String copyright;

  private String privacy;

  private RouteError error;

  private List<Route> routes;

  /**
   * Optimized sequence of waypoints. It shows the index from the user provided waypoint sequence
   * for the original and optimized list. For instance, a response:
   * <pre>{@literal
   * <optimizedWaypoints>
   * <waypoint providedIndex="0" optimizedIndex="1"/>
   * <waypoint providedIndex="1" optimizedIndex="2"/>
   * <waypoint providedIndex="2" optimizedIndex="0"/>
   * </optimizedWaypoints>}
   * </pre>
   * means that the original sequence is [0, 1, 2] and optimized sequence is [1, 2, 0]. Since the
   * index starts by 0 the original is "first, second, third" while the optimized is "second, third,
   * first".
   */
  private List<OptimizedWaypoint> optimizedWaypoints;

  private RouteResponseReport report;

}
