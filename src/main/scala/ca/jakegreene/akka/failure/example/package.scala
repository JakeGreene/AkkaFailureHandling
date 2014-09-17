package ca.jakegreene.akka.failure

package example {
  case class Product(id: Long) extends AnyVal
  case class WriteProduct(product: Product)
  case class ProductWritten(product: Product)
}