#!/bin/bash
echo "Starting..."

sudo yum install -y docker

sudo service docker start


sudo docker login -u "simons36" -p "dckr_pat_tm0faZvK84dYdwwOpzaFmJETGKc"
sudo docker pull simons36/discount_coupon:1.0.0-SNAPSHOT
sudo docker run -d --name discount_coupon -p 8080:8080 simons36/discount_coupon:1.0.0-SNAPSHOT
