#!/bin/bash
echo "Starting..."

sudo yum install -y docker

sudo service docker start

sudo docker network create kong-net

sudo docker run -d --name kong-database \
  -e "KONG_DATABASE=postgres" \
  -e "POSTGRES_USER=kong" \
  -e "POSTGRES_DB=kong" \
  -e "POSTGRES_PASSWORD=kong" \
  -p 5432:5432 \
  --network kong-net \
  postgres:13

sudo docker run --rm --network=kong-net \
  -e "KONG_DATABASE=postgres" \
  -e "KONG_PG_HOST=kong-database" \
  -e "KONG_PG_PASSWORD=kong" \
  -e "KONG_PASSWORD=test" \
  kong/kong-gateway:3.9.0.0 kong migrations bootstrap

# sudo docker run --rm --network=kong-net  -e "KONG_DATABASE=postgres"  -e "KONG_PG_HOST=kong-database"  -e "KONG_PG_PASSWORD=kong"  -e "KONG_PASSWORD=test"  kong/kong-gateway:3.9.0.0 kong migrations bootstrap

sudo docker run -d --name kong-gateway \
  --network=kong-net \
  -e "KONG_DATABASE=postgres" \
  -e "KONG_PG_HOST=kong-database" \
  -e "KONG_PG_USER=kong" \
  -e "KONG_PG_PASSWORD=kong" \
  -e "KONG_PROXY_ACCESS_LOG=/dev/stdout" \
  -e "KONG_ADMIN_ACCESS_LOG=/dev/stdout" \
  -e "KONG_PROXY_ERROR_LOG=/dev/stderr" \
  -e "KONG_ADMIN_ERROR_LOG=/dev/stderr" \
  -e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl" \
  -e "KONG_ADMIN_GUI_URL=http://localhost:8002" \
  -e KONG_LICENSE_DATA \
  -p 8000:8000 \
  -p 8443:8443 \
  -p 8001:8001 \
  -p 8002:8002 \
  -p 8445:8445 \
  -p 8003:8003 \
  -p 8004:8004 \
  -p 127.0.0.1:8444:8444 \
  kong/kong-gateway:3.9.0.0

# Wait for Kong Gateway to start
sleep 10

#sudo docker run -d --name kong-gateway  --network=kong-net  -e "KONG_DATABASE=postgres"  -e "KONG_PG_HOST=kong-database"  -e "KONG_PG_USER=kong"  -e "KONG_PG_PASSWORD=kong"  -e "KONG_PROXY_ACCESS_LOG=/dev/stdout"  -e "KONG_ADMIN_ACCESS_LOG=/dev/stdout"  -e "KONG_PROXY_ERROR_LOG=/dev/stderr"  -e "KONG_ADMIN_ERROR_LOG=/dev/stderr"  -e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl"  -e "KONG_ADMIN_GUI_URL=http://localhost:8002"  -e KONG_LICENSE_DATA  -p 8000:8000  -p 8443:8443  -p 8001:8001  -p 8002:8002  -p 8445:8445  -p 8003:8003  -p 8004:8004  -p 127.0.0.1:8444:8444  kong/kong-gateway:3.9.0.0

# Create a service for the customer API
curl -i -X POST http://localhost:8001/services \
  --data "name=Customer" --data "url=http://ec2-3-83-202-118.compute-1.amazonaws.com:8080/Customer"

# Create a route for the customer service
curl -i -X POST http://localhost:8001/services/customer/routes \
  --data "paths[]=/Customer" \
  --data "paths[]=/Customer/(?<id>/d+)" \
  --data "paths[]=/Customer/(?<id>/d+)/(?<name>[^/]+)/(?<FiscalNumber>[^/]+)/(?<location>[^/]+)" \
  --data "methods[]=GET" \
  --data "methods[]=POST" \
  --data "methods[]=PUT" \
  --data "methods[]=DELETE"

# Create a service for the shop API
curl -i -X POST http://localhost:8001/services \
  --data "name=Shop" --data "url=http://ec2-44-211-127-59.compute-1.amazonaws.com:8080/Shop"

# Create a route for the shop service
curl -i -X POST http://localhost:8001/services/shop/routes \
  --data "paths[]=/Shop" \
  --data "paths[]=/Shop/(?<id>/d+)" \
  --data "paths[]=/Shop/(?<id>/d+)/(?<name>[^/]+)/(?<location>[^/]+)" \
  --data "methods[]=GET" \
  --data "methods[]=POST" \
  --data "methods[]=PUT" \
  --data "methods[]=DELETE"

# Create a service for the cross-selling recommendation API
curl -i -X POST http://localhost:8001/services \
  --data "name=CrossSellingRecommendation" --data "url=http://cross_selling_recommendation:8080/CrossSellingRecommendation"

# Create a route for the cross-selling recommendation service
curl -i -X POST http://localhost:8001/services/cross_selling_recommendation/routes \
  --data "paths[]=/CrossSellingRecommendation" \
  --data "paths[]=/CrossSellingRecommendation/(?<id>/d+)" \
  --data "methods[]=GET" \
  --data "methods[]=POST" \
  --data "methods[]=PUT" \
  --data "methods[]=DELETE"

# Create a service for the discount coupon API
curl -i -X POST http://localhost:8001/services \
  --data "name=Discountcoupon" --data "url=http://ec2-3-83-202-118.compute-1.amazonaws.com:8080/Discountcoupon"

# Create a route for the discount coupon service
curl -i -X POST http://localhost:8001/services/discount_coupon/routes \
  --data "paths[]=/Discountcoupon" \
  --data "paths[]=/Discountcoupon/(?<id>/d+)" \
  --data "methods[]=GET" \
  --data "methods[]=POST" \
  --data "methods[]=PUT" \
  --data "methods[]=DELETE"

# Create a service for the purchase API
curl -i -X POST http://localhost:8001/services \
  --data "name=Purchase" --data "url=http://ec2-3-83-202-118.compute-1.amazonaws.com:8080/Purchase"

# Create a route for the purchase service
curl -i -X POST http://localhost:8001/services/purchase/routes \
  --data "paths[]=/Purchase" \
  --data "paths[]=/Purchase/(?<id>/d+)" \
  --data "paths[]=/Purchase/Consume" \
  --data "methods[]=GET" \
  --data "methods[]=POST" \
  --data "methods[]=PUT" \
  --data "methods[]=DELETE"

# Create a service for the loyalty card API
curl -i -X POST http://localhost:8001/services \
  --data "name=Loyaltycard" --data "url=http://ec2-3-83-202-118.compute-1.amazonaws.com:8080/Loyaltycard"

# Create a route for the loyalty card service
curl -i -X POST http://localhost:8001/services/loyaltycard/routes \
  --data "paths[]=/Loyaltycard" \
  --data "paths[]=/Loyaltycard/(?<id>/d+)" \
  --data "paths[]=/Loyaltycard/(?<id>/d+)/(?<idCustomer>/d+)/(?<idShop>/d+)" \
  --data "methods[]=GET" \
  --data "methods[]=POST" \
  --data "methods[]=PUT" \
  --data "methods[]=DELETE"
