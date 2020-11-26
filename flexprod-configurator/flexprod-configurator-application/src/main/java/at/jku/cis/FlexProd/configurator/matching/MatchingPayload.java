package at.jku.cis.FlexProd.configurator.matching;

import java.util.ArrayList;
import java.util.List;

public class MatchingPayload {

	private List<ClassInstance> leftClassInstances = new ArrayList<>();
	private List<ClassInstance> rightClassInstances = new ArrayList<>();
	private List<ClassDefinition> classDefinitions = new ArrayList<>();
	private List<MatchingOperatorRelationship> relationships = new ArrayList<>();
	
	
	public MatchingPayload() {
	}


}
