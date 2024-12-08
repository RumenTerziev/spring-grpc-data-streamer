package com.example.spring_grpc_data_streamer.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import src.main.proto.DataStreamerGrpc;
import src.main.proto.StreamRequest;
import src.main.proto.StreamResponse;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@GrpcService
public class DataStreamerService extends DataStreamerGrpc.DataStreamerImplBase {

    @Override
    public void streamData(StreamRequest request, StreamObserver<StreamResponse> responseObserver) {
        Duration duration = Duration.ofMinutes(5);
        Instant startTime = Instant.now();

        try {
            while (true) {
                Duration elapsedTime = Duration.between(startTime, Instant.now());
                if (elapsedTime.compareTo(duration) > 0) {
                    break;
                }

                String message = String.format("Elapsed time: %.2f seconds", elapsedTime.toMillis() / 1000.0);
                log.info(message);

                StreamResponse response = StreamResponse.newBuilder()
                        .setMessage(message)
                        .build();
                responseObserver.onNext(response);

                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            responseObserver.onCompleted();
        }
    }
}
