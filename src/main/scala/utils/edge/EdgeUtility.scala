package utils.edge

import domain.table.{TableList, TableName}
import domain.table.column.{ColumnList, ColumnName, ColumnType}
import gremlin.scala.Edge

import scala.jdk.CollectionConverters.SetHasAsScala

object EdgeUtility {

  // TODO: Set a more detailed table name
  private val tableName = TableName("edge")

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

      Map(tableName -> ColumnList(inVColumn ++ outVColumn ++ column))
    }

  def toSqlSentence(edge: Edge): String = {
    // TODO: pull request for gremlin-scala
    val (columnList, valueList) = edge.keys().asScala.map { key => (key, edge.value(key)) }.unzip
    s"INSERT INTO ${tableName.toSqlSentence} (in_v_id, out_v_id, ${columnList.mkString(",")}) VALUES (${edge.inVertex().id()}, ${edge.outVertex().id()}, ${valueList.mkString(", ")});"
  }
}
