package plantae.citrus.mqtt.packet

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scodec.bits._
import scodec.{Codec, DecodeResult}

@RunWith(classOf[JUnitRunner])
class PingTest extends FunSuite {
  test("encode/decode PingReq") {
    val fh = FixedHeader()
    val pingReq = PingReqPacket(fh)

    assert(Codec[ControlPacket].decode(Codec[ControlPacket].encode(pingReq).require).require === DecodeResult(pingReq, bin""))
    assert(Codec[ControlPacket].encode(pingReq).require.bytes.size === 2)
  }

  test("encode/decode PingResp") {
    val fh = FixedHeader()
    val pingResp = PingRespPacket(fh)

    assert(Codec[ControlPacket].encode(pingResp).require.bytes.size === 2)
    assert(Codec[ControlPacket].decode(Codec[ControlPacket].encode(pingResp).require).require === DecodeResult(pingResp, bin""))
  }

}
