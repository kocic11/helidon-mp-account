Build and run docker images:

docker build -t account-mp target

docker run --rm -p 8090:8090 --name account --link user account-mp:latest

Test (requires User service up and running, see helidon-mp-user)

Create account:

curl -X PUT -H "Content-Type: application/json" -d '{"type:"premium", "userId":0001}' localhost:8090/account/add

Get acount by type:

curl -X GET -H "Content-Type: application/json"  localhost:8090/account/get/{type} && echo

Get account by Id:

curl -X GET -H "Content-Type: application/json"  localhost:8090/account/id/{id} && echo
