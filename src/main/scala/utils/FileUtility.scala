package utils

import java.io.{File, FileOutputStream, PrintWriter}
import scala.collection.View
import scala.io.Source
import scala.util.{Try, Using}

object FileUtility {

  def readJson(filePath: String): Try[String] = {
    Using(Source.fromFile(filePath)) { bufferedSource =>
      bufferedSource.mkString
    }
  }

  def outputSql(
      directoryPath: String,
      filename: String,
      sqlSentenceList: => View[String]
  ): Unit = {
    val directory = new File(directoryPath)

    if (!directory.exists()) {
      directory.mkdirs()
    }

    Using.Manager { use =>
      val fileOutputStream =
        use(new FileOutputStream(s"${directory.getPath}/$filename.sql"))
      val writer = use(new PrintWriter(fileOutputStream))
      sqlSentenceList.foreach(writer.println)
    }
  }
}
