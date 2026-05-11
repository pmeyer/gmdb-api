package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConditionalNotNullValidatorTest {

    private ConditionalNotNullValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ConditionalNotNullValidator();
        validator.initialize(annotationFrom(AnyCheckInput.class));
    }

    @Test
    void anyCheckRequiresValueWhenAnyDependentFieldIsNull() {
        final TestInput value = new TestInput(null, null, "present");

        assertThat(validator.isValid(value, null)).isFalse();
    }

    @Test
    void anyCheckAllowsNullValueWhenAllDependentFieldsArePresent() {
        final TestInput value = new TestInput(null, "present", "also present");

        assertThat(validator.isValid(value, null)).isTrue();
    }

    @Test
    void anyCheckAllowsPresentValueWhenDependentFieldIsNull() {
        final TestInput value = new TestInput("required", null, "present");

        assertThat(validator.isValid(value, null)).isTrue();
    }

    @Test
    void allCheckRequiresValueWhenAllDependentFieldsAreNull() {
        validator.initialize(annotationFrom(AllCheckInput.class));
        final TestInput value = new TestInput(null, null, null);

        assertThat(validator.isValid(value, null)).isFalse();
    }

    @Test
    void allCheckAllowsNullValueWhenAnyDependentFieldIsPresent() {
        validator.initialize(annotationFrom(AllCheckInput.class));
        final TestInput value = new TestInput(null, null, "present");

        assertThat(validator.isValid(value, null)).isTrue();
    }

    @Test
    void checkFieldNullAppliesAnyAndAllSemantics() {
        final TestInput value = new TestInput("required", null, "present");

        assertThat(validator.checkFieldNull(value, ConditionalNotNull.CheckType.ANY, "first", "second")).isTrue();
        assertThat(validator.checkFieldNull(value, ConditionalNotNull.CheckType.ALL, "first", "second")).isFalse();
    }

    @Test
    void getFieldReturnsPrivateFieldValue() {
        final TestInput value = new TestInput("required", "first", "second");

        assertThat(validator.getField(value, "required")).isEqualTo("required");
    }

    @Test
    void getFieldThrowsRuntimeExceptionWhenFieldDoesNotExist() {
        final TestInput value = new TestInput("required", "first", "second");

        assertThatThrownBy(() -> validator.getField(value, "missing"))
            .isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(NoSuchFieldException.class);
    }

    private static ConditionalNotNull annotationFrom(Class<?> type) {
        return type.getAnnotation(ConditionalNotNull.class);
    }

    private record TestInput(String required, String first, String second) {
    }

    @ConditionalNotNull(value = "required", ifNull = {"first", "second"})
    private static class AnyCheckInput {
    }

    @ConditionalNotNull(value = "required", ifNull = {"first", "second"}, checkType = ConditionalNotNull.CheckType.ALL)
    private static class AllCheckInput {
    }
}
