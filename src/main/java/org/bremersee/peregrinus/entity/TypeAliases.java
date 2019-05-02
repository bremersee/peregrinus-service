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

package org.bremersee.peregrinus.entity;

import org.bremersee.peregrinus.model.Feature;

/**
 * @author Christian Bremer
 */
public abstract class TypeAliases {

  public static final String BRANCH = "Branch";

  public static final String BRANCH_SETTINGS = BRANCH + "Settings";

  public static final String FEATURE_LEAF = "FeatureLeaf";

  public static final String FEATURE_LEAF_SETTINGS = FEATURE_LEAF + "Settings";

  public static final String RTE = Feature.RTE_TYPE;

  public static final String RTE_SETTINGS = Feature.RTE_TYPE + "Settings";

  public static final String TRK = Feature.TRK_TYPE;

  public static final String TRK_SETTINGS = Feature.TRK_TYPE + "Settings";

  public static final String WPT = Feature.WPT_TYPE;

  public static final String WPT_SETTINGS = Feature.WPT_TYPE + "Settings";

  private TypeAliases() {
  }

  public static boolean isLeaf(String typeAlias) {
    return FEATURE_LEAF.equalsIgnoreCase(typeAlias);
  }

  public static boolean isLeafSettings(String typeAlias) {
    return FEATURE_LEAF_SETTINGS.equalsIgnoreCase(typeAlias);
  }

}
