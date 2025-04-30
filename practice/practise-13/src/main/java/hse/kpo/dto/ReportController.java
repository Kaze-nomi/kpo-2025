package hse.kpo.dto;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hse.kpo.builders.Report;
import hse.kpo.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import hse.kpo.facade.HSE;
import hse.kpo.interfaces.domainInterfaces.ITransport;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Отчёты", description = "Управление отчётами")
public class ReportController {
    private final HSE hse;
    private final HseService hseService;

    @GetMapping("/report")
    @Operation(summary = "Получить отчёт по продажам")
    public ResponseEntity<Report> getReport() {
        return ResponseEntity.ok(hse.generateReport());
    }

    @GetMapping("/transport")
    @Operation(summary = "Получить отчёт по транспорту")
    public ResponseEntity<List<ITransport>> export() {
                List<ITransport> transport = Stream.concat(
                hseService.getCarProvider().getCars().stream(),
                hseService.getShipProvider().getShips().stream())
                .toList();
                
                return ResponseEntity.ok(transport);
    }        
}
