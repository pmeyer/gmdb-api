package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.r2dbc;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.PGDataConversionException;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Readable;
import io.r2dbc.spi.ReadableMetadata;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.parameter.ParameterHandlerContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseJsonbTypeHandlerAdapterTest {

    @Mock
    private Statement statement;

    @Mock
    private Readable readable;

    @Mock
    private ReadableMetadata metadata;

    @Test
    void adaptClazzReturnsConfiguredType() {
        BaseJsonbTypeHandlerAdapter<TestPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(TestPayload.class);

        assertThat(adapter.adaptClazz()).isEqualTo(TestPayload.class);
    }

    @Test
    void setParameterSerializesParameterAsJsonb() {
        BaseJsonbTypeHandlerAdapter<TestPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(TestPayload.class);
        ParameterHandlerContext context = new ParameterHandlerContext();
        context.setIndex(2);
        TestPayload payload = new TestPayload("Alice", 7);

        adapter.setParameter(statement, context, payload);

        ArgumentCaptor<Json> captor = ArgumentCaptor.forClass(Json.class);
        verify(statement).bind(eq(2), captor.capture());
        assertThat(captor.getValue().asString()).isEqualTo("{\"name\":\"Alice\",\"count\":7}");
    }

    @Test
    void setParameterWrapsJsonSerializationFailure() {
        BaseJsonbTypeHandlerAdapter<SelfReferencingPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(SelfReferencingPayload.class);
        ParameterHandlerContext context = new ParameterHandlerContext();
        context.setIndex(1);

        assertThatThrownBy(() -> adapter.setParameter(statement, context, new SelfReferencingPayload()))
            .isInstanceOf(PGDataConversionException.class)
            .hasMessage("Unable to convert object to JSON representation.")
            .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }

    @Test
    void getResultByColumnNameReadsJsonbValue() {
        BaseJsonbTypeHandlerAdapter<TestPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(TestPayload.class);
        when(readable.get("details", Json.class)).thenReturn(Json.of("{\"name\":\"Alice\",\"count\":7}"));

        TestPayload result = adapter.getResult(readable, metadata, "details");

        assertThat(result).isEqualTo(new TestPayload("Alice", 7));
        verify(readable).get("details", Json.class);
    }

    @Test
    void getResultByColumnIndexReadsJsonbValue() {
        BaseJsonbTypeHandlerAdapter<TestPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(TestPayload.class);
        when(readable.get(3, Json.class)).thenReturn(Json.of("{\"name\":\"Bob\",\"count\":4}"));

        TestPayload result = adapter.getResult(readable, metadata, 3);

        assertThat(result).isEqualTo(new TestPayload("Bob", 4));
        verify(readable).get(3, Json.class);
    }

    @Test
    void mapColumnValueReturnsNullForNullJsonbValue() {
        BaseJsonbTypeHandlerAdapter<TestPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(TestPayload.class);

        assertThat(adapter.mapColumnValue(null)).isNull();
    }

    @Test
    void mapColumnValueWrapsJsonDeserializationFailure() {
        BaseJsonbTypeHandlerAdapter<TestPayload> adapter = new BaseJsonbTypeHandlerAdapter<>(TestPayload.class);

        assertThatThrownBy(() -> adapter.mapColumnValue(Json.of("{not valid json")))
            .isInstanceOf(PGDataConversionException.class)
            .hasMessage("Unable to deserialize object from JSON string");
    }

    record TestPayload(String name, int count) {
    }

    static class SelfReferencingPayload {
        public final SelfReferencingPayload self = this;
    }
}
