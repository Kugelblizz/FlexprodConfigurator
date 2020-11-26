package at.jku.cis.FlexProd.configurator.model.property.constraints;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.ConstraintType;
import at.jku.cis.FlexProd.configurator.model.constraint.property.DatePropertyConstraint;

@Document
public class MinimumDate extends DatePropertyConstraint<Date> {

	MinimumDate(Date value) {
		setConstraintType(ConstraintType.MIN);
		this.setValue(value);
	}

}
