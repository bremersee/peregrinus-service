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

import org.bremersee.peregrinus.content.model.Trk;
import org.bremersee.peregrinus.content.model.TrkProperties;
import org.bremersee.peregrinus.content.model.TrkSettings;
import org.bremersee.peregrinus.content.repository.entity.TrkEntity;
import org.bremersee.peregrinus.content.repository.entity.TrkEntitySettings;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
class TrkRepositorympl extends AbstractFeatureRepositoryImpl implements TrkRepository {

  public TrkRepositorympl(
      ReactiveMongoOperations mongoOperations) {
    super(mongoOperations);
  }

  public Mono<Trk> findTrkById(final String id, final String userId) {
    return getMongoOperations()
        .findOne(Query.query(Criteria.where("id").is(id)), TrkEntity.class)
        .zipWith(getMongoOperations()
            .findOne(Query.query(featureSettingsCriteria(id, userId)),
                TrkEntitySettings.class)
            .switchIfEmpty(Mono.just(new TrkEntitySettings(id, userId))))
        .map(this::mapTrk);
  }

  private Trk mapTrk(Tuple2<TrkEntity, TrkEntitySettings> tuple) {
    return mapFeature(tuple.getT1(), tuple.getT2(), Trk::new, TrkProperties::new, TrkSettings::new);
  }

}
