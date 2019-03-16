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

package org.bremersee.peregrinus.content.repository.entity;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Christian Bremer
 */
@TypeAlias("FeatureProperties")
@Getter
@Setter
@ToString
public abstract class FeatureEntityProperties {

  private AccessControl accessControl = new AccessControl();

  private OffsetDateTime created;

  @Indexed
  private OffsetDateTime modified;

  @Indexed
  private String name;

  private String plainTextDescription; // desc == cmt

  private String markdownDescription;

  private String internalComments;

  private List<Link> links;

  /**
   * Start time of tracks.
   */
  @Indexed
  private Instant startTime;

  /**
   * Stop time of tracks.
   */
  @Indexed
  private Instant stopTime;

  public FeatureEntityProperties() {
    final OffsetDateTime now = OffsetDateTime.now(Clock.system(ZoneId.of("Z")));
    created = now;
    modified = now;
  }

}
