package fixturesup

import java.sql.Connection
import scala.collection.mutable
import scala.util.Using

private[fixturesup] case class ForeignKey(pktable: String, pkcolumn: String, fktable: String, fkcolumn: String)

private[fixturesup] object ForeignKey {
  def listImportedKeys(conn: Connection, tables: Set[String]): Seq[ForeignKey] = {
    val metadata = conn.getMetaData
    val catalog = conn.getCatalog
    val schema = conn.getSchema
    val result = mutable.ListBuffer.empty[ForeignKey]
    tables.foreach { table =>
      Using.resource(metadata.getImportedKeys(catalog, schema, table)) { rs =>
        while (rs.next()) {
          val pkcat = rs.getString("PKTABLE_CAT")
          val pksch = rs.getString("PKTABLE_SCHEM")
          val pktable = rs.getString("PKTABLE_NAME")
          val pkcolumn = rs.getString("PKCOLUMN_NAME")
          val fkcat = rs.getString("FKTABLE_CAT")
          val fksch = rs.getString("FKTABLE_SCHEM")
          val fktable = rs.getString("FKTABLE_NAME")
          val fkcolumn = rs.getString("FKCOLUMN_NAME")
          if (pkcat != fkcat) {
            throw new IllegalStateException(s"PKTABLE_CAT ($pkcat) != FKTABLE_CAT ($fkcat)")
          }
          if (pksch != fksch) {
            throw new IllegalStateException(s"PKTABLE_SCHEM ($pksch) != FKTABLE_SCHEM ($fksch)")
          }
          result += ForeignKey(pktable, pkcolumn, fktable, fkcolumn)
        }
      }
    }
    result.toSeq
  }
}
