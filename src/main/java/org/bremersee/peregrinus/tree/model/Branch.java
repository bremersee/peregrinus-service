/*
 * Copyright 2017 the original author or authors.
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

package org.bremersee.peregrinus.tree.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

/**
 * @author Christian Bremer
 */
@Document(collection = "directory")
@TypeAlias("Branch")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Branch extends Node {

  @NotNull(message = "Name must not be null.")
  private String name;

  @Transient
  private BranchSettings settings;

  @Transient
  private List<Node> children;

  public Branch(
      @NotNull String name,
      @Nullable String parentId,
      @NotNull String owner) {
    super(parentId, owner);
    this.name = name;
  }

  public Branch(
      @NotNull String name,
      @Nullable String parentId,
      @NotNull AccessControl accessControl) {
    super(parentId, accessControl);
    this.name = name;
  }

  int orderValue() {
    return 0;
  }

}
