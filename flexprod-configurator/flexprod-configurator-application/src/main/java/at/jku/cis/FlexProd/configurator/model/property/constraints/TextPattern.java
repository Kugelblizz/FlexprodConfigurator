package at.jku.cis.FlexProd.configurator.model.property.constraints;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.ConstraintType;
import at.jku.cis.FlexProd.configurator.model.constraint.property.TextPropertyConstraint;

@Document
public class TextPattern extends TextPropertyConstraint<String> {

	public TextPattern(String value) {
		setConstraintType(ConstraintType.PATTERN);
		this.setValue(value);
	}

}
