package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scodec.bits._
import scodec.{Codec, DecodeResult}

@RunWith(classOf[JUnitRunner])
class DisconnectTest extends FunSuite {
  test("encode/decode Disconnect") {
    val fh = FixedHeader()
    val disconnect = DisconnectPacket(fh)

    assert(Codec[Packet].encode(disconnect).require.bytes.size === 2)
    assert(Codec[Packet].decode(Codec[Packet].encode(disconnect).require).require === DecodeResult(disconnect, bin""))
  }

}
