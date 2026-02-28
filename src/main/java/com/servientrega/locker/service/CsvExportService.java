package com.servientrega.locker.service;

import com.opencsv.CSVWriter;
import com.servientrega.locker.dto.ReportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CsvExportService {

    public byte[] exportReportToCsv(ReportResponse report) {
        log.info("Exporting report to CSV: {}", report.reportType());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Header
            writer.writeNext(new String[]{
                "Reporte: " + report.reportType(),
                "Período: " + report.startDate() + " a " + report.endDate(),
                "Generado: " + report.generatedAt()
            });
            writer.writeNext(new String[]{}); // Línea vacía
            
            // Summary
            writer.writeNext(new String[]{"RESUMEN"});
            for (Map.Entry<String, Object> entry : report.summary().entrySet()) {
                writer.writeNext(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
            }
            writer.writeNext(new String[]{}); // Línea vacía
            
            // Data
            writer.writeNext(new String[]{"DATOS DETALLADOS"});
            exportDataSection(writer, report.data());
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error exporting report to CSV", e);
            throw new RuntimeException("Error exporting to CSV: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void exportDataSection(CSVWriter writer, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            writer.writeNext(new String[]{entry.getKey().toUpperCase()});
            
            if (entry.getValue() instanceof List) {
                List<?> list = (List<?>) entry.getValue();
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    List<Map<String, Object>> mapList = (List<Map<String, Object>>) list;
                    exportListOfMaps(writer, mapList);
                }
            }
            writer.writeNext(new String[]{}); // Línea vacía
        }
    }

    private void exportListOfMaps(CSVWriter writer, List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            return;
        }
        
        // Headers
        Map<String, Object> first = data.get(0);
        List<String> headers = new ArrayList<>(first.keySet());
        writer.writeNext(headers.toArray(new String[0]));
        
        // Rows
        for (Map<String, Object> row : data) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                Object value = row.get(header);
                values.add(value != null ? String.valueOf(value) : "");
            }
            writer.writeNext(values.toArray(new String[0]));
        }
    }
}
