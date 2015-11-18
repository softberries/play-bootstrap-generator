package playbootstrap.template

import playbootstrap.model.ModelEntity
import org.fusesource.scalate._
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

class BootstrapTemplateEngine {

  def processTemplate(templatePath: String, values: ModelEntity, outputPath: String) = {
    val engine = new TemplateEngine
    val output = engine.layout(templatePath, Map("name" -> values.name, "properties" -> values.properties))
    Files.write(Paths.get(outputPath), output.getBytes(StandardCharsets.UTF_8))
  }

}
