#!/bin/bash
cd `dirname $0`/../.. || exit
echo "================== 构建 jar =================="
mvn clean package -Pdev -DskipTests -U -e
echo "================== 拷贝 jar =================="
/bin/cp -rf cocojob-server/cocojob-server-starter/target/*.jar cocojob-server/docker/cocojob-server.jar
/bin/cp -rf cocojob-worker-agent/target/*.jar cocojob-worker-agent/cocojob-agent.jar
echo "================== 关闭老应用 =================="
docker stop cocojob-server
docker stop cocojob-agent
docker stop cocojob-agent2
echo "================== 删除老容器 =================="
docker container rm cocojob-server
docker container rm cocojob-agent
docker container rm cocojob-agent2
echo "================== 删除旧镜像 =================="
docker rmi -f tjqq/cocojob-server:latest
docker rmi -f tjqq/cocojob-agent:latest
echo "================== 构建 cocojob-server 镜像 =================="
docker build -t tjqq/cocojob-server:latest cocojob-server/docker/. || exit
echo "================== 构建 cocojob-agent 镜像 =================="
docker build -t tjqq/cocojob-agent:latest cocojob-worker-agent/. || exit
echo "================== 准备启动 cocojob-server =================="
docker run -d \
       --restart=always \
       --name cocojob-server \
       -p 7700:7700 -p 10086:10086 -p 5001:5005 -p 10001:10000 \
       -e JVMOPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=10000 -Dcom.sun.management.jmxremote.rmi.port=10000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
       -e PARAMS="--oms.swagger.enable=true --spring.profiles.active=product --spring.datasource.core.jdbc-url=jdbc:mysql://remotehost:3306/cocojob-product?useUnicode=true&characterEncoding=UTF-8 --spring.data.mongodb.uri=mongodb://remotehost:27017/cocojob-product" \
       -v ~/docker/cocojob-server:/root/cocojob/server -v ~/.m2:/root/.m2 \
       tjqq/cocojob-server:latest
sleep 60
echo "================== 准备启动 cocojob-agent =================="
serverIP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' cocojob-server)
serverAddress="$serverIP:7700"
echo "使用的Server地址：$serverAddress"

docker run -d \
       --restart=always \
       --name cocojob-agent \
       -p 27777:27777 -p 5002:5005 -p 10002:10000 \
       -e JVMOPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=10000 -Dcom.sun.management.jmxremote.rmi.port=10000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
       -e PARAMS="--app cocojob-agent-test --server $serverAddress" \
       -v ~/docker/cocojob-agent:/root \
       tjqq/cocojob-agent:latest

docker run -d \
       --restart=always \
       --name cocojob-agent2 \
       -p 27778:27777 -p 5003:5005 -p 10003:10000 \
       -e JVMOPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=10000 -Dcom.sun.management.jmxremote.rmi.port=10000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
       -e PARAMS="--app cocojob-agent-test --server $serverAddress" \
       -v ~/docker/cocojob-agent2:/root \
       tjqq/cocojob-agent:latest

