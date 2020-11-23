package com.ntankard.javaObjectDatabase.dataField.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class NumberRange_FieldValidatorTest {

    private static final int TEST_NUM = 100;

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new NumberRange_FieldValidator<>(null, null));
        assertThrows(IllegalArgumentException.class, () -> new NumberRange_FieldValidator<>(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new NumberRange_FieldValidator<>(50, 50));
        assertThrows(IllegalArgumentException.class, () -> new NumberRange_FieldValidator<>(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new NumberRange_FieldValidator<>(50, -50));
        assertDoesNotThrow(() -> new NumberRange_FieldValidator<>(-10, 10));
        assertDoesNotThrow(() -> new NumberRange_FieldValidator<>(null, 10));
        assertDoesNotThrow(() -> new NumberRange_FieldValidator<>(-10, null));
    }

    @Test
    @Execution(CONCURRENT)
    void integerMinOnlyValidator() {
        for (int i = 0; i < TEST_NUM; i++) {
            int min = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE + 1000, Integer.MAX_VALUE - 1000);

            NumberRange_FieldValidator<Integer, ?> validator = new NumberRange_FieldValidator<>(min, null);

            // edge
            assertTrue(validator.isValid(null, null, null));
            assertTrue(validator.isValid(min, null, null));
            assertFalse(validator.isValid(min - 1, null, null));

            for (int j = 0; j < TEST_NUM; j++) {
                // distinct valid
                int validValue = ThreadLocalRandom.current().nextInt(min, Integer.MAX_VALUE);
                assertTrue(validator.isValid(validValue, null, null));

                // distinct invalid
                int invalidValue = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, min);
                assertFalse(validator.isValid(invalidValue, null, null));
            }
        }
    }

    @Test
    @Execution(CONCURRENT)
    void integerMaxOnlyValidator() {
        for (int i = 0; i < TEST_NUM; i++) {
            int max = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE + 1000, Integer.MAX_VALUE - 1000);

            NumberRange_FieldValidator<Integer, ?> validator = new NumberRange_FieldValidator<>(null, max);

            // edge
            assertTrue(validator.isValid(null, null, null));
            assertTrue(validator.isValid(max, null, null));
            assertFalse(validator.isValid(max + 1, null, null));

            for (int j = 0; j < TEST_NUM; j++) {
                // distinct valid
                int validValue = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, max + 1);
                assertTrue(validator.isValid(validValue, null, null));

                // distinct invalid
                int invalidValue = ThreadLocalRandom.current().nextInt(max + 1, Integer.MAX_VALUE);
                assertFalse(validator.isValid(invalidValue, null, null));
            }
        }
    }

    @Test
    @Execution(CONCURRENT)
    void integerBothValidator() {
        for (int i = 0; i < TEST_NUM; i++) {
            int min = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE + 1000, Integer.MAX_VALUE - 1000);
            int max = ThreadLocalRandom.current().nextInt(min + 100, Integer.MAX_VALUE - 1000);

            NumberRange_FieldValidator<Integer, ?> validator = new NumberRange_FieldValidator<>(min, max);

            // edge
            assertTrue(validator.isValid(null, null, null));
            assertTrue(validator.isValid(max, null, null));
            assertFalse(validator.isValid(max + 1, null, null));
            assertTrue(validator.isValid(min, null, null));
            assertFalse(validator.isValid(min - 1, null, null));

            for (int j = 0; j < TEST_NUM; j++) {
                // distinct valid
                int validValue = ThreadLocalRandom.current().nextInt(min, max + 1);
                assertTrue(validator.isValid(validValue, null, null));

                // distinct invalid
                int invalidValue = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, min);
                assertFalse(validator.isValid(invalidValue, null, null));
                invalidValue = ThreadLocalRandom.current().nextInt(max + 1, Integer.MAX_VALUE);
                assertFalse(validator.isValid(invalidValue, null, null));
            }
        }
    }

    @Test
    @Execution(CONCURRENT)
    void doubleMinOnlyValidator() {
        for (int i = 0; i < TEST_NUM; i++) {
            double min = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE / 4, Double.MAX_VALUE / 4);
            NumberRange_FieldValidator<Double, ?> validator = new NumberRange_FieldValidator<>(min, null);

            // edge
            assertTrue(validator.isValid(null, null, null));
            assertTrue(validator.isValid(min, null, null));
            assertTrue(validator.isValid(Math.nextUp(min), null, null));
            assertFalse(validator.isValid(Math.nextDown(min), null, null));

            for (int j = 0; j < TEST_NUM; j++) {
                // distinct valid
                double validValue = ThreadLocalRandom.current().nextDouble(min, Double.MAX_VALUE);
                assertTrue(validator.isValid(validValue, null, null));

                // distinct invalid
                double invalidValue = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, min);
                assertFalse(validator.isValid(invalidValue, null, null));
            }
        }
    }

    @Test
    @Execution(CONCURRENT)
    void doubleMaxOnlyValidator() {
        for (int i = 0; i < TEST_NUM; i++) {
            double max = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE / 4, Double.MAX_VALUE / 2);
            NumberRange_FieldValidator<Double, ?> validator = new NumberRange_FieldValidator<>(null, max);

            // edge
            assertTrue(validator.isValid(null, null, null));
            assertTrue(validator.isValid(max, null, null));
            assertTrue(validator.isValid(Math.nextDown(max), null, null));
            assertFalse(validator.isValid(Math.nextUp(max), null, null));

            for (int j = 0; j < TEST_NUM; j++) {
                // distinct valid
                double validValue = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, Math.nextUp(max));
                assertTrue(validator.isValid(validValue, null, null));

                // distinct invalid
                double invalidValue = ThreadLocalRandom.current().nextDouble(Math.nextUp(max), Double.MAX_VALUE);
                assertFalse(validator.isValid(invalidValue, null, null));
            }
        }
    }

    @Test
    @Execution(CONCURRENT)
    void doubleBothValidator() {
        for (int i = 0; i < TEST_NUM; i++) {
            double min = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE / 4, Double.MAX_VALUE / 4);
            double max = ThreadLocalRandom.current().nextDouble(min + 100, Double.MAX_VALUE / 2);
            NumberRange_FieldValidator<Double, ?> validator = new NumberRange_FieldValidator<>(min, max);

            // edge
            assertTrue(validator.isValid(null, null, null));
            assertTrue(validator.isValid(max, null, null));
            assertTrue(validator.isValid(Math.nextDown(max), null, null));
            assertFalse(validator.isValid(Math.nextUp(max), null, null));
            assertTrue(validator.isValid(min, null, null));
            assertTrue(validator.isValid(Math.nextUp(min), null, null));
            assertFalse(validator.isValid(Math.nextDown(min), null, null));

            for (int j = 0; j < TEST_NUM; j++) {
                // distinct valid
                double validValue = ThreadLocalRandom.current().nextDouble(min, Math.nextUp(max));
                assertTrue(validator.isValid(validValue, null, null));

                // distinct invalid
                double invalidValue = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, min);
                assertFalse(validator.isValid(invalidValue, null, null));
                invalidValue = ThreadLocalRandom.current().nextDouble(Math.nextUp(max), Double.MAX_VALUE);
                assertFalse(validator.isValid(invalidValue, null, null));
            }
        }
    }
}
