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

package org.bremersee.peregrinus.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The TomTom properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.tomtom")
@Component
@ToString
@EqualsAndHashCode
@SuppressWarnings("WeakerAccess")
public class TomTomProperties {

  public static final String DEFAULT_ROUTING_URI = "https://api.tomtom.com/routing/1/calculateRoute";

  public static final String DEFAULT_GEOCODE_URI = "https://api.tomtom.com/search/2";

  public static final String DEFAULT_REVERSE_GEOCODE_URI = "https://api.tomtom.com/search/2/reverseGeocode";

  @Getter
  @Setter
  private String key;

  @Getter
  @Setter
  private String routingUri = DEFAULT_ROUTING_URI;

  @Getter
  @Setter
  private String geocodeUri = DEFAULT_GEOCODE_URI;

  @Getter
  @Setter
  private String reverseGeocodeUri = DEFAULT_REVERSE_GEOCODE_URI;

  @Getter
  @Setter
  private String userAgent = "curl/7.54.0";

}
