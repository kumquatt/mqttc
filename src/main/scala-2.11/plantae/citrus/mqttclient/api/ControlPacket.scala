//package plantae.citrus.mqttclient.api
//
//import plantae.citrus.mqtt.packet._
//import scodec.Codec
//import scodec.bits.BitVector
//
//
//sealed trait ControlPacket {
//  def encode : Array[Byte]
//}
//
//case class Will(qos: Int, topic: String, message: String, retain: Boolean)
//case class Authentication(userName: String, password: Option[String])
//
//case class Connect(clientId: String,
//                    cleanSession: Boolean,
//                    will: Option[Will],
//                    authentication: Option[Authentication],
//                    keepAlive: Int) extends ControlPacket {
//  def encode: Array[Byte] = {
//    val fixedHeader = FixedHeader(dup =false, qos = 0, retain = false)
//
//    val userNameFlag = authentication match {
//      case Some(auth) => true
//      case None => false
//    }
//
//    val passwordFlag = authentication match {
//      case Some(auth) => {
//        auth.password match {
//          case Some(password) => true
//          case None => false
//        }
//      }
//      case None => false
//    }
//
//    val willFlag = will match {
//      case Some(will) => true
//      case None => false
//    }
//
//    val willRetain = willFlag match {
//      case true => will.get.retain
//      case _ => false
//    }
//
//    val willQos = willFlag match {
//      case true => will.get.qos
//      case _ => 0
//    }
//
//
//    val variableHeader = ConnectVariableHeader(userNameFlag, passwordFlag, willRetain, willQos, willFlag, cleanSession, keepAlive)
//
//    val connectPacket = ConnectPacket(fixedHeader, variableHeader, clientId,
//      if(willFlag) Some(will.get.topic) else None,
//      if(willFlag) Some(will.get.message) else None,
//      if(userNameFlag) Some(authentication.get.userName) else None,
//      if(passwordFlag) authentication.get.password else None)
//
//    Codec[MqttControlPacket].encode(connectPacket).getOrElse(BitVector.empty).toByteArray
//  }
//}
//
//case class ConnAck(sessionPresent: Boolean, returnCode: Int) extends ControlPacket {
//  def encode: Array[Byte] = {
//    val fixedHeader = FixedHeader()
//    val connackPacket = ConnAckPacket(fixedHeader, sessionPresent, returnCode)
//
//    Codec[MqttControlPacket].encode(connackPacket).getOrElse(BitVector.empty).toByteArray
//  }
//}
//
//case object Disconnect extends ControlPacket {
//  def encode: Array[Byte] = {
//    val fixedHeader = FixedHeader()
//    val disconnectPacket = DisconnectPacket(fixedHeader)
//
//    Codec[MqttControlPacket].encode(disconnectPacket).getOrElse(BitVector.empty).toByteArray
//
//  }
//}
//
//case object PingReq extends ControlPacket {
//  def encode: Array[Byte] = {
//    val fixedHeader = FixedHeader()
//    val pingreqPacket = PingReqPacket(fixedHeader)
//
//    Codec[MqttControlPacket].encode(pingreqPacket).getOrElse(BitVector.empty).toByteArray
//
//  }
//}
//
//case object PingResp extends ControlPacket {
//  def encode: Array[Byte] = {
//    val fixedHeader = FixedHeader()
//    val pingrespPacket = PingRespPacket(fixedHeader)
//
//    Codec[MqttControlPacket].encode(pingrespPacket).getOrElse(BitVector.empty).toByteArray
//
//  }
//}
