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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bremersee.garmin.GarminJaxbContextDataProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.gpx")
@Component
@Data
@NoArgsConstructor
public class GpxProperties {

  private String[] nameSpaces = GarminJaxbContextDataProvider.GPX_NAMESPACES;

  private String version = "1.1";

  private String creator = "Peregrinus Web App";

  private String link = "https://peregrinus.bremersee.org";

  private String linkText = "Peregrinus Web App";

}
