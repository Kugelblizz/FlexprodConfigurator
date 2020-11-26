package at.jku.cis.FlexProd.configurator.model.constraint.property;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.property.PropertyType;


@Document
public class BooleanPropertyConstraint<T> extends PropertyConstraint<T> {
	
	public BooleanPropertyConstraint() {
		setPropertyType(PropertyType.BOOL);
	}
}
