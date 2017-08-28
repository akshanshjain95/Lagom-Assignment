package edu.knoldus.assignment.consumer.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import com.softwaremill.macwire.wire
import edu.knoldus.assignment.consumer.api.LagomAssignmentConsumerService
import edu.knoldus.assignment.producer.api.LagomassignmentService
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * Created by knoldus on 27/8/17.
  */
class LagomAssignmentConsumerLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomAssignmentConsumerApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomAssignmentConsumerApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomassignmentService])
}

abstract class LagomAssignmentConsumerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    //with CassandraPersistenceComponents
    with LagomKafkaClientComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomAssignmentConsumerService](wire[LagomAssignmentConsumerServiceImpl])

  lazy val employeeService = serviceClient.implement[LagomassignmentService]

  // Register the JSON serializer registry
  //override lazy val jsonSerializerRegistry = LagomassignmentSerializerRegistry

  //lazy val externalEmployeeService = serviceClient.implement[ExternalEmployeeService]

  // Register the Lagom-Assignment persistent entity
  //persistentEntityRegistry.register(wire[LagomassignmentEntity])
}
