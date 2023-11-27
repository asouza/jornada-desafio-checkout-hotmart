package com.deveficiente.desafiocheckouthotmart.compartilhado;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//#chatgptGerou
public class FieldsValueMatchValidator
		implements ConstraintValidator<FieldsValueMatch, Object> {

	private String field;
	private String fieldMatch;

	@Override
	public void initialize(FieldsValueMatch constraintAnnotation) {
		this.field = constraintAnnotation.field();
		this.fieldMatch = constraintAnnotation.fieldMatch();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			final Object fieldValue = ReflectionUtilsCopiedFromSpringTest
					.getField(value, field);
			final Object fieldMatchValue = ReflectionUtilsCopiedFromSpringTest
					.getField(value, fieldMatch);

			return fieldValue != null && fieldValue.equals(fieldMatchValue);
		} catch (Exception e) {
			throw new RuntimeException(
					"Deu algum problema no acesso aos atributos durante a validacao de match de valores. Passo os nomes corretos?",
					e);
		}

	}
}
