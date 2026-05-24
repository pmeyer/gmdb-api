# Container Image

Release builds publish the API image to GitHub Container Registry as:

- `ghcr.io/pmeyer/gmdb-api:<version>`
- `ghcr.io/pmeyer/gmdb-api:latest`

The image runs the packaged Spring Boot application on container port `8080` by default.

## Run a Published Image

To run a published image with Docker Compose:

```shell
export GMDB_API_IMAGE_TAG='latest'
export GMDB_URL='r2dbc:postgresql://host.docker.internal:1970/gmdb'
export GMDB_DATABASE_USERNAME='gmdb_app_user'
export GMDB_APP_USER_PASSWORD='...'
export GMDB_FILE_REPO_ROOT="$HOME/gmdb/file-repo"
export GMDB_API_PORT='8080'
docker compose up
```

## Build Locally

When building the image locally, package the JAR first and expose the packaged project version to Compose:

```shell
mvn clean -DskipTests package
export GMDB_API_VERSION="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)"
docker compose up --build
```

## Runtime Configuration

Runtime configuration is supplied through environment variables:

- `FILE_SERVICE_ROOT`: In-container file repository root. The image defaults this to `/var/lib/gmdb/file-repo`, and
  `docker-compose.yml` uses that value.
- `GMDB_FILE_REPO_ROOT`: Host path mounted by `docker-compose.yml` to `FILE_SERVICE_ROOT`.
- `GMDB_URL`: R2DBC PostgreSQL URL consumed by the application.
- `SPRING_R2DBC_MYBATIS_USERNAME`: Database username consumed directly by Spring Boot. The image and Compose default to
  `gmdb_app_user`.
- `GMDB_DATABASE_USERNAME`: Compose convenience variable mapped to `SPRING_R2DBC_MYBATIS_USERNAME`.
- `GMDB_APP_USER_PASSWORD`: Database password consumed by `application.yml`.
- `SERVER_PORT`: In-container web port. The image defaults this to `8080`.
- `GMDB_API_PORT`: Host port mapped by `docker-compose.yml` to the container web port, defaulting to `8080`.
- `GMDB_API_IMAGE_TAG`: Image tag used by `docker-compose.yml`, defaulting to `latest`.
- `GMDB_API_VERSION`: Local packaged JAR version used only when building the image with Docker Compose.
