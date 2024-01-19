package utils

import com.typesafe.config.ConfigFactory

final case class AnalysysMethod(
    value: String,
    using_specific_key_list_filepath: String
)
final case class GraphDb(remoteGraphProperties: String)
final case class Sql(
    ddl_edge: String,
    ddl_vertex: String,
    dml_edge: String,
    dml_vertex: String,
    output_directory: String
)
final case class TableName(
    edge: String,
    private val vertex: String
)
final case class ColumnName(
    edgeId: String,
    edgeInVId: String,
    edgeOutVId: String,
    private val vertexId: String,
    prefixProperty: String
)
final case class Config(
    analysysMethod: AnalysysMethod,
    graphDb: GraphDb,
    sql: Sql,
    tableName: TableName,
    columnName: ColumnName
)

object Config {

  val default: Config = {
    val config = ConfigFactory.load()

    val analysysMethod = AnalysysMethod(
      config.getString("analysis_method"),
      config.getString("analysis_method_using_specific_key_list_filepath")
    )
    val graphDb = GraphDb(config.getString("graphdb_remote_graph_properties"))
    val sql = Sql(
      config.getString("sql_ddl_edge"),
      config.getString("sql_ddl_vertex"),
      config.getString("sql_dml_edge"),
      config.getString("sql_dml_vertex"),
      config.getString("sql_output_directory")
    )
    val tableName = TableName(
      config.getString("table_name_edge"),
      config.getString("table_name_vertex")
    )
    val columnName = ColumnName(
      config.getString("column_name_edge_id"),
      config.getString("column_name_edge_in_v_id"),
      config.getString("column_name_edge_out_v_id"),
      config.getString("column_name_vertex_id"),
      config.getString("column_name_prefix_property")
    )

    Config(analysysMethod, graphDb, sql, tableName, columnName)
  }
}
