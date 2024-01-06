package domain.table.column

case class ColumnLength(private val value: Int) {

  def max(target: ColumnLength): ColumnLength = max(target.value)

  def max(target: Int): ColumnLength = ColumnLength(Math.max(value, target))
}
