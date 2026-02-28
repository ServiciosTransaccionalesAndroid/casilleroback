package com.servientrega.locker.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.servientrega.locker.dto.ReportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PdfExportService {

    private static final DeviceRgb SERVIENTREGA_RED = new DeviceRgb(237, 28, 36);
    private static final DeviceRgb HEADER_GRAY = new DeviceRgb(240, 240, 240);
    private static final DeviceRgb BORDER_GRAY = new DeviceRgb(200, 200, 200);

    public byte[] exportReportToPdf(ReportResponse report) {
        log.info("Exporting report to PDF: {}", report.reportType());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(40, 40, 40, 40);
            
            // Header with Servientrega branding
            document.add(new Paragraph("SERVIENTREGA")
                .setFontSize(24)
                .setBold()
                .setFontColor(SERVIENTREGA_RED)
                .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Sistema de Casilleros Inteligentes")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
            
            // Title
            document.add(new Paragraph(formatReportTitle(report.reportType()))
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));
            
            // Period info
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
            
            infoTable.addCell(createInfoCell("Período", report.startDate() + " al " + report.endDate()));
            infoTable.addCell(createInfoCell("Generado", report.generatedAt().toString().substring(0, 19)));
            document.add(infoTable);
            
            // Summary section
            document.add(new Paragraph("RESUMEN EJECUTIVO")
                .setFontSize(14)
                .setBold()
                .setFontColor(SERVIENTREGA_RED)
                .setMarginTop(10)
                .setMarginBottom(10));
            
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();
            
            for (Map.Entry<String, Object> entry : report.summary().entrySet()) {
                summaryTable.addCell(createDataCell(formatFieldName(entry.getKey()), true));
                summaryTable.addCell(createDataCell(formatValue(entry.getValue()), false));
            }
            document.add(summaryTable);
            
            // Data section
            document.add(new Paragraph("DATOS DETALLADOS")
                .setFontSize(14)
                .setBold()
                .setFontColor(SERVIENTREGA_RED)
                .setMarginTop(20)
                .setMarginBottom(10));
            
            exportDataSection(document, report.data());
            
            // Footer
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Documento generado automáticamente por el Sistema de Casilleros Servientrega")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error exporting report to PDF", e);
            throw new RuntimeException("Error exporting to PDF: " + e.getMessage());
        }
    }

    private Cell createInfoCell(String label, String value) {
        Paragraph p = new Paragraph()
            .add(new Paragraph(label + ": ").setBold().setFontSize(10))
            .add(new Paragraph(value).setFontSize(10));
        
        return new Cell()
            .add(p)
            .setBackgroundColor(HEADER_GRAY)
            .setBorder(new SolidBorder(BORDER_GRAY, 1))
            .setPadding(8);
    }

    private Cell createDataCell(String text, boolean isHeader) {
        Cell cell = new Cell()
            .add(new Paragraph(text).setFontSize(10))
            .setBorder(new SolidBorder(BORDER_GRAY, 1))
            .setPadding(8);
        
        if (isHeader) {
            cell.setBackgroundColor(HEADER_GRAY).setBold();
        }
        
        return cell;
    }

    private String formatReportTitle(String reportType) {
        return switch (reportType) {
            case "OCCUPANCY_RATE" -> "Reporte de Ocupación";
            case "DEPOSITS_BY_PERIOD" -> "Reporte de Depósitos";
            case "RETRIEVALS_BY_PERIOD" -> "Reporte de Retiros";
            case "COMPARTMENT_USAGE" -> "Reporte de Uso de Compartimentos";
            case "ACTIVE_PACKAGES" -> "Reporte de Paquetes Activos";
            case "COURIER_PERFORMANCE" -> "Reporte de Desempeño de Mensajeros";
            case "EXPIRED_CODES" -> "Reporte de Códigos Expirados";
            case "DAILY_SUMMARY" -> "Resumen Diario";
            default -> reportType;
        };
    }

    private String formatFieldName(String field) {
        return switch (field) {
            case "totalCompartments" -> "Total Compartimentos";
            case "occupied" -> "Ocupados";
            case "available" -> "Disponibles";
            case "occupancyRate" -> "Tasa de Ocupación (%)";
            case "totalDeposits" -> "Total Depósitos";
            case "averagePerDay" -> "Promedio por Día";
            case "daysWithData" -> "Días con Datos";
            case "peakDay" -> "Día Pico";
            case "peakDayCount" -> "Depósitos Día Pico";
            case "totalActive" -> "Total Activos";
            case "totalUsage" -> "Uso Total";
            case "averageUsagePerCompartment" -> "Promedio por Compartimento";
            default -> field;
        };
    }

    private String formatValue(Object value) {
        if (value instanceof Double) {
            return String.format("%.2f", value);
        }
        return String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private void exportDataSection(Document document, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof List) {
                List<?> list = (List<?>) entry.getValue();
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    document.add(new Paragraph(formatSectionName(entry.getKey()))
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(10)
                        .setMarginBottom(5));
                    
                    List<Map<String, Object>> mapList = (List<Map<String, Object>>) list;
                    exportListOfMaps(document, mapList);
                }
            }
        }
    }

    private String formatSectionName(String section) {
        return switch (section.toLowerCase()) {
            case "bysize" -> "Por Tamaño";
            case "daily" -> "Diario";
            case "packages" -> "Paquetes";
            case "compartments" -> "Compartimentos";
            case "couriers" -> "Mensajeros";
            default -> section;
        };
    }

    private void exportListOfMaps(Document document, List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            document.add(new Paragraph("No hay datos disponibles").setItalic().setFontSize(10));
            return;
        }
        
        Map<String, Object> first = data.get(0);
        List<String> headers = new ArrayList<>(first.keySet());
        
        float[] columnWidths = new float[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            columnWidths[i] = 1;
        }
        
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
            .useAllAvailableWidth()
            .setMarginBottom(10);
        
        // Headers
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                .add(new Paragraph(formatFieldName(header)).setBold().setFontSize(10))
                .setBackgroundColor(SERVIENTREGA_RED)
                .setFontColor(ColorConstants.WHITE)
                .setBorder(new SolidBorder(BORDER_GRAY, 1))
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER));
        }
        
        // Rows with alternating colors
        boolean alternate = false;
        for (Map<String, Object> row : data) {
            for (String header : headers) {
                Object value = row.get(header);
                Cell cell = new Cell()
                    .add(new Paragraph(formatValue(value)).setFontSize(9))
                    .setBorder(new SolidBorder(BORDER_GRAY, 1))
                    .setPadding(6);
                
                if (alternate) {
                    cell.setBackgroundColor(new DeviceRgb(250, 250, 250));
                }
                
                table.addCell(cell);
            }
            alternate = !alternate;
        }
        
        document.add(table);
    }
}
