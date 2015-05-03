package plantae.citrus.mqttclient.api

import plantae.citrus.mqtt.packet.{ConnectPacket, ConnectVariableHeader, ControlPacket, FixedHeader}
import scodec.Codec
import scodec.bits.{ByteVector, BitVector}

sealed trait MqttClientRequest

case class Will(qos: Int, topic: String, message: String, retain: Boolean) {
  // assert qos must 0 ~ 2
}
case class TopicQosPair(topicName : String, qos : Int) {
  // assert qos must 0 ~ 2
}
case class Topic(topicName : String)

case class Connect(clientId: String,
                   cleanSession: Boolean,
                   will: Option[Will] = None,
                   userName: Option[String] = None,
                   password: Option[String] = None,
                   keepAlive: Int) extends MqttClientRequest {
  assert(keepAlive >= 0 && keepAlive < 65536, "KeepAlive should be 0 ~ 655535")
//  assert(!userName.isDefined && password.isDefined, "Password must defined with userName")
}
case object Disconnect extends MqttClientRequest
case object Status extends MqttClientRequest
case class Publish(topic: String,
                   payload: ByteVector,
                   qos: Int,
                   packetId: Option[Int],
                   retain: Boolean = false) extends MqttClientRequest {
//  assert() check packetId
  // assert qos chcek
  // assert qos == 0 -> no packetId
}
case class Subscribe(topics: List[TopicQosPair], packetId: Int) extends MqttClientRequest {
  // assert check packetId
}
case class Unsubscribe(topics: List[Topic], packetId: Int) extends MqttClientRequest {
  // assert check packetId
}

case class TopicResult(qos: Int) {
  // assert qos must 0 ~ 2 or 0x80
}
sealed trait MqttClientResponse

case object Connected extends MqttClientResponse
case class ConnectionFailure(reason: ConnectionFailureReason) extends MqttClientResponse
case object Disconnected extends MqttClientResponse
case class Published(packetId: Int) extends MqttClientResponse
case class MessageArrived(topic: String, payload: ByteVector) extends MqttClientResponse
case class Subscribed(topicResult: List[TopicResult], packetId: Int) extends MqttClientResponse
case class Unsubscribed(packetId: Int) extends MqttClientResponse

case object Error extends MqttClientResponse

sealed trait ConnectionFailureReason
case object ServerNotResponding extends ConnectionFailureReason
case object BadProtocolVersion extends ConnectionFailureReason
case object IdentifierRejected extends ConnectionFailureReason
case object ServerUnavailable extends ConnectionFailureReason
case object BadUsernameOrPassword extends ConnectionFailureReason
case object NotAuthorized extends ConnectionFailureReason

sealed trait ErrorReason
case object NotConnected extends ErrorReason
