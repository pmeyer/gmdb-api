# Guitar Transcription Catalog Backend

A personal development project designed to maintain a database cataloging my decades-spanning collection of 
guitar magazines and song books containing guitar tablature song transcriptions. A UI will be provided to:
- allow users to upload files to the system and associate them with songs and publications
- allow users to browse and search the catalog and view song transcriptions

## Data Model

Metadata will be stored about transcribed songs and the publications in which they appear in a relational database.
Generally, there is a need to store the following information:
- publication in which a transcription appears:
  - magazine/book title
  - ISBN (or another identifier if applicable)
  - publication date
  - issue number
  - volume number
  - edition
- song artist and their role(s) on the transcribed version of the song (performed by, words by, music by)
- transcriber
- song title
- the page it appears on in the publication
- album that song as transcribed appears on (if any)

### Types
- `artist_type` - is a custom enumerated type of either 'BAND' or 'PERSON'
- `pub_type` - is a custom enumerated type of either 'MAGAZINE' or 'BOOK'
### Entities
- `pub_idx` - is a publication that is an index of the publications name, type (of `pub_type`), and optionally a 
serial number (for magazines/books, this is an ISBN).
- `pub` - is an "instance" of a publication (`pub_idx`) that has a publication date and additional "details" 
such as edition (books) or volume, issue and issue name (magazines). Details that are required or optional are a 
business rule constraint not enforced in the database model.  A `pub` must have one and only one `pub_idx`.
- `artist` - has a name and a type (of `artist_type`) that combined are unique.  
- `album` - has a title and additional "details" such as release date. Details that are required or optional are a 
business rule constraint not enforced in the database model.  An `album` optionally has a primary `artist`.
- `song` - has a title and additional "details" such as track number on an album. Details that are required or optional 
are a business rule constraint not enforced in the database model. A `song` optionally appears on an `album`. 
  - A `song`optionally has one or more `artist` where each `artist` has one or more roles on the `song`. An artist's 
  roles on a song are one or more of "WORDS_BY", "MUSIC_BY", "PERFORMED_BY" (stored as an array of strings).
  - An `artist` may have one or more roles on many different songs. 
- `transcriber` - has a unique name. A `transcriber` may have transcribed many `Transcriptions`.
- `transcription` - is a representation of a `song` appearing in an instance of a publication (`pub`) and has additional
details, such as the page number on which the transcription starts in the publication and a url to the file containing 
the transcription.
  - A `transcription` may have one or more `transcriber`s.
  - A `transcriber` may have transcribed many `transcription`s.

## Blob Storage
Aside from song transcription and publication metadata, the following files will be stored:
- An image of the magazine/book cover
- A PDF (or image) of each song transcriptionA
- Album art of the album that a song (as transcribed) appears on (if any)

"Blobs" or files associated with songs and publications will be stored in an object store.  URL's or some sort of unique
identifier that can be used to retrieve these files will be stored in the database along with the metadata.

The blob files are associated with stored data as follows:
- A song transcription file is directly associated with a `transcription` in the database.
- A publication cover image is associated with an issue or edition of a publication (`pub`) in the database.
- Album art is associated with an `album` in the database.

## API
A GraphQL API will be provided that will allow retrieval of data from the database.  Different perspectives of 
searching the data will be available:
- Search/browse transcriptions by publication
- Search/browse transcriptions by song
- Search/browse transcriptions by artist

The GraphQL API will also provide the ability to add new transcriptions and publications to the database.  The API will
support uploading files to the database as part of a GraphQL mutation using a custom type, handler, and protocol 
conforming to the [GraphQL multipart request specification](https://github.com/jaydenseric/graphql-multipart-request-spec).

## Mutations, Metadata and Blob Storage
Mutations supporting the addition or update of metadata to the database along with storage of associated files will need
to:
- Store a URL to the file in the database along with the metadata
- Should storage of the file fail or storage of the metadata fails, the mutation should fail.

### Abstract Object Storage Service
An abstract object storage service will be defined to support the storage of files/blobs.  This service should support a
basic API that allows CRUD of an object at a specific URL.  Suggestions for the API include:
- `PUT` - create or update an object at a specific URL
- `GET` - retrieve an object at a specific URL
- `DELETE` - delete an object at a specific URL
All operations should be idempotent.

### Metadata and Blob Association
An ID to correlate the metadata and file will be needed to form a URL to support retrieval of the correct file 
referenced by the metadata.  Options for this value might be database IDs or other attributes of the associated 
entities, or a hash of the file contents.  The attribute metadata and the hash are poor candidates as they are both
subject to change — for example, a misspelled name or title may be corrected and thus affect the associated file URL; 
likewise a hash of the file contents may change if the file is updated. 

Using primary keys as IDs is an option as uniqueness of the ID is only required within the scope of the entity with 
which the blob is associated (presently, pub-cover, song-transcription, and album-art).  However, future expansion of 
blob types and possibly associating them not only with entities but possibly with entity relationships rules out this 
approach, especially in the case of association with entity relationships.  Relationships between entities may change
over time as they are effectively permutations of entity keys.  

The preferred approach will be to generate a separate UUID for each blob and use that as the ID, storing it along with 
the metadata.  Using a generated UUID allows the ID to be treated as intrinsically unique data identifying a blob.  
Exporting data from the database and reloading it into a new database will not affect this value, whereas using primary
keys might result in different values should the database leverage generated primary keys.

### Transactional Storage
Orphaned and/or disconnected blobs or data should be avoided as it affects overall data integrity and can lead to 
increased storage costs.  The preferred approach is to store all blobs and associated metadata in a single transaction, 
as described above. However, file storage is generally not supported as a transactional operation, especially if the 
object storage service is implemented as a remote service (such as S3).  

#### Who Generates the UUID?
The application layer can certainly generate the UUID for the blob.  This UUID can then augment the metadata being 
stored during the execution of a mutation as well as for storing the blob.  However, in the case of an _update_ 
operation, the application layer will need to get the existing UUID for the blob rather than generating a new one.

Given that in some scenarios the application will need to _get_ the UUID rather than generate one, it makes more sense
to have the database layer generate the UUID and return it to the application layer.  Database operations can 
effectively be implemented as "upserts" (insert or update) returning a UUID as required as part of the operation, 
whether it retrieves the existing UUID or generates a new one.  

The only issue with this approach is that the application should indicate during an upsert if it needs a UUID generated
if it doesn't exist.  In the event that metadata is being stored but a blob is not, a UUID should not be generated if it
does not exist.  After all, a magazine issue, for example, is not _required_ to have an image of its cover stored as
well.  This is a minor problem that can be solved based on input provided in the form of a filename or flag to represent
the blob to be stored.  The database won't store this value, but if present, it will return the UUID (whether retrieved
or generated) as part of the upserted data.

#### Achieving "Good Enough" Consistency
With the database being responsible for generating and/or retrieving a UUID for associated blobs, the general approach
to achieving "good enough" consistency or data integrity is to leverage database transactions along with compensating 
actions:
- When a mutation is executed, a new database transaction should be started.  
- Required modifying database statements are executed within the transaction, and any UUIDs required are returned as
part of the row data returned from the statement executions. 
- If any of the database statements fail, the transaction will be rolled back and no attempt will be made to store 
blobs.
- In the event of successful database statement execution, blobs will be stored using the abstract object storage 
service using the UUIDs returned from the database operations.  
- If any of the blob storage operations fail, the transaction will be rolled back and any blobs that were successfully 
stored will be deleted.

## Technology Stack
- The application layer will be written in Java (23) using Spring Boot (3.5.x).
- The reactive web layer will be implemented using Spring Boot Starter WebFlux.
- The GraphQL layer will be implemented using Spring Boot Starter GraphQL.
- Support for file updates will leverage graphql-multipart-fileupload (Yellow Moon Software).
- The database will be accessed using MyBatis; however, given the use of Java reactor, the mybatis-r2dbc project will
provide both MyBatis and R2DBC support.
- PostgreSQL will be used as the database engine.
- It is planned that the object storage service will be implemented using AWS S3; however, the implementation will be
abstracted away from the application layer, allowing it to be swapped out for another implementation such as the file 
system or MinIO.

## Maven Package Access
Test-scoped database migration resources are provided by the private `gmdb-liquibase` Maven package hosted in GitHub
Packages. Maven resolves that artifact from `https://maven.pkg.github.com/pmeyer/gmdb-liquibase` using the repository
id `github`.

Configure credentials in `~/.m2/settings.xml` before running a build that needs to resolve the package:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>${env.GITHUB_ACTOR}</username>
      <password>${env.GITHUB_TOKEN}</password>
    </server>
  </servers>
</settings>
```

For local builds, set `GITHUB_ACTOR` to the GitHub username associated with the PAT and set `GITHUB_TOKEN` to a PAT
with package read access.
In GitHub Actions, prefer `GITHUB_ACTOR` and `GITHUB_TOKEN` or a repository secret with equivalent package read access.

## Integration Tests
Integration tests live under `src/integrationTest` and run during `mvn verify` through Maven Failsafe. Database-backed
fixtures use Testcontainers to build the PostgreSQL image from the `gmdb-liquibase` test resources artifact, start a
fresh container, apply the bootstrap changelog, and then apply the migration changelog before the Spring application
context starts.

Docker must be available when running integration tests.

## Container Image

Release builds publish the API image to GitHub Container Registry as `ghcr.io/pmeyer/gmdb-api:<version>` and
`ghcr.io/pmeyer/gmdb-api:latest`.

The image runs the packaged Spring Boot application on container port `8080` by default. To run a published image with
Docker Compose:

```shell
export GMDB_API_IMAGE_TAG='latest'
export GMDB_DATABASE_URL='r2dbc:postgresql://host.docker.internal:1970/gmdb'
export GMDB_DATABASE_USERNAME='gmdb_app_user'
export GMDB_APP_USER_PASSWORD='...'
export GMDB_FILE_REPO_ROOT="$HOME/gmdb/file-repo"
export GMDB_API_PORT='8080'
docker compose up
```

When building the image locally, package the JAR first and expose the packaged project version to Compose:

```shell
mvn clean -DskipTests package
export GMDB_API_VERSION="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)"
docker compose up --build
```

Runtime configuration is supplied through environment variables:

- `FILE_SERVICE_ROOT`: In-container file repository root. The image defaults this to `/var/lib/gmdb/file-repo`, and
  `docker-compose.yml` uses that value.
- `GMDB_FILE_REPO_ROOT`: Host path mounted by `docker-compose.yml` to `FILE_SERVICE_ROOT`.
- `SPRING_R2DBC_MYBATIS_R2DBC_URL`: R2DBC PostgreSQL URL consumed directly by Spring Boot.
- `GMDB_DATABASE_URL`: Compose convenience variable mapped to `SPRING_R2DBC_MYBATIS_R2DBC_URL`.
- `SPRING_R2DBC_MYBATIS_USERNAME`: Database username consumed directly by Spring Boot. The image and Compose default to
  `gmdb_app_user`.
- `GMDB_DATABASE_USERNAME`: Compose convenience variable mapped to `SPRING_R2DBC_MYBATIS_USERNAME`.
- `GMDB_APP_USER_PASSWORD`: Database password consumed by `application.yml`.
- `SERVER_PORT`: In-container web port. The image defaults this to `8080`.
- `GMDB_API_PORT`: Host port mapped by `docker-compose.yml` to the container web port, defaulting to `8080`.
- `GMDB_API_IMAGE_TAG`: Image tag used by `docker-compose.yml`, defaulting to `latest`.
- `GMDB_API_VERSION`: Local packaged JAR version used only when building the image with Docker Compose.
