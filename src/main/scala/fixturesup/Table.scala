package fixturesup

case class Table(data: Seq[Map[String, String]]) extends AnyVal {
  def columns: Set[String] = data.head.keys.toSet
}

object Table {
  val emptyRow: Table = Table(Seq(Map.empty))

  def unsafeCreate(data: Seq[Map[String, String]]): Table = {
    data.headOption.map { head =>
      val cols = head.keys.toSet
      if (data.exists(_.keys.toSet != cols)) {
        throw new IllegalArgumentException("Columns must be the same")
      }
      Table(data)
    }.getOrElse {
      throw new IllegalArgumentException("Table must have at least one row")
    }
  }
}
