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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.springframework.data.annotation.TypeAlias;

/**
 * @author Christian Bremer
 */
@TypeAlias("TrkProperties")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TrkEntityProperties extends FeatureEntityProperties {

  private List<List<BigDecimal>> eleLines = new ArrayList<>();

  private List<List<OffsetDateTime>> timeLines = new ArrayList<>();

  @Builder
  public TrkEntityProperties(
      AccessControlEntity accessControl,
      OffsetDateTime created, OffsetDateTime modified, String name,
      String plainTextDescription, String markdownDescription, String internalComments,
      List<Link> links, OffsetDateTime startTime,
      OffsetDateTime stopTime, List<List<BigDecimal>> eleLines,
      List<List<OffsetDateTime>> timeLines) {
    super(accessControl, created, modified, name, plainTextDescription, markdownDescription,
        internalComments, links, startTime, stopTime);
    this.eleLines = eleLines;
    this.timeLines = timeLines;
  }
}