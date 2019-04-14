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

import io.swagger.annotations.ApiModel;
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
 * @author Christian Bremer
 */
@ApiModel(
    value = "RteProperties",
    description = "Properties of a route.",
    parent = FeatureProperties.class)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RteProperties extends FeatureProperties<RteSettings> {

  private RteCalculationProperties calculationProperties;

  private List<RtePt> rtePts;

  public RteProperties() {
    setSettings(new RteSettings());
    rtePts = new ArrayList<>();
  }

  @Builder
  public RteProperties(
      AccessControlList acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime departureTime,
      OffsetDateTime arrivalTime,
      RteSettings settings,
      RteCalculationProperties calculationProperties,
      List<RtePt> rtePts) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, internalComments, links, departureTime, arrivalTime, settings);
    setCalculationProperties(calculationProperties);
    setRtePts(rtePts);
  }

  public void setRtePts(List<RtePt> rtePts) {
    if (rtePts == null) {
      this.rtePts = new ArrayList<>();
    } else {
      this.rtePts = rtePts;
    }
  }
}
