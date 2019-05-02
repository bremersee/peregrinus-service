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

import com.mongodb.DBObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.peregrinus.entity.AclEntity;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.bremersee.peregrinus.entity.NodeEntity;
import org.bremersee.peregrinus.entity.NodeEntitySettings;
import org.bremersee.security.access.AclMapper;
import org.bremersee.security.access.PermissionConstants;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class QueryRepo extends AbstractMongoRepository implements CommandLineRunner {

  private String branchId = "5cc88dd334705937f36a88f4";

  private String userId = "b978d214-0750-45e4-bf73-7d8654480a00";

  private TreeRepository treeRepository;

  public QueryRepo(
      ReactiveMongoTemplate mongoTemplate,
      ReactiveMongoOperations mongoOperations,
      AclMapper<AclEntity> aclMapper,
      TreeRepository treeRepository) {
    super(mongoTemplate, mongoOperations, aclMapper);
    this.treeRepository = treeRepository;
  }

  @Override
  protected String aclPath() {
    return NodeEntity.ACL_PATH;
  }

  @Override
  public void run(String... args) throws Exception {

    MongoEntityInformation m;
    SimpleReactiveMongoRepository r;

    Set<String> roles = Collections.singleton("ROLE_ADMIN");
    Set<String> groups = new HashSet<>();

    log.info("==============================================================");

    Criteria c = Criteria.where("_class").in(Arrays.asList("Branch", "GeoLeaf"));
    Criteria c0 = Criteria.where("id").is(branchId);
    Criteria c1 = any(true, userId, roles, groups, PermissionConstants.READ);

    //getMongoTemplate().getCollection("").

    //getMongoTemplate().getCollection("").find(null)

    //getMongoTemplate().

//    NodeEntity e = treeRepository.findBranchById(branchId, PermissionConstants.READ, true, userId, roles, groups)
//        .block();
    //getMongoTemplate().getConverter().read(BranchEntity.class, )

//    BranchEntity e = treeRepository.findBranchById(branchId, PermissionConstants.WRITE, true, userId, roles, groups)
//        .block();

//    log.info("Result: {}", e);
//
//    treeRepository.findNodeSettings(branchId, userId)
//        .cast(BranchEntitySettings.class)
//        .map(branchEntitySettings -> {
//          log.info(">>>>>> Settings {}" , branchEntitySettings);
//          return branchEntitySettings;
//        }).block();
    //log.info(">>>> Settings = {}", s);

  }

}
