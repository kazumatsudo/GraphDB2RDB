package domain.table.ddl

final case class TableAttribute(private val foreignKeyList: Seq[ForeignKey])
    extends AnyVal
