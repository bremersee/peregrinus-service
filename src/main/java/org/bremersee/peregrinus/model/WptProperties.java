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
import io.swagger.annotations.ApiModelProperty;
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
 * The waypoint properties.
 *
 * @author Christian Bremer
 */
@ApiModel(description = "The waypoint properties.")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WptProperties extends FeatureProperties<WptSettings> {

  @ApiModelProperty("The elevation in meters.")
  private BigDecimal ele;

  @ApiModelProperty("The address of this waypoint.")
  private Address address;

  @ApiModelProperty("The phone numbers")
  private List<PhoneNumber> phoneNumbers;

  @ApiModelProperty("The polygon of the building.")
  private Polygon area;

  @ApiModelProperty("The OSM ID.")
  private String osmId;

  @ApiModelProperty("The OSM type.")
  private String osmType;

  @ApiModelProperty("The OSM place ID.")
  private String osmPlaceId;

  @ApiModelProperty("The OSM category.")
  private String osmCategory;

  /**
   * Instantiates new waypoint properties.
   */
  public WptProperties() {
    setSettings(new WptSettings());
  }

  /**
   * Instantiates new waypoint properties.
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
   * @param ele                  the ele
   * @param address              the address
   * @param phoneNumbers         the phone numbers
   * @param area                 the area
   * @param osmId                the osm id
   * @param osmType              the osm type
   * @param osmPlaceId           the osm place id
   * @param osmCategory          the osm category
   */
  @Builder
  public WptProperties(
      AccessControlList acl,
      OffsetDateTime created,
      String createdBy,
      OffsetDateTime modified,
      String modifiedBy,
      String name,
      String plainTextDescription,
      String markdownDescription,
      List<Link> links,
      WptSettings settings,
      BigDecimal ele,
      Address address,
      List<PhoneNumber> phoneNumbers,
      Polygon area,
      String osmId,
      String osmType,
      String osmPlaceId,
      String osmCategory) {

    super(acl, created, createdBy, modified, modifiedBy, name, plainTextDescription,
        markdownDescription, links, settings);
    setEle(ele);
    setAddress(address);
    setPhoneNumbers(phoneNumbers);
    setArea(area);
    setOsmId(osmId);
    setOsmType(osmType);
    setOsmPlaceId(osmPlaceId);
    setOsmCategory(osmCategory);
  }

  /**
   * Gets phone numbers.
   *
   * @return the phone numbers
   */
  public List<PhoneNumber> getPhoneNumbers() {
    if (phoneNumbers == null) {
      phoneNumbers = new ArrayList<>();
    }
    return phoneNumbers;
  }

  /**
   * Sets phone numbers.
   *
   * @param phoneNumbers the phone numbers
   */
  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    if (phoneNumbers == null) {
      this.phoneNumbers = new ArrayList<>();
    } else {
      this.phoneNumbers = phoneNumbers;
    }
  }
}
