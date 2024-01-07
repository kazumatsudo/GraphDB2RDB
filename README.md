# GraphDB2RDB

generate RDB (MySQL) DDL and INSERT sentence from GraphDB (Tinkerpop).

## Demo

![demo](https://github.com/kazumatsudo/GraphDB2RDB/assets/25892776/fd9a5a56-c099-4088-91e7-31f4702e1ff1)

## Features

- this script generate 4 SQL files.
    1. [DDL] CREATE TABLE "vertex"
        - analyze all vertices and define columns
        - the columns are as follows.
            - id
            - all propertyKeys
    2. [DDL] CREATE TABLE "edge"
        - analyze all edges and define columns
        - the columns are as follows.
           - in_v_id (= vertex.id)
           - out_v_id (= vertex.id)
           - all propertyKeys
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
