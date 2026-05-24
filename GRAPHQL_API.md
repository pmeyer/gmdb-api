# GraphQL API Guide

The GMDB API exposes a GraphQL endpoint for searching the catalog and for mutating publication, song, album,
transcription, artist, transcriber, and resource metadata. The endpoint is served at `/graphql`; GraphiQL is enabled by
the application configuration for local exploration.

## General Semantics

The API models a catalog of publications, songs, albums, artists, transcribers, and transcription files.

Read operations are query-oriented and accept optional criteria objects. Criteria fields are combined as filters unless a
field description says otherwise. An omitted criteria object, an empty criteria object, or an omitted optional criteria
field means that no filter is applied for that dimension.

Mutation operations are upsert-oriented. Where an input supports both `id` and `data`, the API applies these rules:

- Supplying only `id` references an existing row.
- Supplying `id` and `data` updates the existing row before using it.
- Supplying only `data` creates a new row unless the operation can resolve an existing row by natural matching.
- Supplying neither `id` nor `data` is invalid input.
- Supplying an unknown `id` is invalid input.

The mutation layer validates referenced IDs close to where the referenced entity is used. A mutation should not partially
proceed when any supplied ID is unknown.

Resource URLs returned from the API are relative URLs intended to be requested from the same host as the GraphQL API.
Clients should treat them as opaque API URLs, not as file-system paths.

## Queries

### `songSearch(criteria: SongSearchCriteria! = {})`

Searches songs and can return album, artist, and transcription details.

Criteria semantics:

- `titleSearch` matches song title case-insensitively.
- `pubNameSearch` matches publication index name for publications where a song appears.
- `artistSearch` matches all direct song artist names and the album artist name, when present.
- `albumSearch` matches album title.
- `artists` matches songs associated with any supplied artist criterion. Criteria can include artist roles; album artist
  matching uses `ALBUM_ARTIST`.
- `albums` matches songs appearing on any supplied album id.
- `pubs` matches songs transcribed in any supplied publication id.

### `artistSearch(criteria: ArtistSearchCriteria! = {})`

Searches artists with song or album associations.

Criteria semantics:

- `searchName` matches artist name case-insensitively.
- `type` filters by artist type.
- `roles` matches artists with any supplied role. Omitting `roles` is the same as not applying a role filter.
- `restrictToTranscribedArtists`, when true, restricts results to artists associated with songs that have at least one
  transcription.
- `orderBy` applies ordered sort specifications.

### `pubSearch(criteria: PubSearchCriteria! = {})`

Searches publication instances, including magazine issues and book editions.

Criteria semantics:

- `searchName` matches publication index name, magazine issue name, or book edition case-insensitively.
- `type` filters by publication type.
- `dateStart` and `dateEnd` apply inclusive publication date bounds.
- `hasTranscriptions`, when true, returns only publications with at least one transcription.

### `albumSearch(criteria: AlbumSearchCriteria! = {})`

Searches albums.

Criteria semantics:

- `searchName` matches album title or album artist name case-insensitively.
- `dateStart` and `dateEnd` apply inclusive release date bounds.
- `orderBy` applies ordered sort specifications with album id as the final tiebreaker.

### `transcriberSearch(searchName: String)`

Searches transcribers. When `searchName` is supplied, it matches transcriber names case-insensitively; omitting it
returns all transcribers.

### `getPubIndices(criteria: PubIndexCriteria! = {})`

Returns publication index entries, optionally filtered by publication type.

## Mutations

### `upsertPubIndex(pubIndexInput: PubIndexInput!)`

Creates, updates, or resolves a publication index. A publication index represents the reusable publication title and
serial metadata used by concrete magazine issues or book editions.

### `addMagazineIssue(magInput: MagazineInput!)`

Creates or updates a magazine issue. The nested publication index must resolve to a `MAG` publication index. The mutation
can also upload a cover image and create initial transcriptions for the issue.

### `addBookEdition(bookInput: BookInput!)`

Creates or updates a book edition. The nested publication index must resolve to a `BOOK` publication index. The mutation
can also upload a cover image and create initial transcriptions for the edition.

### `addPubCoverImage(imgInput: PubCoverImageInput!)`

Stores or replaces the cover image for an existing publication. The returned publication contains a relative cover URL in
its details object.

### `addTranscription(pubId: Long!, transcriptionInput: TranscriptionInput!)`

Creates or updates a transcription for an existing publication. The `pubId` must refer to an existing publication. The
transcription is resolved by publication and song.

Nested input can create, update, or reuse:

- the song
- direct song artists and their roles
- album track data
- album metadata
- album primary artist
- transcription transcribers

When `transcribers` is omitted during an update, existing transcriber associations are left unchanged. When supplied as an
empty array, existing transcriber associations are removed.

## File Uploads

The API supports file uploads through the GraphQL multipart request specification. Upload-capable fields are:

- `MagazineIssueInput.cover`
- `BookEditionInput.cover`
- `PubCoverImageInput.cover`
- `AlbumData.coverArt`
- `TranscriptionInput.file`

Supplying an upload stores or replaces the corresponding resource and returns a relative URL in the appropriate response
field:

- publication cover image: `details.cover`
- album art: `albumArtUrl`
- transcription file: `url`

## Ordering

Ordering inputs use `OrderByDirection` and `OrderByNulls`.

When multiple order specifications are supplied, they are applied in the order provided. Where documented, the backend
adds a stable id tiebreaker after supplied order criteria.

## Dates

Date values use ISO-8601 calendar date strings, for example `1987-07-21`. Date range filters are inclusive.

## Error Handling

Validation and invalid input errors are returned as GraphQL errors. Unknown IDs supplied in mutation input are treated as
invalid input. File resource requests that do not map to an existing stored resource are returned as HTTP `404` responses.
