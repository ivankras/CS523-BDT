## WordCount

### Terminal 1
```sh
sudo docker-compose up
```

### Terminal 2 (write on this one)
```sh
nc -lk 9999
```

### Terminal 3 (execute & results)
```sh
sudo docker exec -it spark-master /usr/local/spark-2.2.1/bin/run-example streaming.NetworkWordCount 172.17.0.1 9999
```
