package plantae.citrus.mqtt.dto2

import scodec.Codec
import scodec.bits._
import scodec.codecs._

case class TestClass(a: Boolean, b: Boolean)
object TestClass {
  implicit val testClassCodec = (bool :: bool).as[TestClass]
}




object Test extends App {

  val aa = TestClass(true, true)
  val bb = Codec[TestClass].encode(aa).require



  val c = Codec[TestClass].decode(bin"11")
  val e = c.require.value
  val d = Codec[TestClass].decode(bb)


  println("hello world")

}