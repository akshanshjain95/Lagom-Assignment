package edu.knoldus.assignment.producer.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.{LagomKafkaClientComponents, LagomKafkaComponents}
import com.softwaremill.macwire._
import edu.knoldus.assignment.producer.api.{ExternalEmployeeService, LagomassignmentService}

class LagomassignmentLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomassignmentApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomassignmentApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomassignmentService])
}

abstract class LagomassignmentApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomassignmentService](wire[LagomassignmentServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = LagomassignmentSerializerRegistry

  lazy val externalEmployeeService = serviceClient.implement[ExternalEmployeeService]

  // Register the Lagom-Assignment persistent entity
  persistentEntityRegistry.register(wire[LagomassignmentEntity])
}
