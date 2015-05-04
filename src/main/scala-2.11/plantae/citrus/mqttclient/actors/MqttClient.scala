package plantae.citrus.mqttclient.actors

import java.net.InetSocketAddress

import akka.actor._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import plantae.citrus.mqttclient.api.Status
import plantae.citrus.mqttclient.api._
import scodec.bits.{ByteVector, BitVector}
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit


case object TimeSignal
case object MessageSignal

object MqttClient {
  def props(host: String, port: Int, clientId: String, keepAliveTime: Int) = {
    Props(classOf[MqttClient], host, port, clientId, keepAliveTime)
  }
}
class MqttClient(host: String, port:Int,  clientId: String, keepAliveTime: Int) extends Actor with ActorLogging {
  val address = host + ":" + port
  val inetSocketAddress = new InetSocketAddress(host, port)
  val system = context.system
  var tcpsession: ActorRef = null
  var connectOption : Option[ConnectOption] = None
  var timer: Cancellable = null
  var timer2: Cancellable = null

  import system.dispatcher

  def receive = {
    case coption : ConnectOption =>
      log.info("client({}),host({}),set option for connection",clientId, address, coption)
      connectOption = Some(coption)
    case Connect => {
      log.info("client({}),host({}),try to connect", clientId, address)
      // try to connect with coption
      tcpsession = system.actorOf(Session.props(inetSocketAddress, self))


    }
    case "CONNECTED" => {
      log.info("client({}),host({}),connected!!", clientId, address)
      tcpsession ! Connect(clientId, keepAliveTime)
    }

    case plantae.citrus.mqttclient.api.Connected => {
      log.info("client({}),host({}),connect succ,", clientId, address)
      timer = system.scheduler.schedule(Duration((keepAliveTime/2).toLong, TimeUnit.SECONDS), Duration(keepAliveTime.toLong, TimeUnit.SECONDS),self, TimeSignal)
      timer2 = system.scheduler.schedule(Duration(60.toLong, TimeUnit.SECONDS), Duration(300.toLong, TimeUnit.SECONDS),self, MessageSignal)
      context become connected
      self ! Subscribe(List(TopicQosPair("a/b", 0), TopicQosPair(clientId, 0)), 1)
    }
    case ConnectionFailure(reason) =>
      log.info("client({}),host({}),connection fail,{}", clientId, address, reason.toString)

    case Status =>
      log.info("client({}),host({}),status,notconnected",clientId, address)
    case other : MqttClientRequest =>
      log.info("client({}),host({}),client not connected,{}", other)

    case x =>
      log.info("client({}),host({}),client not connected,unknown event,({})", clientId, address, x)
  }

  private def connected : Receive = {
    // Request From Client
    case MessageSignal =>
      log.info("client({}),host({}),message",clientId,address)
      tcpsession ! Publish(clientId, ByteVector((clientId + "_" +System.currentTimeMillis()).getBytes()), 0, None, false)
    case TimeSignal =>
      log.info("client({}),host({}),need to send ping", clientId, address)
      tcpsession ! PingReq
    case c : Connect =>
      log.info("client({}),host({}),already connected", clientId, address)
    case Disconnect =>
      log.info("client({}),host({}),try to disconnect", clientId, address)
    case Status =>
      log.info("client({}),host({}),status,connected", clientId, address)
    case p : Publish => {
      log.info("client({}),host({}),publish", clientId, address)
      tcpsession ! p
    }
    case s : Subscribe => {
      log.info("client({}),host({}),subscribe", clientId, address)
      tcpsession ! s
    }
    case u : Unsubscribe => {
      log.info("client({}),host({}),unsubscribe", clientId, address)
      tcpsession ! u
    }

    // Response From Session
    case resp : MqttClientResponse => processResponse(resp)
  }

  def processResponse(response: MqttClientResponse): Unit = {
    response match {
      case Connected => log.info("client({}),host({}),connect succ,", clientId, address)
      case ConnectionFailure(reason) =>
        log.info("client({}),host({}),connection fail,{}", clientId, address, reason.toString)
      case Disconnected => log.info("client({}),host({}),disconnect succ,", clientId, address)
      case Published(pid) =>
        log.info("client({}),host({}),message publish succ,packetid({})",clientId, address, pid)
      case MessageArrived(topic, payload) =>
        log.info("client({}),host({}),message arrived succ,(topic({})-payload({})",
                  clientId, address, topic, new String(payload.toArray))
      case Subscribed(topicResult, pid) =>
        log.info("client({}),host({}),subscribe result succ,topicResult({})-packetid({})",
                  clientId, address, topicResult, pid)
      case Unsubscribed(pid) =>
        log.info("client({}),host({}),unsubscribe succ,packetid({})",
                  clientId, address, pid)
    }
  }
}
