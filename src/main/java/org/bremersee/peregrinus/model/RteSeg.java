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

package org.bremersee.peregrinus.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class RteSeg {

  private List<RtePt> rtePts;

  private RteSegCalcSettings calculationSettings;

  private BigInteger travelTimeInSeconds;

  private BigInteger lengthInMeters;

  @Builder
  public RteSeg(
      List<RtePt> rtePts,
      RteSegCalcSettings calculationSettings,
      BigInteger travelTimeInSeconds,
      BigInteger lengthInMeters) {
    this.rtePts = rtePts;
    this.calculationSettings = calculationSettings;
    this.travelTimeInSeconds = travelTimeInSeconds;
    this.lengthInMeters = lengthInMeters;
  }

  public List<RtePt> getRtePts() {
    if (rtePts == null) {
      rtePts = new ArrayList<>();
    }
    return rtePts;
  }

  public boolean hasDepartureRtePtPresent() {
    return findDepartureRtePt() != null;
  }

  public RtePt findDepartureRtePt() {
    if (rtePts == null || rtePts.isEmpty()) {
      return null;
    }
    return rtePts.get(0);
  }

  public boolean hasArrivalRtePtPresent() {
    return findArrivalRtePt() != null;
  }

  public RtePt findArrivalRtePt() {
    if (rtePts == null || rtePts.isEmpty()) {
      return null;
    }
    return rtePts.get(rtePts.size() - 1);
  }
}