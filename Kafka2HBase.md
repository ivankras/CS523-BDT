## Kafka to HBase

NOTE: zkQuorum, group, topics & numThreads args are now hard-coded; they could be received as inputs

### Terminal 1
```sh
sudo docker-compose up
# (check if kafka is up; otherwise, sudo docker rm kafka before re-running)
```

### Terminal 2 (write on this one)
```sh
sudo docker exec -it kafka bash

# (inside)
#   Create topic
kafka-topics.sh --create \
  --zookeeper 172.17.0.1:2181 \
  --replication-factor 1 --partitions 13 \
  --topic test-topic

#   Write to topic (once we start consuming the stream won't be needed)
kafka-console-producer.sh \
    --broker-list localhost:9092 \
    --topic test-topic
```

### Terminal 3 (execute & results)
```sh

# (on local)
sudo docker cp KafkaWC-1.0-SNAPSHOT-jar-with-dependencies.jar spark-master:/home/kwc.jar
sudo docker cp ../resources/pos-words.dat spark-master:/home/words/pos-words.dat
sudo docker cp ../resources/neg-words.dat spark-master:/home/words/neg-words.dat


sudo docker exec -it spark-master /usr/local/spark-2.2.1/bin/spark-submit --class org.bara.KafkaHBaseWordCount /home/kwc.jar

```

### Terminal 4 (hbase)
```sh
sudo docker exec -it hbase bash
#   (inside)
hbase shell
#   (inside inside)
create 'tweets', 'tweet-data'
```