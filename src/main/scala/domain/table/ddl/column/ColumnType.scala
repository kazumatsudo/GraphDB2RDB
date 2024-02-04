package domain.table.ddl.column

import domain.table.dml.RecordValue
import scala.annotation.tailrec

/** referenced by https://docs.janusgraph.org/schema/#property-key-data-type
  */
sealed trait ColumnType {
  def toSqlSentence: String
}

case object ColumnTypeBoolean extends ColumnType {
  val length: ColumnLength = ColumnLength(5) // false

  override def toSqlSentence: String = "BOOLEAN"
}

final case class ColumnTypeByte(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"TINYINT"
}

final case class ColumnTypeShort(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"SMALLINT"
}

final case class ColumnTypeInt(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"INT"
}

final case class ColumnTypeLong(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"INT"
}

final case class ColumnTypeFloat(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"FLOAT"
}

final case class ColumnTypeDouble(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"DOUBLE"
}

case object ColumnTypeUUID extends ColumnType {
  val length: ColumnLength = ColumnLength(36)
  override def toSqlSentence: String = s"CHAR(${length.toSqlSentence})"
}

final case class ColumnTypeDate(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"DATETIME"
}

final case class ColumnTypeCharacter(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = s"CHAR(${length.toSqlSentence})"
}

final case class ColumnTypeString(private val length: ColumnLength)
    extends ColumnType {
  override def toSqlSentence: String = if (length.needToUseMediumText) {
    s"MEDIUMTEXT"
  } else {
    s"VARCHAR(${length.toSqlSentence})"
  }
}

case object ColumnTypeUnknown extends ColumnType {
  override def toSqlSentence: String = s"TEXT"
}

object ColumnType {

  def apply(value: Any): ColumnType = {
    RecordValue.to[ColumnType](
      value = value,
      callbackBoolean = _ => ColumnTypeBoolean,
      callbackByte =
        valueByte => ColumnTypeByte(ColumnLength(valueByte.toString.length)),
      callbackShort =
        valueShort => ColumnTypeShort(ColumnLength(valueShort.toString.length)),
      callbackInt =
        valueInt => ColumnTypeInt(ColumnLength(valueInt.toString.length)),
      callbackLong =
        valueLong => ColumnTypeLong(ColumnLength(valueLong.toString.length)),
      callbackFloat = valueFloat =>
        ColumnTypeFloat(
          ColumnLength(valueFloat.toString.replaceAll("0*$", "").length)
        ),
      callbackDouble = valueDouble =>
        ColumnTypeDouble(
          ColumnLength(valueDouble.toString.replaceAll("0*$", "").length)
        ),
      callbackUuid = _ => ColumnTypeUUID,
      callbackDate =
        valueDate => ColumnTypeDate(ColumnLength(valueDate.toString.length)),
      callbackChar = _ => ColumnTypeCharacter(ColumnLength(1)),
      callbackString =
        valueString => ColumnTypeString(ColumnLength(valueString.length)),
      callbackRelationIdentifier = valueRelationIdentifier => {
        // execute RelationIdentifier as String
        val valueString = valueRelationIdentifier.toString
        ColumnTypeString(ColumnLength(valueString.length))
      },
      callbackUnknown = _ => ColumnTypeUnknown
    )
  }

  /** merges the attributes (type, length...) in two columns into one
    *
    * @param a
    *   target column
    * @param b
    *   target column
    * @return
    *   merged column attributes
    */
  @tailrec
  def merge(a: ColumnType, b: ColumnType): ColumnType = {
    (a, b) match {
      // ColumnTypeBoolean
      case (ColumnTypeBoolean, ColumnTypeBoolean) => ColumnTypeBoolean
      case (ColumnTypeBoolean, ColumnTypeByte(length)) =>
        ColumnTypeByte(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeShort(length)) =>
        ColumnTypeShort(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeInt(length)) =>
        ColumnTypeInt(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeLong(length)) =>
        ColumnTypeLong(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeFloat(length)) =>
        ColumnTypeFloat(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeDouble(length)) =>
        ColumnTypeDouble(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(ColumnTypeUUID.length)
      case (ColumnTypeBoolean, ColumnTypeDate(length)) =>
        // change Date -> String
        ColumnTypeString(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeCharacter(length)) =>
        ColumnTypeCharacter(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeString(length)) =>
        ColumnTypeString(length.max(ColumnTypeBoolean.length))
      case (ColumnTypeBoolean, ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeByte
      case (ColumnTypeByte(_), ColumnTypeBoolean) => merge(b, a)
      case (ColumnTypeByte(alength), ColumnTypeByte(blength)) =>
        ColumnTypeByte(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeShort(blength)) =>
        ColumnTypeShort(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeInt(blength)) =>
        ColumnTypeInt(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeLong(blength)) =>
        ColumnTypeLong(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeFloat(blength)) =>
        ColumnTypeFloat(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeDouble(blength)) =>
        ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(alength.max(ColumnTypeUUID.length))
      case (ColumnTypeByte(alength), ColumnTypeDate(blength)) =>
        // change Date -> String
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeByte(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeByte(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeShort
      case (ColumnTypeShort(_), ColumnTypeBoolean) => merge(b, a)
      case (ColumnTypeShort(_), ColumnTypeByte(_)) => merge(b, a)
      case (ColumnTypeShort(alength), ColumnTypeShort(blength)) =>
        ColumnTypeShort(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeInt(blength)) =>
        ColumnTypeInt(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeLong(blength)) =>
        ColumnTypeLong(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeFloat(blength)) =>
        ColumnTypeFloat(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeDouble(blength)) =>
        ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(alength.max(ColumnTypeUUID.length))
      case (ColumnTypeShort(alength), ColumnTypeDate(blength)) =>
        // change Date -> String
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeShort(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeShort(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeInt
      case (ColumnTypeInt(_), ColumnTypeBoolean)  => merge(b, a)
      case (ColumnTypeInt(_), ColumnTypeByte(_))  => merge(b, a)
      case (ColumnTypeInt(_), ColumnTypeShort(_)) => merge(b, a)
      case (ColumnTypeInt(alength), ColumnTypeInt(blength)) =>
        ColumnTypeInt(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeLong(blength)) =>
        ColumnTypeLong(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeFloat(blength)) =>
        ColumnTypeFloat(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeDouble(blength)) =>
        ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(alength.max(ColumnTypeUUID.length))
      case (ColumnTypeInt(alength), ColumnTypeDate(blength)) =>
        // change Date -> String
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeInt(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeLong
      case (ColumnTypeLong(_), ColumnTypeBoolean)  => merge(b, a)
      case (ColumnTypeLong(_), ColumnTypeByte(_))  => merge(b, a)
      case (ColumnTypeLong(_), ColumnTypeShort(_)) => merge(b, a)
      case (ColumnTypeLong(_), ColumnTypeInt(_))   => merge(b, a)
      case (ColumnTypeLong(alength), ColumnTypeLong(blength)) =>
        ColumnTypeLong(alength.max(blength))
      case (ColumnTypeLong(alength), ColumnTypeFloat(blength)) =>
        ColumnTypeFloat(alength.max(blength))
      case (ColumnTypeLong(alength), ColumnTypeDouble(blength)) =>
        ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeLong(alength), ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(alength.max(ColumnTypeUUID.length))
      case (ColumnTypeLong(alength), ColumnTypeDate(blength)) =>
        // change Date -> String
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeLong(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeLong(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeLong(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeFloat
      case (ColumnTypeFloat(_), ColumnTypeBoolean)  => merge(b, a)
      case (ColumnTypeFloat(_), ColumnTypeByte(_))  => merge(b, a)
      case (ColumnTypeFloat(_), ColumnTypeShort(_)) => merge(b, a)
      case (ColumnTypeFloat(_), ColumnTypeInt(_))   => merge(b, a)
      case (ColumnTypeFloat(_), ColumnTypeLong(_))  => merge(b, a)
      case (ColumnTypeFloat(alength), ColumnTypeFloat(blength)) =>
        ColumnTypeFloat(alength.max(blength))
      case (ColumnTypeFloat(alength), ColumnTypeDouble(blength)) =>
        ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeFloat(alength), ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(alength.max(ColumnTypeUUID.length))
      case (ColumnTypeFloat(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeFloat(alength), ColumnTypeDate(blength)) =>
        // change Date -> String
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeFloat(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeFloat(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeDouble
      case (ColumnTypeDouble(_), ColumnTypeBoolean)  => merge(b, a)
      case (ColumnTypeDouble(_), ColumnTypeByte(_))  => merge(b, a)
      case (ColumnTypeDouble(_), ColumnTypeShort(_)) => merge(b, a)
      case (ColumnTypeDouble(_), ColumnTypeInt(_))   => merge(b, a)
      case (ColumnTypeDouble(_), ColumnTypeLong(_))  => merge(b, a)
      case (ColumnTypeDouble(_), ColumnTypeFloat(_)) => merge(b, a)
      case (ColumnTypeDouble(alength), ColumnTypeDouble(blength)) =>
        ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeDouble(alength), ColumnTypeUUID) =>
        // change UUID -> Character
        ColumnTypeCharacter(alength.max(ColumnTypeUUID.length))
      case (ColumnTypeDouble(alength), ColumnTypeDate(blength)) =>
        // change Date -> String
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeDouble(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeDouble(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeDouble(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeUUID
      case (ColumnTypeUUID, ColumnTypeBoolean)      => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeByte(_))      => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeShort(_))     => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeInt(_))       => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeLong(_))      => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeFloat(_))     => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeDouble(_))    => merge(b, a)
      case (ColumnTypeUUID, ColumnTypeUUID)         => ColumnTypeUUID
      case (ColumnTypeUUID, ColumnTypeDate(length)) =>
        // change Date -> String
        ColumnTypeString(length.max(ColumnTypeUUID.length))
      case (ColumnTypeUUID, ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(ColumnTypeUUID.length.max(blength))
      case (ColumnTypeUUID, ColumnTypeString(blength)) =>
        ColumnTypeString(ColumnTypeUUID.length.max(blength))
      case (ColumnTypeUUID, ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeDate
      case (ColumnTypeDate(_), ColumnTypeBoolean)   => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeByte(_))   => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeShort(_))  => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeInt(_))    => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeLong(_))   => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeFloat(_))  => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeDouble(_)) => merge(b, a)
      case (ColumnTypeDate(_), ColumnTypeUUID)      => merge(b, a)
      case (ColumnTypeDate(alength), ColumnTypeDate(blength)) =>
        ColumnTypeDate(alength.max(blength))
      case (ColumnTypeDate(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeDate(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeDate(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeCharacter
      case (ColumnTypeCharacter(_), ColumnTypeBoolean)   => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeByte(_))   => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeShort(_))  => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeInt(_))    => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeLong(_))   => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeFloat(_))  => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeDouble(_)) => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeUUID)      => merge(b, a)
      case (ColumnTypeCharacter(_), ColumnTypeDate(_))   => merge(b, a)
      case (ColumnTypeCharacter(alength), ColumnTypeCharacter(blength)) =>
        ColumnTypeCharacter(alength.max(blength))
      case (ColumnTypeCharacter(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeCharacter(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeString
      case (ColumnTypeString(_), ColumnTypeBoolean)      => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeByte(_))      => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeShort(_))     => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeInt(_))       => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeLong(_))      => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeFloat(_))     => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeDouble(_))    => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeUUID)         => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeDate(_))      => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeCharacter(_)) => merge(b, a)
      case (ColumnTypeString(alength), ColumnTypeString(blength)) =>
        ColumnTypeString(alength.max(blength))
      case (ColumnTypeString(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeUnknown
      case (ColumnTypeUnknown, ColumnTypeBoolean)      => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeByte(_))      => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeShort(_))     => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeInt(_))       => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeLong(_))      => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeFloat(_))     => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeDouble(_))    => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeUUID)         => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeDate(_))      => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeCharacter(_)) => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeString(_))    => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeUnknown)      => ColumnTypeUnknown
    }
  }
}
