/*
package utils


import javax.inject.{Inject, Singleton}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.heroku.sdk.EnvKeyStore
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.Configuration
import play.api.mvc.WebSocket

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait Kafka {
  val maybePrefix: Option[String]
  def sink: Try[Sink[ProducerRecord[String, String], _]]
  def source(topic: String): Try[Source[ConsumerRecord[String, String], _]]
}

@Singleton
class KafkaAkkaStream @Inject() (configuration: Configuration) extends Kafka {

  import akka.kafka.ProducerSettings
  import org.apache.kafka.common.serialization.StringSerializer

  lazy val envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT")
  lazy val envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT_KEY", "KAFKA_CLIENT_CERT")

  lazy val maybePrefix = configuration.getOptional[String]("kafka.prefix")

  lazy val trustStore = envTrustStore.storeTemp()
  lazy val keyStore = envKeyStore.storeTemp()

  lazy val sslConfig = {
    Configuration(
      "kafka-clients" -> Map(
        SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG -> envTrustStore.`type`(),
        SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG -> trustStore.getAbsolutePath,
        SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG -> envTrustStore.password(),
        SslConfigs.SSL_KEYSTORE_TYPE_CONFIG -> envKeyStore.`type`(),
        SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG -> keyStore.getAbsolutePath,
        SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG -> envKeyStore.password
      )
    )
  }

  def maybeKafkaUrl[K](f: String => K): Try[K] = {
    configuration.getOptional[String]("kafka.url").fold[Try[K]] {
      Failure(new Error("kafka.url was not set"))
    } { kafkaUrl =>
      import java.net.URI

      val kafkaUrls = kafkaUrl.split(",").map { urlString =>
        val uri = new URI(urlString)
        Seq(uri.getHost, uri.getPort).mkString(":")
      }

      Success(f(kafkaUrls.mkString(",")))
    }
  }

  def producerSettings: Try[ProducerSettings[String, String]] = {
    maybeKafkaUrl { kafkaUrl =>
      val serializer = new StringSerializer()
      val config = configuration.getOptional[Configuration]("akka.kafka.producer").getOrElse(Configuration.empty) ++ sslConfig
      ProducerSettings[String, String](config.underlying, serializer, serializer).withBootstrapServers(kafkaUrl)
    }
  }

  def consumerSettings: Try[ConsumerSettings[String, String]] = {
    maybeKafkaUrl { kafkaUrl =>
      val deserializer = new StringDeserializer()
      val config = configuration.getOptional[Configuration]("akka.kafka.consumer").getOrElse(Configuration.empty) ++ sslConfig
      val groupId = maybePrefix.getOrElse("") + "main"
      ConsumerSettings(config.underlying, deserializer, deserializer)
        .withBootstrapServers(kafkaUrl)
        .withGroupId(groupId)
    }
  }

  def sink: Try[Sink[ProducerRecord[String, String], _]] = {
    producerSettings.map(Producer.plainSink(_))
  }

  def source(topic: String): Try[Source[ConsumerRecord[String, String], _]] = {
    val topicWithPrefix = maybePrefix.getOrElse("") + topic
    val subscriptions = Subscriptions.topics(topicWithPrefix)
    consumerSettings.map(Consumer.plainSource(_, subscriptions))
  }

  def ws = WebSocket.acceptOrResult[Any, String] { _ =>
    source("RandomNumbers") match {
      case Failure(e) =>
        Future.successful(Left(InternalServerError("Could not connect to Kafka")))
      case Success(source) =>
        val flow = Flow.fromSinkAndSource(Sink.ignore, source.map(_.value))
        Future.successful(Right(flow))
    }
  }

}*/
