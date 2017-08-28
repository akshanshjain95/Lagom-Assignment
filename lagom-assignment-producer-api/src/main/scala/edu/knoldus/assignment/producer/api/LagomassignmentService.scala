package edu.knoldus.assignment.producer.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object LagomassignmentService  {
  val TOPIC_NAME = "employee"
}

/**
  * The Lagom-Assignment service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomassignmentService.
  */
trait LagomassignmentService extends Service {

  /**
    * Example: curl http://localhost:9000/api/hello/Alice
    */
  def hello(name: String): ServiceCall[Name, EmployeeData]

  /**
    * This gets published to Kafka.
    */
  def employeesTopic(): Topic[EmployeeData]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("lagom-assignment")
      .withCalls(
        pathCall("/api/employee/:id", hello _)
      )
      .withTopics(
        topic(LagomassignmentService.TOPIC_NAME, employeesTopic _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

trait ExternalEmployeeService extends Service {

  def getEmployee: ServiceCall[NotUsed, EmployeeData]

  override def descriptor: Descriptor = {
    import Service._

    named("external-mock-service")
      .withCalls(
        pathCall("/v2/59a1137b110000d60b6442fd", getEmployee _)
      )
      .withAutoAcl(true)
  }
}

case class EmployeeData(userName: String, id: Int, title:String, body: String)

object EmployeeData {

  implicit val format: Format[EmployeeData] = Json.format[EmployeeData]

}

case class Name(name: String)

object Name {

  implicit val format: Format[Name] = Json.format[Name]

}
