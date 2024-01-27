package domain.table.ddl.column

case class ColumnLength(private val value: Int) {

  private val mysqlColumnMaxLenght = 16383

  def isTooBig: Boolean = value > mysqlColumnMaxLenght

  def max(target: ColumnLength): ColumnLength = max(target.value)

  def max(target: Int): ColumnLength = ColumnLength(Math.max(value, target))

  def toSqlSentence: String = value.toString
}
