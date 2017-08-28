package edu.knoldus.assignment.consumer.impl

import akka.Done
import akka.actor.{Actor, ActorRef, Props}
import akka.stream.scaladsl.Flow
import edu.knoldus.assignment.producer.api.{EmployeeData, LagomassignmentService}
import play.api.Logger

class LagomAssignmentConsumerServiceActor(lagomAssignmentService: LagomassignmentService) extends Actor {

  override def receive: PartialFunction[Any, Unit] = {

    case print: String => Logger.info("-------------------------------------------------------------")
      consumer()

  }

  def consumer() = {
    Logger.info("Inside Consumer")
    lagomAssignmentService.employeesTopic()
      .subscribe
      .atLeastOnce(
        Flow[EmployeeData].map{ msg =>
          Logger.info("Subscribing")
          printMessage(msg)
          Done
        }
      )
  }

  private def printMessage(message: EmployeeData) = {
    Logger.info(message.userName+ ", " + message.id + ", " + message.title + ", " + message.body)
  }

}

object LagomAssignmentConsumerServiceActor {

  def props(lagomassignmentService: LagomassignmentService): Props = Props(classOf[LagomAssignmentConsumerServiceActor], lagomassignmentService)

}
