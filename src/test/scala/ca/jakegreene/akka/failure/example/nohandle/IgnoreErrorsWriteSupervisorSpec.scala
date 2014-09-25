package ca.jakegreene.akka.failure.example.nohandle

import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import ca.jakegreene.akka.failure.example._
import ca.jakegreene.akka.failure.example.DatabaseClient
import ca.jakegreene.akka.failure.example.DatabaseClientProvider
import org.mockito.Mockito._
import akka.actor.Props
import org.mockito.Matchers._
import akka.testkit.TestActorRef
import akka.actor.ActorRef
import akka.actor.Actor

@RunWith(classOf[JUnitRunner])
class IgnoreErrorsWriteSupervisorSpec extends TestKit(ActorSystem("system-with-no-handling")) 
with WordSpecLike with MustMatchers with StopSystemAfterAll with MockitoSugar {
  
  "IgnoreErrorsWriteSupervisor" must {
    
		"forward message to its child" in {
		 
		  // Given    
		  val mock = Props(new Actor {
			  def receive = {
			    case x => testActor forward x
			  }
		  })
		  
		  val actor = system.actorOf(Props(new IgnoreErrorsWriteSupervisor(mock)))
		  
		  // When
		  actor ! "hola"
		  
		  // Then
		  expectMsg("hola")
		}
		
		"resume with eception" in {
		 
		  // Given    
		  val mock = Props(new Actor {
			  def receive = {
			    case ex: Exception => ex
			    case x: String => testActor forward x
			  }
		  })
		  
		  val actor = system.actorOf(Props(new IgnoreErrorsWriteSupervisor(mock)))
		  
		  // When exception is thrown
		  actor ! ConnectionException
		  
		  // When valid message is passed
		  actor ! "hola"
		  
		  // Then actor should resume
		  expectMsg("hola")
		}
    }		
}