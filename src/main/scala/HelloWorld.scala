import com.github.mauricio.async.db.Connection
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import scala.concurrent.Await
import scala.concurrent.duration._

object HelloWorld {
  def main(args: Array[String]): Unit = {
    val configuration = URLParser.parse("jdbc:postgresql://localhost/kukaconnect?user=pgadmin&password=!skcus1N")
    val connection: Connection = new PostgreSQLConnection(configuration)

    val update = for {
      conn <- connection.connect
      result1 <- conn.sendQuery(
        """
          |DO $$
          |BEGIN
          |    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'assoctype') THEN
          |        CREATE TYPE assoctype AS ENUM ('site', 'role', 'user', 'device');
          |    END IF;
          |END$$;
        """.stripMargin)
      result2 <- conn.sendQuery(
        """
          |CREATE TABLE IF NOT EXISTS associations (
          |    id text,
          |    this_type assoctype,
          |    child_id text,
          |    child_type assoctype
          |)
        """.stripMargin)
      result3 <- conn.sendQuery("INSERT INTO associations VALUES ('Larry', 'user', 'awesome', 'site')")
    } yield {
      result3
    }

    Await.result(update.andThen { case _ => connection.disconnect }, 10.seconds)
  }
}
