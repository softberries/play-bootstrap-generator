package playbootstrap.app

import java.io.File
import org.slf4j.LoggerFactory
import playbootstrap.model.{ModelEntityProperty, ModelEntity, Application}
import playbootstrap.template.BootstrapTemplateEngine
import sbt.IO

object PlaceWhereTheMagicHappens {

  val STATIC_RESOURCES_LOCATION_DIR = "~/play-bootstrap"
  val GENERATED_SOURCES_LOCATION_DIR_TEMPLATE = "~/tmp/play-bootstrap-"

  val log = LoggerFactory.getLogger(PlaceWhereTheMagicHappens.getClass)

  def main(args: Array[String]): Unit = {
    log.info("Building your dream CRUD application...")
    buildApplication(Application("dreamApp", List(ModelEntity(name = "Personx", properties = List(
      ModelEntityProperty(name = "somenumber", dataType = "Long"),
      ModelEntityProperty(name = "name", dataType = "String"),
      ModelEntityProperty(name = "age", dataType = "Int")
    )))))
    log.info("Finished!")
  }


  def buildApplication(application: Application) = {
    copyStaticResources(application.name)
    generateFromTemplate(application)
  }

  def copyStaticResources(applicationName: String) = {
    log.info("Copying static resources..")
    IO.copyDirectory(new File(STATIC_RESOURCES_LOCATION_DIR), new File(s"$STATIC_RESOURCES_LOCATION_DIR$applicationName"), true)
  }

  def generateFromTemplate(app: Application) = {
    for (me <- app.modelEntities) {
//      new BootstrapTemplateEngine().processTemplate("templates/models/EntityModel.ssp", me, "/Users/kris/Projects/tmp/test.txt")
      new BootstrapTemplateEngine().processTemplate("templates/models/h2/EntityRepository.ssp", me, "~/tmp/test.txt")
    }
  }
}
