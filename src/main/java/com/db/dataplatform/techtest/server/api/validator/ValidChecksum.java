package com.db.dataplatform.techtest.server.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CheckSumValidator.class)
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface ValidChecksum {

        String message() default "MD5 check sum provided in the client should match to generated one.";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

}
