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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.model.RteSeg;
import org.springframework.data.annotation.TypeAlias;

/**
 * The route properties entity.
 *
 * @author Christian Bremer
 */
@TypeAlias("RteProperties")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RteEntityProperties extends FeatureEntityProperties {

  private List<RteSeg> rteSegments;

  /**
   * Instantiates a new route properties entity.
   */
  @SuppressWarnings("WeakerAccess")
  public RteEntityProperties() {
    rteSegments = new ArrayList<>();
  }

  /**
   * Instantiates a new route properties entity.
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
   * @param rteSegments          the rte segments
   */
  @Builder
  @SuppressWarnings("unused")
  public RteEntityProperties(
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
      List<RteSeg> rteSegments) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, links, address, departureTime, arrivalTime);
    setRteSegments(rteSegments);
  }

  /**
   * Sets route segments.
   *
   * @param rteSegments the route segments
   */
  @SuppressWarnings("WeakerAccess")
  public void setRteSegments(List<RteSeg> rteSegments) {
    if (rteSegments == null) {
      this.rteSegments = new ArrayList<>();
    } else {
      this.rteSegments = rteSegments;
    }
  }
}
