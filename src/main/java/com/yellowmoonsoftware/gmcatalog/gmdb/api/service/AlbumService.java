package com.yellowmoonsoftware.gmcatalog.gmdb.api.service;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.IdAndDataContainer;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumIn;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.AlbumOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.db.ArtistOut;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.AlbumInput;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.dto.input.validation.InvalidInputException;
import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers.AlbumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final FileService fileService;
    private final ArtistService artistService;

    @Transactional
    public Mono<AlbumOut> upsertAlbum(final AlbumInput input) {
        final Mono<AlbumOut> upsertSignal = input.mode() == IdAndDataContainer.DataMode.REF
                ? Mono.defer(() -> albumMapper.getAlbumById(input.id()))
                : upsertAlbumData(input);

        return input.id() == null
                ? upsertSignal
                : albumMapper.getAlbumId(input.id())
                    .switchIfEmpty(Mono.error(new InvalidInputException("Unknown album ID: " + input.id())))
                    .then(upsertSignal);
    }

    private Mono<AlbumOut> upsertAlbumData(final AlbumInput input) {
        return Mono.justOrEmpty(input.data().primaryArtist())
                .flatMap(artistInput -> artistService.upsertArtist(artistInput)
                        .map(ArtistOut::id))
                .singleOptional()
                .map(artistId -> AlbumIn.from(input, artistId.orElse(null)))
                .flatMap(albumMapper::upsertAlbum)
                .flatMap(out -> Mono.justOrEmpty(input.data().coverArt())
                        .flatMap(blob -> fileService.put(blob, ResourceSlug.ALBUM_ART, Map.of("id", out.details().resourceId())))
                        .thenReturn(out));
    }
}
