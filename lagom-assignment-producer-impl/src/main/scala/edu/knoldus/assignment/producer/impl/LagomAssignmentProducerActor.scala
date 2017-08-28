package edu.knoldus.assignment.producer.impl

import akka.actor.{Actor, Props}
import edu.knoldus.assignment.producer.api
import edu.knoldus.assignment.producer.api.{ExternalEmployeeService, LagomassignmentService}
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by knoldus on 28/8/17.
  */
class LagomAssignmentProducerActor(externalEmployeeService: ExternalEmployeeService) extends Actor {

  override def receive: PartialFunction[Any, Unit] = {
    case print: String => Logger.info("---------------------------In Actor print case------------------------------")
      hitRoute()
  }

  def hitRoute(): Unit = {
    Logger.info("-----------------------------In hit route--------------------------------")
    val result: Future[api.EmployeeData] = externalEmployeeService.getEmployee.invoke()
    result onComplete{
      case Success(employee) => Logger.info("Name = " + employee.userName + ", id = " + employee.id + ", title = " + employee.title + ", body = " + employee.body)

      case Failure(error) => error.getStackTrace
    }
  }
}

object LagomAssignmentProducerActor {

  def props(externalEmployeeService: ExternalEmployeeService): Props = Props(classOf[LagomAssignmentProducerActor], externalEmployeeService)

}