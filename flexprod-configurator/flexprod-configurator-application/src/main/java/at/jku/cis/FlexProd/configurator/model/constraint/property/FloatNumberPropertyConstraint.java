package at.jku.cis.FlexProd.configurator.model.constraint.property;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.property.PropertyType;


@Document
public class FloatNumberPropertyConstraint<T> extends PropertyConstraint<T> {
	
	public FloatNumberPropertyConstraint() {
		setPropertyType(PropertyType.FLOAT_NUMBER);
	}
}
