package domain.table.dml

import domain.table.ddl.column.{
  ColumnType,
  ColumnTypeBoolean,
  ColumnTypeByte,
  ColumnTypeCharacter,
  ColumnTypeDouble,
  ColumnTypeFloat,
  ColumnTypeInt,
  ColumnTypeLong,
  ColumnTypeShort,
  ColumnTypeString,
  ColumnTypeUnknown
}

final case class RecordValue(private val value: Map[String, Any])
    extends AnyVal {

  def toSqlSentence: (String, String) = {
    val (keys, values) = value.toSeq.unzip

    val valuesForSql = values.map { value =>
      ColumnType.apply(value) match {
        case ColumnTypeBoolean      => value
        case ColumnTypeByte(_)      => value
        case ColumnTypeShort(_)     => value
        case ColumnTypeInt(_)       => value
        case ColumnTypeLong(_)      => value
        case ColumnTypeFloat(_)     => value
        case ColumnTypeDouble(_)    => value
        case ColumnTypeCharacter(_) => s"\"$value\""
        case ColumnTypeString(_)    => s"\"$value\""
        case ColumnTypeUnknown      => s"\"$value\""
      }
    }

    (keys.mkString(", "), valuesForSql.mkString(", "))
  }
}
