package at.jku.cis.FlexProd.configurator.matching;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {

	@Autowired private MatchingService matchingService;

	@GetMapping("match/flexProd")
	public List<ClassInstanceComparison> matchClassInstancesAndClassInstances(
			@RequestBody MatchingPayload matchingPayload) {

		return matchingService.matchClassInstancesAndClassInstances(matchingPayload.getLeftClassInstances(),
				matchingPayload.getRightClassInstances(), matchingPayload.getClassDefinitions(),
				matchingPayload.getRelationships());
	}

}
