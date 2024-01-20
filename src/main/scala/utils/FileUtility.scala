package utils

import com.typesafe.config.ConfigFactory

import java.io.{File, FileOutputStream, OutputStreamWriter}
import scala.io.Source
import scala.util.{Try, Using}

object FileUtility {

  def readJson(filePath: String): Try[String] = {
    Using(Source.fromFile(filePath)) { bufferedSource =>
      bufferedSource.mkString
    }
  }

  def writeJson(filename: String, jsonString: String): Unit = {
    val config = ConfigFactory.load()
    val directory = new File(config.getString("sql_output_directory"))

    if (!directory.exists()) {
      directory.mkdirs()
    }

    Using.Manager { use =>
      val fileOutputStream =
        use(new FileOutputStream(s"${directory.getPath}/$filename.json"))
      val writer = use(new OutputStreamWriter(fileOutputStream))
      writer.write(jsonString)
    }
  }

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
