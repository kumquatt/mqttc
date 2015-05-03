package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scodec.Codec
import scodec.bits.{BitVector, _}


@RunWith(classOf[JUnitRunner])
class ConnectTest extends FunSuite {

  test("encode connect packet") {
    val fh = FixedHeader()
    val cvh = ConnectVariableHeader(true, true, true, 0, true, true, 30000)
    val connectPacket = Codec[ControlPacket].encode(ConnectPacket(fh, cvh, "client_id", Some("testWillTopic"), Some("TestWillMessage"), Some("id"), Some("password")))

    val a = connectPacket.require.toByteArray
    assert(connectPacket.isSuccessful === true)
    assert(connectPacket.require === BitVector(Array[Byte](16, 67, 0, 4, 77, 81, 84, 84, 4, -26, 117, 48, 0, 9, 99, 108, 105, 101, 110,
      116, 95, 105, 100, 0, 13, 116, 101, 115, 116, 87, 105, 108, 108, 84, 111, 112, 105, 99, 0, 15, 84, 101, 115, 116,
      87, 105, 108, 108, 77, 101, 115, 115, 97, 103, 101, 0, 2, 105, 100, 0, 8, 112, 97, 115, 115, 119, 111, 114, 100)))

    val cvh2 = ConnectVariableHeader(false, false, false, 0, false, true, 60)
    val connectPacket2 = Codec[ControlPacket].encode(ConnectPacket(fh, cvh2, "client_id", None, None, None, None))

    assert(connectPacket2.isSuccessful == true)
    assert(connectPacket2.require === BitVector(Array[Byte](16, 21, 0, 4, 77, 81, 84, 84, 4, 2, 0, 60, 0, 9, 99, 108, 105, 101, 110, 116,
      95, 105, 100)))

  }

  test("decode connect packet #1") {
    val encodedConnectPacket = Array[Byte](16, 21, 0, 4, 77, 81, 84, 84, 4, 2, 0, 60, 0, 9, 99, 108, 105, 101, 110, 116,
      95, 105, 100)

    val packet = Codec[ControlPacket].decode(BitVector(encodedConnectPacket))

    assert(packet.isSuccessful === true)
    assert(packet.require.value.isInstanceOf[ConnectPacket] === true)
    assert(packet.require.value.asInstanceOf[ConnectPacket].clientId === "client_id")
    assert(packet.require.value.asInstanceOf[ConnectPacket].variableHeader.cleanSession === true)
  }

  test("decode connect packet #2") {

    val minPacketFh = FixedHeader()
    val minPacketVh = ConnectVariableHeader(false, false, false, 0, false, true, 60)
    val minPacket = ConnectPacket(minPacketFh, minPacketVh, "client_id", None, None, None, None)

    val min = Codec[ControlPacket].decode(Codec[ControlPacket].encode(minPacket).require).require.value.asInstanceOf[ConnectPacket]

    assert(min.clientId === "client_id")
    assert(min.variableHeader.keepAliveTime === 60)

    val min_idPacketFh = FixedHeader()
    val min_idPacketVh = ConnectVariableHeader(true, false, false, 0, false, true, 60)
    val min_idPacket = ConnectPacket(min_idPacketFh, min_idPacketVh, "client_id", None, None, Some("id"), None)

    val min_id = Codec[ControlPacket].decode(Codec[ControlPacket].encode(min_idPacket).require).require.value.asInstanceOf[ConnectPacket]

    assert(min_id.clientId === "client_id")
    assert(min_id.userName === Some("id"))
    assert(min_id.password === None)

    val min_passPacketFh = FixedHeader()
    val min_passPacketVh = ConnectVariableHeader(true, true, false, 0, false, true, 60)
    val min_passPacket = ConnectPacket(min_passPacketFh, min_passPacketVh, "client_id", None, None, Some("id"), Some("password"))

    val min_pass = Codec[ControlPacket].decode(Codec[ControlPacket].encode(min_passPacket).require).require.value.asInstanceOf[ConnectPacket]

    assert(min_pass.userName === Some("id"))
    assert(min_pass.password === Some("password"))

    val will_message = "will message will messagewill messagewill messagewill messagewill messagewill " +
      "messagewill messagewill messagewill messagewill messagewill messagewill messagewill messagewill messagewill " +
      "message will messagewill messagewill messagewill messagewill messagewill messagewill messagewill messagewill " +
      "messagewill messagewill messagewill messagewill messagewill message"

    val min_willPacketFh = FixedHeader()
    val min_willPacketVh = ConnectVariableHeader(false, false, true, 2, true, false, 60)
    val min_willPacket = ConnectPacket(min_willPacketFh, min_willPacketVh, "client_id", Some("will_topic"), Some(will_message), None, None)

    val min_will = Codec[ControlPacket].decode(Codec[ControlPacket].encode(min_willPacket).require).require.value.asInstanceOf[ConnectPacket]

    assert(min_will.variableHeader.willQoS === 2)
    assert(min_will.willTopic === Some("will_topic"))
    assert(min_will.variableHeader.willRetain === true)
    assert(min_will.willMessage === Some(will_message))
  }

  test("Connack encode test") {
    val fh = FixedHeader()
    val connack = Codec[ControlPacket].encode(ConnAckPacket(fh, true, 1))

    assert(connack.isSuccessful === true)
    assert(connack.require === BitVector(hex"20020101"))
    // 32, 2, 1, 1
  }

  test("Connack decode test") {
    val connack = Codec[ControlPacket].decode(BitVector(hex"20020101"))

    assert(connack.isSuccessful === true)
    assert(connack.require.value.isInstanceOf[ConnAckPacket] === true)
    assert(connack.require.value.asInstanceOf[ConnAckPacket].returnCode === 1)
    assert(connack.require.value.asInstanceOf[ConnAckPacket].sessionPresentFlag === true)
  }
}
