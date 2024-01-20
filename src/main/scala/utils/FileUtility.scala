package utils

import com.typesafe.scalalogging.StrictLogging

import java.io.{File, FileOutputStream, PrintWriter}
import scala.collection.View
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.{Try, Using}

object FileUtility extends StrictLogging {

  def readJson(filePath: String): Try[String] = {
    Using(Source.fromFile(filePath)) { bufferedSource =>
      bufferedSource.mkString
    }.recover { case NonFatal(e) =>
      logger.error(s"${e.getMessage}", e)
      throw e
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

    Using
      .Manager { use =>
        val fileOutputStream =
          use(new FileOutputStream(s"${directory.getPath}/$filename.json"))
        val writer = use(new PrintWriter(fileOutputStream))

        writer.write(jsonString)
      }
      .recover { case NonFatal(e) =>
        logger.error(s"${e.getMessage}", e)
        throw e
      }
  }

  def writeSql(
      directoryPath: String,
      filename: String,
      sqlSentenceList: => View[String]
  ): Unit = {
    val directory = new File(directoryPath)

    if (!directory.exists()) {
      directory.mkdirs()
    }

    Using
      .Manager { use =>
        val fileOutputStream =
          use(new FileOutputStream(s"${directory.getPath}/$filename.sql"))
        val writer = use(new PrintWriter(fileOutputStream))

        sqlSentenceList.foreach(writer.println)
      }
      .recover { case NonFatal(e) =>
        logger.error(s"${e.getMessage}", e)
        throw e
      }
  }
}
