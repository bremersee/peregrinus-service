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

import java.util.Comparator;
import org.bremersee.peregrinus.model.Node;
import org.bremersee.peregrinus.model.NodeSettings;

/**
 * @author Christian Bremer
 */
public class TreeComparator implements Comparator<Node<? extends NodeSettings>> {

  private boolean asc;

  public TreeComparator(boolean asc) {
    this.asc = asc;
  }

  @Override
  public int compare(Node<? extends NodeSettings> node1, Node<? extends NodeSettings> node2) {
    int value = node1.compareTo(node2);
    return asc ? value : -1 * value;
  }
}
