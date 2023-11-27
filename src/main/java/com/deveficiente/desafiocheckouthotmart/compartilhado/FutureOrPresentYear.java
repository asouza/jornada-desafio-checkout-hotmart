package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//#chatgptGerou
@Documented
@Constraint(validatedBy = FutureOrPresentYearValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentYear {
    String message() default "O ano deve estar no futuro";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}