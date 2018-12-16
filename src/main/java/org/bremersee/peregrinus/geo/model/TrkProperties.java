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

package org.bremersee.peregrinus.geo.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.TypeAlias;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@TypeAlias("TrkProperties")
public class TrkProperties extends AbstractGeoJsonFeatureProperties<TrkSettings> {

  private List<List<BigDecimal>> eleLines;

  private List<List<Date>> timeLines;

  @Override
  TrkSettings doCreateDefaultSettings() {

    final TrkSettings settings = new TrkSettings();
    settings.setDisplayColor(DisplayColor.DARK_GRAY);
    return settings;
  }

}
