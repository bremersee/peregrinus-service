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
 * The track properties entity.
 *
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

  /**
   * Instantiates a new track properties entity.
   */
  @SuppressWarnings("WeakerAccess")
  public TrkEntityProperties() {
    eleLines = new ArrayList<>();
    timeLines = new ArrayList<>();
  }

  /**
   * Instantiates a new track properties entity.
   *
   * @param acl                  the acl
   * @param created              the created
   * @param createdBy            the created by
   * @param modified             the modified
   * @param modifiedBy           the modified by
   * @param name                 the name
   * @param plainTextDescription the plain text description
   * @param markdownDescription  the markdown description
   * @param links                the links
   * @param address              the address
   * @param departureTime        the departure time
   * @param arrivalTime          the arrival time
   * @param eleLines             the ele lines
   * @param timeLines            the time lines
   */
  @Builder
  @SuppressWarnings("unused")
  public TrkEntityProperties(
      AclEntity acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      List<Link> links,
      AddressEntity address,
      OffsetDateTime departureTime,
      OffsetDateTime arrivalTime,
      List<List<BigDecimal>> eleLines,
      List<List<OffsetDateTime>> timeLines) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, links, address, departureTime, arrivalTime);
    setEleLines(eleLines);
    setTimeLines(timeLines);
  }

  /**
   * Sets ele lines.
   *
   * @param eleLines the ele lines
   */
  @SuppressWarnings("WeakerAccess")
  public void setEleLines(List<List<BigDecimal>> eleLines) {
    if (eleLines == null) {
      this.eleLines = new ArrayList<>();
    } else {
      this.eleLines = eleLines;
    }
  }

  /**
   * Sets time lines.
   *
   * @param timeLines the time lines
   */
  @SuppressWarnings("WeakerAccess")
  public void setTimeLines(List<List<OffsetDateTime>> timeLines) {
    if (timeLines == null) {
      this.timeLines = new ArrayList<>();
    } else {
      this.timeLines = timeLines;
    }
  }
}
