package controllers.traits

import java.io.File

trait GameAssetController {

  def assetsSeqToString(assets:Option[Seq[File]]):Option[String] = {
    assets.map(files => {
      files.map(gameAssetToString).mkString("\n")
    })
  }

  def gameAssetToString(file: File) : String = {
    val source = scala.io.Source.fromFile(file.getAbsolutePath)
    try source.getLines mkString "\n" finally source.close()
  }
}
