package com.deveficiente.desafiocheckouthotmart.compartilhado;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//#chatgptGerou
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Documented
public @interface FieldsValueMatch {

    String message() default "Os campos não correspondem";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String field();
    String fieldMatch();

    /*
     * A explicação para essa annotation pode ser vista aqui
     * https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#constraintsdefinitionimplementation-multipleconstraints
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsValueMatch[] value();
    }
}
