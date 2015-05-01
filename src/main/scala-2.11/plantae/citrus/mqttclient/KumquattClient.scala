package plantae.citrus.mqttclient

import java.net.InetSocketAddress

import akka.actor.Actor.Receive
import akka.actor.{ActorSystem, Actor, Props, ActorRef}
import akka.io.{IO, Tcp}
import akka.util.ByteString

object KumquattClient extends App {
  println("Hello World")
  val system = ActorSystem()

  val addr = new InetSocketAddress("10.202.32.42", 8888)

  val tcpListener = system.actorOf(Props[TcpListener], "listener")
  val mqttClient = system.actorOf(Props(classOf[MqttClient], addr, tcpListener))

}

class TcpListener extends Actor {
  def receive = {
    case _ => println("aaa")
  }
}

object MqttClient {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[MqttClient], remote, replies)
}

class MqttClient(remote: InetSocketAddress, listener: ActorRef) extends Actor {
  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  def receive = notConnected

  private def notConnected : Receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    case c @ Connected(remote, local) =>
      listener ! c
      sender ! Register(self)
      context become connected
  }

  private def connected : Receive = {
    case data: ByteString =>
      sender ! Write(data)
    case CommandFailed(w: Write) =>
      listener ! "write failed"
    case Received(data) =>
      listener ! data
    case "close" =>
      sender ! Close
    case _: ConnectionClosed =>
      listener ! "connection closed"
      context stop self
  }

}
