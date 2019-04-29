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

package org.bremersee.peregrinus.service.adapter.gpx;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.common.model.Link;
import org.bremersee.gpx.model.LinkType;
import org.springframework.util.StringUtils;

/**
 * The link to gpx link type converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class LinkToLinkTypeConverter {

  /**
   * Convert gpx link type.
   *
   * @param link the link
   * @return the gpx link type
   */
  LinkType convert(final Link link) {
    if (link == null || !StringUtils.hasText(link.getHref())) {
      return null;
    }
    final LinkType linkType = new LinkType();
    linkType.setHref(link.getHref());
    linkType.setText(link.getText());
    linkType.setType(link.getType());
    return linkType;
  }
}
