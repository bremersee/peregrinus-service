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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.content.model.RtePt;
import org.bremersee.peregrinus.security.access.AclEntity;
import org.springframework.data.annotation.TypeAlias;

/**
 * @author Christian Bremer
 */
@TypeAlias("RteProperties")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RteEntityProperties extends FeatureEntityProperties {

  private List<RtePt> rtePts;

  public RteEntityProperties() {
    rtePts = new ArrayList<>();
  }

  @Builder
  public RteEntityProperties(
      AclEntity acl,
      OffsetDateTime created,
      OffsetDateTime modified,
      String name,
      String plainTextDescription,
      String markdownDescription,
      String internalComments,
      List<Link> links,
      OffsetDateTime startTime,
      OffsetDateTime stopTime,
      List<RtePt> rtePts) {

    super(acl, created, modified, name, plainTextDescription, markdownDescription,
        internalComments, links, startTime, stopTime);
    setRtePts(rtePts);
  }

  public void setRtePts(List<RtePt> rtePts) {
    if (rtePts == null) {
      this.rtePts = new ArrayList<>();
    } else {
      this.rtePts = rtePts;
    }
  }
}
