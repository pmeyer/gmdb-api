# Metadata and Resource Storage

> Note: This document describes application storage _behavior_ only. This repository **does not include** copyrighted resource
> files such as transcription scans, magazine/book scans, album artwork, or audio files. Any files added to a running 
> instance are the responsibility of that instance’s user/operator.

Aside from song transcription and publication metadata, the following files are stored:

- an image of the magazine or book cover
- a PDF or image of each song transcription
- album art of the album that a song, as transcribed, appears on

Files associated with songs and publications are stored through an abstract resource service. URLs or identifiers that
can be used to retrieve these files are stored in the database along with the metadata.

The files are associated with stored data as follows:

- A song transcription file is directly associated with a `transcription`.
- A publication cover image is associated with a publication instance, `pub`.
- Album art is associated with an `album`.

## Object Storage Service

An abstract object storage service supports storage of files or blobs. The service supports a basic API that allows CRUD
operations for an object at a specific URL:

- `PUT` creates or updates an object at a specific URL.
- `GET` retrieves an object at a specific URL.
- `DELETE` deletes an object at a specific URL.

All operations should be idempotent.

The initial implementation can use a file-system backed store. The API is designed so a different implementation, such as
S3 or MinIO, can be swapped in later.

## Metadata and Resource Association

An ID is needed to correlate metadata and files and to form URLs that retrieve the correct file referenced by metadata.
Potential identifiers include database IDs, natural entity attributes, or a hash of the file contents. Natural attributes
and hashes are poor candidates because they can change when metadata is corrected or when file contents are replaced.

Using primary keys as IDs is possible when uniqueness is only required within the scope of the associated entity, such as
publication cover, transcription file, and album art. However, future expansion of blob types and possible association
with entity relationships makes primary keys less flexible.

The preferred approach is to generate a separate UUID for each resource and store it with the metadata. A generated UUID
can be treated as intrinsically unique resource data. Exporting and reloading database data does not affect the value,
whereas generated primary keys might change.

## Transactional Storage

Orphaned or disconnected files and metadata should be avoided because they affect data integrity and can increase storage
costs. The preferred model is to store all metadata and associated files as part of one logical transaction.

File storage is generally not transactional, especially when implemented as a remote service. The application therefore
uses database transactions with compensating file actions:

- A mutation starts a database transaction.
- Required modifying database statements run inside the transaction, returning any resource UUIDs needed for file
  storage.
- If a database statement fails, the transaction rolls back and no file storage is attempted.
- If database statements succeed, files are stored using the resource UUIDs returned by the database operations.
- If file storage fails, the transaction rolls back and any successfully stored files are deleted.

## Resource UUID Generation

The database layer generates or retrieves resource UUIDs during upsert operations. This allows update operations to reuse
existing resource IDs rather than generating new ones in application code.

When metadata is stored without a file, a UUID should not be generated if it does not already exist. For example, a
magazine issue is not required to have a cover image. The application indicates whether a UUID is needed based on whether
file-related input, such as a filename or upload flag, is present.
