# GraphDB2RDB

generate RDB DDL/DML from GraphDB (Tinkerpop).

![Latest GitHub release](https://img.shields.io/github/release/kazumatsudo/GraphDB2RDB.svg)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![codecov](https://codecov.io/github/kazumatsudo/GraphDB2RDB/graph/badge.svg?token=9JFEL8HKQR)](https://codecov.io/github/kazumatsudo/GraphDB2RDB)
![workflow](https://github.com/kazumatsudo/GraphDB2RDB/actions/workflows/scala.yml/badge.svg)

## Demo

![demo2](https://github.com/kazumatsudo/GraphDB2RDB/assets/25892776/0f5e7f43-9f02-4a85-b376-e1a1acf163d9)

## Features

- this script generate 4 SQL files.
    1. [DDL] CREATE TABLE "vertex_${vertex label}"
        - the columns are as follows.
            - id
            - property_${all property keys}
    2. [DDL] CREATE TABLE "edge_${edge label}_from_${out vertex label}_to_${in vertex label}"
        - the columns are as follows.
           - id  
           - id_in_v (= vertex_{vertex label}.id)
           - id_out_v (= vertex_{vertex label}.id)
           - property_${all property keys}
    3. [DML] INSERT INTO "vertex_${vertex label}"
    4. [DML] INSERT INTO "edge_${edge label}_from_${out vertex label}_to_${in vertex label}"

## Prerequisites

- Java 11
- Scala 2.13
- sbt 1.9.8

## Installation

1. checkout this repository
    ```shell
    gh repo clone kazumatsudo/GraphDB2RDB
    ```

## Usage

1. launch your GraphDB Server
    ```shell
    docker compose up -d
    ```
2. select analysis method  
    choose analysis method to search Vertex/Edge algorithm.

    - "by_exhaustive_search" (default)   
    - "using_specific_key_list"  

    please see following passage if you want to change method. 
    ```shell
    % export ANALYSIS_METHOD=by_exhaustive_search
    ``` 
3. execute the script by sbt
    ```shell
    sbt run
    ```
4. generate SQL files
    - sql/ddl_edge.sql
    - sql/ddl_vertex.sql
    - sql/insert_edge.sql
    - sql/insert_vertex.sql

### Analysis method

|               | by_exhaustive_search                                                                                                      | using_specific_key_list                                                                                                                  |
|---------------|---------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| summary       | analyze all Vertices and Edges.<br/>[Sequence Diagram](https://github.com/kazumatsudo/GraphDB2RDB/wiki#byexaustivesearch) | analyze specific vertices searched by keys.<br/>[Sequence Diagram](https://github.com/kazumatsudo/GraphDB2RDB/wiki#usingspecifickeylist) |
| pros          | no advance preparation required                                                                                           | faster than by_exhaustive_search (enable to search by index)                                                                             |
| cons          | inefficient (execute full search all vertices and edges count times)                                                      | required to prepare search condition                                                                                                     |

#### How to choose

##### by_exhaustive_search

no advance preparation required because it's selected by default.

##### using_specific_key_list

1. set environment variable "ANALYSIS_METHOD" to "using_specific_key_list"
    ```shell
    % export ANALYSIS_METHOD=using_specific_key_list
    ``` 
2. set target vertex label, property key, and its values in [using_key_list_file.json](https://github.com/kazumatsudo/GraphDB2RDB/blob/e163bdcfb7a50d5275eecfb722ac172214dd8a98/src/main/resources/using_key_list_file.json)
    - json schema: [using_key_list_file_schema.json](https://github.com/kazumatsudo/GraphDB2RDB/blob/e163bdcfb7a50d5275eecfb722ac172214dd8a98/src/main/resources/using_key_list_file_schema.json)

## Settings

You can define following settings as you like.

- GraphDB connection settings
- output SQL directory

The settings you can be changed are summarized in [application.conf](./src/main/resources/application.conf).

## Code of Conduct

- [CODE_OF_CONDUCT](CODE_OF_CONDUCT.md)

## Contributing

- [CONTRIBUTING](CONTRIBUTING.md)

## License

- [LICENSE](LICENSE)

## Authors

- [kazumatsudo](https://github.com/kazumatsudo)

## Contributors

- xxx