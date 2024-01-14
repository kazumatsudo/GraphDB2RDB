package utils

import io.circe.{Decoder, HCursor, Json}
import io.circe.parser.decode
import io.circe.generic.semiauto.deriveDecoder
import usecase.{
  UsingSpecificKeyListRequest,
  UsingSpecificKeyListRequestKey,
  UsingSpecificKeyListRequestLabel
}

import scala.util.Try

object JsonUtility {
  def parseForUsingSpecificKeyListRequest(
      jsonString: String
  ): Try[UsingSpecificKeyListRequest] = {
    implicit val anyDecoder: Decoder[Any] = new Decoder[Any] {
      override def apply(c: HCursor): Decoder.Result[Any] = {
        // parse only the types defined in domain.table.ddl.column.ColumnType
        c.value match {
          case json: Json if json.isBoolean =>
            // ColumnTypeBoolean
            Right(json.asBoolean.get)
          case json: Json if json.isNumber =>
            // ColumnTypeInt, ColumnTypeLong, ColumnTypeDouble
            val number = json.asNumber.get
            val result =
              number.toInt.getOrElse(number.toLong.getOrElse(number.toDouble))
            Right(result)
          case json: Json if json.isString =>
            // ColumnTypeString
            Right(json.asString.get)
          case _ =>
            Left(io.circe.DecodingFailure("Unsupported JSON type", c.history))
        }
      }
    }
    implicit val keyDecoder: Decoder[UsingSpecificKeyListRequestKey] =
      deriveDecoder
    implicit val labelDecoder: Decoder[UsingSpecificKeyListRequestLabel] =
      deriveDecoder
    implicit val decoder: Decoder[UsingSpecificKeyListRequest] = deriveDecoder

    decode[UsingSpecificKeyListRequest](jsonString)
  }.toTry
}
