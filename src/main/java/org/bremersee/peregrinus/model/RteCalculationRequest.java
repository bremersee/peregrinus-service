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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;

/**
 * @author Christian Bremer
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class RteCalculationRequest extends RteCalculationProperties {

  private List<Pt> rtePts;

  public RteCalculationRequest(
      HttpLanguageTag language,
      OffsetDateTime time,
      Boolean timeIsDepartureTime,
      List<Pt> rtePts) {
    super(language, time, timeIsDepartureTime);
    this.rtePts = rtePts;
  }

  public List<Pt> getRtePts() {
    if (rtePts == null) {
      rtePts = new ArrayList<>();
    }
    return rtePts;
  }

  public void setRtePts(List<Pt> rtePts) {
    this.rtePts = rtePts;
  }
}
