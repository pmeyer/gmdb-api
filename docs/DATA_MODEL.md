# Data Model

Metadata is stored about transcribed songs and the publications in which they appear in a relational database.
Generally, there is a need to store the following information:

- publication in which a transcription appears:
  - magazine/book title
  - ISBN or another identifier if applicable
  - publication date
  - issue number
  - volume number
  - edition
- song artist and their role or roles on the transcribed version of the song
- transcriber
- song title
- the page the transcription appears on in the publication
- album that song as transcribed appears on, if any

## Types

- `artist_type` is a custom enumerated type of either `BAND` or `PERSON`.
- `pub_type` is a custom enumerated type of either `MAGAZINE` or `BOOK`.

## Entities

- `pub_idx` is a publication index containing a publication name, publication type, and optional serial number. For books
  this is typically an ISBN.
- `pub` is an instance of a publication index that has a publication date and additional details such as edition for
  books, or volume, issue, and issue name for magazines. Details that are required or optional are business rules, not
  database model constraints. A `pub` must have one and only one `pub_idx`.
- `artist` has a name and type that are unique in combination.
- `album` has a title and additional details such as release date. An `album` optionally has a primary `artist`.
- `song` has a title and additional details such as track number on an album. A `song` optionally appears on an `album`.
- `song_artist` associates a `song` with one or more `artist` rows and stores each artist's song roles. Roles include
  `WORDS_BY`, `MUSIC_BY`, and `PERFORMED_BY`.
- `transcriber` has a unique name. A `transcriber` may have transcribed many transcriptions.
- `transcription` represents a `song` appearing in a `pub` and includes additional details such as the starting page and
  an optional URL to the file containing the transcription.
- `transcription_transcriber` associates a `transcription` with one or more `transcriber` rows.
