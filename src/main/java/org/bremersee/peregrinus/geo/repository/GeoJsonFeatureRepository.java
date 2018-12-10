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

package org.bremersee.peregrinus.geo.repository;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bremersee.peregrinus.geo.model.AbstractGeoJsonFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author Christian Bremer
 */
@Component
public class GeoJsonFeatureRepository {

  private ReactiveMongoOperations mongoOperations;

  @Autowired
  public GeoJsonFeatureRepository(
      ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  public Flux<AbstractGeoJsonFeature> find(final String user, final String permission) {

    final List<String> roles = Arrays.asList("user", "developer");
    final List<String> groups = Arrays.asList("groupA", "groupB");

    final Query query = new Query();

    // 1.:
    //query.addCriteria(Criteria.where("properties.accessControl.owner").is(user));

    // OR
    // 2.:
    //query.addCriteria(Criteria.where("properties.accessControl." + permission + ".users").all(user));

    // OR
    // 3.:
    Criteria c = new Criteria().orOperator(
        roles.stream().map(role -> Criteria.where("properties.accessControl." + permission + ".roles").all(role)).collect(Collectors.toList()).toArray(new Criteria[0]));
    query.addCriteria(c);

    return mongoOperations.find(query, AbstractGeoJsonFeature.class);

    //return Flux.empty();
  }
}
