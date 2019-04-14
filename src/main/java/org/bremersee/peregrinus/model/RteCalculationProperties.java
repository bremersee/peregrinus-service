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

import java.time.Clock;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;

/**
 * @author Christian Bremer
 */
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class RteCalculationProperties {

  private HttpLanguageTag language = HttpLanguageTag.EN_GB;

  private OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

  private Boolean timeIsDepartureTime = Boolean.TRUE;

  public RteCalculationProperties(
      HttpLanguageTag language,
      OffsetDateTime time,
      Boolean timeIsDepartureTime) {
    setLanguage(language);
    setTime(time);
    setTimeIsDepartureTime(timeIsDepartureTime);
  }

  public HttpLanguageTag getLanguage() {
    return language;
  }

  public void setLanguage(HttpLanguageTag language) {
    if (language != null) {
      this.language = language;
    }
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime(OffsetDateTime time) {
    if (time != null) {
      this.time = time;
    }
  }

  public Boolean getTimeIsDepartureTime() {
    return timeIsDepartureTime;
  }

  public void setTimeIsDepartureTime(Boolean timeIsDepartureTime) {
    if (timeIsDepartureTime != null) {
      this.timeIsDepartureTime = timeIsDepartureTime;
    }
  }
}
