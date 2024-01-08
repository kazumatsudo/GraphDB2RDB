package utils

import com.typesafe.config.ConfigFactory

import java.io.{File, FileOutputStream, OutputStreamWriter}
import scala.util.Using

object FileUtility {

  def outputSql(filename: String, sqlSentence: String): Unit = {
    val config = ConfigFactory.load()
    val directory = new File(config.getString("sql_output_directory"))

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
