## WordCount from Kafka

NOTE: zkQuorum, group, topics & numThreads args are now hard-coded; they could be received as inputs

### Terminal 1
```sh
sudo docker-compose up
```

### Terminal 2 (write on this one)
```sh
sudo docker exec -it kafka bash

# (inside)
#   Create topic
kafka-topics.sh --create \
  --zookeeper 172.17.0.1:2181 \
  --replication-factor 1 --partitions 13 \
  --topic my-topic

#   Write to topic (once we start consuming the stream won't be needed)
kafka-console-producer.sh \
    --broker-list localhost:9092 \
    --topic my-topic
```

### Terminal 3 (execute & results)
```sh
sudo docker cp KafkaWC-1.0-SNAPSHOT-jar-with-dependencies.jar spark-master:/home/kwc.jar

#   Create /home on HDFS
sudo docker exec -it spark-master bash
#   (inside)
hadoop fs -mkdir /home
hadoop fs -chmod 755 /home

sudo docker exec -it spark-master /usr/local/spark-2.2.1/bin/spark-submit --class org.bara.NetworkWordCount /home/kwc.jar
```
