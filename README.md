# GraphDB2RDB

## Overview

Tools to enable GraphDB data to be handled in RDB

## installation

TODO  
To be described after first release

## Usage

TODO  
To be described after first release

## License

[LICENSE](LICENSE)

## Contribute

### command

#### GraphDB (JanusGraph/Berkeley DB)

```shell
# launch GraphDB
docker compose up -d

# connect to Gremlin Server
# https://docs.janusgraph.org/v0.3/basics/server/#connecting-to-gremlin-server
docker compose exec janusgraph ./bin/gremlin.sh
gremlin> :remote connect tinkerpop.server conf/remote.yaml
gremlin> :remote console
```

```shell
# execute the script by sbt
sbt run
```
