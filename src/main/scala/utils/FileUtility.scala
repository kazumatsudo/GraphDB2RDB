package utils

import java.io.{File, FileOutputStream, OutputStreamWriter}

object FileUtility {

  def outputSql(filename: String, sqlSentence: String): Unit = {
    val directory = new File("sql")

    if (!directory.exists()) {
      directory.mkdirs()
    }

    val fileOutPutStream = new FileOutputStream(s"${directory.getPath}/$filename.sql")
    val writer = new OutputStreamWriter(fileOutPutStream)

    writer.write(sqlSentence)
    writer.close()
  }
}
