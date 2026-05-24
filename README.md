# Guitar Transcription Catalog Backend

A personal development project designed to maintain a database cataloging my decades-spanning collection of 
guitar magazines and song books containing guitar tablature song transcriptions. A UI will be provided to:
- allow users to upload files to the system and associate them with songs and publications
- allow users to browse and search the catalog and view song transcriptions

## API
The backend exposes a GraphQL API for searching the catalog and mutating publication, song, album, transcription, artist,
transcriber, and resource metadata. It supports file uploads through the GraphQL multipart request specification.

See the [GraphQL API Guide](GRAPHQL_API.md) for operation, input, and response semantics.

## Documentation

- [GraphQL API Guide](GRAPHQL_API.md): query criteria, mutation upsert semantics, uploads, dates, ordering, and errors.
- [Data Model](docs/DATA_MODEL.md): relational entities and their catalog relationships.
- [Metadata and Resource Storage](docs/STORAGE.md): resource IDs, file storage, and transactional consistency model.
- [Development](docs/DEVELOPMENT.md): technology stack, Maven package credentials, and test execution.
- [Container Image](docs/CONTAINER.md): published image, Docker Compose usage, and runtime configuration.
