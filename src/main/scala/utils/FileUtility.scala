package utils

import java.io.{File, FileOutputStream, OutputStreamWriter}
import scala.io.Source
import scala.util.{Try, Using}

object FileUtility {

  def readJson(filePath: String): Try[String] = {
    Using(Source.fromFile(filePath)) { bufferedSource =>
      bufferedSource.mkString
    }
  }

  def writeJson(
      directoryPath: String,
      filename: String,
      jsonString: String
  ): Unit = {
    val directory = new File(directoryPath)

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

  def writeSql(
      directoryPath: String,
      filename: String,
      sqlSentence: String
  ): Unit = {
    val directory = new File(directoryPath)

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
