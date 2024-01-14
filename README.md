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
2. select analysis method  
    choose analysis method to search Vertex/Edge algorithm.  
        - "by_exhaustive_search" (default)   
        - "using_specific_key_list"  
    please see following passage if you want to change method. 
    ```shell
    % ANALYSIS_METHOD="by_exhaustive_search"
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

### How to change analysis method

#### by_exhaustive_search

##### overview

analyze all Vertices and Edges.

- pros
    - no advance preparation required 
- cons
    - inefficient (execute full search all vertices and edges count times)

##### how to choose

no advance preparation required because it's selected by default.

#### using_specific_key_list

##### overview

analyze specific vertices searched by keys

- pros
    - faster than [[ByExhaustiveSearch]] (enable to search by index)
- cons
    - required to prepare search condition

##### how to choose

1. set environment variable "ANALYSIS_METHOD" to "using_specific_key_list"
    ```shell
    % ANALYSIS_METHOD="using_specific_key_list"
    ``` 
2. set target vertex label, property key, and its values in [using_key_list_file.json](https://github.com/kazumatsudo/GraphDB2RDB/blob/e163bdcfb7a50d5275eecfb722ac172214dd8a98/src/main/resources/using_key_list_file.json)
    - json schema: [using_key_list_file_schema.json](https://github.com/kazumatsudo/GraphDB2RDB/blob/e163bdcfb7a50d5275eecfb722ac172214dd8a98/src/main/resources/using_key_list_file_schema.json)
    
## Settings

You can define following settings as you like.

- GraphDB connection settings
- output SQL directory

The settings you can be changed are summarized in [application.conf](./src/main/resources/application.conf).

## design

The design (ex. structure, each diagrams, ...) is summarised in [wiki](https://github.com/kazumatsudo/GraphDB2RDB/wiki).  
this document contains how to contribute to this repository.

## Authors

- [kazumatsudo](https://github.com/kazumatsudo)

## Contributors

- xxx

## License

[LICENSE](LICENSE)
