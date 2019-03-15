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

package org.bremersee.peregrinus.converter.gpx;

import org.bremersee.gpx.model.WptType;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.xml.JaxbContextBuilder;

/**
 * The wpt to garmin wpt type converter.
 *
 * @author Christian Bremer
 */
class WptToWptTypeConverter extends PtToPtTypeConverter<Wpt> {

  /**
   * Instantiates a new wpt to wpt type converter.
   *
   * @param jaxbContextBuilder the jaxb context builder
   */
  WptToWptTypeConverter(final JaxbContextBuilder jaxbContextBuilder) {
    super(jaxbContextBuilder);
  }

  @Override
  WptType convert(final Wpt wpt) {
    return super.convert(wpt);
  }

}
