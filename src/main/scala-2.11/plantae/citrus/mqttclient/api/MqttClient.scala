package plantae.citrus.mqttclient.api

sealed trait MqttClientRequest

case object Will extends MqttClientRequest
case object Connect extends MqttClientRequest
case object Disconnect extends MqttClientRequest
case object Status extends MqttClientRequest
case object Publish extends MqttClientRequest
case object Subscribe extends MqttClientRequest
case object Unsubscribe extends MqttClientRequest

sealed trait MqttClientResponse

case object Connected extends MqttClientResponse
case object ConnectionFailure extends MqttClientResponse
case object Disconnected extends MqttClientResponse
case object Published extends MqttClientResponse
case object MessageArrived extends MqttClientResponse
case object Subscribed extends MqttClientResponse
case object Unsubscribed extends MqttClientResponse
case object Error extends MqttClientResponse

sealed trait ConnectionFailureReason

case object ServerNotResponding extends ConnectionFailureReason
case object BadProtocolVersion extends ConnectionFailureReason
case object IdentifierRejected extends ConnectionFailureReason
case object ServerUnavailable extends ConnectionFailureReason
case object BadUsernameOrPassword extends ConnectionFailureReason
case object NotAuthorized extends ConnectionFailureReason

sealed trait ErrorKind

case object NotConnected extends ErrorKind

