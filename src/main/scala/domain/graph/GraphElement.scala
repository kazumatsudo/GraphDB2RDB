package domain.graph

import domain.table.ddl.TableList
import domain.table.dml.RecordList

trait GraphElement {
  def toDdl: TableList
  def toDml: RecordList
}
