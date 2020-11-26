package at.jku.cis.FlexProd.configurator.matching;

import java.util.ArrayList;
import java.util.List;

import at.jku.cis.FlexProd.configurator.model.clazz.ClassDefinition;
import at.jku.cis.FlexProd.configurator.model.clazz.ClassInstance;
import at.jku.cis.FlexProd.configurator.model.matching.MatchingOperatorRelationship;

public class MatchingPayload {

	private List<ClassInstance> leftClassInstances = new ArrayList<>();
	private List<ClassInstance> rightClassInstances = new ArrayList<>();
	private List<ClassDefinition> classDefinitions = new ArrayList<>();
	private List<MatchingOperatorRelationship> relationships = new ArrayList<>();
	
	
	public MatchingPayload() {
	}


	public List<ClassInstance> getLeftClassInstances() {
		return leftClassInstances;
	}


	public void setLeftClassInstances(List<ClassInstance> leftClassInstances) {
		this.leftClassInstances = leftClassInstances;
	}


	public List<ClassInstance> getRightClassInstances() {
		return rightClassInstances;
	}


	public void setRightClassInstances(List<ClassInstance> rightClassInstances) {
		this.rightClassInstances = rightClassInstances;
	}


	public List<ClassDefinition> getClassDefinitions() {
		return classDefinitions;
	}


	public void setClassDefinitions(List<ClassDefinition> classDefinitions) {
		this.classDefinitions = classDefinitions;
	}


	public List<MatchingOperatorRelationship> getRelationships() {
		return relationships;
	}


	public void setRelationships(List<MatchingOperatorRelationship> relationships) {
		this.relationships = relationships;
	}


}
