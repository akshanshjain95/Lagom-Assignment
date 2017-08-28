package edu.knoldus.assignment.consumer.impl

import akka.{Done, NotUsed}
import akka.actor._
import akka.stream.scaladsl.Flow
import com.lightbend.lagom.scaladsl.api.ServiceCall
import edu.knoldus.assignment.consumer.api.LagomAssignmentConsumerService
import edu.knoldus.assignment.producer.api.{EmployeeData, LagomassignmentService}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Created by knoldus on 26/8/17.
  */
class LagomAssignmentConsumerServiceImpl(lagomAssignmentService: LagomassignmentService)(implicit ec: ExecutionContext) extends LagomAssignmentConsumerService  {

  lagomAssignmentService.employeesTopic()
    .subscribe
    .atLeastOnce(
      Flow[EmployeeData].map{ msg =>
        Logger.info("Subscribing")
        printMessage(msg)
        Done
      }
    )

  private def printMessage(message: EmployeeData) = {
    Logger.info(message.userName+ ", " + message.id + ", " + message.title + ", " + message.body)
  }

  /*val actorSystem = ActorSystem("EmployeeSystemActor")

  val lagomAssignmentConsumerServiceActor = actorSystem.actorOf(LagomAssignmentConsumerServiceActor.props(lagomAssignmentService))

  actorSystem.scheduler.schedule(
    10 seconds,
    10 seconds,
    lagomAssignmentConsumerServiceActor,
    "print")*/

  override def consumerPrintMessage(): ServiceCall[NotUsed, String] = {
    ServiceCall { _ =>
      Future.successful("Consumer Output")
    }
  }

}
