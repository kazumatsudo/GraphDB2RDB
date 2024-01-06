package utils

import domain.table.column.{ColumnLength, ColumnList, ColumnName, ColumnTypeBoolean, ColumnTypeDouble, ColumnTypeInt, ColumnTypeString, ColumnTypeUnknown}
import gremlin.scala._

object VertexUtility {

  /** convert to Database Column Information
   *
   * @param vertex [[Vertex]]
   * @return Database Column Information
   */
  def toColumnList(vertex: Vertex): ColumnList =
    ColumnList(vertex.valueMap.map { case (key, value) =>
      val columnName = ColumnName(key)
      val columnType = value match {
        case valueString: String => ColumnTypeString(ColumnLength(valueString.length))
        case valueInt: Int => ColumnTypeInt(ColumnLength(valueInt.toString.length))
        case valueDouble: Double => ColumnTypeDouble(ColumnLength(valueDouble.toString.replaceAll("0*$", "").length))
        case _: Boolean => ColumnTypeBoolean
        case _ => ColumnTypeUnknown // TODO: classify the type in detail
      }

      columnName -> columnType
    })
}
