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

package org.bremersee.peregrinus.config;

import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * The gpx properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.gpx")
@Component
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class GpxProperties {

  private String[] nameSpaces = GarminJaxbContextDataProvider.GPX_NAMESPACES;

  private String version = "1.1";

  private String creator = "Peregrinus Web App";

  private String link = "https://peregrinus.bremersee.org";

  private String linkText = "Peregrinus Web App";

  private static boolean validLink(String link) {
    if (!StringUtils.hasText(link)) {
      return false;
    }
    try {
      new URL(link);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  /**
   * Get name spaces of gpx xml document.
   *
   * @return the name spaces of gpx xml document
   */
  public String[] getNameSpaces() {
    return nameSpaces != null && nameSpaces.length > 0
        ? nameSpaces
        : GarminJaxbContextDataProvider.GPX_NAMESPACES;
  }

  /**
   * Gets gpx version (default is {@code 1.1}).
   *
   * @return the gpx version
   */
  public String getVersion() {
    return StringUtils.hasText(version) ? version : "1.1";
  }

  /**
   * Gets gpx document creator (default is {@code Peregrinus Web App}).
   *
   * @return the creator
   */
  public String getCreator() {
    return StringUtils.hasText(creator) ? creator : "Peregrinus Web App";
  }

  /**
   * Gets gpx link (default is {@code https://peregrinus.bremersee.org}).
   *
   * @return the link
   */
  public String getLink() {
    return validLink(link) ? link : "https://peregrinus.bremersee.org";
  }

  /**
   * Gets gpx link text (default is {@code Peregrinus Web App}).
   *
   * @return the link text
   */
  public String getLinkText() {
    return StringUtils.hasText(linkText) ? linkText : "Peregrinus Web App";
  }
}
