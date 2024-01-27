package domain.table.ddl.column

case class ColumnLength(private val value: Int) extends AnyVal {

  private def thresholdText = math.pow(2, 16) - 1 // 65,535

  def max(target: ColumnLength): ColumnLength = ColumnLength(
    Math.max(value, target.value)
  )

  def needToUseMediumText: Boolean = value > thresholdText

  def toSqlSentence: String = value.toString
}
