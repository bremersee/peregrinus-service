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

package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;
import org.springframework.util.StringUtils;

/**
 * The route point.
 *
 * @author Christian Bremer
 */
@Schema(description = "Route point.")
@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class RtePt {

  @Schema(description = "The position.")
  @JsonProperty(value = "position", required = true)
  private Point position;

  @Schema(description = "The name.")
  private String name;

  /**
   * Instantiates a new route point.
   *
   * @param position the position
   * @param name     the name
   */
  @Builder
  public RtePt(Point position, String name) {
    this.position = position;
    this.name = name;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  @JsonProperty("name")
  public String getName() {
    if (!StringUtils.hasText(name) && position != null) {
      return BigDecimal.valueOf(position.getY()).toPlainString()
          + ","
          + BigDecimal.valueOf(position.getX()).toPlainString();
    }
    return name;
  }

}
