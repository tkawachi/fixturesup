package fixturesup

case class DbFixture(tables: Map[String, Table])

object DbFixture {
  def unsafeCreate(fixture: Map[String, Seq[Map[String, String]]]): DbFixture =
    DbFixture(fixture.view.mapValues(Table.unsafeCreate).toMap)
}
