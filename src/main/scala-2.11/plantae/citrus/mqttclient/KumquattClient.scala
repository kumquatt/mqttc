package plantae.citrus.mqttclient

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp.Connected
import akka.io.{IO, Tcp}
import akka.util.ByteString
import plantae.citrus.mqtt.packet.{FixedHeader, ConnectVariableHeader, ConnectPacket, ControlPacket}
import plantae.citrus.mqttclient.actors.MqttClient
import plantae.citrus.mqttclient.api.{Publish, TopicQosPair, Subscribe, Connect}
import scodec.Codec
import scodec.bits.{ByteVector, BitVector}

import scala.collection.mutable

object KumquattClient extends App {
  val system = ActorSystem()

//  system.actorOf(Props[SomeActor]) ! "CONNECT"

  Range(1,2000).foreach(
    x => {
      val clientid = "wonsuk_" + x
      val mqttclient = system.actorOf(MqttClient.props("broker.mqtt-dashboard.com", 1883, clientid,3600))
      mqttclient ! Connect
      Thread.sleep(100)
    }
  )

//  mqttclient ! Subscribe(List(TopicQosPair("a/b", 0)), 1)
//  mqttclient ! Publish("a/b", ByteVector("test111".getBytes),0,None, false)

}
//
//class SomeActor extends Actor {
//  val system = context.system
//  val addr = new InetSocketAddress("broker.mqtt-dashboard.com", 1883)
//  var tcpsession : ActorRef = null
//
//  def receive = {
//    case "CONNECT" =>
//      tcpsession = system.actorOf(Client.props(addr, self))
//    case c @ Connected(remote, local) => {
//      println("connected to server")
//      tcpsession ! ByteString(Codec[ControlPacket].encode(ConnectPacket(FixedHeader(), ConnectVariableHeader(false, false, false, 0, false, false, 60), "wonsuk1", None, None, None, None)).require.toByteArray)
//      context become connected
//    }
//
//  }
//
//  def connected: Receive = {
//    case "CONNECT" => println("already connected")
//
//  }
//}
//
//object Client {
//  def props(remote: InetSocketAddress, replies: ActorRef) =
//    Props(classOf[Client], remote, replies)
//}
//
//class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {
//
//  import Tcp._
//  import context.system
//
//  IO(Tcp) ! Connect(remote)
//  val buffer : BitVector = BitVector()
//
//  def receive = {
//    case CommandFailed(_: Connect) =>
//      listener ! "connect failed"
//      context stop self
//
//    case c @ Connected(remote, local) =>
//      listener ! c
//      val connection = sender()
//      connection ! Register(self)
//      context become {
//        case data: ByteString =>
//          connection ! Write(data)
//        case CommandFailed(w: Write) => {
//          // O/S buffer was full
//          println("write failed")
//          listener ! "write failed"
//        }
//        case Received(data) => {
//          buffer ++ BitVector(data)
////          bits.enqueue(BitVector(data))
//
//
//          val data2 = Codec[ControlPacket].decode(BitVector(data.toArray[Byte]))
//          println(data2)
//          listener ! data
//        }
//        case "close" =>
//          connection ! Close
//        case _: ConnectionClosed =>
//          listener ! "connection closed"
//          context stop self
//      }
//  }
//}


