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

package org.bremersee.peregrinus.service;

import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.garmin.ExportSettings;
import org.bremersee.peregrinus.model.garmin.ImportSettings;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Validated
public interface ConverterService {

  @NotNull
  Trk convertRteToTrk(@NotNull Rte rte);

  @NotNull
  Gpx convertFeaturesToGpx(
      @NotNull Collection<? extends Feature> features,
      @NotNull ExportSettings exportSettings);

  @NotNull
  FeatureCollection convertGpxToFeatures(
      @NotNull Gpx gpx,
      @NotNull ImportSettings importSettings);

}
