package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.api.PostgresqlResult;
import io.r2dbc.postgresql.api.PostgresqlStatement;
import io.r2dbc.postgresql.codec.Codec;
import io.r2dbc.postgresql.codec.CodecRegistry;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresqlEnumCodecRegistrarTest {

    @Mock
    private PostgresqlConnection connection;

    @Mock
    private PostgresqlStatement statement;

    @Mock
    private CodecRegistry registry;

    @Mock
    private ByteBufAllocator allocator;

    @Test
    void registerAddsCodecsForConfiguredPostgresqlEnums() {
        PostgresqlEnumCodecRegistrar registrar = new PostgresqlEnumCodecRegistrar();
        PostgresqlResult pubType = postgresTypeResult(100L, "pub_type", "E");
        PostgresqlResult artistType = postgresTypeResult(101L, "artist_type", "E");
        PostgresqlResult nonEnumType = postgresTypeResult(102L, "ignored_type", "S");
        when(connection.createStatement(argThat(sql -> sql.contains("'pub_type'") && sql.contains("'artist_type'"))))
            .thenReturn(statement);
        when(statement.execute()).thenReturn(Flux.just(pubType, artistType, nonEnumType));

        StepVerifier.create(registrar.register(connection, allocator, registry))
            .verifyComplete();

        ArgumentCaptor<Codec<?>> codecCaptor = ArgumentCaptor.forClass(Codec.class);
        verify(registry, times(2)).addLast(codecCaptor.capture());

        List<Class<?>> registeredTypes = codecCaptor.getAllValues().stream()
            .<Class<?>>map(codec -> ((EnumCodec<?>) codec).type())
            .toList();

        assertThat(codecCaptor.getAllValues())
            .hasSize(2)
            .allMatch(EnumCodec.class::isInstance);
        assertThat(registeredTypes).containsExactlyInAnyOrder(PubType.class, ArtistType.class);
    }

    private static PostgresqlResult postgresTypeResult(Long oid, String typeName, String typeCategory) {
        Row row = row(oid, typeName, typeCategory);
        RowMetadata metadata = rowMetadata();
        PostgresqlResult result = org.mockito.Mockito.mock(PostgresqlResult.class);
        when(result.map(org.mockito.ArgumentMatchers.<BiFunction<Row, RowMetadata, Object>>any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            BiFunction<Row, RowMetadata, ?> mapper = invocation.getArgument(0, BiFunction.class);
            return Flux.just(mapper.apply(row, metadata));
        });
        return result;
    }

    private static Row row(Long oid, String typeName, String typeCategory) {
        Row row = org.mockito.Mockito.mock(Row.class);
        when(row.get("oid", Long.class)).thenReturn(oid);
        when(row.get("typname", String.class)).thenReturn(typeName);
        when(row.get("typcategory", String.class)).thenReturn(typeCategory);
        return row;
    }

    private static RowMetadata rowMetadata() {
        RowMetadata metadata = org.mockito.Mockito.mock(RowMetadata.class);
        when(metadata.contains("typarray")).thenReturn(false);
        return metadata;
    }
}
