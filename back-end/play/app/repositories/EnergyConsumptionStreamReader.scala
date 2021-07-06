package repositories

import java.net.InetSocketAddress
import java.util.Properties

import com.datastax.driver.core.Cluster
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.streams.StreamsConfig

import scala.collection.JavaConverters._

object EnergyConsumptionStreamReader {

  val cluster = Cluster.builder().addContactPointsWithPorts(new InetSocketAddress("localhost", 9042)).build()
  val keyspace = "smartenergysystem"
  val session = cluster.connect(keyspace)

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example")
    val bootstrapServers = """localhost:9092"""
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    p.put("group.id", "test")
    p.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p
  }


  def getId(str: String): String = {
    str.split(",").head.split(":").tail.head.replace("\"", "")
  }

  val consumer = new KafkaConsumer[String, String](config)
  consumer.subscribe(List("testtopic").asJava)

  def getStream(): List[(String, String)] = {

    val records = consumer.poll(3000).asScala
    records.map(rec => {
      print(rec)
      (rec.key(), rec.value())
    }).toList
  }

  def getConsumptionById(id: String) = {
    session.execute(s"select values from energyCons where prosumerID='$id'").asScala.map(x => x.getString(0)).head
  }

}
