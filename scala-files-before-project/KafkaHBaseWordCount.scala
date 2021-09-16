/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package org.apache.spark.examples.streaming

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

/**
 * Counts words in UTF8 encoded, '\n' delimited text received from the network every second.
 *
 * Usage: NetworkWordCount <hostname> <port>
 * <hostname> and <port> describe the TCP server that Spark Streaming would connect to receive data.
 *
 * To run this on your local machine, you need to first run a Netcat server
 *    `$ nc -lk 9999`
 * and then run the example
 *    `$ bin/run-example org.apache.spark.examples.streaming.NetworkWordCount localhost 9999`
 */
object KafkaHBaseWordCount {
  def main(args: Array[String]): Unit = {
    // if (args.length < 2) {
    //   System.err.println("Usage: KafkaHBaseWordCount <hostname> <port>")
    //   System.exit(1)
    // }

    val kafkaConf = Map(
      "metadata.broker.list" -> "172.17.0.1:9092",
      "zookeeper.connect" -> "172.17.0.1:2181",
      "group.id" -> "kafka-spark-streaming-example",
      "zookeeper.connection.timeout.ms" -> "1000")

    val lines = KafkaUtils.createDirectStream[Array[Byte], String, 
      DefaultDecoder, StringDecoder](
      ssc,
      kafkaConf,
      Set(topic)).map(_._2)

    /* recieve offsets from the RDD */
    lines.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      // ....
    } 
    StreamingExamples.setStreamingLogLevels()

    /* For stateful operations needed */
    ssc.checkpoint("./checkpoints")       // checkpointing dir
    //ssc.checkpoint("hdfs://checkpoints")  // dir in hdfs for prod

    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L))
      .reduceByKeyAndWindow(_ + _, _ - _, Minutes(5), Seconds(2), 2)

    // wordCounts.print()

    wordCounts.foreachRDD ( rdd => {
      val conf = HBaseConfiguration.create()
      conf.set(TableOutputFormat.OUTPUT_TABLE, "stream_count")
      conf.set("hbase.zookeeper.quorum", "localhost:2181")
      conf.set("hbase.master", "localhost:60000");
      conf.set("hbase.rootdir", "file:///tmp/hbase")

      val jobConf = new Configuration(conf)
      jobConf.set("mapreduce.job.output.key.class", classOf[Text].getName)
      jobConf.set("mapreduce.job.output.value.class", classOf[LongWritable].getName)
      jobConf.set("mapreduce.outputformat.class", classOf[TableOutputFormat[Text]].getName)

      rdd.saveAsNewAPIHadoopDataset(jobConf)
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
// scalastyle:on println