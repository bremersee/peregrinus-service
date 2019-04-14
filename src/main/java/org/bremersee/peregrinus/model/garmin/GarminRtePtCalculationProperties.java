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

package org.bremersee.peregrinus.model.garmin;

import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.garmin.trip.v1.model.ext.NamedRoadT;
import org.bremersee.peregrinus.model.RtePtCalculationProperties;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GarminRtePtCalculationProperties extends RtePtCalculationProperties {

  private String transportationMode;

  private OffsetDateTime departureTime;

  private Long stopDurationMillis;

  private OffsetDateTime arrivalTime;

  private String calculationMode;

  private String elevationMode;

  private NamedRoadT namedRoad;

}
