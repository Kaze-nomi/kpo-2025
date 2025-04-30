package hse.kpo.grpc;

import hse.kpo.builders.Report;
import hse.kpo.facade.HSE;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@RequiredArgsConstructor
@GrpcService
public class ReportGrpcController extends ReportServiceGrpc.ReportServiceImplBase {

    private final HSE hse;

    @Override
    public void getLatestReport(ReportRequest request,
                                StreamObserver<ReportResponse> responseObserver) {
        Report report = hse.generateReport();
        ReportResponse response = ReportResponse.newBuilder()
            .setTitle("Sales Report")
            .setContent(report.toString())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}