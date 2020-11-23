package com.ntankard.javaObjectDatabase.dataField.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class Null_FieldValidatorTest {

    @Test
    void isValid() {
        Null_FieldValidator<Object, ?> validator = new Null_FieldValidator<>(true);
        assertTrue(validator.isValid(null, null, null));
        assertTrue(validator.isValid(new Object(), null, null));

        validator = new Null_FieldValidator<>(false);
        assertFalse(validator.isValid(null, null, null));
        assertTrue(validator.isValid(new Object(), null, null));
    }
}
