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
import lombok.Setter;
import lombok.ToString;
import org.bremersee.peregrinus.security.access.AccessControl;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
public class Branch extends Node<BranchSettings> {

  private List<Node> children;

  public Branch() {
    setSettings(new BranchSettings());
  }

  public Branch(
      final String userId,
      final String parentId,
      final AccessControl accessControl,
      @NotNull(message = "Name must not be null.") final String name) {

    super(userId, parentId, accessControl, new BranchSettings());
    setName(name);
  }
}
