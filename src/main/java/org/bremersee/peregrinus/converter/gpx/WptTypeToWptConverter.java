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

package org.bremersee.peregrinus.converter.gpx;

import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.xml.JaxbContextBuilder;

/**
 * The garmin wpt type to wpt converter.
 *
 * @author Christian Bremer
 */
class WptTypeToWptConverter extends PtTypeToPtConverter {

  /**
   * Instantiates a new garmin wpt type to wpt converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  WptTypeToWptConverter(final JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  /**
   * Convert wpt.
   *
   * @param wptType the wpt type
   * @return the wpt
   */
  Wpt convert(final WptType wptType) {
    return convert(wptType, Wpt::new, WptProperties::new);
  }

}
