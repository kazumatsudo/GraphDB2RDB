package domain.table.ddl.attribute

import domain.table.ddl.column.ColumnName

final case class UniqueIndex(
    private val value: Map[UniqueIndexName, Set[ColumnName]]
) extends AnyVal {

  /** merges UniqueIndex in two indices list into one
    *
    * @param target
    *   target Index
    * @param checkUnique
    *   whether the index is unique.
    * @return
    *   merged indices
    */
  def merge(target: UniqueIndex, checkUnique: Boolean): UniqueIndex =
    UniqueIndex {
      if (checkUnique) {
        value.foldLeft(target.value) { (accumulator, currentValue) =>
          val (indexName, columnList) = currentValue

          accumulator.get(indexName) match {
            case Some(currentColumnList) if currentColumnList == columnList =>
              accumulator
            case Some(currentColumnList) =>
              throw new IllegalArgumentException(
                s"index name must be unique. index name: $indexName, detected values: $currentColumnList and $columnList"
              )
            case None => accumulator.updated(indexName, columnList)
          }
        }
      } else {
        value ++ target.value
      }
    }

  def toSqlSentenceSeq: Seq[String] = value.toSeq
    .sortBy { case (columnName, _) => columnName.toSqlSentence }
    .map { case (_, columnList) =>
      s"UNIQUE INDEX (${columnList.map(_.toSqlSentence).mkString(", ")})"
    }
}
