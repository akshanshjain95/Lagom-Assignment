package edu.knoldus.assignment.producer.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import edu.knoldus.assignment.producer.api.Name
import play.api.Logger
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class LagomassignmentEntity extends PersistentEntity {

  override type Command = LagomassignmentCommand[_]
  override type Event = LagomassignmentEvent
  override type State = LagomassignmentState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: LagomassignmentState = LagomassignmentState(2062, LocalDateTime.now.toString)

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case LagomassignmentState(message, _) => Logger.info("--------------------In behavior case LagomassignmentState---------------------------")
      Actions().onCommand[EmployeeData, Done] {

      // Command handler for the UseGreetingMessage command
      case (EmployeeData(name, id, title, body), ctx, state) => Logger.info("---------------------- In case Name(name ctx state)---------------------")
        // In response to this command, we want to first persist it as a
        // GreetingMessageChanged event
        ctx.thenPersist(
          EmployeeDataChanged(name, id, title, body)
        ) { _: EmployeeDataChanged =>
          // Then once the event is successfully persisted, we respond with done.
          ctx.reply(Done)
        }

    }/*.onReadOnlyCommand[Hello, String] {

      // Command handler for the Hello command
      case (Hello(name), ctx, state) =>
        // Reply with a message built from the current message, and the name of
        // the person we're meant to say hello to.
        ctx.reply(s"$message, $name!")

    }*/.onEvent {
      // Event handler for the GreetingMessageChanged event
      case (EmployeeDataChanged(userName, id, title, body), state) =>
        // We simply update the current state to use the greeting message from
        // the event.
        LagomassignmentState(id, LocalDateTime.now().toString)

    }
  }
}

sealed trait LagomassignmentCommand[R] extends ReplyType[R]

case class LagomassignmentState(id: Int, timestamp: String)

object LagomassignmentState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshddot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[LagomassignmentState] = Json.format
}

sealed trait LagomassignmentEvent extends AggregateEvent[LagomassignmentEvent] {
  def aggregateTag = LagomassignmentEvent.Tag
}

object LagomassignmentEvent {
  val Tag = AggregateEventTag[LagomassignmentEvent]
}

object LagomassignmentSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[EmployeeDataChanged],
    JsonSerializer[Name]
  )
}

case class EmployeeDataChanged(userName: String, id: Int, title:String, body: String) extends LagomassignmentEvent

object EmployeeDataChanged {

  implicit val format: Format[EmployeeDataChanged] = Json.format[EmployeeDataChanged]

}

case class EmployeeData(userName: String, id: Int, title: String, body: String) extends LagomassignmentCommand[Done]

object EmployeeData {

  implicit val format: Format[EmployeeData] = Json.format[EmployeeData]

}
