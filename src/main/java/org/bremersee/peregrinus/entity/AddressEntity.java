/*
 * Copyright 2019 the original author or authors.
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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.TwoLetterCountryCode;
import org.springframework.data.mongodb.core.index.TextIndexed;

/**
 * The address entity.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AddressEntity {

  @TextIndexed
  private String streetNumber;

  @TextIndexed
  private String street;

  @TextIndexed
  private String postalCode;

  @TextIndexed
  private String city;

  private String suburb;

  private String state;

  private String country;

  private TwoLetterCountryCode countryCode;

  @TextIndexed
  private String formattedAddress;

  /**
   * Instantiates a new address entity.
   *
   * @param streetNumber     the street number
   * @param street           the street
   * @param postalCode       the postal code
   * @param city             the city
   * @param suburb           the suburb
   * @param state            the state
   * @param country          the country
   * @param countryCode      the country code
   * @param formattedAddress the formatted address
   */
  @Builder
  @SuppressWarnings("unused")
  public AddressEntity(
      String streetNumber,
      String street,
      String postalCode,
      String city,
      String suburb,
      String state,
      String country,
      TwoLetterCountryCode countryCode,
      String formattedAddress) {
    this.streetNumber = streetNumber;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.suburb = suburb;
    this.state = state;
    this.country = country;
    this.countryCode = countryCode;
    this.formattedAddress = formattedAddress;
  }
}
