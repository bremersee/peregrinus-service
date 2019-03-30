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

package org.bremersee.peregrinus.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.springframework.data.annotation.TypeAlias;

/**
 * @author Christian Bremer
 */
@TypeAlias("TrkProperties")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrkEntityProperties extends FeatureEntityProperties {

  private List<List<BigDecimal>> eleLines;

  private List<List<OffsetDateTime>> timeLines;

  public TrkEntityProperties() {
    eleLines = new ArrayList<>();
    timeLines = new ArrayList<>();
  }

  @Builder
  public TrkEntityProperties(
      AclEntity acl,
      OffsetDateTime created,
      OffsetDateTime modified,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime startTime,
      OffsetDateTime stopTime,
      List<List<BigDecimal>> eleLines,
      List<List<OffsetDateTime>> timeLines) {

    super(acl, created, modified, name, plainTextDescription, markdownDescription,
        internalComments, links, startTime, stopTime);
    setEleLines(eleLines);
    setTimeLines(timeLines);
  }

  public void setEleLines(List<List<BigDecimal>> eleLines) {
    if (eleLines == null) {
      this.eleLines = new ArrayList<>();
    } else {
      this.eleLines = eleLines;
    }
  }

  public void setTimeLines(List<List<OffsetDateTime>> timeLines) {
    if (timeLines == null) {
      this.timeLines = new ArrayList<>();
    } else {
      this.timeLines = timeLines;
    }
  }
}
