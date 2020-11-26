package at.jku.cis.FlexProd.configurator.model.property.constraints;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.ConstraintType;
import at.jku.cis.FlexProd.configurator.model.constraint.property.LongNumberPropertyConstraint;

@Document
public class MinimumValue extends LongNumberPropertyConstraint<Integer> {

	public MinimumValue(int value) {
		setConstraintType(ConstraintType.MIN);
		this.setValue(value);
	}

}
