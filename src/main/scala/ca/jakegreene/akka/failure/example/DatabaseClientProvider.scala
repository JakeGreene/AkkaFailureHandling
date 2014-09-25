package ca.jakegreene.akka.failure.example

class DatabaseClientProvider {
	def get: DatabaseClient = new DatabaseClient(0.75)
	def get(failRate: Double) = new DatabaseClient(failRate)
}