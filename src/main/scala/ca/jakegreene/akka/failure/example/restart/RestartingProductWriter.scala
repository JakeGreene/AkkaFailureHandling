package ca.jakegreene.akka.failure.example

import akka.actor.Actor
import akka.actor.Stash
import ca.jakegreene.akka.failure.example._


/**
 * RestartProductWriter is a product writer that expects to be restarted on failure.
 * 
 * Restarting gives us the ability to use the `preRestart` hook to get the message we failed on
 * BUT it removes any state we may have been keeping
 */
class RestartingProductWriter(db: DatabaseClient) extends Actor {
  
  /*
   * This is state we are going to maintain. The biggest advantage and disadvantage of the 
   * "Restart" directive is that it cleans out any state we might have. It is a good thing
   * because our state might be the reason we are failing. It is a bad thing because the state
   * within our actor might be important.
   * 
   * i.e. This var will be constantly reset to 0 so it will not represent the total number of messages
   * processed but will instead be the total number of messages *this actor* has processed.
   */
  var messagesSeen = 0
  
  override def preRestart(reason: Throwable, msg: Option[Any]): Unit = {
    /*
     * In the event of a failure we need to try the message again. This will break
     * message ordering
     */ 
    msg.foreach(self forward _)
  }
  
  def receive: Receive = {
    case WriteProduct(product) => {
      db.write("PRODUCTS", product)
      messagesSeen += 1
      println(s"This actor has seen $messagesSeen messages")
      sender ! ProductWritten
    }
  }
}