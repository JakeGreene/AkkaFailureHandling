package ca.jakegreene.akka.failure.example.statemachine

import akka.actor.Actor.Receive
import GuardedReceive._

case class MessageFailure(message: Any, failure: Throwable)

object GuardedReceive {
  type FailureHandler = PartialFunction[MessageFailure, Unit]
  private val doNothing: PartialFunction[Any, Unit] = { case _ => }
  
  def apply(onError: FailureHandler)(receive: Receive) = new GuardedReceive(receive, onError)
}

/**
 * A GaurdedReceive acts like a normal Receive but will use the callback `onError` in the event of any Exceptions. 
 * These exceptions are still thrown so that the parent Actor can perform its supervision logic
 */
class GuardedReceive(receive: Receive, onError: FailureHandler) extends Receive {
  private val handleException = onError.orElse(GuardedReceive.doNothing)
  
  def apply(message: Any): Unit = {
    try {
      receive(message)
    } catch {
      case e: Throwable =>
        handleException(MessageFailure(message, e))
        // The exception must be thrown in order for the supervisor to know of the failure
        throw e 
    }
  }

  def isDefinedAt(a: Any) = receive.isDefinedAt(a)
}