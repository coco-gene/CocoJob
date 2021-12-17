```shell
mvn package -Dmaven.test.skip=true
# java -jar --spring.profiles.active=daily
# java -jar --spring.profiles.active=pre

cp cocojob-server/cocojob-server-starter/target/cocojob-server-starter-4.0.1.jar docker/cocojob/cocojob-server.jar
cp cocojob-worker-agent/target/cocojob-worker-agent-4.0.1.jar docker/cocojob-agent/cocojob-agent.jar

sudo rm -rf docker/cocojob/cocojob-server.jar
sudo rm -rf docker/cocojob-agent/cocojob-agent.jar

cd docker
cp -rf ../cocojob-server/cocojob-server-starter/target/*.jar cocojob/cocojob-server.jar
cp -rf ../cocojob-worker-agent/target/*.jar cocojob-agent/cocojob-agent.jar

sudo docker-compose build
sudo docker-compose down
sudo docker-compose up
sudo docker-compose up -d

sudo docker-compose up -d cocojob-mysql80
sudo docker-compose stop cocojob-mysql80
sudo docker-compose rm cocojob-mysql80

sudo docker-compose up -d cocojob-mongodb
sudo docker-compose stop cocojob-mongodb
sudo docker-compose rm cocojob-mongodb

sudo docker-compose up -d cocojob-mongo-express
sudo docker-compose stop cocojob-mongo-express
sudo docker-compose rm cocojob-mongo-express

sudo docker-compose build cocojob-server
sudo docker-compose build cocojob-agent
sudo docker-compose build cocojob-agent1

sudo docker-compose up cocojob-server
sudo docker-compose up -d cocojob-server
sudo docker-compose stop cocojob-server
sudo docker-compose rm cocojob-server

sudo docker-compose up cocojob-agent
sudo docker-compose up -d cocojob-agent
sudo docker-compose stop cocojob-agent
sudo docker-compose rm cocojob-agent

sudo docker-compose up cocojob-agent1
sudo docker-compose up -d cocojob-agent1
sudo docker-compose stop cocojob-agent1
sudo docker-compose rm cocojob-agent1

sudo docker-compose logs -f cocojob-server

mysql -h127.0.0.1 -P3307 -uroot -p
root
CREATE DATABASE IF NOT EXISTS `cocojob-daily` DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS `cocojob-pre` DEFAULT CHARSET utf8mb4;
mysql -h127.0.0.1 -P3307 -uroot -p cocojob-daily < ../others/cocojob-mysql.sql
mysql -h127.0.0.1 -P3307 -uroot -p cocojob-pre < ../others/cocojob-mysql.sql

select * from app_info;
select * from container_info;
select * from instance_info;
select * from job_info;
select * from oms_lock;
select * from server_info;
select * from user_info;
select * from workflow_info;
select * from workflow_instance_info;
select * from workflow_node_info;

truncate table app_info;
truncate table container_info;
truncate table instance_info;
truncate table job_info;
truncate table oms_lock;
truncate table server_info;
truncate table user_info;
truncate table workflow_info;
truncate table workflow_instance_info;
truncate table workflow_node_info;

sudo docker network create --subnet=172.22.0.0/16 cocojob


docker run -d \
         --name cocojob-server \
         -p 7700:7700 -p 10086:10086 -p 5001:5005 -p 10001:10000 \
         -e JVMOPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=10000 -Dcom.sun.management.jmxremote.rmi.port=10000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
         -e PARAMS="--spring.profiles.active=pre" \
         -e TZ="Asia/Shanghai" \
         -v ~/git/CocoJob/docker/data/cocojob/cocojob:/home/cocojob/cocojob -v ~/.m2:/root/.m2 \
         docker_pumpkinjob-server

docker run -d \
         --name cocojob-server \
         -p 7700:7700 -p 10086:10086 -p 5001:5005 -p 10001:10000 \
         -e JVMOPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=10000 -Dcom.sun.management.jmxremote.rmi.port=10000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
         -e PARAMS="--spring.profiles.active=pre" \
         -e TZ="Asia/Shanghai" \
         -v ~/docker/cocojob-server:/root/cocojob-server -v ~/.m2:/root/.m2 \
         docker_pumpkinjob-server
         
docker rm cocojob-server

docker rmi `docker images|grep none |  awk '{print $3}'`
```

```
docker-compose mongo-express

user.home

mongo --username "root" --password
123456

com.yunqiic.cocojob.official.processors.impl.script.ShellProcessor
```