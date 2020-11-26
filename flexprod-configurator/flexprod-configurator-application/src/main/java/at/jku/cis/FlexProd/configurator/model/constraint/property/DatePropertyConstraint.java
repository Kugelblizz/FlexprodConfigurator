package at.jku.cis.FlexProd.configurator.model.constraint.property;

import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class DatePropertyConstraint<T> extends PropertyConstraint<T> {
	
	public DatePropertyConstraint() {
		setPropertyType(at.jku.cis.FlexProd.configurator.model.property.PropertyType.DATE);
	}

}
