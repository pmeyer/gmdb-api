package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConditionalNotNullValidatorTest {

    @Mock
    private ConditionalNotNull conditionalNotNull;

    @Mock
    private ConstraintValidatorContext context;

    private ConditionalNotNullValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ConditionalNotNullValidator();
        lenient().when(conditionalNotNull.value()).thenReturn("required");
        lenient().when(conditionalNotNull.ifNull()).thenReturn(new String[]{"first", "second"});
        lenient().when(conditionalNotNull.checkType()).thenReturn(ConditionalNotNull.CheckType.ANY);
        validator.initialize(conditionalNotNull);
    }

    @Test
    void anyCheckRequiresValueWhenAnyDependentFieldIsNull() {
        TestInput value = new TestInput(null, null, "present");

        assertThat(validator.isValid(value, context)).isFalse();
    }

    @Test
    void anyCheckAllowsNullValueWhenAllDependentFieldsArePresent() {
        TestInput value = new TestInput(null, "present", "also present");

        assertThat(validator.isValid(value, context)).isTrue();
    }

    @Test
    void anyCheckAllowsPresentValueWhenDependentFieldIsNull() {
        TestInput value = new TestInput("required", null, "present");

        assertThat(validator.isValid(value, context)).isTrue();
    }

    @Test
    void allCheckRequiresValueWhenAllDependentFieldsAreNull() {
        when(conditionalNotNull.checkType()).thenReturn(ConditionalNotNull.CheckType.ALL);
        TestInput value = new TestInput(null, null, null);

        assertThat(validator.isValid(value, context)).isFalse();
    }

    @Test
    void allCheckAllowsNullValueWhenAnyDependentFieldIsPresent() {
        when(conditionalNotNull.checkType()).thenReturn(ConditionalNotNull.CheckType.ALL);
        TestInput value = new TestInput(null, null, "present");

        assertThat(validator.isValid(value, context)).isTrue();
    }

    @Test
    void checkFieldNullAppliesAnyAndAllSemantics() {
        TestInput value = new TestInput("required", null, "present");

        assertThat(validator.checkFieldNull(value, ConditionalNotNull.CheckType.ANY, "first", "second")).isTrue();
        assertThat(validator.checkFieldNull(value, ConditionalNotNull.CheckType.ALL, "first", "second")).isFalse();
    }

    @Test
    void getFieldReturnsPrivateFieldValue() {
        TestInput value = new TestInput("required", "first", "second");

        assertThat(validator.getField(value, "required")).isEqualTo("required");
    }

    @Test
    void getFieldThrowsRuntimeExceptionWhenFieldDoesNotExist() {
        TestInput value = new TestInput("required", "first", "second");

        assertThatThrownBy(() -> validator.getField(value, "missing"))
            .isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(NoSuchFieldException.class);
    }

    private record TestInput(String required, String first, String second) {
    }
}
