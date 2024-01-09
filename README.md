# GraphDB2RDB

generate RDB (MySQL) DDL and INSERT sentence from GraphDB (Tinkerpop).

![Latest GitHub release](https://img.shields.io/github/release/kazumatsudo/GraphDB2RDB.svg)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![codecov](https://codecov.io/github/kazumatsudo/GraphDB2RDB/graph/badge.svg?token=9JFEL8HKQR)](https://codecov.io/github/kazumatsudo/GraphDB2RDB)
![workflow](https://github.com/kazumatsudo/GraphDB2RDB/actions/workflows/scala.yml/badge.svg)

## Demo

![demo](https://github.com/kazumatsudo/GraphDB2RDB/assets/25892776/fd9a5a56-c099-4088-91e7-31f4702e1ff1)

## Features

- this script generate 4 SQL files.
    1. [DDL] CREATE TABLE "vertex"
        - analyze all vertices and define columns
        - the columns are as follows.
            - id
            - all propertyKeys
            - label
    2. [DDL] CREATE TABLE "edge"
        - analyze all edges and define columns
        - the columns are as follows.
           - in_v_id (= vertex.id)
           - out_v_id (= vertex.id)
           - all propertyKeys
           - label
    3. [DML] INSERT INTO "vertex"
        - analyze all vertices and generate
    4. [DML] INSERT INTO "edge"
        - analyze all edges and define columns

## Prerequisites

- Java 11
- Scala 2.13
- sbt 1.9.8

## installation

1. checkout this repository
    ```shell
    gh repo clone kazumatsudo/GraphDB2RDB
    ```

## Usage

1. launch your GraphDB Server
    ```shell
    docker compose up -d
    ```
2. execute the script by sbt
    ```shell
    sbt run
    ```
3. generate SQL files
    - sql/ddl_edge.sql
    - sql/ddl_vertex.sql
    - sql/insert_edge.sql
    - sql/insert_vertex.sql

## Settings

You can define following settings as you like.

- GraphDB connection settings
- output SQL directory

The settings you can be changed are summarized in [application.conf](./src/main/resources/application.conf).

## Test

```shell
sbt test
```

## Contribute

### Pull Request Process

1. Ensure all test are passed.
2. Update the README.md when you add new features.
3. If you would like, please list your account in the Contributors field of README.md.
4. After the author submits an approve, the pull request is merged at the author's discretion.

## Authors

- [kazumatsudo](https://github.com/kazumatsudo)

## Contributors

- xxx

## License

[LICENSE](LICENSE)
