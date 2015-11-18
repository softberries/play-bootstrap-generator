package playbootstrap.model

/**
 *  
 * Validation:
 * - must use as database column names
 * @param name
 * @param dataType
 */
case class ModelEntityProperty(name: String, dataType: String)

/**
 * The model, form and repository is created for instances of this class 
 * Every model entity has by default a property 'id' of type Long or other which could be auto-generated
 * by the database engine and can be used later for joins between entities.
 * *
 * Validation:
 *  - must use as db table names 
 *  - good to be plural with 's' at the end ;)
 *  *
 * @param name
 * @param properties
 */
case class ModelEntity(name: String, properties: List[ModelEntityProperty])

case class Application(name: String, modelEntities: List[ModelEntity])
