package edu.knoldus.assignment.consumer.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  * Created by knoldus on 26/8/17.
  */
trait LagomAssignmentConsumerService extends Service {

  override def descriptor: Descriptor = {
    import Service._

    named("consumer-service")
        .withCalls(
          pathCall("/api/consumer", consumerPrintMessage _)
        )
      .withAutoAcl(true)

  }

  def consumerPrintMessage(): ServiceCall[NotUsed, String]

}
