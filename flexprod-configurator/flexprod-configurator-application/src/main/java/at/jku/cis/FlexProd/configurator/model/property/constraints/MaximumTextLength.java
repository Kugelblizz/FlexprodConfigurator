package at.jku.cis.FlexProd.configurator.model.property.constraints;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.ConstraintType;
import at.jku.cis.FlexProd.configurator.model.constraint.property.TextPropertyConstraint;

@Document
public class MaximumTextLength extends TextPropertyConstraint<Integer> {

	public MaximumTextLength(int value) {
		setConstraintType(ConstraintType.MAX_LENGTH);
		this.setValue(value);
	}

}
