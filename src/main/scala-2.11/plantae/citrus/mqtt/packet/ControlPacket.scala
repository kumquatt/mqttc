package plantae.citrus.mqtt.packet

import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs._

sealed trait Packet
object Packet {
  implicit val discriminated: Discriminated[Packet, Int] = Discriminated(uint4)
}

case class ConnectPacket(
                          fixedHeader: FixedHeader,
                          variableHeader: ConnectVariableHeader,
                          clientId: String,
                          topic: Option[String],
                          message: Option[String],
                          user: Option[String],
                          password: Option[String]
                          ) extends Packet
object ConnectPacket {
  implicit val discriminator: Discriminator[Packet, ConnectPacket, Int] = Discriminator(1)
  implicit val codec: Codec[ConnectPacket] = (
    fixedHeaderCodec ::
      variableSizeBytes(
        remainingLengthCodec,
        connectVariableHeaderCodec >>:~ {
          (vh) =>
            clientIdCodec ::
              conditional(vh.willFlag, topicCodec) ::
              conditional(vh.willFlag, messageCodec) ::
              conditional(vh.userNameFlag, userCodec) ::
              conditional(vh.passwordFlag, passwordCodec)
        })).as[ConnectPacket]
}

case class ConnAckPacket(
                          fixedHeader: FixedHeader,
                          connectAcknowledge: Int,
                          sessionPresentFlag: Boolean,
                          returnCode: Int
                          ) extends Packet
object ConnAckPacket {
  implicit val discriminator: Discriminator[Packet, ConnAckPacket, Int] = Discriminator(2)
  implicit val codec: Codec[ConnAckPacket] = (
    fixedHeaderCodec ::
      variableSizeBytes(
        remainingLengthCodec,
        connectAcknowledgeCodec ::
          sessionPresentFlagCodec ::
          returnCodeCodec
      )).as[ConnAckPacket]
}

case class PublishPacket(
                        fixedHeader: FixedHeader,
                        topic: String,
                        packetId: Option[Int],
                        payload: ByteVector
                          ) extends Packet
object PublishPacket {
  implicit val discriminator: Discriminator[Packet, PublishPacket, Int] = Discriminator(3)
  implicit val codec: Codec[PublishPacket] = (
    fixedHeaderCodec >>:~ {
      (fh) => variableSizeBytes(
        remainingLengthCodec,
        topicCodec ::
          conditional(fh.qos != 0, packetIdCodec) ::
          payloadCodec
      )
    }).as[PublishPacket]
}

case class PubAckPacket(
                       fixedHeader: FixedHeader,
                       packetId: Int
                         ) extends Packet
object PubAckPacket {
  implicit val discriminator: Discriminator[Packet, PubAckPacket, Int] = Discriminator(4)
  implicit val codec: Codec[PubAckPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
      packetIdCodec)).as[PubAckPacket]
}

case class PubRecPacket(
                       fixedHeader: FixedHeader,
                       packetId: Int
                         ) extends Packet
object PubRecPacket {
  implicit val discriminator: Discriminator[Packet, PubRecPacket, Int] = Discriminator(5)
  implicit val codec: Codec[PubRecPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[PubRecPacket]
}

case class PubRelPacket(
                       fixedHeader: FixedHeader,
                       packetId: Int
                         ) extends Packet
object PubRelPacket {
  implicit val discriminator: Discriminator[Packet, PubRelPacket, Int] = Discriminator(6)
  implicit val codec: Codec[PubRelPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[PubRelPacket]
}

case class PubCompPacket(
                        fixedHeader: FixedHeader,
                        packetId: Int
                          ) extends Packet
object PubCompPacket {
  implicit val discriminator: Discriminator[Packet, PubCompPacket, Int] = Discriminator(7)
  implicit val codec: Codec[PubCompPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[PubCompPacket]
}

case class SubscribePacket(
                          fixedHeader: FixedHeader,
                          packetId : Int,
                          topicFilter : List[(String, Int)]
                            ) extends Packet
object SubscribePacket {
  implicit val discriminator: Discriminator[Packet, SubscribePacket, Int] = Discriminator(8)
  implicit val codec: Codec[SubscribePacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec ::
    subscribeTopicFilterCodec)).as[SubscribePacket]
}

case class SubAckPacket(
                       fixedHeader: FixedHeader,
                       packetId: Int,
                       returnCode: List[Int]
                         ) extends Packet
object SubAckPacket {
  implicit val discriminator: Discriminator[Packet, SubAckPacket, Int] = Discriminator(9)
  implicit val codec: Codec[SubAckPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec :: topicReturnCodeCodec
    )).as[SubAckPacket]
  // success maximum qos 0 => 0
  // success maximum qos 2 => 2
  // Failure => 128
}

case class UnsubscribePacket(
                            fixedHeader: FixedHeader,
                            packetId: Int,
                            topicFilter : List[String]
                              ) extends Packet
object UnsubscribePacket {
  implicit val discriminator: Discriminator[Packet, UnsubscribePacket, Int] = Discriminator(10)
  implicit val codec: Codec[UnsubscribePacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec :: unsubscribeTopicFilterCodec)).as[UnsubscribePacket]
}

case class UnsubAckPacket(
                         fixedHeader: FixedHeader,
                         packetId: Int
                           ) extends Packet
object UnsubAckPacket {
  implicit val discriminator: Discriminator[Packet, UnsubAckPacket, Int] = Discriminator(11)
  implicit val codec: Codec[UnsubAckPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[UnsubAckPacket]
}

case class PingReqPacket(
                        fixedHeader: FixedHeader
                          ) extends Packet
object PingReqPacket {
  implicit val discriminator: Discriminator[Packet, PingReqPacket, Int] = Discriminator(12)
  implicit val codec: Codec[PingReqPacket] = (
    fixedHeaderCodec :: ignore(8)
    ).dropUnits.as[PingReqPacket]
}

case class PingRespPacket(
                         fixedHeader: FixedHeader
                           ) extends Packet
object PingRespPacket {
  implicit val discriminator: Discriminator[Packet, PingRespPacket, Int] = Discriminator(13)
  implicit val codec: Codec[PingRespPacket] = (
    fixedHeaderCodec :: ignore(8)
    ).dropUnits.as[PingRespPacket]
}

case class DisconnectPacket(
                           fixedHeader: FixedHeader
                             ) extends Packet
object DisconnectPacket {
  implicit val discriminator: Discriminator[Packet, DisconnectPacket, Int] = Discriminator(14)
  implicit val codec: Codec[DisconnectPacket] = (
    fixedHeaderCodec :: ignore(8)
    ).dropUnits.as[DisconnectPacket]
}
