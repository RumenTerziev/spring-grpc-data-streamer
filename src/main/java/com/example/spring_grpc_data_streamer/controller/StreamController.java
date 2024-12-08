package com.example.spring_grpc_data_streamer.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import src.main.proto.DataStreamerGrpc;
import src.main.proto.StreamRequest;
import src.main.proto.StreamResponse;

@RestController
public class StreamController {

    private static final String GRPC_SERVER_ADDRESS = "localhost";

    @GetMapping(value = "/api/stream-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamData(@RequestParam("query") String query) {
        if (query == null || query.isEmpty()) {
            return Flux.error(new IllegalArgumentException("Query parameter 'query' is required"));
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress(GRPC_SERVER_ADDRESS, 9090)
                .usePlaintext()
                .build();

        DataStreamerGrpc.DataStreamerStub stub = DataStreamerGrpc.newStub(channel);

        StreamRequest request = StreamRequest.newBuilder()
                .setData(query)
                .build();

        return Flux.create(sink -> {
            StreamObserver<StreamResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(StreamResponse response) {
                    sink.next("From flux:" + response.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    sink.error(t);
                    channel.shutdownNow();
                }

                @Override
                public void onCompleted() {
                    sink.complete();
                    channel.shutdownNow();
                }
            };

            stub.streamData(request, responseObserver);

            sink.onCancel(channel::shutdownNow);
        });
    }
}
