package ca.jakegreene.akka.failure.example.statemachine

import akka.actor.Actor
import ca.jakegreene.akka.failure.example._
import akka.actor.Stash
import akka.actor.UnboundedStash
import akka.dispatch.ControlMessage
import GuardedReceive._

case class NewClient(db: DatabaseClient) extends ControlMessage

/**
 * A StatefulProductWriter is a product writer which is able to retry messages while maintaining message order and internal state.
 */
class StatefulProductWriter(var db: DatabaseClient) extends Actor with UnboundedStash {
  
  /*
   * Our state is maintained after failure. This var will
   * properly represent the number of processed messages
   */
  var messagesSeen = 0
  
  /*
   * The callback `onDbFailure` is used if an exception is thrown by any
   * of the cases in this `GuardedReceive`
   */
  def receive = GuardedReceive(onDbFailure) {
    case WriteProduct(product) => 
      db.write("PRODUCTS", product)
      messagesSeen += 1
      println(s"This actor has seen $messagesSeen messages")
      sender ! ProductWritten(product)
  }
  
  /*
   * We need to wait for a new database client before we can process any other messages.
   */
  def receiveWhenDbDown: Receive = {
    case NewClient(client) =>
      println(s"Receive New Client $client")
      db = client
      unstashAll()
      context.become(receive)
    case msg => 
      println(s"Stashing $msg")
      stash()
  }
  
  def onDbFailure: FailureHandler = {
    case MessageFailure(_, e: ConnectionException) =>
      context.become(receiveWhenDbDown)
      println("Stashing failed message")
      stash() // stash the message that failed
  }
}