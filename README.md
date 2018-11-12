To configure Kong API Gateway and run the services, follow the steps below.
1.	Create a Docker network
```
docker network create kong-net
```
2.  Start a database
```
docker run -d --name kong-database \
               --network=kong-net \
               -p 5432:5432 \
               -e "POSTGRES_USER=kong" \
               -e "POSTGRES_DB=kong" \
               postgres:9.6
```           
2.	Prepare the database
```
docker run --rm \
     --network=kong-net \
     -e "KONG_DATABASE=postgres" \
     -e "KONG_PG_HOST=kong-database" \
     kong:latest kong migrations up
```    
3.	Start Kong
```
docker run -d --name kong \
     --network=kong-net \
     -e "KONG_DATABASE=postgres" \
     -e "KONG_PG_HOST=kong-database" \
     -e "KONG_CASSANDRA_CONTACT_POINTS=kong-database" \
     -e "KONG_PROXY_ACCESS_LOG=/dev/stdout" \
     -e "KONG_ADMIN_ACCESS_LOG=/dev/stdout" \
     -e "KONG_PROXY_ERROR_LOG=/dev/stderr" \
     -e "KONG_ADMIN_ERROR_LOG=/dev/stderr" \
     -e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl" \
     -p 8000:8000 \
     -p 8443:8443 \
     -p 8001:8001 \
     -p 8444:8444 \
     kong:latest
```     
4.	Test Kong
```
curl -i http://localhost:8001/
```
5.	Add the service using the Admin API
```
curl -i -X POST \
  --url http://localhost:8001/services/ \
  --data 'name=user-service' \
  --data 'url=http://user:8080'

curl -i -X POST \
  --url http://localhost:8001/services/ \
  --data 'name=account-service' \
  --data 'url=http://account:8090'
```
6.	Add the routes for the services
```
curl -i -X POST \
  --url http://localhost:8001/services/user-service/routes \
  --data 'hosts[]=user'

curl -i -X POST \
  --url http://localhost:8001/services/account-service/routes \
  --data 'hosts[]=account'
```
7.	Build and start the services
```
mvn package
docker build -t user-mp target
docker build -t account-mp target
docker run --rm -p 8090:8090 --name user --network kong-net user-mp:latest
docker run --rm -p 8090:8090 --name account --network kong-net account-mp:latest
```
8.	Forward the requests through Kong
```
curl -i -X GET \
  --url http://localhost:8000/user/getall \
  --header 'Host: user'

curl -i -X GET \
  --url http://localhost:8000/account/id/0001 \
  --header 'Host: account'
```
