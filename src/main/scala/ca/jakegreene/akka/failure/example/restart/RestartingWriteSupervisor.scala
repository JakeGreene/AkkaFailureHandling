package ca.jakegreene.akka.failure.example.restart

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.actor.Props
import ca.jakegreene.akka.failure.example._

class RestartingWriteSupervisor(dataProvider: DatabaseClientProvider) extends Actor {
  
  val writer = context.actorOf(Props(classOf[RestartingProductWriter], dataProvider.get))
  
  def receive: Receive = {
    case msg => writer forward msg
  }
  
  /*
   * This simple supervisor strategy will restart any actor that fails with a
   * ConnectionException.
   * 
   * "Restart" implies killing the current actor and creating a new instance. The new instance
   * will use the same mailbox as the failing actor
   */
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 100) {
    case e: ConnectionException => Restart
  }
}