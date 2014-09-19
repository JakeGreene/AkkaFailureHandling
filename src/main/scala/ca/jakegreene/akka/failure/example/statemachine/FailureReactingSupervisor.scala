package ca.jakegreene.akka.failure.example.statemachine

import akka.actor.Actor
import ca.jakegreene.akka.failure.example._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.actor.Props

case object DatabaseDown

class FailureReactingSupervisor extends Actor {
  
  var failureRate = 1.0

  val writer = context.actorOf(Props(classOf[StatefulProductWriter], new DatabaseClient(failureRate)), "stateful-product-writer")

  def receive: Receive = {
    /*
     *  We could have more complicated logic here to determine when the DB has come back up
     *  or to try a different DB in a pool of connections or even ask for help
     */
    case DatabaseDown => 
      failureRate = failureRate / 2.0
      writer ! NewClient(new DatabaseClient(failureRate))
    case msg => writer forward msg
  }

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 100) {
    case _: ConnectionException =>
      // Inform ourself that the database has gone down
      self ! DatabaseDown
      // Resume so that we maintain our important state
      Resume
  }
}