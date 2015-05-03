package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import plantae.citrus.mqtt.dto.{BYTE, INT, STRING}
import plantae.citrus.mqttclient.mqtt.dto.connect.{CONNACK, CONNECT}
import scodec.Attempt.{Failure, Successful}
import scodec.bits._
import scodec.{Codec, DecodeResult, SizeBound}

@RunWith(classOf[JUnitRunner])
class ControlPacketTest extends FunSuite {



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
