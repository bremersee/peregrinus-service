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

package org.bremersee.peregrinus.repository;

import static org.springframework.util.Assert.notNull;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.security.access.AclMapper;
import org.bremersee.security.access.PermissionConstants;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Validated
public abstract class AbstractMongoRepository {

  private ReactiveMongoOperations mongoOperations;

  private AclMapper<AclEntity> aclMapper;

  public AbstractMongoRepository(
      ReactiveMongoOperations mongoOperations,
      AclMapper<AclEntity> aclMapper) {
    notNull(mongoOperations, "Mongo operations must not be null.");
    notNull(aclMapper, "Acl mapper must not be null.");
    this.mongoOperations = mongoOperations;
    this.aclMapper = aclMapper;
  }

  protected ReactiveMongoOperations mongo() {
    return mongoOperations;
  }

  protected AclMapper<AclEntity> aclMapper() {
    return aclMapper;
  }

  /**
   * Returns the acl path, for example {@code 'properties.acl'} for a feature entity and {@code
   * 'acl'} for a node entity.
   *
   * @return the acl path
   */
  protected abstract String aclPath();

  private String aclOwnerPath() {
    return aclPath() + ".owner";
  }

  private String aclPath(final String permission) {
    return aclPath() + "." + permission;
  }

  private String aclPath(final String permission, final String entryProperty) {
    return aclPath(permission) + "." + entryProperty;
  }

  protected @NotNull Update createUpdate(
      @NotNull final AclEntity acl,
      @NotNull final String userId) {

    final Update update = Update.update("modifiedBy", userId)
        .set("modified", OffsetDateTime.now(Clock.systemUTC()));
    return extendUpdate(acl, update);
  }

  protected @NotNull Update extendUpdate(
      @NotNull final AclEntity acl,
      @Nullable final Update update) {

    return (update != null ? update : new Update())
        .set(aclPath(PermissionConstants.ADMINISTRATION), acl.getAdministration())
        .set(aclPath(PermissionConstants.CREATE), acl.getCreate())
        .set(aclPath(PermissionConstants.DELETE), acl.getDelete())
        .set(aclPath(PermissionConstants.READ), acl.getRead())
        .set(aclPath(PermissionConstants.WRITE), acl.getWrite());
  }

  protected @NotNull Criteria orCriteria(@NotNull @NotEmpty final Criteria... criteria) {
    if (criteria.length == 1) {
      return criteria[0];
    }
    return new Criteria().orOperator(criteria);
  }

  protected @NotNull Query queryOr(@NotNull @NotEmpty final Criteria... criteria) {
    return Query.query(orCriteria(criteria));
  }

  protected @NotNull Criteria andCriteria(@NotNull @NotEmpty final Criteria... criteria) {
    if (criteria.length == 1) {
      return criteria[0];
    }
    return new Criteria().andOperator(criteria);
  }

  protected @NotNull Query queryAnd(@NotNull @NotEmpty final Criteria... criteria) {
    return Query.query(andCriteria(criteria));
  }

  protected @NotNull Query queryAnd(
      @NotNull final Criteria criteria,
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups,
      @NotNull @NotEmpty final String... permissions) {
    return queryAnd(criteria, any(includePublic, userId, roles, groups, permissions));
  }

  protected @NotNull Criteria any(
      final boolean includePublic,
      @Nullable final String userId,
      @Nullable final Collection<String> roles,
      @Nullable final Collection<String> groups,
      @NotNull @NotEmpty final String... permissions) {

    final Set<String> roleSet = roles == null ? new HashSet<>() : new HashSet<>(roles);
    final Set<String> groupSet = groups == null ? new HashSet<>() : new HashSet<>(groups);
    final List<Criteria> criteriaList = new ArrayList<>();
    if (StringUtils.hasText(userId)) {
      criteriaList.add(0, Criteria.where(aclOwnerPath()).is(userId));
    }
    for (final String permission : permissions) {
      if (includePublic) {
        criteriaList.add(Criteria.where(aclPath(permission, "guest")).is(true));
      }
      if (StringUtils.hasText(userId)) {
        criteriaList.add(Criteria.where(aclPath(permission, "users")).all(userId));
      }
      criteriaList.addAll(roleSet
          .stream()
          .filter(StringUtils::hasText)
          .map(role -> Criteria.where(aclPath(permission, "roles")).all(role))
          .collect(Collectors.toList()));
      criteriaList.addAll(groupSet
          .stream()
          .filter(StringUtils::hasText)
          .map(group -> Criteria.where(aclPath(permission, "groups")).all(group))
          .collect(Collectors.toList()));
    }
    return orCriteria(criteriaList.toArray(new Criteria[0]));
  }

}
