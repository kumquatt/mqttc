package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scodec.bits._
import scodec.{Codec, DecodeResult}

@RunWith(classOf[JUnitRunner])
class UnsubscribeTest extends FunSuite {

  test("encode/decode test of UnsubscribePacket") {
    val fh = FixedHeader()
    val topicFilter = List[String]("topic/1", "topic/2", "topic/3")
    val unsubscribePacket = UnsubscribePacket(fh, 12345, topicFilter)

    val packet = Codec[Packet].decode(Codec[Packet].encode(unsubscribePacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(unsubscribePacket, bin""))

  }

  test("create/encode/decode UnsubscribePacket") {

    val fh = FixedHeader()
    val topicFilter = List[String]("topic/1", "topic/2", "topic/3")
    val unsubscribePacket = UnsubscribePacket(fh, 12345, topicFilter)

    val packet = Codec[Packet].decode(Codec[Packet].encode(unsubscribePacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(unsubscribePacket, bin""))

    val unsubscribe = packet.require.value.asInstanceOf[UnsubscribePacket]

    assert(unsubscribe.packetId == 12345)
    assert(unsubscribe.topicFilter === topicFilter)
  }

  test("encode/decode test of UnsubAckPacket") {

    val fh = FixedHeader()
    val unsubackPacket = UnsubAckPacket(fh, 12345)

    val packet = Codec[Packet].decode(Codec[Packet].encode(unsubackPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(unsubackPacket, bin""))
  }

  test("create/encode/decode unsuback packet") {

    val fh = FixedHeader()
    val unsubackPacket = UnsubAckPacket(fh, 12345)

    val packet = Codec[Packet].decode(Codec[Packet].encode(unsubackPacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(unsubackPacket, bin""))

    val unsuback = packet.require.value.asInstanceOf[UnsubAckPacket]
    assert(unsuback.packetId == 12345)
  }
}
