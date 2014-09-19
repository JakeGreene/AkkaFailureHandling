# Akka Failure Handling Examples

A collection of examples for handling failure within Akka

### Ignore Failure

The first example uses the simplest error handling possible in Akka: ignore failure completely. A `Resume` directive is sent from the supervisor and the child writer will drop the failing message and move to the next message in the queue. Use this strategy when the message is the reason for failure.

### Restart

The most common approach to handling failure is to restart the actor with the `Restart` directive and use the `preRestart` hook to deal with the message in some way. The provided example forwards the message back onto the mailbox so that it can be processed later. This strategy is suitable when destroying an actor's state is appropriate and message order does not matter.

### Stash

The final example is able to retry messages while ensuring that every message is processed in order all while the internal state of the actor is maintained. The caveat is that this strategy is more complex than the others. The `Resume` directive is used in order to keep the actor instance alive and the `Stash` trait is used store all of the messages that could not be processed. Additionally, the actor is a simple state machine which will flip to a "database down" state until a new client is sent to it. For efficiencies sake a control-message-aware mailbox is used to allow the `NewClient` messages to pass right past the other queued messages.

It is possible to mix these strategies depending on the needs of the actor but be warned: a complicated mix of strategies for handling failure may indicate the actor is attempting to do too much.

