package org.bremersee.peregrinus.tree.repository.adapter;

import static org.bremersee.peregrinus.TestData.accessControlEntity;
import static org.bremersee.peregrinus.TestData.rte;
import static org.bremersee.peregrinus.TestData.rteLeaf;
import static org.bremersee.peregrinus.TestData.trk;
import static org.bremersee.peregrinus.TestData.trkLeaf;
import static org.bremersee.peregrinus.TestData.wpt;
import static org.bremersee.peregrinus.TestData.wptLeaf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.bremersee.peregrinus.TestConfig;
import org.bremersee.peregrinus.content.repository.FeatureRepository;
import org.bremersee.peregrinus.security.access.model.AccessControlDto;
import org.bremersee.peregrinus.tree.model.FeatureLeaf;
import org.bremersee.peregrinus.tree.model.FeatureLeafSettings;
import org.bremersee.peregrinus.tree.repository.entity.FeatureLeafEntity;
import org.bremersee.peregrinus.tree.repository.entity.FeatureLeafEntitySettings;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Christian Bremer
 */
public class FeatureLeafAdapterTest {

  private static FeatureLeafAdapter adapter;

  @BeforeClass
  public static void setup() {
    FeatureRepository featureRepository = mock(FeatureRepository.class);
    when(
        featureRepository.updateNameAndAccessControl(
            anyString(),
            anyString(),
            any(AccessControlDto.class)))
        .thenReturn(Mono.just(Boolean.TRUE));
    when(
        featureRepository.findById(
            eq(wpt().getId()),
            anyString()))
        .thenReturn(Mono.just(wpt()));
    when(
        featureRepository.findById(
            eq(trk().getId()),
            anyString()))
        .thenReturn(Mono.just(trk()));
    when(
        featureRepository.findById(
            eq(rte().getId()),
            anyString()))
        .thenReturn(Mono.just(rte()));

    adapter = new FeatureLeafAdapter(
        TestConfig.getModelMapper(),
        featureRepository);
  }

  @Test
  public void getSupportedClasses() {
    final List<Class<?>> classes = Arrays.asList(adapter.getSupportedClasses());
    assertTrue(classes.contains(FeatureLeaf.class));
    assertTrue(classes.contains(FeatureLeafSettings.class));
    assertTrue(classes.contains(FeatureLeafEntity.class));
    assertTrue(classes.contains(FeatureLeafEntitySettings.class));
  }

  @Test
  public void mapNode() {
    for (FeatureLeaf featureLeaf : Arrays.asList(wptLeaf(), rteLeaf(), trkLeaf())) {
      StepVerifier
          .create(adapter.mapNode(featureLeaf, featureLeaf.getSettings().getUserId()))
          .assertNext(tuple -> {
            assertNotNull(tuple);
            assertNotNull(tuple.getT1());
            assertNotNull(tuple.getT2());

            System.out.println("### Feature leaf entity");
            System.out.println(tuple.getT1());

            FeatureLeafEntity featureLeafEntity = tuple.getT1();
            assertEquals(
                accessControlEntity(featureLeaf.getAccessControl()),
                featureLeafEntity.getAccessControl());
            assertEquals(featureLeaf.getCreated(), featureLeafEntity.getCreated());
            assertEquals(featureLeaf.getCreatedBy(), featureLeafEntity.getCreatedBy());
            assertEquals(featureLeaf.getFeature().getId(), featureLeafEntity.getFeatureId());
            assertEquals(featureLeaf.getId(), featureLeafEntity.getId());
            assertEquals(featureLeaf.getModified(), featureLeafEntity.getModified());
            assertEquals(featureLeaf.getModifiedBy(), featureLeafEntity.getModifiedBy());
            assertEquals(featureLeaf.getParentId(), featureLeafEntity.getParentId());

            System.out.println("### Feature leaf entity settings");
            System.out.println(tuple.getT2());

            FeatureLeafEntitySettings featureLeafEntitySettings = tuple.getT2();
            assertEquals(
                featureLeaf.getSettings().getDisplayedOnMap(),
                featureLeafEntitySettings.getDisplayedOnMap());
            assertEquals(
                featureLeaf.getSettings().getId(),
                featureLeafEntitySettings.getId());
            assertEquals(
                featureLeaf.getSettings().getNodeId(),
                featureLeafEntitySettings.getNodeId());
            assertEquals(
                featureLeaf.getSettings().getUserId(),
                featureLeafEntitySettings.getUserId());

          })
          .expectNextCount(0)
          .verifyComplete();
    }
  }

  @Test
  public void mapNodeSettings() {
    for (FeatureLeaf featureLeaf : Arrays.asList(wptLeaf(), rteLeaf(), trkLeaf())) {
      StepVerifier
          .create(adapter.mapNodeSettings(
              featureLeaf.getSettings(), featureLeaf.getSettings().getUserId()))
          .assertNext(nodeEntitySettings -> {
            assertNotNull(nodeEntitySettings);
            assertTrue(nodeEntitySettings instanceof FeatureLeafEntitySettings);

            FeatureLeafEntitySettings featureLeafEntitySettings
                = (FeatureLeafEntitySettings) nodeEntitySettings;

            System.out.println("### Feature leaf entity settings");
            System.out.println(featureLeafEntitySettings);

            assertEquals(
                featureLeaf.getSettings().getDisplayedOnMap(),
                featureLeafEntitySettings.getDisplayedOnMap());
            assertEquals(
                featureLeaf.getSettings().getId(),
                featureLeafEntitySettings.getId());
            assertEquals(
                featureLeaf.getSettings().getNodeId(),
                featureLeafEntitySettings.getNodeId());
            assertEquals(
                featureLeaf.getSettings().getUserId(),
                featureLeafEntitySettings.getUserId());

          })
          .expectNextCount(0)
          .verifyComplete();
    }
  }

  @Test
  public void mapNodeEntity() {
  }

  @Test
  public void getNodeName() {
  }

  @Test
  public void mapNodeEntitySettings() {
  }

  @Test
  public void updateName() {
  }

  @Test
  public void updateAccessControl() {
  }

  @Test
  public void removeNode() {
  }

  @Test
  public void defaultSettings() {
  }
}