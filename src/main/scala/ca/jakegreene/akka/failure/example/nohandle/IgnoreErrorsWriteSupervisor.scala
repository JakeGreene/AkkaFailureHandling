package ca.jakegreene.akka.failure.example.nohandle

import akka.actor.Actor
import akka.actor.Props
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import ca.jakegreene.akka.failure.example.ConnectionException
import ca.jakegreene.akka.failure.example.DatabaseClient
import ca.jakegreene.akka.failure.example.DatabaseClientProvider

class IgnoreErrorsWriteSupervisor(dataProvider: DatabaseClientProvider) extends Actor {
  
  val writer = context.actorOf(Props(classOf[SimpleProductWriter], dataProvider.get))
  
  def receive: Receive = {
    case msg => writer forward msg 
  }
  
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 100) {
    case e: ConnectionException => Resume
  }
}