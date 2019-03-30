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

package org.bremersee.peregrinus.service;

import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.entity.BranchEntitySettings;

/**
 * @author Christian Bremer
 */
enum OpenBranchCommand {

  ALL, CURRENT, RETAIN;

  public OpenBranchCommand getCommandForChildren() {
    if (this == CURRENT) {
      return RETAIN;
    }
    return this;
  }

  public boolean isCurrentAndBranchNotOpen(BranchEntitySettings branchSettings) {
    return this == CURRENT && (branchSettings.getOpen() == null || branchSettings.getOpen());
  }

  public boolean openBranch(Branch branch) {
    return this == ALL
        || (branch != null
        && branch.getSettings() != null
        && branch.getSettings().getOpen() != null
        && branch.getSettings().getOpen());
  }
}
