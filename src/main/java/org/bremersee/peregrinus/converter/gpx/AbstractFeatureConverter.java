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

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.CommonGpxType;
import org.bremersee.peregrinus.content.model.FeatureProperties;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.springframework.util.StringUtils;

/**
 * The abstract feature converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class AbstractFeatureConverter {

  private static final LinkToLinkTypeConverter linkToLinkTypeConverter
      = new LinkToLinkTypeConverter();

  /**
   * Convert feature properties.
   *
   * @param <T>               the type parameter
   * @param featureProperties the feature properties
   * @param gpxTypeSupplier   the gpx type supplier
   * @return the t
   */
  <T extends CommonGpxType> T convertFeatureProperties(
      final FeatureProperties<? extends FeatureSettings> featureProperties,
      final Supplier<T> gpxTypeSupplier) {

    final T gpxType = gpxTypeSupplier.get();
    if (featureProperties != null) {
      gpxType.setCmt(getDescOrCmt(featureProperties, false));
      gpxType.setDesc(getDescOrCmt(featureProperties, true));
      gpxType.setName(featureProperties.getName());
      if (featureProperties.getLinks() != null) {
        for (Link link : featureProperties.getLinks()) {
          if (link != null && StringUtils.hasText(link.getHref())) {
            gpxType.getLinks().add(linkToLinkTypeConverter.convert(link));
          }
        }
      }
    }
    return gpxType;
  }

  private String getDescOrCmt(
      final FeatureProperties<? extends FeatureSettings> featureProperties, final boolean isDesc) {

    if (featureProperties == null
        || !StringUtils.hasText(featureProperties.getPlainTextDescription())) {
      return null;
    }
    final String desc = featureProperties.getPlainTextDescription();
    int i = desc.indexOf("\n---\n");
    if (i > -1) {
      if (isDesc) {
        return desc.substring(0, i);
      } else {
        return desc.substring(i + "\n---\n".length());
      }
    }
    return desc;
  }

}
