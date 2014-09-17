package ca.jakegreene.akka.failure.example.nohandle

import akka.actor.Actor
import akka.actor.actorRef2Scala
import ca.jakegreene.akka.failure.example._

case object Ping
case object Pong

class SimpleProductWriter(db: DatabaseClient) extends Actor {
  def receive: Receive = {
    case WriteProduct(product) => db.write("PRODUCTS", product)
    case Ping => sender ! Pong
  }
}