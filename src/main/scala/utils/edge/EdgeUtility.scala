package utils.edge

import domain.table.{TableList, TableName}
import domain.table.column.{ColumnList, ColumnName, ColumnType}
import gremlin.scala.Edge

import scala.jdk.CollectionConverters.SetHasAsScala

object EdgeUtility {

  /** convert to Database Table Information
   *
   * @param edge [[Edge]]
   * @return Database Table Information
   */
  def toTableList(edge: Edge): TableList =
    TableList {
      val inVColumn = Map(ColumnName("in_v_id") -> ColumnType.apply(edge.inVertex().id()))
      val outVColumn = Map(ColumnName("out_v_id") -> ColumnType.apply(edge.outVertex().id()))

      // TODO: pull request for gremlin-scala
      val column = edge.keys().asScala.map { key =>
        ColumnName(key) -> ColumnType.apply(edge.value[Any](key))
      }.toMap
      val columnList = ColumnList(inVColumn ++ outVColumn ++ column)

      // TODO: Set a more detailed table name
      val tableName = TableName("edge")
      Map(tableName -> columnList)
    }
}
