#!/bin/bash

set -e

echo "Starting setup..."

# Install Java (required for Zookeeper and Kafka)
# echo "Installing Java..."
# sudo apt update
# sudo apt install -y openjdk-17-jdk curl

# Download and install Zookeeper
echo "Installing Zookeeper..."
cd ~
wget https://dlcdn.apache.org/zookeeper/zookeeper-3.9.3/apache-zookeeper-3.9.3-bin.tar.gz
tar -zxf apache-zookeeper-3.9.3-bin.tar.gz
sudo mv apache-zookeeper-3.9.3-bin /usr/local/zookeeper
sudo mkdir -p /var/lib/zookeeper

# Create Zookeeper config
cat <<EOF | sudo tee /usr/local/zookeeper/conf/zoo.cfg
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
EOF

# Start Zookeeper
echo "Starting Zookeeper..."
/usr/local/zookeeper/bin/zkServer.sh start

# Download and install Kafka
echo "Installing Kafka..."
wget https://downloads.apache.org/kafka/3.9.0/kafka_2.13-3.9.0.tgz
tar -zxf kafka_2.13-3.9.0.tgz
sudo mv kafka_2.13-3.9.0 /usr/local/kafka
sudo mkdir -p /tmp/kafka-logs

# Use localhost instead of AWS metadata IP
echo "Configuring Kafka..."
sudo sed -i "s|#listeners=PLAINTEXT://:9092|listeners=PLAINTEXT://localhost:9092|g" /usr/local/kafka/config/server.properties

# Start Kafka (non-blocking)
echo "Starting Kafka..."
/usr/local/kafka/bin/kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties

echo "Kafka & Zookeeper installed and running!"
