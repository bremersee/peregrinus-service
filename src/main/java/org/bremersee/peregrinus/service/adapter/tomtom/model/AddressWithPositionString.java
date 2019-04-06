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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.model.LatitudeLongitude;
import org.bremersee.plain.model.UnknownAware;
import org.springframework.util.StringUtils;

/**
 * The address with position string.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class AddressWithPositionString extends UnknownAware {

  private Address address;

  private String position;

  /**
   * Parse position to latitude and longitude.
   *
   * @return the latitude longitude (can be {@code null})
   */
  @SuppressWarnings("unused")
  public LatitudeLongitude parsePosition() {
    if (StringUtils.hasText(position)) {
      final String[] posArray = StringUtils.commaDelimitedListToStringArray(position);
      if (posArray != null && posArray.length == 2) {
        try {
          return new LatitudeLongitude(new BigDecimal(
              posArray[0].trim()),
              new BigDecimal(posArray[1].trim()));
        } catch (final RuntimeException ignored) {
          // ignored
        }
      }
    }
    return null;
  }

}
