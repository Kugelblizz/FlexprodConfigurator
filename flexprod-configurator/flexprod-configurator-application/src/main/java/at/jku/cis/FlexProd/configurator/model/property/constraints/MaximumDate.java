package at.jku.cis.FlexProd.configurator.model.property.constraints;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.ConstraintType;
import at.jku.cis.FlexProd.configurator.model.constraint.property.DatePropertyConstraint;

@Document
public class MaximumDate extends DatePropertyConstraint<Date> {

	MaximumDate(Date value) {
		setConstraintType(ConstraintType.MAX);
		this.setValue(value);
	}

}
