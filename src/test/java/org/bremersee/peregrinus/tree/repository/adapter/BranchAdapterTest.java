package org.bremersee.peregrinus.tree.repository.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.peregrinus.config.ModelMapperConfiguration;
import org.bremersee.peregrinus.content.model.Wpt;
import org.bremersee.peregrinus.content.model.WptProperties;
import org.bremersee.peregrinus.content.model.WptSettings;
import org.bremersee.peregrinus.security.access.AccessControl;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.security.access.repository.entity.AccessControlEntity;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.model.BranchSettings;
import org.bremersee.peregrinus.tree.model.FeatureLeaf;
import org.bremersee.peregrinus.tree.model.FeatureLeafSettings;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntity;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntitySettings;
import org.junit.Assert;
import org.junit.Test;
import reactor.test.StepVerifier;

/**
 * @author Christian Bremer
 */
public class BranchAdapterTest {

  private static final BranchAdapter branchAdapter = new BranchAdapter(
      new ModelMapperConfiguration().modelMapper());

  private static final String ANNA = "anna";

  private static final String ANNAS_FRIEND = "stephan";

  private static AccessControlDto accessControlDto(String owner) {
    AccessControlDto accessControl = new AccessControlDto();
    accessControl.setOwner(owner);
    accessControl.addUser(owner, PermissionConstants.ALL);
    accessControl.addGroup("friends", PermissionConstants.READ, PermissionConstants.WRITE);
    accessControl.addRole("ROLE_AUDIT", PermissionConstants.READ, PermissionConstants.DELETE);
    return accessControl;
  }

  private static Wpt wpt() {
    return Wpt.builder()
        .geometry(GeometryUtils.createPointWGS84(52.1, 10.2))
        .id("333")
        .properties(WptProperties.builder()
            .accessControl(accessControlDto(ANNAS_FRIEND))
            .ele(BigDecimal.valueOf(123.4))
            .name("Child leaf")
            .settings(WptSettings.builder()
                .id("3333")
                .featureId("333")
                .userId(ANNAS_FRIEND)
                .build())
            .build())
        .build();
  }

  private static Branch childBranch() {
    return Branch.builder()
        .accessControl(accessControlDto(ANNA))
        .createdBy(ANNA)
        .id("2")
        .modifiedBy(ANNA)
        .name("Child branch")
        .parentId("1")
        .settings(BranchSettings.builder().id("22").nodeId("2").open(false).userId(ANNA).build())
        .build();
  }

  private static FeatureLeaf childLeaf() {
    final Wpt wpt = wpt();
    return FeatureLeaf.builder()
        .accessControl(accessControlDto(ANNA))
        .createdBy(ANNA)
        .feature(wpt)
        .id("3")
        .modifiedBy(ANNA)
        .name("Child leaf")
        .parentId("1")
        .settings(FeatureLeafSettings.builder().id("33").nodeId("3").userId(ANNA).build())
        .build();
  }

  private static AccessControlEntity accessControlEntity(AccessControl accessControl) {
    return new AccessControlEntity(accessControl.ensureAdminAccess());
  }

  private static Branch root() {
    return Branch.builder()
        .accessControl(accessControlDto(ANNA))
        .children(Arrays.asList(childLeaf(), childBranch()))
        .createdBy(ANNAS_FRIEND)
        .id("1")
        .modifiedBy(ANNA)
        .name("root")
        .settings(BranchSettings.builder().id("11").nodeId("1").userId(ANNA).build())
        .build();
  }

  private static BranchEntity rootEntity() {
    return BranchEntity.builder()
        .accessControl(accessControlEntity(accessControlDto(ANNAS_FRIEND)))
        .createdBy(ANNA)
        .modifiedBy(ANNAS_FRIEND)
        .name("child")
        .parentId("1")
        .build();
  }

  private static BranchEntitySettings rootEntitySettings() {
    return BranchEntitySettings.builder()
        .id("11")
        .nodeId("1")
        .userId(ANNAS_FRIEND)
        .open(Boolean.FALSE)
        .build();
  }

  @Test
  public void mapNode() {
    final Branch branch = root();
    StepVerifier
        .create(branchAdapter.mapNode(branch, ANNA))
        .assertNext(tuple -> {
          assertNotNull(tuple);
          assertNotNull(tuple.getT1());
          assertNotNull(tuple.getT2());

          System.out.println("### Branch entity");
          System.out.println(tuple.getT1());

          BranchEntity branchEntity = tuple.getT1();
          assertEquals(
              branch.getAccessControl(),
              new AccessControlDto(branchEntity.getAccessControl().removeAdminAccess()));
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

    final BranchSettings branchSettings = root().getSettings();
    StepVerifier
        .create(branchAdapter.mapNodeSettings(branchSettings, ANNA))
        .assertNext(branchEntitySettings -> {
          assertNotNull(branchEntitySettings);

          System.out.println("### Branch entity settings");
          System.out.println(branchEntitySettings);

          assertEquals(branchSettings.getId(), branchEntitySettings.getId());
          assertEquals(root().getId(), branchEntitySettings.getNodeId());
          assertEquals(branchSettings.getOpen(), branchEntitySettings.getOpen());
          assertEquals(branchSettings.getUserId(), branchEntitySettings.getUserId());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void mapNodeEntity() {
    final BranchEntity root = rootEntity();
    final BranchEntitySettings rootSettings = rootEntitySettings();
    StepVerifier
        .create(branchAdapter.mapNodeEntity(root, rootSettings))
        .assertNext(branch -> {
          assertNotNull(branch);

          System.out.println("### Branch");
          System.out.println(branch);

          assertEquals(
              root.getAccessControl(),
              accessControlEntity(branch.getAccessControl()));
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
  public void getNodeName() {
    assertEquals(rootEntity().getName(), branchAdapter.getNodeName(rootEntity(), root()));
  }

  @Test
  public void mapNodeEntitySettings() {
    final BranchEntitySettings rootSettings = rootEntitySettings();
    StepVerifier
        .create(branchAdapter.mapNodeEntitySettings(rootSettings))
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
        .create(branchAdapter.updateName(
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
    final AccessControl accessControl = accessControlDto(ANNAS_FRIEND);
    final BranchEntity branchEntity = BranchEntity.builder()
        .accessControl(accessControlEntity(accessControlDto(ANNA)))
        .build();
    StepVerifier
        .create(branchAdapter.updateAccessControl(
            branchEntity, accessControl, ANNA, Collections.emptyList(), Collections.emptyList()))
        .assertNext(nodeEntity -> {
          assertNotNull(nodeEntity);
          assertEquals(accessControl, accessControlDto(ANNAS_FRIEND).ensureAdminAccess());
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  public void defaultSettings() {
    BranchEntity branchEntity = rootEntity();
    StepVerifier
        .create(branchAdapter.defaultSettings(branchEntity, ANNA))
        .assertNext(branchEntitySettings -> {
          assertNotNull(branchEntitySettings);
          assertEquals(branchEntity.getId(), branchEntitySettings.getNodeId());
          assertEquals(ANNA, branchEntitySettings.getUserId());
        })
        .expectNextCount(0)
        .verifyComplete();
  }
}