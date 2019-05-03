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

package org.bremersee.peregrinus.entity.converter;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;

/**
 * The mongo entity converters.
 *
 * @author Christian Bremer
 */
public abstract class EntityConverters {

  private EntityConverters() {
  }

  /**
   * Gets converters to register.
   *
   * @param applicationContext the application context
   * @return the converters to register
   */
  public static List<Converter<?, ?>> getConvertersToRegister(
      ApplicationContext applicationContext) {
    return Arrays.asList(
        new LocaleReadConverter(),
        new LocaleWriteConverter(),
        new OffsetDateTimeReadConverter(),
        new OffsetDateTimeWriteConverter(),

        new NodeEntityReadConverter(applicationContext),
        new NodeEntitySettingsReadConverter(applicationContext),
        new BranchEntityReadConverter(applicationContext),
        new BranchEntitySettingsReadConverter(applicationContext),
        new LeafEntityReadConverter(applicationContext),
        new LeafEntitySettingsReadConverter(applicationContext),
        new FeatureLeafEntityReadConverter(applicationContext),
        new FeatureLeafEntitySettingsReadConverter(applicationContext),

        new FeatureEntityReadConverter(applicationContext),
        new FeatureEntitySettingsReadConverter(applicationContext),
        new RteEntityReadConverter(applicationContext),
        new RteEntitySettingsReadConverter(applicationContext),
        new TrkEntityReadConverter(applicationContext),
        new TrkEntitySettingsReadConverter(applicationContext),
        new WptEntityReadConverter(applicationContext),
        new WptEntitySettingsReadConverter(applicationContext)
    );
  }
}
