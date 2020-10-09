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

package org.bremersee.peregrinus.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.common.model.Link;

/**
 * The route properties.
 *
 * @author Christian Bremer
 */
@Schema(description = "Route properties.")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RteProperties extends FeatureProperties<RteSettings> {

  @Schema(description = "The route segments.")
  private List<RteSeg> rteSegments;

  /**
   * Instantiates new route properties.
   */
  public RteProperties() {
    setSettings(new RteSettings());
    rteSegments = new ArrayList<>();
  }

  /**
   * Instantiates new route properties.
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
   * @param settings             the settings
   * @param rteSegments          the rte segments
   */
  @Builder
  @SuppressWarnings("unused")
  public RteProperties(
      AccessControlList acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      List<Link> links,
      RteSettings settings,
      List<RteSeg> rteSegments) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, links, settings);
    setRteSegments(rteSegments);
  }

  /**
   * Sets rte segments.
   *
   * @param rteSegments the rte segments
   */
  public void setRteSegments(List<RteSeg> rteSegments) {
    if (rteSegments == null) {
      this.rteSegments = new ArrayList<>();
    } else {
      this.rteSegments = rteSegments;
    }
  }

  /**
   * Calculate travel time in seconds.
   *
   * @return travel time in seconds
   */
  public BigInteger calculateTravelTimeInSeconds() {
    BigInteger sum = BigInteger.valueOf(0);
    for (RteSeg rteSegment : rteSegments) {
      if (rteSegment.getTravelTimeInSeconds() != null) {
        sum = sum.add(rteSegment.getTravelTimeInSeconds());
      } else {
        return null;
      }
    }
    return sum;
  }

  /**
   * Calculate length in meters.
   *
   * @return the big integer
   */
  public BigInteger calculateLengthInMeters() {
    BigInteger sum = BigInteger.valueOf(0);
    for (RteSeg rteSegment : rteSegments) {
      if (rteSegment.getLengthInMeters() != null) {
        sum = sum.add(rteSegment.getLengthInMeters());
      } else {
        return null;
      }
    }
    return sum;
  }

  @Override
  public int compareTo(FeatureProperties other) {
    if (other instanceof WptProperties) {
      return 1;
    }
    if (other instanceof TrkProperties) {
      return -1;
    }
    return 0;
  }
}
