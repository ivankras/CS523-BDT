### Terminal 1 (execute & get results)
```sh
sudo docker cp HBaseToFile-1.0-SNAPSHOT-jar-with-dependencies.jar spark-master:/home/hbase2file.jar

#   Create /home on HDFS (if not created)
sudo docker exec -it spark-master bash
#   (inside)
hadoop fs -mkdir /home
hadoop fs -chmod 777 /home  # check permissions

sudo docker exec -it spark-master /usr/local/spark-2.2.1/bin/spark-submit /home/hbase2file.jar

sudo docker exec -it spark-master bash

# (inside)
hadoop fs -copyToLocal /home/tweets/part-000... /home/tweets.csv
exit

sudo docker cp sparkMaster:/home/tweets.csv tweets.csv
```