package utils

import com.typesafe.config.ConfigFactory

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

  def outputSql(filename: String, sqlSentenceList: => View[String]): Unit = {
    val config = ConfigFactory.load()
    val directory = new File(config.getString("sql_output_directory"))

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
