package domain.graph

import com.typesafe.config.ConfigFactory
import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType, ColumnTypeBoolean, ColumnTypeDouble, ColumnTypeInt, ColumnTypeLong, ColumnTypeString, ColumnTypeUnknown}
import domain.table.ddl.{TableList, TableName}
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

  def toDml: String = {
    val (propertyColumnList, propertyValueList) = value.valueMap.unzip
    val valueListForSql = propertyValueList.map { value =>
      ColumnType.apply(value) match {
        case ColumnTypeBoolean   => value
        case ColumnTypeInt(_)    => value
        case ColumnTypeLong(_)   => value
        case ColumnTypeDouble(_) => value
        case ColumnTypeString(_) => s"\"$value\""
        case ColumnTypeUnknown   => s"\"$value\""
      }
    }

    val labelColumn = s"$columnNamePrefixLabel${value.label()}"

    val (keys, values) = (
      Seq(
        (config.getString("column_name_vertex_id"), value.id())
      ) ++ propertyColumnList
        .map(columnName => s"$columnNamePrefixProperty$columnName")
        .zip(valueListForSql) ++ Seq(
        (labelColumn, true)
      )
    ).unzip

    s"INSERT INTO ${tableName.toSqlSentence} (${keys.mkString(", ")}) VALUES (${values.mkString(", ")});"
  }
}
