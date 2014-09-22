package ca.jakegreene.akka.failure.example


case class ConnectionException(msg: String) extends Exception(msg)

class DatabaseClient(error: Double) {
  @throws(classOf[ConnectionException])
  def write(table: String, entity: Any): Unit = {
    if (Math.random() < error) {
      println(s"Failed to Persist $entity to $table")
      throw new ConnectionException(s"Failed to store $entity to $table")
    } else {
      println(s"Persisting $entity to $table")
    }
  }
  
  override def toString(): String = {
    s"DatabaseClient(errorRate=$error)"
  }
}