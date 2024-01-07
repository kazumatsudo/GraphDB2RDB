package domain.table.column

import scala.annotation.tailrec

sealed trait ColumnType
case object ColumnTypeBoolean extends ColumnType

case class ColumnTypeInt(private val length: ColumnLength) extends ColumnType
case class ColumnTypeDouble(private val length: ColumnLength) extends ColumnType
case class ColumnTypeString(private val length: ColumnLength) extends ColumnType
case object ColumnTypeUnknown extends ColumnType

object ColumnType {

  def apply(value: Any): ColumnType = value match {
    case valueString: String => ColumnTypeString(ColumnLength(valueString.length))
    case valueInt: Int => ColumnTypeInt(ColumnLength(valueInt.toString.length))
    case valueDouble: Double => ColumnTypeDouble(ColumnLength(valueDouble.toString.replaceAll("0*$", "").length))
    case _: Boolean => ColumnTypeBoolean
    case _ => ColumnTypeUnknown // TODO: classify the type in detail
  }

  /** merges the attributes (type, length...) in two columns into one
   *
   * @param a target column
   * @param b target column
   * @return merged column attributes
   */
  @tailrec
  def merge(a: ColumnType, b: ColumnType): ColumnType = {
    (a, b) match {
      // ColumnTypeBoolean
      case (ColumnTypeBoolean, ColumnTypeBoolean) => ColumnTypeBoolean
      case (ColumnTypeBoolean, ColumnTypeInt(length)) =>ColumnTypeInt(length.max(5)) // 5 = false.toString
      case (ColumnTypeBoolean, ColumnTypeDouble(length)) => ColumnTypeDouble(length.max(5)) // 5 = false.toString
      case (ColumnTypeBoolean, ColumnTypeString(length)) => ColumnTypeString(length.max(5)) // 5 = false.toString
      case (ColumnTypeBoolean, ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeInt
      case (ColumnTypeInt(_), ColumnTypeBoolean) => merge(b, a)
      case (ColumnTypeInt(alength), ColumnTypeInt(blength)) => ColumnTypeInt(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeDouble(blength)) => ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeInt(alength), ColumnTypeString(blength)) => ColumnTypeString(alength.max(blength))
      case (ColumnTypeInt(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeDouble
      case (ColumnTypeDouble(_), ColumnTypeBoolean) => merge(b, a)
      case (ColumnTypeDouble(_), ColumnTypeInt(_)) => merge(b, a)
      case (ColumnTypeDouble(alength), ColumnTypeDouble(blength)) => ColumnTypeDouble(alength.max(blength))
      case (ColumnTypeDouble(alength), ColumnTypeString(blength)) => ColumnTypeString(alength.max(blength))
      case (ColumnTypeDouble(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeString
      case (ColumnTypeString(_), ColumnTypeBoolean) => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeInt(_)) => merge(b, a)
      case (ColumnTypeString(_), ColumnTypeDouble(_)) => merge(b, a)
      case (ColumnTypeString(alength), ColumnTypeString(blength)) => ColumnTypeString(alength.max(blength))
      case (ColumnTypeString(_), ColumnTypeUnknown) => ColumnTypeUnknown

      // ColumnTypeUnknown
      case (ColumnTypeUnknown, ColumnTypeBoolean) => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeInt(_)) => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeDouble(_)) => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeString(_)) => merge(b, a)
      case (ColumnTypeUnknown, ColumnTypeUnknown) => ColumnTypeUnknown
    }
  }
}