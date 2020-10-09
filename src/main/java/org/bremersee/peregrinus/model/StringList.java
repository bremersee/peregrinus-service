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

package org.bremersee.peregrinus.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The string list.
 *
 * @author Christian Bremer
 */
@Schema(description = "List of strings.")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class StringList extends ArrayList<String> {

  /**
   * Instantiates a new String list.
   */
  public StringList() {
  }

  /**
   * Instantiates a new String list.
   *
   * @param initialCapacity the initial capacity
   */
  public StringList(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Instantiates a new String list.
   *
   * @param c the c
   */
  public StringList(Collection<? extends String> c) {
    super(c);
  }

}
