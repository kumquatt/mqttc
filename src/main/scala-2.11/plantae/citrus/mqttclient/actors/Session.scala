package plantae.citrus.mqttclient.actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import plantae.citrus.mqtt.packet._
import plantae.citrus.mqttclient.api._
import scodec.Codec
import scodec.bits.BitVector


object Session {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Session], remote, replies)
}

class Session(remote: InetSocketAddress, listener: ActorRef) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  var connection: ActorRef = null

  def receive = notConnected

  private def notConnected: Receive = {
    case CommandFailed(x: Connect) =>
      listener ! "connect failed"
      println("connect failed!!! " + x)
      context stop self

    case c@Connected(remote, local) =>
      listener ! "CONNECTED"
      connection = sender
      connection ! Register(self, false, true)
      context become connected
  }

  def connected: Receive = {
    case mqttResult: MqttClientRequest => {
      mqttResult match {
        case c: plantae.citrus.mqttclient.api.Connect => {
          val fh = FixedHeader(false, 0, false)
          val vh = ConnectVariableHeader(false, false, false, 0, false, false, c.keepAlive)
          val connectPacket = ConnectPacket(fh, vh, c.clientId, None, None, None, None)
          connection ! Write(ByteString(Codec[ControlPacket].encode(connectPacket).require.toByteArray))
        }
        case s: Subscribe => {
          val subscribePacket = SubscribePacket(packetId = s.packetId, topicFilter = s.topics.map(x => (x.topicName, x.qos)))
          connection ! Write(ByteString(Codec[ControlPacket].encode(subscribePacket).require.toByteArray))
        }
        case u: Unsubscribe => {
          val unsubscribePacket = UnsubscribePacket(packetId = u.packetId, topicFilter = u.topics.map(x => x.topicName))
          connection ! Write(ByteString(Codec[ControlPacket].encode(unsubscribePacket).require.toByteArray))
        }
        case p: Publish => {
          val publishPacket = PublishPacket(FixedHeader(), p.topic, None, p.payload)
          connection ! Write(ByteString(Codec[ControlPacket].encode(publishPacket).require.toByteArray))
        }
        case PingReq => {
          val pingPacket = PingReqPacket()
          connection ! Write(ByteString(Codec[ControlPacket].encode(pingPacket).require.toByteArray))
        }
      }
    }
    case CommandFailed(w: Write) =>
      println("Somethign wrong!!!")
    case Received(data) =>
      // decode here!!!
      val controlPacket = Codec[ControlPacket].decode(BitVector(data.toArray[Byte]))

      println(controlPacket)
      if (controlPacket.isSuccessful) {
        controlPacket.require.value match {
          case c: ConnAckPacket =>
            c.returnCode match {
              case 0 => listener ! plantae.citrus.mqttclient.api.Connected
              case 1 => listener ! ServerNotResponding
              case 2 => listener ! BadProtocolVersion
              case 3 => listener ! IdentifierRejected
              case 4 => listener ! ServerUnavailable
              case 5 => listener ! BadUsernameOrPassword
            }
          case s: SubAckPacket =>
            listener ! Subscribed(s.returnCode.map(x => TopicResult(x)), s.packetId)
          case u: UnsubAckPacket =>
            listener ! Unsubscribed(u.packetId)
          case p: PublishPacket =>
            listener ! MessageArrived(p.topic, p.payload)
          case x =>
            println(x)
        }
      } else {
        println("something wrong!!")
      }

    case "close" =>
      connection ! Close
    case _: ConnectionClosed =>
      listener ! Disconnected
      context stop self
  }

}

