package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ArtistType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.codec.CodecRegistry;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.postgresql.extension.CodecRegistrar;
import org.reactivestreams.Publisher;
import org.springframework.lang.NonNull;

public class PostgresqlEnumCodecRegistrar implements CodecRegistrar {

    private final CodecRegistrar delegate = EnumCodec.builder()
            .withEnum("pub_type", PubType.class)
            .withEnum("artist_type", ArtistType.class)
            .build();

    @NonNull
    @Override
    public Publisher<Void> register(@NonNull final PostgresqlConnection connection, @NonNull final ByteBufAllocator allocator, @NonNull final CodecRegistry registry) {
        return delegate.register(connection, allocator, registry);
    }
}
