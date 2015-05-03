package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import plantae.citrus.mqtt.dto.{PUBLISHPAYLOAD, BYTE, INT, STRING}
import plantae.citrus.mqttclient.mqtt.dto.connect.{CONNACK, CONNECT}
import plantae.citrus.mqttclient.mqtt.dto.publish._
import plantae.citrus.mqttclient.mqtt.dto.subscribe.{TopicFilter, SUBSCRIBE, SUBACK}
import plantae.citrus.mqttclient.mqtt.dto.unsubscribe.{UNSUBSCRIBE, UNSUBACK}
import scodec.{DecodeResult, Codec}
import scodec.bits.BitVector
import scodec.bits._

// compatibility test between Packet and DTO
@RunWith(classOf[JUnitRunner])
class CompatibilityTest extends FunSuite {
  test("Connect encode Test with dto") {
    val connect1ByDto = CONNECT(STRING("client1"), true, None, None, INT(60)).encode

    val fixedHeader = FixedHeader()
    val connectVariableHeader = ConnectVariableHeader(false, false, false, 0, false, true, 60)
    val connect1ByPacket = Codec[ControlPacket].encode(ConnectPacket(fixedHeader, connectVariableHeader, "client1", None, None, None, None)).require

    assert(connect1ByPacket === BitVector(connect1ByDto))
  }

  test("Connect decode Test with dto") {
    val connect1ByDto = CONNECT(STRING("client1"), true, None, None, INT(60)).encode
    val connect = Codec[ControlPacket].decode(BitVector(connect1ByDto))

    assert(connect.isSuccessful === true)
    assert(connect.require.value.isInstanceOf[ConnectPacket] === true)

    assert(connect.require.value.asInstanceOf[ConnectPacket].clientId === "client1")
    assert(connect.require.value.asInstanceOf[ConnectPacket].variableHeader.keepAliveTime === 60)
  }

  test("Conack encode Test with dto") {
    val connackByDto = CONNACK(true, BYTE(0)).encode

    val fh = FixedHeader()
    val connackByPacket = Codec[ControlPacket].encode(ConnAckPacket(fh, true, 0)).require

    assert(connackByPacket === BitVector(connackByDto))
  }

  test("Conack decode Test with dto") {
    val conackByDto = CONNACK(true, BYTE(1)).encode
    val connack = Codec[ControlPacket].decode(BitVector(conackByDto))

    assert(connack.isSuccessful === true)
    assert(connack.require.value.isInstanceOf[ConnAckPacket] === true)

    assert(connack.require.value.asInstanceOf[ConnAckPacket].sessionPresentFlag === true)
    assert(connack.require.value.asInstanceOf[ConnAckPacket].returnCode === 1)
  }


  test("Discconect encode/decode Test with dto") {

  }


  test("PingReq encode/decode Test with dto") {
    //    val pingreqByDto = PINGREQ
    //    val pingreqByPacket = PingReqPacket(FixedHeader())
    //
    //    val a = pingreqByDto.encode
    //    assert(Codec[Packet].encode(pingreqByPacket).require === BitVector(pingreqByDto.encode))

  }

  test("PingResp encode/decode Test with dto") {

  }

  test("PubAck encode/decode Test with dto") {
    val pubackDto = PUBACK(INT(12345))
    val pubackPacket = PubAckPacket(FixedHeader(), 12345)

    val puback = Codec[ControlPacket].decode(BitVector(pubackDto.encode))

    assert(puback.isSuccessful === true)
    assert(puback.require === DecodeResult(pubackPacket, bin""))

    val packet = Codec[ControlPacket].encode(pubackPacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(pubackDto.encode))


  }

  test("PubComp encode/decode Test with dto") {
    val pubcompDto = PUBCOMB(INT(12345))
    val pubcompPacket = PubCompPacket(FixedHeader(), 12345)

    val pubcomp = Codec[ControlPacket].decode(BitVector(pubcompDto.encode))

    assert(pubcomp.isSuccessful === true)
    assert(pubcomp.require === DecodeResult(pubcompPacket, bin""))

    val packet = Codec[ControlPacket].encode(pubcompPacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(pubcompDto.encode))
  }

  test("Publish encode/decode Test with dto") {
    val fh = FixedHeader(dup = true, qos = 1, retain = true)
    val publishPacket = PublishPacket(fh, "test/topic", Some(12345), ByteVector("helloworld".getBytes))
    val publishDto = PUBLISH(true, INT(1), true, STRING("test/topic"), Some(INT(12345)), PUBLISHPAYLOAD("helloworld".getBytes))

    val publish = Codec[ControlPacket].decode(BitVector(publishDto.encode))

    assert(publish.isSuccessful === true)
    assert(publish.require === DecodeResult(publishPacket, bin""))

    val packet = Codec[ControlPacket].encode(publishPacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(publishDto.encode))

  }

  test("PubRec encode/decode Test with dto") {
    val pubrecDto = PUBREC(INT(12345))
    val pubrecPacket = PubRecPacket(FixedHeader(), 12345)

    val pubrec = Codec[ControlPacket].decode(BitVector(pubrecDto.encode))

    assert(pubrec.isSuccessful === true)
    assert(pubrec.require === DecodeResult(pubrecPacket, bin""))

    val packet = Codec[ControlPacket].encode(pubrecPacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(pubrecDto.encode))
  }

  test("PubRel encode/decode Test with dto") {
    val pubrelDto = PUBREL(INT(12345))
    val pubrelPacket = PubRelPacket(FixedHeader(qos=1), 12345)

    val pubrel = Codec[ControlPacket].decode(BitVector(pubrelDto.encode))

    assert(pubrel.isSuccessful === true)
    assert(pubrel.require === DecodeResult(pubrelPacket, bin""))

    val packet = Codec[ControlPacket].encode(pubrelPacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(pubrelDto.encode))
  }

  test("SubAck encode/decode Test with dto") {

    val fh = FixedHeader()
    val topicFilter = List(0, 1, 2, 80)
    val subackPakcet = SubAckPacket(fh, 40293, topicFilter)
    val subackDto = SUBACK(INT(40293.toShort),List(BYTE(0), BYTE(1), BYTE(2), BYTE(80)))

    val suback = Codec[ControlPacket].decode(BitVector(subackDto.encode))

    assert(suback.isSuccessful === true)
    assert(suback.require === DecodeResult(subackPakcet, bin""))

    val packet = Codec[ControlPacket].encode(subackPakcet)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(subackDto.encode))
  }

  test("Subscribe encode/decode Test with dto") {
    val fh = FixedHeader(qos=1)
    val topicFilter = List(("topic/1", 0), ("topic/2", 1))
    val subscribePacket = SubscribePacket(fh, 40293, topicFilter)

    val subscribeDto = SUBSCRIBE(INT(40293.toShort), List(
      TopicFilter(STRING("topic/1"), BYTE(0)),
      TopicFilter(STRING("topic/2"), BYTE(1)) ))

    val subscribe = Codec[ControlPacket].decode(BitVector(subscribeDto.encode))

    assert(subscribe.isSuccessful === true)
    assert(subscribe.require === DecodeResult(subscribePacket, bin""))

    val packet = Codec[ControlPacket].encode(subscribePacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(subscribeDto.encode))

  }

  test("UnsubAck encode/decode Test with dto") {
    val fh = FixedHeader()
    val unsubackPacket = UnsubAckPacket(fh, 12345)
    val unsubackDto = UNSUBACK(INT(12345))

    val unsuback = Codec[ControlPacket].decode(BitVector(unsubackDto.encode))

    assert(unsuback.isSuccessful === true)
    assert(unsuback.require === DecodeResult(unsubackPacket, bin""))

    val packet = Codec[ControlPacket].encode(unsubackPacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(unsubackDto.encode))

  }

  test("Unsubscribe encode/decode Test with dto") {
    val fh = FixedHeader(qos=1)
    val topicFilter = List[String]("topic/1", "topic/2", "topic/3")
    val unsubscribePacket = UnsubscribePacket(fh, 12345, topicFilter)
    val unsubscribeDto = UNSUBSCRIBE(INT(12345), List(STRING("topic/1"), STRING("topic/2"), STRING("topic/3")))

    val unsubscribe = Codec[ControlPacket].decode(BitVector(unsubscribeDto.encode))

    assert(unsubscribe.isSuccessful === true)
    assert(unsubscribe.require === DecodeResult(unsubscribePacket, bin""))

    val packet = Codec[ControlPacket].encode(unsubscribePacket)

    assert(packet.isSuccessful === true)
    assert(packet.require === BitVector(unsubscribeDto.encode))

  }

}
