package com.db.dataplatform.techtest.validator;

import com.db.dataplatform.techtest.server.api.validator.BlockNameValidator;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlockNameValidatorTest {

    BlockNameValidator validator;
    ConstraintValidatorContext context;
    @Before
    public void setup() {
        validator = new BlockNameValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    public void testValidatorWithValidName() {
        assertTrue(validator.isValid("ABc019213-432", context));
    }

    @Test
    public void testValidatorWithInvalidName() {
        assertFalse(validator.isValid("ABc019213~432", context));
    }
}
