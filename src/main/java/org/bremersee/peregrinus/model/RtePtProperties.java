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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.AccessControlList;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.locationtech.jts.geom.Polygon;

/**
 * @author Christian Bremer
 */
@ApiModel(
    value = "RtePtProperties",
    description = "Properties of a route point.",
    parent = PtProperties.class)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RtePtProperties extends PtProperties<RtePtSettings> {

  private RtePtCalculationProperties calculationProperties;

  private RtePtCalculationResults calculationResults;

  private List<RtePtInstruction> instructions;

  //private Integer lengthInMeters;

  //private Integer travelTimeInSeconds;

  //private Integer trafficDelayInSeconds;

  //private Integer noTrafficTravelTimeInSeconds;

  //private Integer historicTrafficTravelTimeInSeconds;

  //private Integer liveTrafficIncidentsTravelTimeInSeconds;

  public RtePtProperties() {
    noAcl();
    noSettings();
  }

  @Builder
  public RtePtProperties(
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
      RtePtSettings settings,
      String internalType,
      BigDecimal ele,
      Address address,
      List<PhoneNumber> phoneNumbers,
      Polygon area,
      String osmId,
      String osmType,
      String osmPlaceId,
      String osmCategory,
      RtePtCalculationProperties calculationProperties,
      RtePtCalculationResults calculationResults,
      List<? extends RtePtInstruction> instructions) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, internalComments, links, departureTime, arrivalTime, settings,
        internalType, ele, address, phoneNumbers, area, osmId, osmType, osmPlaceId, osmCategory);
    this.calculationProperties = calculationProperties;
    this.calculationResults = calculationResults;
    if (instructions != null) {
      this.instructions = new ArrayList<>(instructions);
    }
  }

  @Override
  public AccessControlList getAcl() {
    // Route points have no acl.
    return null;
  }

  @Override
  public void setAcl(AccessControlList acl) {
    // Route points have no acl.
  }

  @Override
  public RtePtSettings getSettings() {
    // Route points have no settings.
    return null;
  }

  @Override
  public void setSettings(RtePtSettings settings) {
    // Route points have no settings.
  }

  public List<RtePtInstruction> getInstructions() {
    if (instructions == null) {
      instructions = new ArrayList<>();
    }
    return instructions;
  }
}
