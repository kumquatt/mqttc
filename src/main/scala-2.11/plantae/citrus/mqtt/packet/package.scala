package plantae.citrus.mqtt

import scodec.Codec
import scodec.bits._
import scodec.codecs._

package object packet {
  val protocolName: BitVector = variableSizeBytes(uint16, utf8).encode("MQTT").require
  val protocolLevel: BitVector = uint8.encode(4).require

  val dupCodec = bool
  val qosCodec = uint2
  val retainCodec = bool


  val userNameFlagCodec = bool
  val passwordFlagCodec = bool
  val willRetainCodec = bool
  val willQosCodec = uint2
  val willFlagCodec = bool
  val cleanSessionCodec = bool
  val keepAliveCodec = uint16
  val connectAcknowledgeCodec = uint(7)
  val sessionPresentFlagCodec = bool
  val clientIdCodec = variableSizeBytes(uint16, utf8)
  val topicCodec = variableSizeBytes(uint16, utf8)
  val messageCodec = variableSizeBytes(uint16, utf8)
  val userCodec = variableSizeBytes(uint16, utf8)
  val passwordCodec = variableSizeBytes(uint16, utf8)
  val returnCodeCodec = uint8
  val packetIdCodec = uint16
  val remainingLengthCodec = new RemainingLengthCodec
  val payloadCodec = bytes
  val subscribeTopicFilterCodec: Codec[List[(String, Int)]] = list((topicCodec :: ignore(6) :: qosCodec).dropUnits.as[(String, Int)])
  val unsubscribeTopicFilterCodec: Codec[List[String]] = list(topicCodec)
  val topicReturnCodeCodec: Codec[List[Int]] = list(uint8)

  val fixedHeaderCodec = (dupCodec :: qosCodec :: retainCodec).as[FixedHeader]
  val connectVariableHeaderCodec = (
    constant(protocolName) :~>: constant(protocolLevel) :~>:
      userNameFlagCodec ::
      passwordFlagCodec ::
      willRetainCodec ::
      willQosCodec ::
      willFlagCodec ::
      cleanSessionCodec ::
      ignore(1) :~>:
      keepAliveCodec).as[ConnectVariableHeader]

}
