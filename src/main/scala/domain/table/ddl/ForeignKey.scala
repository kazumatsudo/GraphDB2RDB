package domain.table.ddl

import domain.table.ddl.column.ColumnName

final case class ForeignKey(
    private val columnName: ColumnName,
    private val references: (TableName, ColumnName)
)
