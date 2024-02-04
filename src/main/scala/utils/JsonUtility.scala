package utils

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.parser.decode
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax.EncoderOps
import org.janusgraph.graphdb.relations.RelationIdentifier
import usecase.{
  UsingSpecificKeyListRequest,
  UsingSpecificKeyListRequestKey,
  UsingSpecificKeyListRequestLabel
}

import java.util.UUID
import scala.util.Try

object JsonUtility {
  implicit private val anyDecoder: Decoder[Any] = new Decoder[Any] {
    override def apply(c: HCursor): Decoder.Result[Any] = {
      // decode only the types defined in domain.table.ddl.column.ColumnType
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
  implicit private val keyDecoder: Decoder[UsingSpecificKeyListRequestKey] =
    deriveDecoder
  implicit private val labelDecoder: Decoder[UsingSpecificKeyListRequestLabel] =
    deriveDecoder
  implicit private val decoder: Decoder[UsingSpecificKeyListRequest] =
    deriveDecoder

  implicit private val anyEncoder: Encoder[Any] = new Encoder[Any] {
    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    override def apply(value: Any): Json = {
      // encode only the types defined in domain.table.ddl.column.ColumnType
      value match {
        case v: Boolean            => v.asJson
        case v: Byte               => v.asJson
        case v: Short              => v.asJson
        case v: Int                => v.asJson
        case v: Long               => v.asJson
        case v: Float              => v.asJson
        case v: Double             => v.asJson
        case v: UUID               => v.asJson
        case v: Char               => v.asJson
        case v: String             => v.asJson
        case v: RelationIdentifier => v.toString.asJson
        case v => v.toString.asJson // TODO: classify the type in detail
      }
    }
  }
  implicit private val keyEncoder: Encoder[UsingSpecificKeyListRequestKey] =
    deriveEncoder
  implicit private val labelEncoder: Encoder[UsingSpecificKeyListRequestLabel] =
    deriveEncoder
  implicit private val encoder: Encoder[UsingSpecificKeyListRequest] =
    deriveEncoder

  def readForUsingSpecificKeyListRequest(
      jsonString: String
  ): Try[UsingSpecificKeyListRequest] = {
    decode[UsingSpecificKeyListRequest](jsonString)
  }.toTry

  def writeForUsingSpecificKeyListRequest(
      request: UsingSpecificKeyListRequest
  ): String = request.asJson.noSpaces
}
