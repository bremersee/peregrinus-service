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

import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
public class LinkTypeToLinkConverter implements Converter<LinkType, Link> {

  @Override
  public Link convert(LinkType linkType) {
    if (linkType == null || !StringUtils.hasText(linkType.getHref())) {
      return null;
    }
    return new Link()
        .href(linkType.getHref())
        .type(linkType.getType())
        .text(linkType.getText());
  }
}
