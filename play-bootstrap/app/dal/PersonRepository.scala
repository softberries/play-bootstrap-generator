package dal

import javax.inject.{Inject, Singleton}
import binders.Pager
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Person

import scala.concurrent.{Future, ExecutionContext}

/**
 * A repsotiory for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class PersonRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.

  import dbConfig._
  import driver.api._

  /**
   * Here we define the table. It will have a name of people
   */
  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The age column */
    def age = column[Int]("age")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (id, name, age) <>((Person.apply _).tupled, Person.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val people = TableQuery[PeopleTable]

  /**
   * Create a person with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create(name: String, age: Int): Future[Person] = db.run {
    // We create a protection of just the name and age columns, since we're not inserting a value for the id column
    (people.map(p => (p.name, p.age))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning people.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((nameAge, id) => Person(id, nameAge._1, nameAge._2))
      // And finally, insert the person into the database
      ) +=(name, age)
  }

  def update(id: Long, person: Person): Future[Unit] = {
    val personToUpdate: Person = person.copy(id)
    db.run(people.filter(_.id === id).update(personToUpdate)).map(_ => ())
  }

  def list(): Future[Seq[Person]] = db.run(people.result)

  def listWithPager(pager: Pager): Future[(Seq[Person])] = {
    println("drop: " + pager.offset + ", take: " + pager.perPage)
    var query = people.filter(_.id > 0L)
    pager.sorts.map(sort => {
      query = query.sortBy(sort._1 match {
        case "id" => if (sort._2 == "1") _.id.desc else _.id.asc
        case "name" => if (sort._2 == "1") _.name.desc else _.name.asc
        case "age" => if (sort._2 == "1") _.age.desc else _.age.asc
      })
    })
    pager.queries.map(term => {
      query = query.filter(term._1 match {
        case "search" => _.name like "%" + term._2 + "%"
        case _ => _.name like "%" + term._2 + "%"
      })
    })
    query = query.drop(pager.offset).take(pager.perPage)
    db.run(query.result)
  }

  def size(): Future[Int] = db.run(people.size.result)

  def getById(id: Long): Future[Option[Person]] = db.run(people.filter(_.id === id).result.headOption)

  def delete(id: Long): Future[Unit] = db.run(people.filter(_.id === id).delete).map(_ => ())
}