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

package org.bremersee.peregrinus.geo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@TypeAlias("FeatureProperties")
@JsonTypeInfo(use = Id.NAME, property = "subType", visible = true)
@JsonSubTypes({
    @Type(value = RteProperties.class, name = "RTE"),
    @Type(value = TrkProperties.class, name = "TRK"),
    @Type(value = WptProperties.class, name = "WPT")
})
@Getter
@Setter
@ToString
public abstract class FeatureProperties<S extends FeatureSettings>
    implements Comparable<FeatureProperties> {

  private AccessControl accessControl = new AccessControl();

  private Instant created;

  @Indexed
  private Instant modified;

  @Indexed
  private String name;

  private String plainTextDescription; // desc == cmt

  private String markdownDescription;

  private String internalComments;

  private List<Link> links;

  /**
   * Start time of tracks or way points
   */
  @Indexed
  private Date startTime;

  /**
   * Start time of tracks or way points
   */
  @Indexed
  private Date stopTime;

  @Transient
  private S settings;

  public FeatureProperties() {
    final Instant now = Instant.now(Clock.system(ZoneId.of("UTC")));
    created = now;
    modified = now;
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

  @Override
  public int compareTo(final FeatureProperties o) {
    if (this == o) {
      return 0;
    }
    if (o == null) {
      return -1;
    }
    final String name1 = StringUtils.hasText(getName()) ? getName() : "";
    final String name2 = StringUtils.hasText(o.getName()) ? o.getName() : "";
    final int c = name1.compareTo(name2);
    if (c != 0) {
      return c;
    }
    if (getModified() != null && o.getModified() != null) {
      return getModified().compareTo(o.getModified());
    }
    return 0;
  }

}
