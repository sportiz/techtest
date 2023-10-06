package com.db.dataplatform.techtest.server.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = BlockNameValidator.class)
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface ValidBlockName {

        String message() default "Block name should be valid";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

}
