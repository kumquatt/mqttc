package plantae.citrus.mqtt.packet

import scodec.Codec
import scodec.codecs._


case class TestClass(a: Boolean, b: Boolean)
object TestClass {
  implicit val testClassCodec = (bool :: bool).as[TestClass]

}



object Test extends App {

//  val aa = TestClass(true, true)
//  val bb = Codec[TestClass].encode(aa).require
//
//  val c = Codec[TestClass].decode(bin"11")
//  val e = c.require.value
//  val d = Codec[TestClass].decode(bb)
//
//
//  val test = uint8.decode(bin"10000000111111110000000100000010")
//
//  test.map(a => println(a))
//
//
//  println("hello world")
//
//
//  val connack = CONNACK(true, BYTE(0))
//
//  println(connack.encode)
//
//  val connack2 = Codec[Packet].encode(ConnAckPacket(FixedHeader(false, 0), 0, true, 0)).require
//
//  println(connack2)


//  val aaa = Codec[Sprocket].encode(Wocket(3, true)).require
//  println(aaa)

//  val test1 = Codec[Packet].encode(TestPacket(1, 1)).require
//  println(test1)

//  val test2 = Codec[Packet].decode(test1)
//  println(test2)

//  val test3 = Codec[Packet].decode(connack2)
//  println(test3)
}