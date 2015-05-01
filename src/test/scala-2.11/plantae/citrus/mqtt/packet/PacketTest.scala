package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import plantae.citrus.mqtt.dto.{ControlPacketType, BYTE, INT, STRING}
import plantae.citrus.mqttclient.mqtt.dto.connect.{CONNACK, Will, CONNECT}
import plantae.citrus.mqttclient.mqtt.dto.ping.PINGREQ
import scodec.Attempt.{Successful, Failure}
import scodec.{Codec, DecodeResult, SizeBound}
import scodec.bits._

@RunWith(classOf[JUnitRunner])
class PacketTest extends FunSuite {

  test("Connect encode Test with dto") {
    val connect1ByDto = CONNECT(STRING("client1"), true, None, None, INT(60)).encode

    val fixedHeader = FixedHeader()
    val connectVariableHeader = ConnectVariableHeader(false, false, false, 0, false, true, 60)
    val connect1ByPacket = Codec[Packet].encode(ConnectPacket(fixedHeader, connectVariableHeader, "client1", None, None,None, None)).require

    assert(connect1ByPacket === BitVector(connect1ByDto))
  }

  test("Connect decode Test with dto") {
    val connect1ByDto = CONNECT(STRING("client1"), true, None, None, INT(60)).encode
    val connect = Codec[Packet].decode(BitVector(connect1ByDto))

    assert(connect.isSuccessful === true)
    assert(connect.require.value.isInstanceOf[ConnectPacket] === true)

    assert(connect.require.value.asInstanceOf[ConnectPacket].clientId === "client1")
    assert(connect.require.value.asInstanceOf[ConnectPacket].variableHeader.keepAliveTime === 60)
  }

  test("Conack encode Test with dto") {
    val connackByDto = CONNACK(true, BYTE(0)).encode

    val fh = FixedHeader()
    val connackByPacket = Codec[Packet].encode(ConnAckPacket(fh, true, 0)).require

    assert(connackByPacket === BitVector(connackByDto))
  }

  test("Conack decode Test with dto") {
    val conackByDto = CONNACK(true, BYTE(1)).encode
    val connack = Codec[Packet].decode(BitVector(conackByDto))

    assert(connack.isSuccessful === true)
    assert(connack.require.value.isInstanceOf[ConnAckPacket] === true)

    assert(connack.require.value.asInstanceOf[ConnAckPacket].sessionPresentFlag === true)
    assert(connack.require.value.asInstanceOf[ConnAckPacket].returnCode === 1)
  }

  test("PingReq encode/decode Test with dto") {
//    val pingreqByDto = PINGREQ
//    val pingreqByPacket = PingReqPacket(FixedHeader())
//
//    val a = pingreqByDto.encode
//    assert(Codec[Packet].encode(pingreqByPacket).require === BitVector(pingreqByDto.encode))

  }

  test("Remaining Length encode Test") {
    assert(remainingLengthCodec.sizeBound === SizeBound.bounded(8, 32))

    assert(remainingLengthCodec.encode(0).isInstanceOf[Successful[BitVector]])
    assert(remainingLengthCodec.encode(0).require === hex"00".bits)
    assert(remainingLengthCodec.encode(127).require === hex"7f".bits)
    assert(remainingLengthCodec.encode(128).require === hex"8001".bits)
    assert(remainingLengthCodec.encode(16383).require === hex"ff7f".bits)
    assert(remainingLengthCodec.encode(16384).require === hex"808001".bits)
    assert(remainingLengthCodec.encode(2097151).require === hex"ffff7f".bits)
    assert(remainingLengthCodec.encode(2097152).require === hex"80808001".bits)
    assert(remainingLengthCodec.encode(268435455).require === hex"ffffff7f".bits)

    assert(remainingLengthCodec.encode(-1).isInstanceOf[Failure])
    assert(remainingLengthCodec.encode(268435456).isInstanceOf[Failure])


  }

  test("Remaining Length decode Test") {
    assert(remainingLengthCodec.decode(hex"00".bits).require === DecodeResult(0, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"7f".bits).require === DecodeResult(127, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"8001".bits).require === DecodeResult(128, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"ff7f".bits).require === DecodeResult(16383, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"808001".bits).require === DecodeResult(16384, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"ffff7f".bits).require === DecodeResult(2097151, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"80808001".bits).require === DecodeResult(2097152, BitVector.empty))
    assert(remainingLengthCodec.decode(hex"ffffff7f".bits).require === DecodeResult(268435455, BitVector.empty))

  }
}
