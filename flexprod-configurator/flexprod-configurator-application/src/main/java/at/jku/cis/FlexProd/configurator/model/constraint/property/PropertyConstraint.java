package at.jku.cis.FlexProd.configurator.model.constraint.property;

import org.springframework.data.mongodb.core.mapping.Document;

import at.jku.cis.FlexProd.configurator.model.constraint.Constraint;
import at.jku.cis.FlexProd.configurator.model.property.PropertyType;

@Document
public class PropertyConstraint<T> extends Constraint {
	
	T value;
	
	PropertyType propertyType;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}	
	
}
