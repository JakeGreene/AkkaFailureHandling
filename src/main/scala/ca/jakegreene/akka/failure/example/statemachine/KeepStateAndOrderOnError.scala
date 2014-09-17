package ca.jakegreene.akka.failure.example.statemachine

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import ca.jakegreene.akka.failure.example._

object KeepStateAndOrderOnError extends App {
  val system = ActorSystem("restart-system")
  val supervisor = system.actorOf(Props[FailureReactingSupervisor])
  
  implicit val timeout = Timeout(1.minute)
  import system.dispatcher
  
  val responses = Future.traverse(0 to 5)(id => supervisor ? WriteProduct(Product(id)))
  responses.foreach(_ => system.shutdown)
}