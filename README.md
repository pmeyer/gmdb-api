# Guitar Transcription Catalog Backend
> Part of a personal development project designed to maintain a database cataloging my decades-spanning collection of 
> guitar magazines and song books containing guitar tablature song transcriptions. A UI will be provided to:
> - allow users to upload files to the system and associate them with songs and publications
> - allow users to browse and search the catalog and view song transcriptions
>
> See the following repositories that are part of the entire system:
> - [gmdb-liquibase](https://github.com/pmeyer/gmdb-liquibase)
> - [gmdb-ui](https://github.com/pmeyer/gmdb-ui)

The backend exposes a GraphQL API for searching the catalog and mutating publication, song, album, transcription, artist,
transcriber, and resource metadata. It supports file uploads through the GraphQL multipart request specification.

See the [GraphQL API Guide](GRAPHQL_API.md) for operation, input, and response semantics.

## Copyright and Third-Party Materials

This repository contains source code, database schema/migration logic, documentation, and development tooling for a
personal cataloging application. It **does not include** copyrighted guitar tablature, transcription scans, magazine or book
scans, album artwork, audio files, or other third-party media.

The application is designed to manage metadata and privately supplied resource files for a user's own catalog. Any
copyrighted materials referenced by metadata in the application remain the property of their respective copyright
holders. Users are responsible for ensuring that any files they add to a running instance of the application are used
in accordance with applicable law and any rights or licenses they hold.

Any license applied to this repository covers only the software and documentation in this repository. It does not grant
rights to any third-party music, publications, artwork, transcriptions, tablature, or other copyrighted content.

## Documentation

- [GraphQL API Guide](GRAPHQL_API.md): query criteria, mutation upsert semantics, uploads, dates, ordering, and errors.
- [Data Model](docs/DATA_MODEL.md): relational entities and their catalog relationships.
- [Metadata and Resource Storage](docs/STORAGE.md): resource IDs, file storage, and transactional consistency model.
- [Development](docs/DEVELOPMENT.md): technology stack, Maven package credentials, and test execution.
- [Container Image](docs/CONTAINER.md): published image, Docker Compose usage, and runtime configuration.
