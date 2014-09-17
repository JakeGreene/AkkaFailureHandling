package ca.jakegreene.akka.failure.example.statemachine

import akka.actor.Actor.Receive

object GuardedReceive {
  private val doNothing: PartialFunction[Any, Unit] = { case _ => }
  
  def apply(onError: PartialFunction[Throwable, Unit])(receive: Receive) = new GuardedReceive(receive, onError)
}

/**
 * A GaurdedReceive acts like a normal Receive but will use the callback `onError` in the event of any Exceptions. 
 * These exceptions are still thrown so that the parent Actor can perform its supervision logic
 */
class GuardedReceive(receive: Receive, onError: PartialFunction[Throwable, Unit]) extends Receive {
  private val handleException = onError.orElse(GuardedReceive.doNothing)
  
  def apply(a: Any): Unit = {
    try {
      receive(a)
    } catch {
      case e: Throwable =>
        handleException(e)
        // The exception must be thrown in order for the supervisor to know of the failure
        throw e 
    }
  }

  def isDefinedAt(a: Any) = receive.isDefinedAt(a)
}