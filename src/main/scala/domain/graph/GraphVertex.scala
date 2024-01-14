package domain.graph

import com.typesafe.config.ConfigFactory
import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import gremlin.scala._

case class GraphVertex(private val value: Vertex) {

  private val config = ConfigFactory.load()

  // TODO: Set a more detailed table name
  private val tableName = TableName(config.getString("table_name_vertex"))
  private val columnNamePrefixProperty =
    config.getString("column_name_prefix_property")
  private val columnNamePrefixLabel =
    config.getString("column_name_prefix_label")

  /** convert to Database Table Information
    *
    * @return
    *   Database Table Information
    */
  def toDdl: TableList =
    TableList {
      val idColumn = Map(
        ColumnName(config.getString("column_name_vertex_id")) -> ColumnType
          .apply(value.id())
      )
      val propertyColumn = value.valueMap.map { case (key, value) =>
        ColumnName(s"$columnNamePrefixProperty$key") -> ColumnType.apply(
          value
        )
      }
      val labelColumn = Map(
        ColumnName(
          s"$columnNamePrefixLabel${value.label()}"
        ) -> ColumnType.apply(true)
      )

      Map(tableName -> ColumnList(idColumn ++ propertyColumn ++ labelColumn))
    }

  def toDml: RecordList = {
    val id = value.id()

    val propertyColumnList = value.valueMap.map { case (columnName, value) =>
      (s"$columnNamePrefixProperty$columnName", value)
    }
    val labelColumn = s"$columnNamePrefixLabel${value.label()}"

    val recordValue = Map(
      (config.getString("column_name_vertex_id"), id)
    ) ++ propertyColumnList ++ Map((labelColumn, true))

    RecordList(
      Map((RecordKey(tableName, RecordId(id)), RecordValue(recordValue)))
    )
  }
}
