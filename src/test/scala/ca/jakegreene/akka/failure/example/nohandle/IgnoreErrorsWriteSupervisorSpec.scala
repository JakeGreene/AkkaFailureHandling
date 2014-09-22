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

@RunWith(classOf[JUnitRunner])
class IgnoreErrorsWriteSupervisorSpec extends TestKit(ActorSystem("system-with-no-handling")) 
with WordSpecLike with MustMatchers with StopSystemAfterAll with MockitoSugar {
  
  "IgnoreErrorsWriteSupervisor forward message to the SimpleProductWrite which" must {
    
    "persist to the database if there is no exception" in {
     
      // Given
      val databaseClient = mock[DatabaseClient]
     
      val dataProvider = mock[DatabaseClientProvider]
      when(dataProvider.get).thenReturn(databaseClient)
      
      val actor = system.actorOf(Props(new IgnoreErrorsWriteSupervisor(dataProvider)))
      
      val product = Product(1)
      // When
      actor ! WriteProduct(product)
      
      // Then
      verify(databaseClient).write("PRODUCTS", product)
    }
    
    "not persist to the database if there is an exception" in {
 
      // Given
      val product = Product(1)
     
      val databaseClient = mock[DatabaseClient]
      when(databaseClient.write(any(), any())).thenThrow(ConnectionException("Error!"))
     
      
      val actor = system.actorOf(Props(new SimpleProductWriter(databaseClient)))
      
      // When
      actor ! WriteProduct(product)
      
      // Then
      intercept[ConnectionException] {
    	  actor
      }  
    }
        
    "resume handling messages when exception occurs" in {
 
      // Given
      val product1 = Product(1)
      val product2 = Product(2)
      val databaseClient = mock[DatabaseClient]
      when(databaseClient.write("PRODUCTS", product1)).thenThrow(ConnectionException("Error!"))
      val dataProvider = mock[DatabaseClientProvider]
      when(dataProvider.get).thenReturn(databaseClient)
      val actor = system.actorOf(Props(new IgnoreErrorsWriteSupervisor(dataProvider)))
    
      // When
      intercept[ConnectionException] {
    	  actor ! WriteProduct(product1)
      }
      
      // When
      actor ! WriteProduct(product2)
      
      // Then
      verify(databaseClient.write("PRODUCTS", product1), times(1))
      verify(databaseClient.write("PRODUCTS", product2), times(1))     
    }
  }

}