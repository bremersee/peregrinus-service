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

package org.bremersee.peregrinus.garmin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.springframework.util.StringUtils;

/**
 * The garmin link type to link converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class LinkTypeToLinkConverter {

  /**
   * Convert link.
   *
   * @param linkType the garmin link type
   * @return the link
   */
  Link convert(final LinkType linkType) {
    if (linkType == null || !StringUtils.hasText(linkType.getHref())) {
      return null;
    }
    return new Link()
        .href(linkType.getHref())
        .type(linkType.getType())
        .text(linkType.getText());
  }
}
