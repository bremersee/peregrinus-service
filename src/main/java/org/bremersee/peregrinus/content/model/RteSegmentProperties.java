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

package org.bremersee.peregrinus.content.model;

import java.time.Instant;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.geo.model.AbstractRteCalculationProperties;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
public class RteSegmentProperties {

  private AbstractRteCalculationProperties calculationProperties;

  private String name;

  private Instant time; // arrival and departure

  private Integer lengthInMeters;

  private Integer travelTimeInSeconds;

  private Integer trafficDelayInSeconds;

  private Integer noTrafficTravelTimeInSeconds;

  private Integer historicTrafficTravelTimeInSeconds;

  private Integer liveTrafficIncidentsTravelTimeInSeconds;

}
