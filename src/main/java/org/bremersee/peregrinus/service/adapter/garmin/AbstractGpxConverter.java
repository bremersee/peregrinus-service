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

package org.bremersee.peregrinus.service.adapter.garmin;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.gpx.model.CommonGpxType;
import org.bremersee.peregrinus.model.FeatureProperties;
import org.bremersee.peregrinus.model.FeatureSettings;
import org.springframework.util.StringUtils;

/**
 * The abstract garmin converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class AbstractGpxConverter {

  private static final LinkTypeToLinkConverter linkTypeToLinkConverter
      = new LinkTypeToLinkConverter();

  /**
   * Convert common garmin type t.
   *
   * @param <T>                       the type parameter
   * @param commonGpxType             the common garmin type
   * @param featurePropertiesSupplier the feature properties supplier
   * @return the t
   */
  <T extends FeatureProperties<? extends FeatureSettings>> T convertCommonGpxType(
      final CommonGpxType commonGpxType,
      final Supplier<T> featurePropertiesSupplier) {

    final T featureProperties = featurePropertiesSupplier.get();
    featureProperties.setCreated(OffsetDateTime.now(Clock.system(ZoneId.of("Z"))));
    featureProperties.setModified(featureProperties.getCreated());
    if (commonGpxType != null) {
      featureProperties.setName(commonGpxType.getName());
      featureProperties.setPlainTextDescription(getPlainTextDescription(commonGpxType));
      featureProperties.setMarkdownDescription(featureProperties.getPlainTextDescription());
      featureProperties.setLinks(
          commonGpxType.getLinks()
              .stream()
              .filter(Objects::nonNull)
              .map(linkTypeToLinkConverter::convert)
              .collect(Collectors.toList()));
    }
    return featureProperties;
  }

  private String getPlainTextDescription(final CommonGpxType commonGpxType) {
    final String a = StringUtils.hasText(commonGpxType.getDesc()) ? commonGpxType.getDesc() : "";
    final String b = StringUtils.hasText(commonGpxType.getCmt()) ? commonGpxType.getCmt() : "";
    final StringBuilder sb = new StringBuilder();
    sb.append(a);
    if (!b.equals(a) && b.length() > 0) {
      if (a.length() > 0) {
        sb.append("\n---\n");
      }
      sb.append(b);
    }
    return sb.length() > 0 ? sb.toString() : null;
  }

}
