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

package org.bremersee.peregrinus.service.adapter.nominatim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.UnknownAware;

/**
 * Nominatim address response object.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Address extends UnknownAware {

  private String address26;

  private String address29;

  @JsonProperty("building")
  private String building; // "Kommandantenhaus"

  @JsonProperty("city")
  private String city; // "Berlin",

  @JsonProperty("city_district")
  private String cityDistrict; // "Mitte",

  @JsonProperty("road")
  private String road; // "Unter den Linden",

  @JsonProperty("continent")
  private String continent; // "European Union",

  @JsonProperty("country")
  private String country; // "Deutschland",

  @JsonProperty("county")
  private String county; // "Landkreis Peine",

  @JsonProperty("country_code")
  private String countryCode; // "de",

  @JsonProperty("house_number")
  private String houseNumber; // "1",

  @JsonProperty("neighbourhood")
  private String neighbourhood; // "Spandauer Vorstadt",

  @JsonProperty("postcode")
  private String postcode; // "10117",

  @JsonProperty("public_building")
  private String publicBuilding; // "Kommandantenhaus",

  @JsonProperty("state")
  private String state; // "Berlin", "Lower Saxony"

  @JsonProperty("suburb")
  private String suburb; // "Mitte", "Dechsendorf"

  @JsonProperty("town")
  private String town; // "Peine"

  @JsonProperty("tram_stop")
  private String tramStop;

  @JsonProperty("village")
  private String village;

  /**
   * Find the city, town or village.
   *
   * @return the city, town or village (can be {@code null})
   */
  public String findCity() {
    return city != null ? city : town != null ? town : village;
  }

  /**
   * Get the country code.
   *
   * @return the country code (can be {@code null})
   */
  public String getCountryCode() {
    return countryCode != null ? countryCode.toUpperCase() : null;
  }

  /**
   * Get the formatted address.
   *
   * @return the formatted address (can be {@code null})
   */
  @JsonIgnore
  public String getFormattedAddress() {
    final StringBuilder sb = new StringBuilder(buildStreet());
    final String city = findCity();
    if (city != null) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(city);
    }
    final String country = getCountry();
    if (country != null) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(country);
    }
    return sb.length() > 0 ? sb.toString() : null;
  }

  private String buildStreet() {
    if (road == null) {
      return "";
    }
    return road + (houseNumber != null ? " " + houseNumber : "");
  }

}
