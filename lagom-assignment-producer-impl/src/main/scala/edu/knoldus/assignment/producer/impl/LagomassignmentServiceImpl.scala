package edu.knoldus.assignment.producer.impl

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.query.Offset
import akka.stream.scaladsl.Source
import edu.knoldus.assignment.producer.api.{Name => producerName}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.concurrent.duration._
import edu.knoldus.assignment.producer
import edu.knoldus.assignment.producer.api
import edu.knoldus.assignment.producer.api.{EmployeeData, ExternalEmployeeService, LagomassignmentService, Name}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the LagomassignmentService.
  */
class LagomassignmentServiceImpl(externalEmployeeService: ExternalEmployeeService, persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends LagomassignmentService {

  val actorSystem = ActorSystem("EmployeeSystemActor")

  val lagomAssignmentProducerActor = actorSystem.actorOf(LagomAssignmentProducerActor.props(externalEmployeeService))

  actorSystem.scheduler.schedule(
    10 seconds,
    10 seconds,
    lagomAssignmentProducerActor,
    "print")

  override def hello(name: String): ServiceCall[producerName, api.EmployeeData] = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[LagomassignmentEntity](name)

    // Tell the entity to use the greeting message specified.
    //ref.ask(Name(request.name))
    val result: Future[api.EmployeeData] = externalEmployeeService.getEmployee.invoke()
    val newEmployee: Future[EmployeeData] = result.map(employee => EmployeeData(employee.userName, employee.id, employee.title, employee.body))
    newEmployee.map(employee => ref.ask(employee))
    result.map(response => response)
  }

  override def employeesTopic(): Topic[api.EmployeeData] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        Logger.info("-------------------------------In Employees Topic----------------------------------")
        val a: Source[EventStreamElement[LagomassignmentEvent], NotUsed] = persistentEntityRegistry.eventStream(LagomassignmentEvent.Tag, fromOffset)
        Logger.info("-------------------------------- a = " + a)
        Logger.info("---------------------After a-----------------------------")
          val b: Source[(api.EmployeeData, Offset), NotUsed] = a.map(ev => { Logger.info("-----------------------Should go to convert now------------------")
            (convertEvent(ev), ev.offset)
          })
        Logger.info("--------------------After b and b = " + b)
        b
    }

  private def convertEvent(employeeEvent: EventStreamElement[LagomassignmentEvent]): api.EmployeeData = {
    Logger.info("----------------------In Convert Event------------------------------")
    employeeEvent.event match {
      case EmployeeDataChanged(name, id, title, body) => Logger.info("------------------ matched with EmployeeDataChanged------------------------")
        api.EmployeeData(name, id, title, body)
    }
  }
}
