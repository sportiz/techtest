package com.db.dataplatform.techtest.server.api.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class BlockNameValidator implements ConstraintValidator<ValidBlockName, String> {

    @Override
    public boolean isValid(
            String blockName, ConstraintValidatorContext context) {

        Pattern p = Pattern.compile("[^-a-z0-9 -,:;]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(blockName);
        boolean b = m.find();
        if (b) {
            return false;
        }

        //TODO We can validate input for security here like sql injection
        return true;
    }
}
