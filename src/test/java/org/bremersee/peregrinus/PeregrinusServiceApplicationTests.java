package org.bremersee.peregrinus;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.peregrinus.tree.repository.TreeBranchRepository;
import org.bremersee.peregrinus.tree.repository.GeoTreeLeafRepository;
import org.bremersee.peregrinus.tree.repository.TreeNodeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PeregrinusServiceApplicationTests {

	@Autowired
	private TreeNodeRepository treeNodeRepository;

	@Autowired
	private TreeBranchRepository treeBranchRepository;

	@Autowired
	private GeoTreeLeafRepository treeLeafRepository;

	@Autowired
	private Jackson2ObjectMapperBuilder objectMapperBuilder; // = new Jackson2ObjectMapperBuilder();

	@Test
	public void contextLoads() throws Exception{

		log.info("############## Start");
		final GeoJsonPoint geometry = new GeoJsonPoint(1., 2.);
		final String json = objectMapperBuilder.build()
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(geometry);
		System.out.println(json);

		/*
		TreeBranch root = new TreeBranch();
		root.setCreatedBy("cbr");
		root.setModifiedBy("cbr");
		root.setName("root_of_cbr");

		treeBranchRepository.save(root).subscribe(parent0 -> {
			log.info("############# Saved {}", parent0);
		});
		*/

		/*
		treeBranchRepository.save(root).doOnSuccess(parent0 -> {
			TreeBranch child1 = new TreeBranch();
			child1.setCreatedBy("cbr");
			child1.setModifiedBy("cbr");
			child1.setName("child 1");
			child1.setParent(parent0);
			treeBranchRepository.save(child1).doOnSuccess(parent1 -> {
				TreeLeaf child2 = new TreeLeaf();
				child2.setCreatedBy("cbr");
				child2.setModifiedBy("cbr");
				child2.setName("child 2");
				child2.setParent(parent1);
				treeLeafRepository.save(child2).doOnSuccess(parent2 -> {
					System.out.println("###########################################");
				});
			});
		});
		*/
	}

}
