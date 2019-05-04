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
 * The entity type aliases.
 *
 * @author Christian Bremer
 */
public abstract class TypeAliases {

  /**
   * The constant BRANCH.
   */
  public static final String BRANCH = "Branch";

  /**
   * The constant BRANCH_SETTINGS.
   */
  public static final String BRANCH_SETTINGS = BRANCH + "Settings";

  /**
   * The constant FEATURE_LEAF.
   */
  public static final String FEATURE_LEAF = "FeatureLeaf";

  /**
   * The constant FEATURE_LEAF_SETTINGS.
   */
  public static final String FEATURE_LEAF_SETTINGS = FEATURE_LEAF + "Settings";

  /**
   * The constant RTE.
   */
  public static final String RTE = Feature.RTE_TYPE;

  /**
   * The constant RTE_SETTINGS.
   */
  public static final String RTE_SETTINGS = Feature.RTE_TYPE + "Settings";

  /**
   * The constant TRK.
   */
  public static final String TRK = Feature.TRK_TYPE;

  /**
   * The constant TRK_SETTINGS.
   */
  public static final String TRK_SETTINGS = Feature.TRK_TYPE + "Settings";

  /**
   * The constant WPT.
   */
  public static final String WPT = Feature.WPT_TYPE;

  /**
   * The constant WPT_SETTINGS.
   */
  public static final String WPT_SETTINGS = Feature.WPT_TYPE + "Settings";

  private TypeAliases() {
  }

  /**
   * Is leaf entity.
   *
   * @param typeAlias the type alias
   * @return the boolean
   */
  public static boolean isLeaf(String typeAlias) {
    return FEATURE_LEAF.equalsIgnoreCase(typeAlias);
  }

  /**
   * Is leaf settings.
   *
   * @param typeAlias the type alias
   * @return the boolean
   */
  public static boolean isLeafSettings(String typeAlias) {
    return FEATURE_LEAF_SETTINGS.equalsIgnoreCase(typeAlias);
  }

}
