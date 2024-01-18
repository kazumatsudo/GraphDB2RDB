package domain.graph

import com.typesafe.config.ConfigFactory
import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import gremlin.scala._

import scala.collection.parallel.CollectionConverters.ImmutableMapIsParallelizable
import scala.collection.parallel.immutable.ParMap

case class GraphVertex(private val value: Vertex) {

  private val config = ConfigFactory.load()

  private val tableName = TableName(
    s"${config.getString("table_name_vertex")}_${value.label()}"
  )
  private val columnNamePrefixProperty =
    config.getString("column_name_prefix_property")

  val id: AnyRef = value.id()

  /** convert to Database Table Information
    *
    * @return
    *   Database Table Information
    */
  def toDdl: TableList =
    TableList {
      val idColumn = ParMap(
        ColumnName(config.getString("column_name_vertex_id")) -> ColumnType
          .apply(value.id())
      )
      val propertyColumn = value.valueMap.map { case (key, value) =>
        ColumnName(s"$columnNamePrefixProperty$key") -> ColumnType.apply(
          value
        )
      }.par
      ParMap(tableName -> ColumnList(idColumn ++ propertyColumn))
    }

  def toDml: RecordList = {
    val propertyColumnList = value.valueMap.map { case (columnName, value) =>
      (s"$columnNamePrefixProperty$columnName", value)
    }.par

    val recordValue =
      ParMap(
        (config.getString("column_name_vertex_id"), id)
      ) ++ propertyColumnList

    RecordList(
      ParMap(RecordKey(tableName, RecordId(id)) -> RecordValue(recordValue))
    )
  }
}
