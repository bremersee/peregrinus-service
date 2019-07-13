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

package org.bremersee.peregrinus.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.bremersee.comparator.ComparatorBuilder;
import org.bremersee.comparator.DefaultValueExtractor;
import org.bremersee.comparator.ValueComparator;
import org.bremersee.comparator.model.ComparatorField;
import org.bremersee.comparator.spring.ComparatorSpringUtils;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.Feature;
import org.bremersee.peregrinus.model.FeatureCollection;
import org.bremersee.peregrinus.model.FeatureLeaf;
import org.bremersee.peregrinus.model.Node;
import org.bremersee.peregrinus.model.gpx.GpxImportSettings;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Validated
public interface TreeService {

  String DEFAULT_SORT_ORDER = "type|name|modified,desc";

  Sort DEFAULT_SORT = Sort.by(Order.asc("type"), Order.asc("name"), Order.desc("modified"));

  default Comparator<Object> newTreeComparator(Sort sort) {
    Sort sortOrder = sort != null ? sort : DEFAULT_SORT;
    ComparatorBuilder comparatorBuilder = ComparatorBuilder.builder();
    comparatorBuilder.add((Comparator<Node>) (node1, node2) -> {
      if (node1 instanceof Branch && !(node2 instanceof Branch)) {
        return -1;
      }
      if (!(node1 instanceof Branch) && node2 instanceof Branch) {
        return 1;
      }
      return 0;
    });
    for (ComparatorField comparatorField : ComparatorSpringUtils.fromSort(sortOrder)) {
      if ("type".equalsIgnoreCase(comparatorField.getField())) {
        comparatorBuilder.add((Comparator<Node>) (o1, o2) -> {
          int result = o1.compareTo(o2);
          return comparatorField.isAsc() ? result : result * -1;
        });
      } else {
        comparatorBuilder.add(
            new ValueComparator(comparatorField,
                new DefaultValueExtractor(false)));
      }
    }
    return comparatorBuilder.build();
  }

  Mono<Branch> createBranch(
      @NotNull @Length(min = 1) String name,
      @NotNull String userId);

  Mono<Branch> createBranch(
      @NotNull @Length(min = 1) String name,
      @NotNull String parentId,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  default Mono<FeatureLeaf> createFeatureLeaf(
      @NotNull String parentId,
      @NotNull Feature feature,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups) {
    final FeatureCollection col = new FeatureCollection(Collections.singletonList(feature), null);
    return createFeatureLeafs(parentId, col, userId, roles, groups)
        .single();
  }

  Flux<FeatureLeaf> createFeatureLeafs(
      @NotNull String parentId,
      @NotNull FeatureCollection featureCollection,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Flux<FeatureLeaf> importGpx(
      @NotNull String parentId,
      @NotNull Gpx gpx,
      @NotNull GpxImportSettings importSettings,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Flux<Branch> loadBranches(
      boolean openAll,
      boolean omitGeometries,
      boolean includePublic,
      @Nullable Sort sort,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Branch> openBranch(
      @NotNull String branchId,
      @NotNull OpenBranchCommand openBranchCommand,
      @NotNull GeometryCommand geometryCommand,
      @Nullable Sort sort,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Void> closeBranch(
      @NotNull String branchId,
      @NotNull String userId);

  Mono<Boolean> renameNode(
      @NotNull String nodeId,
      @NotNull @Length(min = 1) String name,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

  Mono<Void> displayNodeOnMap(
      @NotNull String nodeId,
      @NotNull Boolean displayedOnMap,
      @NotNull String userId,
      @NotNull Set<String> roles,
      @NotNull Set<String> groups);

}
