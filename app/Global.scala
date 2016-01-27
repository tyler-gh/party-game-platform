import java.io.{FileInputStream, File}

import models.{GameDefinition, Games}
import org.yaml.snakeyaml.Yaml
import play.api._
import java.util

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    new File(getClass.getResource("./games").getPath).listFiles().foreach(file => {
      val gameDef = new Yaml().load(new FileInputStream(new File(file, "definition.yml"))).asInstanceOf[util.Map[String, AnyRef]]

      Games.addGameDefinition(new GameDefinition(
        gameDef.get("id").toString,
        gameDef.get("title").toString,
        gameDef.get("color").toString)
      )
    })
  }

}
