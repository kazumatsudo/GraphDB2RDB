package utils

import java.io.{File, FileOutputStream, OutputStreamWriter}
import scala.util.Using

object FileUtility {

  def outputSql(filename: String, sqlSentence: String): Unit = {
    val directory = new File("sql")

    if (!directory.exists()) {
      directory.mkdirs()
    }

    Using.Manager { use =>
      val fileOutputStream =
        use(new FileOutputStream(s"${directory.getPath}/$filename.sql"))
      val writer = use(new OutputStreamWriter(fileOutputStream))
      writer.write(sqlSentence)
    }
  }
}
