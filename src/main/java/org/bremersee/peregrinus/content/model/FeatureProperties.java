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

package org.bremersee.peregrinus.content.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;

/**
 * @author Christian Bremer
 */
@JsonTypeInfo(use = Id.NAME, property = "subType", visible = true)
@JsonSubTypes({
    @Type(value = WptProperties.class, name = "Wpt"),
    @Type(value = TrkProperties.class, name = "Trk"),
    @Type(value = RteProperties.class, name = "Rte")
})
@Getter
@Setter
@ToString
public abstract class FeatureProperties<S extends FeatureSettings> {

  private AccessControlDto accessControl = new AccessControlDto();

  private OffsetDateTime created;

  private OffsetDateTime modified;

  private String name;

  private String plainTextDescription; // desc == cmt

  private String markdownDescription;

  private String internalComments;

  private List<Link> links;

  /**
   * Start time of tracks or way points
   */
  private OffsetDateTime startTime;

  /**
   * Stop time of tracks or way points
   */
  private OffsetDateTime stopTime;

  private S settings;

  public FeatureProperties() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.system(ZoneId.of("Z")));
    created = now;
    modified = now;
    settings = doCreateDefaultSettings();
  }

  public S createDefaultSettings(
      final String featureId,
      final String userId) {

    final S settings = doCreateDefaultSettings();
    settings.setFeatureId(featureId);
    settings.setUserId(userId);
    return settings;
  }

  abstract S doCreateDefaultSettings();

}
