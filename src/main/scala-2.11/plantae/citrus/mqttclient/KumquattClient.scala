package plantae.citrus.mqttclient

import akka.actor.ActorSystem
import plantae.citrus.mqttclient.actors.MqttClient
import plantae.citrus.mqttclient.api.Connect

object KumquattClient extends App {
  val system = ActorSystem()

  //  system.actorOf(Props[SomeActor]) ! "CONNECT"
  val prefix = java.net.InetAddress.getLocalHost.getHostName
  val usage = """
    Usage: <server address> <port> <count> <time_wait>
              """


  if (args.length != 4) {
    println(usage)

  }else {

    val host = args(0)
    val port = args(1).toInt
    val count = args(2).toInt
    val time_wait = args(3).toInt

    Range(1, count).foreach(
      x => {
        val clientid = prefix + "_client_" + x
        val mqttclient = system.actorOf(MqttClient.props(host, port, clientid, 3600))
//        val mqttclient = system.actorOf(MqttClient.props("broker.mqtt-dashboard.com", 1883, clientid, 3600))
        mqttclient ! Connect
        Thread.sleep(time_wait)
      }
    )
  }

}
