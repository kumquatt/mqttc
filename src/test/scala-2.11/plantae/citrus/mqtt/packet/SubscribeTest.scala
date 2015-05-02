package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import plantae.citrus.mqtt.dto.{BYTE, STRING, INT}
import plantae.citrus.mqttclient.mqtt.dto.subscribe._
import scodec.{DecodeResult, Codec}
import scodec.bits._

@RunWith(classOf[JUnitRunner])
class SubscribeTest extends FunSuite{

  test("encode/decode test of SubscribePacket") {
    val fh = FixedHeader()
    val topicFilter = List(("testTopic", 0))
    val subscribePacket = SubscribePacket(fh, 100, topicFilter)

    val packet = Codec[Packet].decode(Codec[Packet].encode(subscribePacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(subscribePacket, bin""))
  }


  test("create/encode/decode SubscribePacket") {
    val fh = FixedHeader()
    val topicFilter = List(("topic/1", 0), ("topic/2", 1), ("topic/3", 2), ("topic/4", 0))
    val subscribePacket = SubscribePacket(fh, 40293, topicFilter)

    val packet = Codec[Packet].decode(Codec[Packet].encode(subscribePacket).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(subscribePacket, bin""))

    val subscribe : SubscribePacket = packet.require.value.asInstanceOf[SubscribePacket]

    assert(subscribe.packetId === 40293)
    assert(subscribe.topicFilter === topicFilter)

  }

  test("encode/decode test of SubAckPacket") {
    val fh = FixedHeader()
    val topicFilter = List(0, 1, 2, 80)
    val subackPakcet = SubAckPacket(fh, 40293, topicFilter)

    val packet = Codec[Packet].decode(Codec[Packet].encode(subackPakcet).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(subackPakcet, bin""))

  }

  test("create/encode/decode test suback packet") {
    val fh = FixedHeader()
    val topicFilter = List(0, 1, 2, 80)
    val subackPakcet = SubAckPacket(fh, 40293, topicFilter)

    val packet = Codec[Packet].decode(Codec[Packet].encode(subackPakcet).require)

    assert(packet.isSuccessful === true)
    assert(packet.require === DecodeResult(subackPakcet, bin""))

    val suback = packet.require.value.asInstanceOf[SubAckPacket]

    assert(suback.packetId == 40293)
    assert(suback.returnCode === topicFilter)
  }

}
