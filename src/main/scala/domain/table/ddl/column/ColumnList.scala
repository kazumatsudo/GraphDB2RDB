package domain.table.ddl.column

case class ColumnList(private val value: Map[ColumnName, ColumnType])
    extends AnyVal {

  /** merges columnList in two columns into one
    *
    * @param target
    *   target columnList
    * @return
    *   merged column list
    */
  def merge(target: ColumnList): ColumnList =
    ColumnList {
      value.foldLeft(target.value) { (accumulator, currentValue) =>
        val (columnName, columnType) = currentValue

        accumulator.updated(
          columnName,
          accumulator
            .get(columnName)
            .map(ColumnType.merge(columnType, _))
            .getOrElse(columnType)
        )
      }
    }

  def toSqlSentence: String =
    value
      .map { case (columnName, columnType) =>
        s"${columnName.toSqlSentence} ${columnType.toSqlSentence}"
      }
      .mkString(", ")
}
