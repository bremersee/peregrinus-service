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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.xml.bind.Unmarshaller;
import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.CommonGpxType;
import org.bremersee.peregrinus.content.model.FeatureProperties;
import org.bremersee.peregrinus.content.model.FeatureSettings;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
abstract class AbstractGpxConverter {

  private LinkTypeToLinkConverter linkTypeToLinkConverter = new LinkTypeToLinkConverter();

  private LinkToLinkTypeConverter linkToLinkTypeConverter = new LinkToLinkTypeConverter();

  private final JaxbContextBuilder jaxbContextBuilder;

  public AbstractGpxConverter(JaxbContextBuilder jaxbContextBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
  }

  Unmarshaller getUnmarshaller() {
    return jaxbContextBuilder.buildUnmarshaller();
  }

  <T extends FeatureProperties<? extends FeatureSettings>> T convertCommonGpxType(
      CommonGpxType commonGpxType,
      Supplier<T> geoJsonPropertiesSupplier) {

    final T geoJsonProperties = geoJsonPropertiesSupplier.get();
    geoJsonProperties.setCreated(Instant.now(Clock.system(ZoneId.of("UTC"))));
    geoJsonProperties.setModified(geoJsonProperties.getCreated());
    if (commonGpxType != null) {
      geoJsonProperties.setName(commonGpxType.getName());
      geoJsonProperties.setPlainTextDescription(getPlainTextDescription(commonGpxType));
      geoJsonProperties.setMarkdownDescription(geoJsonProperties.getPlainTextDescription());
      geoJsonProperties.setLinks(
          commonGpxType.getLinks()
              .stream()
              .filter(Objects::nonNull)
              .map(linkTypeToLinkConverter::convert)
              .collect(Collectors.toList()));
    }
    return geoJsonProperties;
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
      final FeatureProperties<? extends FeatureSettings> featureProperties, boolean isDesc) {
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
