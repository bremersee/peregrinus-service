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

package org.bremersee.peregrinus.security.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Validated
public abstract class MongoRepositoryUtils {

  @NotNull
  public static List<Criteria> buildCriteriaList(
      @NotNull final String permission,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups) {
    return buildCriteriaList(permission, includePublic, userId, roles, groups, null);
  }

  @NotNull
  public static Criteria buildCriteria(
      @NotNull final String permission,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups) {
    return new Criteria().orOperator(buildCriteriaList(
        permission,
        includePublic,
        userId,
        roles,
        groups,
        null)
        .toArray(new Criteria[0]));
  }

  @NotNull
  public static Criteria buildCriteria(
      @NotNull final Criteria criteria,
      @NotNull final String permission,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups) {
    return new Criteria().andOperator(
        criteria,
        buildCriteria(permission, includePublic, userId, roles, groups, null));
  }

  @NotNull
  public static List<Criteria> buildCriteriaList(
      @NotNull final String permission,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups,
      @Nullable final String accessControlPropertyName) {

    final String propName = StringUtils.hasText(accessControlPropertyName)
        ? accessControlPropertyName
        : "acl";
    final String path = propName + "." + permission;
    final Set<String> roleSet = roles == null ? new HashSet<>() : new HashSet<>(roles);
    final Set<String> groupSet = groups == null ? new HashSet<>() : new HashSet<>(groups);
    final List<Criteria> criteriaList = new ArrayList<>();
    if (includePublic) {
      criteriaList.add(Criteria.where(path + ".guest").is(true));
    }
    if (StringUtils.hasText(userId)) {
      criteriaList.add(Criteria.where(propName + ".owner").is(userId));
      criteriaList.add(Criteria.where(path + ".users").all(userId));
    }
    criteriaList.addAll(roleSet
        .stream()
        .filter(StringUtils::hasText)
        .map(role -> Criteria.where(path + ".roles").all(role))
        .collect(Collectors.toList()));
    criteriaList.addAll(groupSet
        .stream()
        .filter(StringUtils::hasText)
        .map(group -> Criteria.where(path + ".groups").all(group))
        .collect(Collectors.toList()));
    return criteriaList;
  }

  public static Criteria buildCriteria(
      @NotNull final String permission,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups,
      @Nullable final String accessControlPropertyName) {
    return new Criteria().orOperator(buildCriteriaList(
        permission,
        includePublic,
        userId,
        roles,
        groups,
        accessControlPropertyName)
        .toArray(new Criteria[0]));
  }

  public static Criteria buildCriteria(
      @NotNull final Criteria criteria,
      @NotNull final String permission,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups,
      @Nullable final String accessControlPropertyName) {
    return new Criteria().andOperator(
        criteria,
        buildCriteria(permission, includePublic, userId, roles, groups, accessControlPropertyName));
  }
}
