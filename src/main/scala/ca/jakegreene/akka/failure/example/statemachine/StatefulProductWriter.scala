package ca.jakegreene.akka.failure.example.statemachine

import akka.actor.Actor
import ca.jakegreene.akka.failure.example._
import akka.actor.Stash
import akka.actor.UnboundedStash
import akka.dispatch.ControlMessage

case class NewClient(db: DatabaseClient) extends ControlMessage

/**
 * 
 */
class StatefulProductWriter(var db: DatabaseClient) extends Actor with UnboundedStash {
  
  /*
   * Our state is maintained after failure. This var will
   * properly represent the number of processed messages
   */
  var messagesSeen = 0
  
  def receive = GuardedReceive(onDbFailure) {
    case WriteProduct(product) => 
      db.write("PRODUCTS", product)
      messagesSeen += 1
      println(s"This actor has seen $messagesSeen messages")
      sender ! ProductWritten(product)
  }
  
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
  
  def onDbFailure: PartialFunction[Throwable, Unit] = {
    case e: ConnectionException => 
      context.become(receiveWhenDbDown)
      println("Stashing failed message")
      stash() // stash the message that failed
  }
}