package utility

import java.net.InetSocketAddress
import java.util.Properties

import com.datastax.driver.core.Cluster
import models.EnergyConsumption
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.streams.StreamsConfig
import play.api.libs.json.Json
import repositories.EnergyConsumptionStreamReader.{getId, session}

import scala.util.Random


object KafkaStreamGen extends App {

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example")
    val bootstrapServers = if (args.length > 0) args(0) else """localhost:9092"""
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    p.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    p.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    p
  }

  val cluster = Cluster.builder().addContactPointsWithPorts(new InetSocketAddress("localhost", 9042)).build()
  val keyspace = "smartenergysystem"
  val session = cluster.connect(keyspace)

  // val producer = new KafkaProducer[String, String](config)

  val prosumerIDs = (1 to 1).map(_.toString).toList

  val streamData = generateSeed(prosumerIDs)

  val len = streamData.length
  implicit val feedFormat = Json.format[EnergyConsumption]

  while (true) {
    val randomPick = Random.nextInt(len)
    if(streamData.indices.contains(randomPick)) {
      val data = streamData(randomPick)
     val newData = EnergyConsumption(_id = data._id, prosumerId = data.prosumerId,
                                      refrigeration = Random.nextInt(60).toString,
                                      plugLoads = Random.nextInt(100).toString,
                                      evCharge = Random.nextInt(200).toString,
                                      others = Random.nextInt(100).toString)

      streamData.updated(randomPick, newData)
      // val rec = new ProducerRecord[String, String]("testtopic", data._id, Json.toJson(newData).toString())
      // producer.send(rec)
      Thread.sleep(3000)
        session.execute("Insert into energyCons (prosumerID,values) values ('" + data._id+ "','" + Json.toJson(newData).toString() + "')")
    }
  }

  def generateSeed(prosumerIds: List[String]): List[EnergyConsumption] = {
    implicit val feedFormat = Json.format[EnergyConsumption]
    prosumerIds.map(id =>
      EnergyConsumption(_id = id, prosumerId = id,
        refrigeration = Random.nextInt(60).toString,
        plugLoads = Random.nextInt(100).toString,
        evCharge = Random.nextInt(200).toString,
        others = Random.nextInt(100).toString))
  }
}
