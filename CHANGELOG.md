# Changelog

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
