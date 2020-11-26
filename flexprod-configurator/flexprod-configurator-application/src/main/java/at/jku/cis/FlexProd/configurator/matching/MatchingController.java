package at.jku.cis.FlexProd.configurator.matching;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import at.jku.cis.FlexProd.configurator.model.clazz.ClassDefinition;
import at.jku.cis.FlexProd.configurator.model.clazz.ClassInstance;
import at.jku.cis.FlexProd.configurator.model.configurations.matching.MatchingOperatorRelationship;
import at.jku.cis.iVolunteer.marketplace.configurations.matching.relationships.MatchingOperatorRelationshipRepository;

@RestController
public class MatchingController {

	@Autowired private MatchingService matchingService;


	@GetMapping("matching/test1")
	public void testMatching1() {
		List<MatchingOperatorRelationship> relationships = this.matchingOperatorRelationshipRepository
				.findAll();

		matchingService.matchClassInstanceAndClassInstance(LEFT_CLASS_INSTANCE, RIGHT_CLASS_INSTANCE, relationships);
	}
	
	@GetMapping("match/flexProd")
	public Map<ClassInstanceComparison, Double> matchClassInstancesAndClassInstances(List<ClassInstance> leftClassInstances, List<ClassInstance> rightClassInstances,
			List<ClassDefinition> classDefinitions, List<MatchingOperatorRelationship> relationships) {
		return matchingService.matchClassInstancesAndClassInstances(leftClassInstances, rightClassInstances, classDefinitions, relationships);
	}

}
