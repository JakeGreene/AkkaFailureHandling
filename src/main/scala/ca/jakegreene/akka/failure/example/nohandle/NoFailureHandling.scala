package ca.jakegreene.akka.failure.example.nohandle

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.actorRef2Scala
import ca.jakegreene.akka.failure.example._
import ca.jakegreene.akka.failure._

/**
 * The NoFailureHandling Scenario:
 * 
 * This is the base case of no error handling. In the event of failure
 * the failing actor will throw an exception and the supervisor will allow that actor to
 * continue processing its mailbox without going back to the failed message.
 * 
 * This results in lost data and is not recommended for mission critical data. Its only
 * advantage is that is is simple
 */
object NoFailureHandling extends App {
  val system = ActorSystem("system-with-no-handling")
  val supervisor = system.actorOf(Props(new IgnoreErrorsWriteSupervisor(new DatabaseClientProvider)))
  (0L until 5).foreach { id =>
    supervisor ! WriteProduct(Product(id))
  }
  
  implicit val timeout = Timeout(1.minute)
  import system.dispatcher
  /* 
   * This is a simple way to find out when work has been completed. 
   * This is assuming messages are processed and completed in order 
   */
  (supervisor ? Ping).foreach(p => system.shutdown)
}