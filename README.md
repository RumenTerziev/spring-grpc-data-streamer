# spring-grpc-data-streamer

## This is a simple example project for spring boot with gRPC setup.
> The idea is to simulate a communication for server and client with gRPC. The project start a simple gRPC server
> which listens on port 9090 and handles predefined types of requests.


### To run the project open a terminal from root directory and run the following commands

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

### You may test the request to server from postman by choosing gRPC type of reques, importing the proto 
### file - /src/main/proto/streamer.proto and providing a valid request body which is for example purposes for now:

```json
{
  "data": "example"
}
```
