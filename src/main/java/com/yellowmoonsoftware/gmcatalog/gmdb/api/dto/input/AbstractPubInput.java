package com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.PubType;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.ResourceDetailsConverter;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.output.PubDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class AbstractPubInput<P extends AbstractPubSpecificInput<D>, D extends PubDetails> {
    final private Long id;
    final private LocalDate pubDate;
    @NotNull
    @Valid
    final private PubIndexInput index;
    @NotNull
    @Valid
    final private P info;
    @Valid
    final List<TranscriptionInput> transcriptions;

    public abstract PubType supportedPubType();
}
