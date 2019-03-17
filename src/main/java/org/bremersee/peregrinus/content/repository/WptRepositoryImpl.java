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

package org.bremersee.peregrinus.content.repository;

import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.peregrinus.content.model.WptSettings;
import org.bremersee.peregrinus.content.repository.entity.WptEntity;
import org.bremersee.peregrinus.content.repository.entity.WptEntitySettings;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
class WptRepositoryImpl extends AbstractFeatureRepositoryImpl implements WptRepository {

  public WptRepositoryImpl(
      ReactiveMongoOperations mongoOperations) {
    super(mongoOperations);
  }

  public Mono<Wpt> findWptById(final String id, final String userId) {
    return getMongoOperations()
        .findOne(Query.query(Criteria.where("id").is(id)), WptEntity.class)
        .zipWith(getMongoOperations()
            .findOne(Query.query(featureSettingsCriteria(id, userId)),
                WptEntitySettings.class)
            .switchIfEmpty(Mono.just(new WptEntitySettings(id, userId))))
        .map(this::mapWpt);
  }

  private Wpt mapWpt(Tuple2<WptEntity, WptEntitySettings> tuple) {
    return mapFeature(tuple.getT1(), tuple.getT2(), Wpt::new, WptProperties::new, WptSettings::new);
  }

}
