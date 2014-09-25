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
import ca.jakegreene.akka.failure.example.Product
import ca.jakegreene.akka.failure.example.Product

@RunWith(classOf[JUnitRunner])
class SimpleProductWriterSpec extends TestKit(ActorSystem("system-with-no-handling")) 
with WordSpecLike with MustMatchers with StopSystemAfterAll with MockitoSugar {
  
  "SimpleProductWriter" must {
        
    "persist to database if message is not corrupted" in {
     
      // Given
      val databaseClient = mock[DatabaseClient]
      val product = Product(1)     
      val actor = TestActorRef(Props(new SimpleProductWriter(databaseClient)))
      
      // When
      actor ! WriteProduct(product)
      
      // Then
      verify(databaseClient).write("PRODUCTS", product)
    }
    
    "throw an exception if persistance fails" in {
     
      // Given
      val databaseClient = mock[DatabaseClient]
      when(databaseClient.write(any(), any())).thenThrow(ConnectionException("Error!"))         
      val actor = TestActorRef(Props(new SimpleProductWriter(databaseClient)))
      
      // Then
      intercept[ConnectionException] {
    	  actor.receive(WriteProduct(Product(1)))
      }
    }
  }
}