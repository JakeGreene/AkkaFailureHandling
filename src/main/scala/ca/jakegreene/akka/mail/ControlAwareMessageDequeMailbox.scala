package ca.jakegreene.akka.mail

import java.util.Deque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingDeque
import akka.dispatch.MailboxType
import java.util.Queue
import akka.dispatch.Envelope
import akka.actor.ActorSystem
import com.typesafe.config.Config
import akka.dispatch.MessageQueue
import akka.actor.ActorRef
import akka.dispatch.UnboundedDequeBasedMessageQueue
import akka.dispatch.ProducesMessageQueue
import akka.dispatch.ControlAwareMessageQueueSemantics
import akka.dispatch.ControlMessage

final case class UnboundedControlAwareMessageDequeMailbox() extends MailboxType with ProducesMessageQueue[UnboundedControlAwareMessageDequeMailbox.MessageQueue] {
  def this(settings: ActorSystem.Settings, config: Config) = this()
  final override def create(owner: Option[ActorRef], system: Option[ActorSystem]): MessageQueue = {
    new UnboundedControlAwareMessageDequeMailbox.MessageQueue()
  }
}

object UnboundedControlAwareMessageDequeMailbox {
  class MessageQueue extends ControlAwareMessageQueueSemantics with UnboundedDequeBasedMessageQueue {
    val controlQueue: Deque[Envelope] = new LinkedBlockingDeque[Envelope]()
    val queue: Deque[Envelope] = new LinkedBlockingDeque[Envelope]()
    
    override def enqueue(receiver: ActorRef, handle: Envelope) = super[ControlAwareMessageQueueSemantics].enqueue(receiver, handle)
    
    override def dequeue(): Envelope = super[ControlAwareMessageQueueSemantics].dequeue()
    
    override def enqueueFirst(receiver: ActorRef, handle: Envelope): Unit = handle match {
      case envelope @ Envelope(_: ControlMessage, _) => controlQueue addFirst envelope
      case envelope => queue addFirst envelope
    }
  }
}