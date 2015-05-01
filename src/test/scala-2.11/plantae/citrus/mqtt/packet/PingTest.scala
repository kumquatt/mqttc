package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import plantae.citrus.mqtt.dto.ControlPacketType
import plantae.citrus.mqttclient.mqtt.dto.ping.{PINGRESPDecoder, PINGRESP, PINGREQDecoder, PINGREQ}
import scodec.{DecodeResult, Codec}
import scodec.bits._

@RunWith(classOf[JUnitRunner])
class PingTest extends FunSuite{
  test("encode/decode PingReq"){
    val fh = FixedHeader()
    val pingReq = PingReqPacket(fh)

    assert(Codec[Packet].decode(Codec[Packet].encode(pingReq).require).require === DecodeResult(pingReq, bin""))
    assert(Codec[Packet].encode(pingReq).require.bytes.size === 2)
  }

  test("encode/decode PingResp"){
    val fh = FixedHeader()
    val pingResp = PingRespPacket(fh)

    assert(Codec[Packet].encode(pingResp).require.bytes.size === 2)
    assert(Codec[Packet].decode(Codec[Packet].encode(pingResp).require).require === DecodeResult(pingResp, bin""))
  }

}
