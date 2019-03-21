package org.bremersee.peregrinus.tree.repository.adapter;

import java.util.Collections;
import org.bremersee.peregrinus.security.access.PermissionConstants;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.tree.model.Branch;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntity;
import org.bremersee.peregrinus.tree.repository.entity.BranchEntitySettings;
import org.junit.Test;
import reactor.util.function.Tuple2;

/**
 * @author Christian Bremer
 */
public class NodeAdapterTest {

  private static final AbstractNodeAdapter nodeAdapter = new AbstractNodeAdapter() {
  };

  @Test
  public void mapNode() {
    /*
    Branch branch = new Branch("owner", null, new AccessControlDto(), "root");
    branch.setId("1");
    branch.getAccessControl().addGroup("group", PermissionConstants.ALL);
    branch.setChildren(Collections.singletonList(new Branch(
        "guest", branch.getId(), branch.getAccessControl(), "child")));
    branch.getSettings().setId("11");
    branch.getSettings().setNodeId("1");
    branch.getSettings().setOpen(false);

    System.out.println("### Branch ###");
    System.out.println(branch);

    Tuple2<BranchEntity, BranchEntitySettings> tuple = nodeAdapter.mapNode(
        branch, "owner", BranchEntity::new, BranchEntitySettings::new);

    System.out.println("\n### BranchEntity ###");
    System.out.println(tuple.getT1());

    System.out.println("\n### BranchEntitySettings ###");
    System.out.println(tuple.getT2());
    */
  }

  @Test
  public void mapNodeEntity() {
  }

  @Test
  public void mapNodeSettings() {
  }

  @Test
  public void mapNodeEntitySettings() {
  }
}