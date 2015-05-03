package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scodec.bits._
import scodec.{Codec, DecodeResult}

@RunWith(classOf[JUnitRunner])
class PublishTest extends FunSuite {

  test("create/encode/decode publish packet") {

    val fh = FixedHeader(dup = false, qos = 1)
    val publishPacket = PublishPacket(fh, "test/topic", Some(12345), ByteVector("helloworld".getBytes))

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(publishPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(publishPacket, bin""))

    val publish = packet.require.value.asInstanceOf[PublishPacket]

    assert(publish.fixedHeader.dup === false)
    assert(publish.fixedHeader.qos === 1)
    assert(publish.fixedHeader.retain === false)
    assert(publish.packetId === Some(12345))
    assert(publish.topic === "test/topic")
    assert(publish.payload === ByteVector("helloworld".getBytes))
  }


  test("create/encode/decode publish packet - retain & dup") {
    val fh = FixedHeader(dup = true, qos = 1, retain = true)
    val publishPacket = PublishPacket(fh, "test/topic", Some(12345), ByteVector("helloworld".getBytes))

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(publishPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(publishPacket, bin""))

    val publish = packet.require.value.asInstanceOf[PublishPacket]

    assert(publish.fixedHeader.dup === true)
    assert(publish.fixedHeader.qos === 1)
    assert(publish.fixedHeader.retain === true)
    assert(publish.packetId === Some(12345))
    assert(publish.topic === "test/topic")
    assert(publish.payload === ByteVector("helloworld".getBytes))
  }

  test("create/encode/decode publish packet - qos 0, packetId None") {
    val fh = FixedHeader(dup = false, qos = 0)
    val publishPacket = PublishPacket(fh, "test/topic", None, ByteVector("helloworld".getBytes))

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(publishPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(publishPacket, bin""))

    val publish = packet.require.value.asInstanceOf[PublishPacket]

    assert(publish.fixedHeader.dup === false)
    assert(publish.fixedHeader.qos === 0)
    assert(publish.fixedHeader.retain === false)
    assert(publish.packetId === None)
    assert(publish.topic === "test/topic")
    assert(publish.payload === ByteVector("helloworld".getBytes))
  }

  test("encode/decode publish packet - empty payload") {
    val fh = FixedHeader(dup = false, qos = 1)
    val publishPacket = PublishPacket(fh, "test/topic", Some(12345), ByteVector.empty)

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(publishPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(publishPacket, bin""))

    val publish = packet.require.value.asInstanceOf[PublishPacket]

    assert(publish.fixedHeader.dup === false)
    assert(publish.fixedHeader.qos === 1)
    assert(publish.fixedHeader.retain === false)
    assert(publish.packetId === Some(12345))
    assert(publish.topic === "test/topic")
    assert(publish.payload === ByteVector.empty)
  }

  test("create/encode/decode puback packet") {
    val fh = FixedHeader()
    val pubackPacket = PubAckPacket(fh, 12345)

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(pubackPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(pubackPacket, bin""))

    val puback = packet.require.value.asInstanceOf[PubAckPacket]

    assert(puback.packetId === 12345)
  }

  test("create/encode/decode pubrec packet") {
    val fh = FixedHeader()
    val pubrecPacket = PubRecPacket(fh, 12345)

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(pubrecPacket).require)

    assert(packet.isSuccessful === true)

    assert(packet.require === DecodeResult(pubrecPacket, bin""))

    val pubrec = packet.require.value.asInstanceOf[PubRecPacket]

    assert(pubrec.packetId === 12345)
  }

  test("create/encode/decode pubrel packet") {
    val fh = FixedHeader()
    val pubrelPacket = PubRelPacket(fh, 12345)

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(pubrelPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(pubrelPacket, bin""))

    val pubrel = packet.require.value.asInstanceOf[PubRelPacket]

    assert(pubrel.packetId === 12345)
  }

  test("create/encode/decode pubcomb packet") {
    val fh = FixedHeader()
    val pubcompPacket = PubCompPacket(fh, 12345)

    val packet = Codec[ControlPacket].decode(Codec[ControlPacket].encode(pubcompPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(pubcompPacket, bin""))

    val pubcomp = packet.require.value.asInstanceOf[PubCompPacket]

    assert(pubcomp.packetId === 12345)
  }

}
