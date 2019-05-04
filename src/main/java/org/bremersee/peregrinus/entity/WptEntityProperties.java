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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.common.model.PhoneNumber;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.annotation.TypeAlias;

/**
 * The waypoint properties entity.
 *
 * @author Christian Bremer
 */
@TypeAlias("WptProperties")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WptEntityProperties extends FeatureEntityProperties {

  /**
   * Elevation in meters
   */
  private BigDecimal ele;

  private List<PhoneNumber> phoneNumbers = new ArrayList<>();

  private Polygon area;

  private String osmId;

  private String osmType;

  private String osmPlaceId;

  private String osmCategory;

  /**
   * Instantiates a new waypoint properties entity.
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
   * @param ele                  the elevation in meters
   * @param phoneNumbers         the phone numbers
   * @param area                 the area
   * @param osmId                the osm id
   * @param osmType              the osm type
   * @param osmPlaceId           the osm place id
   * @param osmCategory          the osm category
   */
  @Builder
  @SuppressWarnings("unused")
  public WptEntityProperties(
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
      BigDecimal ele,
      List<PhoneNumber> phoneNumbers,
      Polygon area,
      String osmId,
      String osmType,
      String osmPlaceId,
      String osmCategory) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, links, address, departureTime, arrivalTime);
    setEle(ele);
    setPhoneNumbers(phoneNumbers);
    setArea(area);
    setOsmId(osmId);
    setOsmType(osmType);
    setOsmPlaceId(osmPlaceId);
    setOsmCategory(osmCategory);
  }

  /**
   * Sets phone numbers.
   *
   * @param phoneNumbers the phone numbers
   */
  @SuppressWarnings("WeakerAccess")
  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    if (phoneNumbers == null) {
      this.phoneNumbers = new ArrayList<>();
    } else {
      this.phoneNumbers = phoneNumbers;
    }
  }

}
