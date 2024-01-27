package domain.table.dml

import com.typesafe.scalalogging.StrictLogging
import org.janusgraph.graphdb.relations.RelationIdentifier

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

final case class RecordValue(private val value: Map[String, Any])
    extends AnyVal {

  private def notEnclose[T](value: T): String = s"$value"
  private def enclose[T](value: T): String = s"'$value'"

  def toSqlSentence: (String, String) = {
    val (keys, values) = value.toSeq.sortBy { case (key, _) => key }.unzip

    val valuesForSql = values.map { value =>
      RecordValue.to(
        value = value,
        callbackBoolean = notEnclose,
        callbackByte = notEnclose,
        callbackShort = notEnclose,
        callbackInt = notEnclose,
        callbackLong = notEnclose,
        callbackFloat = notEnclose,
        callbackDouble = notEnclose,
        callbackUuid = enclose,
        callbackDate = valueDate => enclose(Timestamp.from(valueDate)),
        callbackChar = enclose,
        callbackString = enclose,
        callbackRelationIdentifier = enclose,
        callbackUnknown = enclose
      )
    }

    (keys.mkString(", "), valuesForSql.mkString(", "))
  }
}

object RecordValue extends StrictLogging {

  def to[T](
      value: Any,
      callbackBoolean: Boolean => T,
      callbackByte: Byte => T,
      callbackShort: Short => T,
      callbackInt: Int => T,
      callbackLong: Long => T,
      callbackFloat: Float => T,
      callbackDouble: Double => T,
      callbackUuid: UUID => T,
      callbackDate: Instant => T,
      callbackChar: Char => T,
      callbackString: String => T,
      callbackRelationIdentifier: RelationIdentifier => T,
      callbackUnknown: Any => T
  ): T = value match {
    case valueBoolean: Boolean       => callbackBoolean(valueBoolean)
    case valueByte: Byte             => callbackByte(valueByte)
    case valueShort: Short           => callbackShort(valueShort)
    case valueInt: Int               => callbackInt(valueInt)
    case valueLong: Long             => callbackLong(valueLong)
    case valueFloat: Float           => callbackFloat(valueFloat)
    case valueDouble: Double         => callbackDouble(valueDouble)
    case valueUuid: UUID             => callbackUuid(valueUuid)
    case valueDate: Instant          => callbackDate(valueDate)
    case valueChar: Char             => callbackChar(valueChar)
    case valueString: String         => callbackString(valueString)
    case valueRI: RelationIdentifier => callbackRelationIdentifier(valueRI)
    case valueUnknown =>
      logger.info(s"valueUnknown: $valueUnknown")
      callbackUnknown(valueUnknown)
  }
}
