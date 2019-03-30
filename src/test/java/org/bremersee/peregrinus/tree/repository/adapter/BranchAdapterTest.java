package org.bremersee.peregrinus.tree.repository.adapter;

import static org.bremersee.peregrinus.TestData.ANNA;
import static org.bremersee.peregrinus.TestData.accessControlEntity;
import static org.bremersee.peregrinus.TestData.rootBranch;
import static org.bremersee.peregrinus.TestData.rootBranchEntity;
import static org.bremersee.peregrinus.TestData.rootBranchEntitySettings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bremersee.peregrinus.TestConfig;
import org.bremersee.peregrinus.TestData;
import org.bremersee.peregrinus.model.Branch;
import org.bremersee.peregrinus.model.BranchSettings;
import org.bremersee.peregrinus.entity.BranchEntity;
import org.bremersee.peregrinus.entity.BranchEntitySettings;
import org.junit.Assert;
import org.junit.Test;
import reactor.test.StepVerifier;

/**
 * @author Christian Bremer
 */
public class BranchAdapterTest {

  private static final BranchAdapter adapter = new BranchAdapter(
      TestConfig.getModelMapper());

  @Test
  public void getSupportedClasses() {
    final List<Class<?>> classes = Arrays.asList(adapter.getSupportedClasses());
    assertTrue(classes.contains(BranchEntity.class));
    assertTrue(classes.contains(BranchEntitySettings.class));
    assertTrue(classes.contains(Branch.class));
    assertTrue(classes.contains(BranchSettings.class));
  }

  @Test
  public void mapNode() {
    final Branch branch = rootBranch();
    StepVerifier
        .create(adapter.mapNode(branch, ANNA))
        .assertNext(tuple -> {
          assertNotNull(tuple);
          assertNotNull(tuple.getT1());
          assertNotNull(tuple.getT2());

          System.out.println("### Branch entity");
          System.out.println(tuple.getT1());

          BranchEntity branchEntity = tuple.getT1();
          assertEquals(
              branch.getAcl(),
              TestData.accessControlDto(branchEntity.getAcl()));
          assertEquals(branch.getCreated(), branchEntity.getCreated());
          assertEquals(branch.getCreatedBy(), branchEntity.getCreatedBy());
          assertEquals(branch.getId(), branchEntity.getId());
          assertEquals(branch.getModified(), branchEntity.getModified());
          assertEquals(branch.getModifiedBy(), branchEntity.getModifiedBy());
          assertEquals(branch.getName(), branchEntity.getName());
          Assert.assertNull(branchEntity.getParentId());

          System.out.println("### Branch entity settings");
          System.out.println(tuple.getT2());

          BranchEntitySettings branchEntitySettings = tuple.getT2();
          assertEquals(branch.getSettings().getId(), branchEntitySettings.getId());
          assertEquals(branch.getId(), branchEntitySettings.getNodeId());
          assertEquals(branch.getSettings().getOpen(), branchEntitySettings.getOpen());
          assertEquals(branch.getSettings().getUserId(), branchEntitySettings.getUserId());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void mapNodeSettings() {

    final BranchSettings branchSettings = rootBranch().getSettings();
    StepVerifier
        .create(adapter.mapNodeSettings(branchSettings, ANNA))
        .assertNext(branchEntitySettings -> {
          assertNotNull(branchEntitySettings);

          System.out.println("### Branch entity settings");
          System.out.println(branchEntitySettings);

          assertEquals(branchSettings.getId(), branchEntitySettings.getId());
          assertEquals(rootBranch().getId(), branchEntitySettings.getNodeId());
          assertEquals(branchSettings.getOpen(), branchEntitySettings.getOpen());
          assertEquals(branchSettings.getUserId(), branchEntitySettings.getUserId());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void mapNodeEntity() {
    final BranchEntity root = rootBranchEntity();
    final BranchEntitySettings rootSettings = rootBranchEntitySettings();
    StepVerifier
        .create(adapter.mapNodeEntity(root, rootSettings))
        .assertNext(branch -> {
          assertNotNull(branch);

          System.out.println("### Branch");
          System.out.println(branch);

          assertEquals(
              root.getAcl(),
              accessControlEntity(branch.getAcl()));
          assertEquals(root.getCreated(), branch.getCreated());
          assertEquals(root.getCreatedBy(), branch.getCreatedBy());
          assertEquals(root.getId(), branch.getId());
          assertEquals(root.getModified(), branch.getModified());
          assertEquals(root.getModifiedBy(), branch.getModifiedBy());
          assertEquals(root.getName(), branch.getName());
          assertEquals(root.getParentId(), branch.getParentId());
          assertNotNull(branch.getSettings());

          System.out.println("### Branch settings");
          System.out.println(branch.getSettings());

          assertEquals(rootSettings.getId(), branch.getSettings().getId());
          assertEquals(rootSettings.getNodeId(), branch.getSettings().getNodeId());
          assertEquals(rootSettings.getUserId(), branch.getSettings().getUserId());
          assertEquals(rootSettings.getOpen(), branch.getSettings().getOpen());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void mapNodeEntitySettings() {
    final BranchEntitySettings rootSettings = rootBranchEntitySettings();
    StepVerifier
        .create(adapter.mapNodeEntitySettings(rootSettings))
        .assertNext(branchSettings -> {
          assertNotNull(branchSettings);
          assertEquals(rootSettings.getId(), branchSettings.getId());
          assertEquals(rootSettings.getNodeId(), branchSettings.getNodeId());
          assertEquals(rootSettings.getUserId(), branchSettings.getUserId());
          assertEquals(rootSettings.getOpen(), branchSettings.getOpen());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void updateName() {
    final String expectedNewName = UUID.randomUUID().toString();
    final BranchEntity branchEntity = BranchEntity.builder().build();
    StepVerifier
        .create(adapter.updateName(
            branchEntity, expectedNewName, ANNA, Collections.emptyList(), Collections.emptyList()))
        .assertNext(nodeEntity -> {
          assertNotNull(nodeEntity);
          assertEquals(expectedNewName, ((BranchEntity) nodeEntity).getName());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void updateAccessControl() {
    /*
    final AccessControlList accessControl = accessControlDto(ANNAS_FRIEND);
    final BranchEntity branchEntity = BranchEntity.builder()
        .acl(accessControlEntity(accessControlDto(ANNA)))
        .build();
    StepVerifier
        .create(adapter.updateAccessControl(
            branchEntity, accessControl, ANNA, Collections.emptyList(), Collections.emptyList()))
        .assertNext(nodeEntity -> {
          assertNotNull(nodeEntity);
          assertEquals(accessControl, accessControlDto(ANNAS_FRIEND).ensureAdminAccess());
        })
        .expectNextCount(0)
        .verifyComplete();
        */
  }

  @Test
  public void defaultSettings() {
    BranchEntity branchEntity = rootBranchEntity();
    StepVerifier
        .create(adapter.defaultSettings(branchEntity, ANNA))
        .assertNext(branchEntitySettings -> {
          assertNotNull(branchEntitySettings);
          assertEquals(branchEntity.getId(), branchEntitySettings.getNodeId());
          assertEquals(ANNA, branchEntitySettings.getUserId());
        })
        .expectNextCount(0)
        .verifyComplete();
  }
}