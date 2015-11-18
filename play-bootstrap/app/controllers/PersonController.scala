package controllers

import binders.{Bounds, Pager}
import models.Person
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import views.html

import dal._

import scala.concurrent.{ExecutionContext, Future}

import javax.inject._

class PersonController @Inject()(repo: PersonRepository, val messagesApi: MessagesApi)
                                (implicit ec: ExecutionContext) extends Controller with I18nSupport {


  val exampleJson = "{\n  \"records\": [\n    {\n      \"someAttribute\": \"I am record one\",\n      \"someOtherAttribute\": \"Fetched by AJAX\"\n    },\n    {\n      \"someAttribute\": \"I am record two\",\n      \"someOtherAttribute\": \"Cuz it's awesome\"\n    },\n    {\n      \"someAttribute\": \"I am record three\",\n      \"someOtherAttribute\": \"Yup, still AJAX\"\n    }\n  ],\n  \"queryRecordCount\": 3,\n  \"totalRecordCount\": 3\n}"
  /**
   * The mapping for the person form.
   */
  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> number.verifying(min(0), max(140))
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  /**
   * The index action.
   */
  def list = Action.async { persons =>
    repo.list().map { people =>
      Ok(views.html.listPersons(personForm, people))
    }
  }

  def create = Action {
    Ok(views.html.createPersonForm(personForm))
  }

  def edit(id: Long) = Action.async { implicit rs =>
    val person = repo.getById(id)

    person.map { case p =>
      p match {
        case Some(c) => Ok(html.editPersonForm(id, personForm.fill(CreatePersonForm(c.name, c.age))))
        case None => NotFound
      }
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    personForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.editPersonForm(id, errorForm)))
      },
      // There were no errors in the from, so create the person.
      person => {
        repo.update(id, Person(id, person.name, person.age)).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PersonController.list)
        }
      }
    )
  }

  /**
   * The add person action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
   */
  def save = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    personForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.createPersonForm(errorForm)))
      },
      // There were no errors in the from, so create the person.
      person => {
        repo.create(person.name, person.age).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PersonController.list)
        }
      }
    )
  }

  def delete(id: Long) = Action.async {
    repo.delete(id)
    repo.list().map { people =>
      Ok(views.html.listPersons(personForm, people))
    }
  }

  implicit val dynatableRestResultFormat = Json.format[DynatableRestResult]

  /**
   * A REST endpoint that gets all the people as JSON.
   */
  def getPersons() = Action.async { implicit request =>
    val url: String = request.uri
    val pager: Pager = Pager.fromString(url)
    val response = for {
      resultList <- repo.listWithPager(pager)
      resultSize <- repo.size()
    } yield {
      Ok(Json.toJson(DynatableRestResult(resultList, resultList.size + pager.offset, resultSize)))
    }
    response
  }


}

/**
 * The create person form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreatePersonForm(name: String, age: Int)

case class DynatableRestResult(records: Seq[Person], queryRecordCount: Long, totalRecordCount: Long)

object DynatableRestResult {

}
