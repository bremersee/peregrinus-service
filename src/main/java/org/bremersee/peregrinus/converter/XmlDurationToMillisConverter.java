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

package org.bremersee.peregrinus.converter;

import java.util.Date;
import javax.xml.datatype.Duration;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Christian Bremer
 */
public class XmlDurationToMillisConverter implements Converter<Duration, Long> {

  @Override
  public Long convert(Duration duration) {
    if (duration == null) {
      return null;
    }
    final Date tmp = new Date(0L);
    duration.addTo(tmp);
    return tmp.getTime();
  }
}
