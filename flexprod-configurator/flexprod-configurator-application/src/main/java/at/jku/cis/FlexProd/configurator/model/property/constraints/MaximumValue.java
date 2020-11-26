package at.jku.cis.FlexProd.configurator.model.property.constraints;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.ConstraintType;
import at.jku.cis.FlexProd.configurator.model.constraint.property.LongNumberPropertyConstraint;

@Document
public class MaximumValue extends LongNumberPropertyConstraint<Integer> {

	public MaximumValue(int value) {
		setConstraintType(ConstraintType.MAX);
		this.setValue(value);
	}
}
