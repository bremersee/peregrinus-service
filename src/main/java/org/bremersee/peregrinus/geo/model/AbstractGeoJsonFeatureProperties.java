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
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Link;
import org.bremersee.peregrinus.security.access.EmbeddedAccessControl;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Christian Bremer
 */
@ToString
@Getter
@Setter
@TypeAlias("AbstractGeoJsonFeatureProperties")
@JsonTypeInfo(use = Id.NAME, property = "subType", visible = true)
@JsonSubTypes({
    @Type(value = RteProperties.class, name = "RTE"),
    @Type(value = TrkProperties.class, name = "TRK"),
    @Type(value = WptProperties.class, name = "WPT")
})
public abstract class AbstractGeoJsonFeatureProperties {

  private EmbeddedAccessControl accessControl = new EmbeddedAccessControl();

  private Date created = new Date();

  @Indexed
  private Date modified = new Date();

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

}
