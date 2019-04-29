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

package org.bremersee.peregrinus.service.adapter.tomtom.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.UnknownAware;
import org.bremersee.geojson.model.LatitudeLongitude;
import org.springframework.util.StringUtils;

/**
 * The address.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Address extends UnknownAware {

  private String buildingNumber;

  private List<String> routeNumbers;

  private String streetNumber;

  private String streetName;

  private String crossStreet;

  private String municipalitySubdivision; // Ortsteil

  private String municipality; // Stadt

  private String countrySecondarySubdivision; // Stadt

  private String countrySubdivision; // Niedersachsen

  private String postalCode;

  private String countryCode; // DE

  private String country; // Deutschland

  private String countryCodeISO3; // DEU

  private String freeformAddress; // Straße Nummer, PZL Stadt

  /**
   * Find bounding box rectangle.
   *
   * @return the rectangle
   */
  @SuppressWarnings("unused")
  public Rectangle findBoundingBox() {
    final Optional<String> northEast = findUnknown("$.boundingBox.northEast", String.class);
    final Optional<String> southWest = findUnknown("$.boundingBox.southWest", String.class);
    if (northEast.isPresent() && southWest.isPresent()) {
      final String[] neArray = StringUtils.commaDelimitedListToStringArray(northEast.get());
      final String[] swArray = StringUtils.commaDelimitedListToStringArray(southWest.get());
      if (neArray != null && neArray.length == 2 && swArray != null && swArray.length == 2) {
        try {
          final LatitudeLongitude ne = new LatitudeLongitude(
              new BigDecimal(neArray[0].trim()),
              new BigDecimal(neArray[1].trim()));
          final LatitudeLongitude sw = new LatitudeLongitude(
              new BigDecimal(swArray[0].trim()),
              new BigDecimal(swArray[1].trim()));
          return new Rectangle(sw, ne);
        } catch (final RuntimeException ignored) {
          // ignored
        }
      }
    }
    return null;
  }

}
