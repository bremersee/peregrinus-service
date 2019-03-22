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

package org.bremersee.peregrinus.content.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.peregrinus.geo.model.AbstractRteCalculationProperties;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RtePtProperties extends PtProperties<RtePtSettings> {

  private AbstractRteCalculationProperties calculationProperties;

  private Integer lengthInMeters;

  private Integer travelTimeInSeconds;

  private Integer trafficDelayInSeconds;

  private Integer noTrafficTravelTimeInSeconds;

  private Integer historicTrafficTravelTimeInSeconds;

  private Integer liveTrafficIncidentsTravelTimeInSeconds;

  public RtePtProperties() {
    noAccessControl();
    noSettings();
  }

  @Builder
  public RtePtProperties(
      AccessControlDto accessControl,
      OffsetDateTime created,
      OffsetDateTime modified,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime startTime,
      OffsetDateTime stopTime,
      RtePtSettings settings,
      String internalType,
      BigDecimal ele,
      Address address,
      List<PhoneNumber> phoneNumbers,
      AbstractRteCalculationProperties calculationProperties,
      Integer lengthInMeters,
      Integer travelTimeInSeconds,
      Integer trafficDelayInSeconds,
      Integer noTrafficTravelTimeInSeconds,
      Integer historicTrafficTravelTimeInSeconds,
      Integer liveTrafficIncidentsTravelTimeInSeconds) {

    super(accessControl, created, modified, name, plainTextDescription, markdownDescription,
        internalComments, links, startTime, stopTime, settings, internalType, ele, address,
        phoneNumbers);
    this.calculationProperties = calculationProperties;
    this.lengthInMeters = lengthInMeters;
    this.travelTimeInSeconds = travelTimeInSeconds;
    this.trafficDelayInSeconds = trafficDelayInSeconds;
    this.noTrafficTravelTimeInSeconds = noTrafficTravelTimeInSeconds;
    this.historicTrafficTravelTimeInSeconds = historicTrafficTravelTimeInSeconds;
    this.liveTrafficIncidentsTravelTimeInSeconds = liveTrafficIncidentsTravelTimeInSeconds;
  }

  @Override
  public AccessControlDto getAccessControl() {
    return null;
  }

  @Override
  public void setAccessControl(AccessControlDto accessControl) {
  }

  @Override
  public RtePtSettings getSettings() {
    return null;
  }

  @Override
  public void setSettings(RtePtSettings settings) {
  }

}
