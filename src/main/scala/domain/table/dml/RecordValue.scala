package domain.table.dml

import domain.table.ddl.column.{
  ColumnType,
  ColumnTypeBoolean,
  ColumnTypeByte,
  ColumnTypeCharacter,
  ColumnTypeDate,
  ColumnTypeDouble,
  ColumnTypeFloat,
  ColumnTypeInt,
  ColumnTypeLong,
  ColumnTypeShort,
  ColumnTypeString,
  ColumnTypeUUID,
  ColumnTypeUnknown
}

import java.sql.Timestamp
import java.time.Instant

final case class RecordValue(private val value: Map[String, Any])
    extends AnyVal {

  def toSqlSentence: (String, String) = {
    val (keys, values) = value.toSeq.sortBy { case (key, _) => key }.unzip

    val valuesForSql = values.map { value =>
      ColumnType.apply(value) match {
        case ColumnTypeBoolean   => value
        case ColumnTypeByte(_)   => value
        case ColumnTypeShort(_)  => value
        case ColumnTypeInt(_)    => value
        case ColumnTypeLong(_)   => value
        case ColumnTypeFloat(_)  => value
        case ColumnTypeDouble(_) => value
        case ColumnTypeUUID      => s"'$value'"
        case ColumnTypeDate(_) =>
          s"'${Timestamp.from(value.asInstanceOf[Instant])}'"
        case ColumnTypeCharacter(_) => s"'$value'"
        case ColumnTypeString(_)    => s"'$value'"
        case ColumnTypeUnknown      => s"'$value'"
      }
    }

    (keys.mkString(", "), valuesForSql.mkString(", "))
  }
}
