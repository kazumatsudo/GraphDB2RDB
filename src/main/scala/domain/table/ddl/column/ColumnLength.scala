package domain.table.ddl.column

case class ColumnLength(private val value: Int) {

  private val thresholdText = math.pow(2, 16) - 1 // 65,535

  def needToUseMediumText: Boolean = value > thresholdText

  def max(target: ColumnLength): ColumnLength = max(target.value)

  def max(target: Int): ColumnLength = ColumnLength(Math.max(value, target))

  def toSqlSentence: String = value.toString
}
