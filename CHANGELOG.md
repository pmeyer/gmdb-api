# Changelog

## [1.1.0](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.6...gmdb-api-v1.1.0) (2026-05-24)


### Features

* publish API container images ([0317a46](https://github.com/pmeyer/gmdb-api/commit/0317a466650f123beb62e38e4277cc5a43483c72))
* publish API container images ([dad0831](https://github.com/pmeyer/gmdb-api/commit/dad083138d1ad08f3178219301b48afc810c870c))

## [1.0.6](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.5...gmdb-api-v1.0.6) (2026-05-24)


### Bug Fixes

* return not found for missing file resources ([a344f58](https://github.com/pmeyer/gmdb-api/commit/a344f582d4579b04030635232b912aff8135018e))
* validate album references ([27cee1f](https://github.com/pmeyer/gmdb-api/commit/27cee1ffbe3bb65ce3c29b65b6bb0910aa149a92))
* validate artist references ([8caf604](https://github.com/pmeyer/gmdb-api/commit/8caf604f80dc48e8f655344ae5620b2c7c1e9984))
* validate publication cover image references ([78109a3](https://github.com/pmeyer/gmdb-api/commit/78109a35293a45698a8bf952a1993936e01a5072))
* validate publication index references ([635c221](https://github.com/pmeyer/gmdb-api/commit/635c221c60e6fd3d86c360a8a92cdacba952ad79))
* validate song references ([a9337f7](https://github.com/pmeyer/gmdb-api/commit/a9337f796a2fc3769a0619f8ab62d9a53a788590))
* validate transcriber references ([03c3d8b](https://github.com/pmeyer/gmdb-api/commit/03c3d8b1929d59cbc90a06bb19e1622aee4411a7))
* validate transcription publication references ([2fdb0a7](https://github.com/pmeyer/gmdb-api/commit/2fdb0a7036110897ded8e6936a64c9750499c040))

## [1.0.5](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.4...gmdb-api-v1.0.5) (2026-05-23)


### Bug Fixes

* avoid sending empty lists to upsertSongArtists mapper function ([#30](https://github.com/pmeyer/gmdb-api/issues/30)) ([b41fb29](https://github.com/pmeyer/gmdb-api/commit/b41fb2937d806417f702ffda3627351ae91bbddc))
* date serialization in JsonbTypeHandlerAdapter ([d36925e](https://github.com/pmeyer/gmdb-api/commit/d36925ecd69fe07cde4e6ffe89e558bee81d42d8))
* support mutation data upsert scenarios ([cc0b711](https://github.com/pmeyer/gmdb-api/commit/cc0b711df7ed5e97871fc171f4a819f5fad8dd52))
* support transcription transcriber mutation permutations ([cb22135](https://github.com/pmeyer/gmdb-api/commit/cb22135cdb48b75c1e32e20edfeda72f4da27668))
* update schema and annotate entry point with `@NonNull` to properly reflect semantic behavior ([4b0e849](https://github.com/pmeyer/gmdb-api/commit/4b0e849918bacdd1716799a5f7becb86ce61e903))

## [1.0.4](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.3...gmdb-api-v1.0.4) (2026-05-19)


### Bug Fixes

* map transcription resources from details ([79889ee](https://github.com/pmeyer/gmdb-api/commit/79889eec1c7e7794963d0b918c0ed38f3aeb64da))
* map transcription resources from details ([54c9b0c](https://github.com/pmeyer/gmdb-api/commit/54c9b0c7862dc7a4cc92ad3c05c0e737f3583c0f))

## [1.0.3](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.2...gmdb-api-v1.0.3) (2026-05-18)


### Bug Fixes

* added new GraphQL type for publications returned for transcriptions returned for songs in the song search API ([238f8c2](https://github.com/pmeyer/gmdb-api/commit/238f8c21ccfb76a811ce87f88c184a4bf325fcc9))
* properly cast artist type to criteria parameter to custom `artist_type` ([bba2d1a](https://github.com/pmeyer/gmdb-api/commit/bba2d1a2102f883f28e8ef3eb25c1ea2af18b4b4))
* support song search by album name ([7449828](https://github.com/pmeyer/gmdb-api/commit/744982885d02daf5a75e861c882c0f87e72c714a))
* support song search by artist criteria ([f0b146c](https://github.com/pmeyer/gmdb-api/commit/f0b146c6d6071c27a5ebbe23d9bfb487e5def37d))
* support song search by artist name ([9eb375e](https://github.com/pmeyer/gmdb-api/commit/9eb375e6348c35f5dc8beaa71aced8c0b4d1a327))
* support song search by publication and album ids ([7f531fb](https://github.com/pmeyer/gmdb-api/commit/7f531fb985d4dbf8022519cb4e4180f3020d0fcd))
* support song search by publication name ([020acce](https://github.com/pmeyer/gmdb-api/commit/020accec0655d054bbffdad7db3d1ac609b954e1))


### Documentation

* describe graphql query criteria ([59ba6ba](https://github.com/pmeyer/gmdb-api/commit/59ba6bad182a08b5dd6185ef2c8304b0e94e5db1))
* describe graphql query criteria ([fd3d04c](https://github.com/pmeyer/gmdb-api/commit/fd3d04c2933a4c5475575d7159e90e1338a7bfe0))

## [1.0.2](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.1...gmdb-api-v1.0.2) (2026-05-15)


### Bug Fixes

* separate package credentials for release publish ([1a2472f](https://github.com/pmeyer/gmdb-api/commit/1a2472faa926729ee874d974db58af281b6cfeb2))

## [1.0.1](https://github.com/pmeyer/gmdb-api/compare/gmdb-api-v1.0.0...gmdb-api-v1.0.1) (2026-05-15)


### Bug Fixes

* address CVE-2024-25710 and CVE-2024-26308 ([f98d4a8](https://github.com/pmeyer/gmdb-api/commit/f98d4a84b843b330d5f67a2b0520503fdd3f89be))
* use workflow token for package publishing ([923bead](https://github.com/pmeyer/gmdb-api/commit/923bead85108a6c9a58d29a51b8748e9debd6a91))

## 1.0.0 (2026-05-15)


### ⚠ BREAKING CHANGES

* fix artist search
* fix album search
* added get operation for pub indices

### Features

* added get operation for pub indices ([591392d](https://github.com/pmeyer/gmdb-api/commit/591392d92bb3ebf5f71f9d14380bfb90348ea9b7))
* convert mybatis access to r2dbc-mybatis ([7bb00e6](https://github.com/pmeyer/gmdb-api/commit/7bb00e66ab71279de0dc4435a0b96a126f98dc91))
* initial implementation ([cc558a7](https://github.com/pmeyer/gmdb-api/commit/cc558a74834a0e661879bc2c3080d87c7eca948d))
* Initial implementation of full API operations ([1560946](https://github.com/pmeyer/gmdb-api/commit/1560946b56b8d1856296a3fd3cde03bafc3566e6))
* pom cleanup to better reflect correct project organization ([8f55211](https://github.com/pmeyer/gmdb-api/commit/8f5521134d2db31e55b2fa18fe76c7ffd274d668))
* refactor namespace and remove old test id and credentials ([1f86c4c](https://github.com/pmeyer/gmdb-api/commit/1f86c4cf1b3f08cb8d5f86688ce673725e4fb913))
* remove graphql multipart upload support and take dependency on new graphql multipart library ([c15c642](https://github.com/pmeyer/gmdb-api/commit/c15c642e13e6948933be732713fbfdbc8583c2ac))
* transition namespace from `com.yellowmoon` to `com.yellowmoonsoftware` ([7e10961](https://github.com/pmeyer/gmdb-api/commit/7e109618e5124fcf934b4a9df05e714c8b27f169))


### Bug Fixes

* add missing null check for order by spec on album search ([356d880](https://github.com/pmeyer/gmdb-api/commit/356d88007e4fd44f941b6f7882f87ac2cb08987e))
* add missing null check for order by spec on album search, removed unused imports ([356d880](https://github.com/pmeyer/gmdb-api/commit/356d88007e4fd44f941b6f7882f87ac2cb08987e))
* add resource ID field in publication details ([1d23f8a](https://github.com/pmeyer/gmdb-api/commit/1d23f8a2707c408fd512561efa02fcede315c3db))
* add sort order to artist search ([82f7e4c](https://github.com/pmeyer/gmdb-api/commit/82f7e4c3c305f141bc1f76093010438cc2b9359b))
* explicit type specification for pub_type criteria ([8f69327](https://github.com/pmeyer/gmdb-api/commit/8f69327f21df1b6c6b3da5a38893ef39603c281f))
* fix album search ([1b24902](https://github.com/pmeyer/gmdb-api/commit/1b249021502795560d2a2675116ca928ea9267a4))
* fix artist search ([f652415](https://github.com/pmeyer/gmdb-api/commit/f6524159a3f2ee984623a0626fc30bcf525a5d64))
* fix how album details are retrieved ([ab8820a](https://github.com/pmeyer/gmdb-api/commit/ab8820ac15bb78cc5ae900845f133e55f42dd880))
* fix jsonb array to set deserialization on DTO ([80123b1](https://github.com/pmeyer/gmdb-api/commit/80123b1dd136dff9f11b847e81073cf9e283dfb1))
* nullability on the result for getPubIndices ([9ec47bb](https://github.com/pmeyer/gmdb-api/commit/9ec47bbfdbe63505f61bf301093d842cfb034c4b))
* update nullability of details returned for a publication. ([123c417](https://github.com/pmeyer/gmdb-api/commit/123c4175570c46cbeae658ac80cd99d06157b05c))


### Documentation

* update readme ([eec6807](https://github.com/pmeyer/gmdb-api/commit/eec68077946b39472d73bc019056fc4eaac42dfb))
