package ca.jakegreene.akka.failure.example.restart

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import ca.jakegreene.akka.failure.example._

/**
 * The simplest message-retry logic uses the "Restart" directive: any time an
 * actor fails to process a message the supervisor will tell it to "Restart".
 * The failing actor will then handle the failed message in some way using
 * the `preRestart` handler (often by sending the message to itself to be processed later)
 */
object RestartOnError extends App { 
  val system = ActorSystem("restart-system")
  val supervisor = system.actorOf(Props[RestartingWriteSupervisor])
  
  implicit val timeout = Timeout(1.minute)
  import system.dispatcher
  
  val responses = Future.traverse(0 to 5)(id => supervisor ? WriteProduct(Product(id)))
  responses.foreach(_ => system.shutdown)
}