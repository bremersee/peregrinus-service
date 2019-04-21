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
import java.util.function.Function;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.Rte;
import org.bremersee.peregrinus.model.Trk;
import org.bremersee.peregrinus.model.garmin.ExportSettings;
import org.bremersee.peregrinus.model.garmin.ImportSettings;

/**
 * @author Christian Bremer
 */
public class ConverterServiceImpl implements ConverterService {

  private final Function<Rte, Trk> rteToTrkConvert = new RteToTrkConverter();

  @Override
  public Trk convertRteToTrk(Rte rte) {
    return rteToTrkConvert.apply(rte);
  }

  @Override
  public Gpx convertFeaturesToGpx(Collection<? extends Feature> features,
      ExportSettings exportSettings) {
    return null;
  }

  @Override
  public FeatureCollection convertGpxToFeatures(Gpx gpx, ImportSettings importSettings) {
    return null;
  }
}
