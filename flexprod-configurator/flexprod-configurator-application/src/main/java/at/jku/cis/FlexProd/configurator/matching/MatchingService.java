package at.jku.cis.FlexProd.configurator.matching;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.jku.cis.FlexProd.configurator.model.clazz.ClassDefinition;
import at.jku.cis.FlexProd.configurator.model.clazz.ClassInstance;
import at.jku.cis.FlexProd.configurator.model.matching.MatchingOperatorRelationship;
import at.jku.cis.FlexProd.configurator.model.property.definition.ClassProperty;
import at.jku.cis.FlexProd.configurator.model.property.instance.PropertyInstance;

@Service
public class MatchingService {

	@Autowired private MatchingPreparationService matchingPreparationService;
	@Autowired private DateTimeService dateTimeService;
	Logger logger = Logger.getLogger(MatchingService.class);

	public List<ClassInstanceComparison> matchClassInstancesAndClassInstances(
			List<ClassInstance> leftClassInstances, List<ClassInstance> rightClassInstances,
			List<ClassDefinition> classDefinitions, List<MatchingOperatorRelationship> relationships) {

		List<ClassInstanceComparison> rankedList = new ArrayList<ClassInstanceComparison>();

		for (ClassInstance leftClassInstance : leftClassInstances) {
			for (ClassInstance rightClassInstance : rightClassInstances) {
				double score = matchClassInstanceAndClassInstance(leftClassInstance, rightClassInstance,
						classDefinitions, relationships);
				rankedList.add(new ClassInstanceComparison(leftClassInstance.getId(), rightClassInstance.getId(),score));
			}
		}
		return rankedList;
	}

	public double matchClassInstanceAndClassInstance(ClassInstance leftClassInstance, ClassInstance rightClassInstance,
			List<ClassDefinition> classDefinitions, List<MatchingOperatorRelationship> relationships) {

		double sum = 0;

		for (MatchingOperatorRelationship relationship : relationships) {
			List<ClassDefinition> leftClassDefinitions = matchingPreparationService
					.retriveLeftClassDefinition(classDefinitions, relationship);

			ClassProperty<Object> leftClassProperty = matchingPreparationService
					.retrieveLeftClassProperty(leftClassDefinitions, relationship);

			List<ClassDefinition> rightClassDefinitions = matchingPreparationService
					.retrieveRightClassDefinitionEntity(classDefinitions, relationship);

			ClassProperty<Object> rightClassProperty = matchingPreparationService
					.retrieveRightClassProperty(rightClassDefinitions, relationship);

			boolean leftInstanceMatch = leftClassDefinitions.stream()
					.anyMatch(lcd -> lcd.getId().equals(leftClassInstance.getClassDefinitionId()));

			boolean rightInstanceMatch = rightClassDefinitions.stream()
					.anyMatch(rcd -> rcd.getId().equals(rightClassInstance.getClassDefinitionId()));

			if (leftInstanceMatch && rightInstanceMatch) {
				sum += this.matchSingleAndSingle(leftClassInstance, leftClassProperty, rightClassInstance,
						rightClassProperty, relationship);
			}

		}
		double totalWeighting = relationships.stream().mapToDouble(r -> r.getWeighting()).sum();
		return (sum / totalWeighting);
	}

	public double matchListAndList(List<ClassInstance> leftClassInstances, ClassProperty<Object> leftClassProperty,
			List<ClassInstance> rightClassInstances, ClassProperty<Object> rightClassProperty,
			MatchingOperatorRelationship relationship) {
		double sum = 0;
		for (ClassInstance ci : leftClassInstances) {
			sum += this.matchListAndSingle(ci, leftClassProperty, rightClassInstances, rightClassProperty,
					relationship);
		}
		return sum;
	}

	public double matchListAndSingle(ClassInstance leftClassInstance, ClassProperty<Object> leftClassProperty,
			List<ClassInstance> classInstances, ClassProperty<Object> rightClassProperty,
			MatchingOperatorRelationship relationship) {

		double sum = 0;
		for (ClassInstance rightClassInstance : classInstances) {
			sum += matchSingleAndSingle(leftClassInstance, leftClassProperty, rightClassInstance, rightClassProperty,
					relationship);
		}

		return sum;
	}

	public double matchSingleAndSingle(ClassInstance leftClassInstance, ClassProperty<Object> leftClassProperty,
			ClassInstance rightClassInstance, ClassProperty<Object> rightClassProperty,
			MatchingOperatorRelationship relationship) {

		if (leftClassProperty.getType() != rightClassProperty.getType()) {
			logger.warn("cannot compare two properties with different types");
		}

		PropertyInstance<Object> leftPropertyInstance = findPropertyInstance(leftClassInstance,
				leftClassProperty.getId());
		PropertyInstance<Object> rightPropertyInstance = findPropertyInstance(rightClassInstance,
				rightClassProperty.getId());

		if (leftPropertyInstance != null && rightPropertyInstance != null) {

			if (leftPropertyInstance.getValues().size() != 1 || rightPropertyInstance.getValues().size() != 1) {
				logger.warn("property value is either not set or multiple are set.");
			}

			switch (leftClassProperty.getType()) {
			case BOOL:
				return compareBoolean(leftPropertyInstance, rightPropertyInstance, relationship);
			case DATE:
				return compareDate(leftPropertyInstance, rightPropertyInstance, relationship);
			case ENUM:
//			TODO
				return 0;
			case FLOAT_NUMBER:
				return compareFloat(leftPropertyInstance, rightPropertyInstance, relationship);
			case LONG_TEXT:
			case TEXT:
				return compareText(leftPropertyInstance, rightPropertyInstance, relationship);
			case TUPLE:
//			TODO
				return 0;
			case WHOLE_NUMBER:
				return compareWholeNumber(leftPropertyInstance, rightPropertyInstance, relationship);
			default:
				return 0;
			}
		} else {
			logger.error("Could not find left and right property instance!!");
		}
		return 0;
	}

	private PropertyInstance<Object> findPropertyInstance(ClassInstance leftClassInstance, String propertyId) {
		PropertyInstance<Object> property = null;

		// @formatter:off
		property =  leftClassInstance
						.getProperties()
						.stream()
						.filter(p -> p.getId().equals(propertyId))
						.findFirst()
						.orElse(null);
			 
		// @formatter:on
		if (property != null) {
			return property;
		}

		List<ClassInstance> childClassInstances = leftClassInstance.getChildClassInstances();
		for (ClassInstance classInstance : childClassInstances) {
			property = findPropertyInstance(classInstance, propertyId);
			if (property != null) {
				return property;
			}
		}

		return null;
	}

	private float compareBoolean(PropertyInstance<Object> leftPropertyInstance,
			PropertyInstance<Object> rightPropertyInstance, MatchingOperatorRelationship relationship) {
		boolean leftBoolean = Boolean.parseBoolean(leftPropertyInstance.getValues().get(0).toString());
		boolean rightBoolean = Boolean.parseBoolean(rightPropertyInstance.getValues().get(0).toString());
		switch (relationship.getMatchingOperatorType()) {
		case ALL:
		case EXISTS:
		case GREATER:
		case GREATER_EQUAL:
		case LESS:
		case LESS_EQUAL:
			logger.warn("Matching Operator not supported for boolean!");
		case EQUAL:
			return leftBoolean == rightBoolean ? 1 : 0;
		}
		return 0;
	}

	private double compareDate(PropertyInstance<Object> leftPropertyInstance,
			PropertyInstance<Object> rightPropertyInstance, MatchingOperatorRelationship relationship) {
		Date leftDate = this.dateTimeService
				.parseMultipleDateFormats(leftPropertyInstance.getValues().get(0).toString());
		Date rightDate = this.dateTimeService
				.parseMultipleDateFormats(rightPropertyInstance.getValues().get(0).toString());
		switch (relationship.getMatchingOperatorType()) {
		case ALL:
		case EXISTS:
			logger.warn("Matching Operator not supported for date!");
		case GREATER:
			return leftDate.compareTo(rightDate) > 0 ? 1 : 0;
		case GREATER_EQUAL:
			return leftDate.compareTo(rightDate) >= 0 ? 1 : 0;
		case LESS:
			return leftDate.compareTo(rightDate) < 0 ? 1 : 0;
		case LESS_EQUAL:
			return leftDate.compareTo(rightDate) <= 0 ? 1 : 0;
		case EQUAL:
			return leftDate.compareTo(rightDate) == 0 ? 1 : 0;
		}
		return 0;
	}

	private double compareFloat(PropertyInstance<Object> leftPropertyInstance,
			PropertyInstance<Object> rightPropertyInstance, MatchingOperatorRelationship relationship) {
		double leftDouble = Double.parseDouble(leftPropertyInstance.getValues().get(0).toString());
		double rightDouble = Double.parseDouble(rightPropertyInstance.getValues().get(0).toString());
		switch (relationship.getMatchingOperatorType()) {
		case ALL:
		case EXISTS:
			logger.warn("Matching Operator not supported for float!");
		case EQUAL:
			return calculateFuzzynessLower(leftDouble, relationship.getFuzzyness()) <= rightDouble
					&& rightDouble >= calculateFuzzynessUpper(leftDouble, relationship.getFuzzyness()) ? 1 : 0;
		case GREATER:
			return calculateFuzzynessUpper(leftDouble, relationship.getFuzzyness()) > rightDouble ? 1 : 0;
		case GREATER_EQUAL:
			return calculateFuzzynessUpper(leftDouble, relationship.getFuzzyness()) >= rightDouble ? 1 : 0;
		case LESS:
			return calculateFuzzynessLower(leftDouble, relationship.getFuzzyness()) < rightDouble ? 1 : 0;
		case LESS_EQUAL:
			return calculateFuzzynessLower(leftDouble, relationship.getFuzzyness()) <= rightDouble ? 1 : 0;
		}
		return 0;
	}

	private double compareText(PropertyInstance<Object> leftPropertyInstance,
			PropertyInstance<Object> rightPropertyInstance, MatchingOperatorRelationship relationship) {
		String leftString = leftPropertyInstance.getValues().get(0).toString();
		String rightString = rightPropertyInstance.getValues().get(0).toString();
		switch (relationship.getMatchingOperatorType()) {
		case ALL:
		case EXISTS:
		case GREATER:
		case GREATER_EQUAL:
		case LESS:
		case LESS_EQUAL:
			logger.warn("Matching Operator not supported for text!");
		case EQUAL:
			return leftString.equals(rightString) ? 1 : 0;
		}
		return 0;

	}

	private double compareWholeNumber(PropertyInstance<Object> leftPropertyInstance,
			PropertyInstance<Object> rightPropertyInstance, MatchingOperatorRelationship relationship) {
		long leftLong = Long.parseLong(leftPropertyInstance.getValues().get(0).toString());
		long rightLong = Long.parseLong(rightPropertyInstance.getValues().get(0).toString());
		switch (relationship.getMatchingOperatorType()) {
		case ALL:
		case EXISTS:
			logger.warn("Matching Operator not supported for whole number!");
		case EQUAL:
			return calculateFuzzynessLower(leftLong, relationship.getFuzzyness()) <= rightLong
					&& rightLong >= calculateFuzzynessUpper(leftLong, relationship.getFuzzyness()) ? 1 : 0;
		case GREATER:
			return calculateFuzzynessUpper(leftLong, relationship.getFuzzyness()) > rightLong ? 1 : 0;
		case GREATER_EQUAL:
			return calculateFuzzynessUpper(leftLong, relationship.getFuzzyness()) >= rightLong ? 1 : 0;
		case LESS:
			return calculateFuzzynessLower(leftLong, relationship.getFuzzyness()) < rightLong ? 1 : 0;
		case LESS_EQUAL:
			return calculateFuzzynessLower(leftLong, relationship.getFuzzyness()) <= rightLong ? 1 : 0;
		}
		return 0;
	}

	private double calculateFuzzynessLower(double value, float weighting) {
		return value - value * weighting / 100;
	}

	private double calculateFuzzynessUpper(double value, float weighting) {
		return value + value * weighting / 100;
	}

}
