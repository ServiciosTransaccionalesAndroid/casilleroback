# Plan de Implementación: Reportes e Histórico de Operaciones

## Objetivo
Implementar un sistema de reportes y consulta de histórico de operaciones para el sistema de casilleros inteligentes.

---

## 1. Análisis de Requerimientos

### 1.1 Reportes Necesarios

#### Reportes Operacionales
- **Depósitos por período**: Total de paquetes depositados por día/semana/mes
- **Retiros por período**: Total de paquetes retirados por día/semana/mes
- **Tiempo promedio de permanencia**: Tiempo que los paquetes permanecen en lockers
- **Tasa de ocupación**: Porcentaje de compartimentos ocupados vs disponibles
- **Códigos expirados**: Códigos de retiro que expiraron sin ser usados

#### Reportes por Locker
- **Estado de compartimentos**: Disponibles, ocupados, en mantenimiento
- **Histórico de uso por compartimento**: Cuántas veces se ha usado cada compartimento
- **Paquetes activos por locker**: Paquetes actualmente almacenados

#### Reportes por Mensajero
- **Depósitos realizados por mensajero**: Total de depósitos por courier
- **Rendimiento por mensajero**: Depósitos por día/hora
- **Histórico de actividad**: Todas las operaciones realizadas

#### Reportes de Clientes
- **Paquetes por destinatario**: Histórico de paquetes recibidos
- **Tiempo de retiro promedio**: Cuánto tardan los clientes en retirar
- **Códigos no utilizados**: Clientes que no retiraron sus paquetes

### 1.2 Histórico de Operaciones
- Registro completo de todas las transacciones
- Auditoría de cambios de estado
- Trazabilidad de paquetes
- Logs de acceso y operaciones

---

## 2. Diseño de Base de Datos

### 2.1 Tablas Existentes a Utilizar
- `deposits` - Información de depósitos
- `retrievals` - Información de retiros
- `retrieval_codes` - Códigos generados y su estado
- `status_history` - Historial de cambios de estado (ya existe)
- `packages` - Información de paquetes
- `compartments` - Estado de compartimentos
- `couriers` - Información de mensajeros

### 2.2 Nueva Tabla: operation_logs
```sql
CREATE TABLE operation_logs (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL, -- DEPOSIT, RETRIEVAL, STATUS_CHANGE, etc.
    entity_type VARCHAR(50) NOT NULL,    -- PACKAGE, COMPARTMENT, LOCKER, etc.
    entity_id BIGINT NOT NULL,
    user_type VARCHAR(50),               -- COURIER, SYSTEM, CLIENT
    user_id BIGINT,
    description TEXT,
    metadata JSONB,                      -- Datos adicionales flexibles
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_operation_logs_type ON operation_logs(operation_type);
CREATE INDEX idx_operation_logs_entity ON operation_logs(entity_type, entity_id);
CREATE INDEX idx_operation_logs_created ON operation_logs(created_at);
```

### 2.3 Vista Materializada: daily_statistics
```sql
CREATE MATERIALIZED VIEW daily_statistics AS
SELECT 
    DATE(d.deposit_timestamp) as date,
    COUNT(DISTINCT d.id) as total_deposits,
    COUNT(DISTINCT r.id) as total_retrievals,
    COUNT(DISTINCT d.locker_id) as active_lockers,
    COUNT(DISTINCT d.courier_id) as active_couriers,
    AVG(EXTRACT(EPOCH FROM (r.retrieval_timestamp - d.deposit_timestamp))/3600) as avg_hours_in_locker
FROM deposits d
LEFT JOIN retrievals r ON d.id = r.deposit_id
GROUP BY DATE(d.deposit_timestamp);

CREATE UNIQUE INDEX ON daily_statistics(date);
```

---

## 3. Estructura de Código

### 3.1 Nuevas Entidades

#### OperationLog.java
```java
@Entity
@Table(name = "operation_logs")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    
    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    
    private Long entityId;
    private String userType;
    private Long userId;
    private String description;
    
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    private String ipAddress;
    private LocalDateTime createdAt;
}
```

### 3.2 Nuevos DTOs

#### ReportRequest.java
```java
public record ReportRequest(
    LocalDate startDate,
    LocalDate endDate,
    Long lockerId,
    Long courierId,
    ReportType reportType,
    ReportFormat format  // JSON, CSV, PDF
) {}
```

#### ReportResponse.java
```java
public record ReportResponse(
    String reportType,
    LocalDate startDate,
    LocalDate endDate,
    Map<String, Object> data,
    Map<String, Object> summary,
    LocalDateTime generatedAt
) {}
```

#### OperationHistoryDTO.java
```java
public record OperationHistoryDTO(
    Long id,
    String operationType,
    String entityType,
    Long entityId,
    String description,
    String userType,
    String userName,
    LocalDateTime timestamp
) {}
```

### 3.3 Nuevos Enums

```java
public enum ReportType {
    DEPOSITS_BY_PERIOD,
    RETRIEVALS_BY_PERIOD,
    OCCUPANCY_RATE,
    COURIER_PERFORMANCE,
    EXPIRED_CODES,
    COMPARTMENT_USAGE,
    PACKAGE_HISTORY,
    DAILY_SUMMARY
}

public enum OperationType {
    DEPOSIT,
    RETRIEVAL,
    STATUS_CHANGE,
    CODE_GENERATED,
    CODE_EXPIRED,
    COMPARTMENT_OPENED,
    ALERT_CREATED,
    MAINTENANCE
}

public enum EntityType {
    PACKAGE,
    DEPOSIT,
    RETRIEVAL,
    COMPARTMENT,
    LOCKER,
    COURIER,
    RETRIEVAL_CODE
}
```

---

## 4. Servicios

### 4.1 ReportService.java
```java
@Service
public class ReportService {
    
    // Reportes operacionales
    ReportResponse getDepositsByPeriod(LocalDate start, LocalDate end, Long lockerId);
    ReportResponse getRetrievalsByPeriod(LocalDate start, LocalDate end, Long lockerId);
    ReportResponse getOccupancyRate(Long lockerId);
    ReportResponse getExpiredCodes(LocalDate start, LocalDate end);
    
    // Reportes por mensajero
    ReportResponse getCourierPerformance(Long courierId, LocalDate start, LocalDate end);
    ReportResponse getDepositsByCourier(LocalDate start, LocalDate end);
    
    // Reportes por compartimento
    ReportResponse getCompartmentUsage(Long lockerId);
    ReportResponse getActivePackages(Long lockerId);
    
    // Reporte consolidado
    ReportResponse getDailySummary(LocalDate date);
    
    // Exportación
    byte[] exportReportToCsv(ReportResponse report);
    byte[] exportReportToPdf(ReportResponse report);
}
```

### 4.2 OperationLogService.java
```java
@Service
public class OperationLogService {
    
    void logOperation(OperationType type, EntityType entityType, Long entityId, 
                     String description, Map<String, Object> metadata);
    
    void logDeposit(Deposit deposit, Courier courier);
    void logRetrieval(Retrieval retrieval);
    void logStatusChange(Package pkg, PackageStatus oldStatus, PackageStatus newStatus);
    void logCodeGeneration(RetrievalCode code);
    
    List<OperationLog> getOperationHistory(LocalDate start, LocalDate end, 
                                          OperationType type, Long entityId);
    
    List<OperationLog> getEntityHistory(EntityType entityType, Long entityId);
}
```

### 4.3 HistoryService.java
```java
@Service
public class HistoryService {
    
    // Histórico de paquetes
    List<OperationHistoryDTO> getPackageHistory(String trackingNumber);
    
    // Histórico de compartimentos
    List<OperationHistoryDTO> getCompartmentHistory(Long compartmentId);
    
    // Histórico de mensajero
    List<OperationHistoryDTO> getCourierHistory(Long courierId, LocalDate start, LocalDate end);
    
    // Histórico de locker
    List<OperationHistoryDTO> getLockerHistory(Long lockerId, LocalDate start, LocalDate end);
    
    // Auditoría completa
    List<OperationHistoryDTO> getFullAuditTrail(LocalDate start, LocalDate end);
}
```

---

## 5. Controladores

### 5.1 ReportController.java
```java
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @GetMapping("/deposits")
    ResponseEntity<ReportResponse> getDepositsReport(
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate,
        @RequestParam(required = false) Long lockerId
    );
    
    @GetMapping("/retrievals")
    ResponseEntity<ReportResponse> getRetrievalsReport(...);
    
    @GetMapping("/occupancy")
    ResponseEntity<ReportResponse> getOccupancyReport(@RequestParam Long lockerId);
    
    @GetMapping("/courier-performance")
    ResponseEntity<ReportResponse> getCourierPerformance(...);
    
    @GetMapping("/daily-summary")
    ResponseEntity<ReportResponse> getDailySummary(@RequestParam LocalDate date);
    
    @GetMapping("/export/csv")
    ResponseEntity<byte[]> exportToCsv(@RequestParam ReportType type, ...);
    
    @GetMapping("/export/pdf")
    ResponseEntity<byte[]> exportToPdf(@RequestParam ReportType type, ...);
}
```

### 5.2 HistoryController.java
```java
@RestController
@RequestMapping("/api/history")
public class HistoryController {
    
    @GetMapping("/package/{trackingNumber}")
    ResponseEntity<List<OperationHistoryDTO>> getPackageHistory(
        @PathVariable String trackingNumber
    );
    
    @GetMapping("/compartment/{compartmentId}")
    ResponseEntity<List<OperationHistoryDTO>> getCompartmentHistory(
        @PathVariable Long compartmentId
    );
    
    @GetMapping("/courier/{courierId}")
    ResponseEntity<List<OperationHistoryDTO>> getCourierHistory(
        @PathVariable Long courierId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    );
    
    @GetMapping("/locker/{lockerId}")
    ResponseEntity<List<OperationHistoryDTO>> getLockerHistory(...);
    
    @GetMapping("/operations")
    ResponseEntity<List<OperationHistoryDTO>> getOperations(
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate,
        @RequestParam(required = false) OperationType type
    );
}
```

---

## 6. Integración con Servicios Existentes

### 6.1 Modificar DepositService
```java
@Transactional
public DepositResult processDeposit(...) {
    // ... código existente ...
    
    // Agregar logging
    operationLogService.logDeposit(savedDeposit, courier);
    
    return result;
}
```

### 6.2 Modificar RetrievalService
```java
@Transactional
public RetrievalResult processRetrieval(...) {
    // ... código existente ...
    
    // Agregar logging
    operationLogService.logRetrieval(savedRetrieval);
    
    return result;
}
```

### 6.3 Modificar PackageService
```java
public void updatePackageStatus(String trackingNumber, PackageStatus newStatus) {
    Package pkg = findByTrackingNumber(trackingNumber);
    PackageStatus oldStatus = pkg.getStatus();
    
    pkg.setStatus(newStatus);
    packageRepository.save(pkg);
    
    // Log del cambio
    operationLogService.logStatusChange(pkg, oldStatus, newStatus);
}
```

---

## 7. Queries Optimizadas

### 7.1 Repositorio: ReportRepository
```java
@Repository
public interface ReportRepository {
    
    @Query("""
        SELECT DATE(d.deposit_timestamp) as date, COUNT(*) as total
        FROM deposits d
        WHERE d.deposit_timestamp BETWEEN :start AND :end
        AND (:lockerId IS NULL OR d.locker_id = :lockerId)
        GROUP BY DATE(d.deposit_timestamp)
        ORDER BY date
    """)
    List<DailyCount> getDepositsByDay(LocalDate start, LocalDate end, Long lockerId);
    
    @Query("""
        SELECT c.size, 
               COUNT(*) as total,
               SUM(CASE WHEN c.status = 'OCUPADO' THEN 1 ELSE 0 END) as occupied
        FROM compartments c
        WHERE c.locker_id = :lockerId
        GROUP BY c.size
    """)
    List<OccupancyStats> getOccupancyBySize(Long lockerId);
    
    @Query("""
        SELECT co.name as courierName,
               COUNT(d.id) as totalDeposits,
               DATE(d.deposit_timestamp) as date
        FROM deposits d
        JOIN couriers co ON d.courier_id = co.id
        WHERE d.deposit_timestamp BETWEEN :start AND :end
        GROUP BY co.name, DATE(d.deposit_timestamp)
        ORDER BY date, totalDeposits DESC
    """)
    List<CourierStats> getCourierPerformance(LocalDate start, LocalDate end);
}
```

---

## 8. Plan de Implementación por Fases

### Fase 1: Infraestructura Base (2-3 días)
1. Crear migración V4__add_operation_logs.sql
2. Crear entidad OperationLog
3. Crear enums (OperationType, EntityType, ReportType)
4. Crear OperationLogService básico
5. Integrar logging en servicios existentes

### Fase 2: Reportes Básicos (3-4 días)
1. Crear DTOs de reportes
2. Implementar ReportService con reportes básicos:
   - Depósitos por período
   - Retiros por período
   - Tasa de ocupación
3. Crear ReportController
4. Crear queries optimizadas
5. Pruebas unitarias

### Fase 3: Histórico de Operaciones (2-3 días)
1. Implementar HistoryService
2. Crear HistoryController
3. Endpoints de consulta de histórico
4. Pruebas de integración

### Fase 4: Reportes Avanzados (3-4 días)
1. Reportes por mensajero
2. Reportes por compartimento
3. Códigos expirados
4. Reporte consolidado diario
5. Vista materializada para estadísticas

### Fase 5: Exportación (2-3 días)
1. Exportación a CSV
2. Exportación a PDF (usando iText o similar)
3. Generación de gráficos (opcional)

### Fase 6: Optimización y Dashboard (2-3 días)
1. Índices de base de datos
2. Caché de reportes frecuentes
3. Paginación de resultados
4. Documentación Swagger

---

## 9. Endpoints Finales

### Reportes
```
GET  /api/reports/deposits?startDate=2024-01-01&endDate=2024-01-31&lockerId=1
GET  /api/reports/retrievals?startDate=2024-01-01&endDate=2024-01-31
GET  /api/reports/occupancy?lockerId=1
GET  /api/reports/courier-performance?courierId=1&startDate=2024-01-01&endDate=2024-01-31
GET  /api/reports/expired-codes?startDate=2024-01-01&endDate=2024-01-31
GET  /api/reports/daily-summary?date=2024-01-15
GET  /api/reports/export/csv?type=DEPOSITS_BY_PERIOD&startDate=...
GET  /api/reports/export/pdf?type=DAILY_SUMMARY&date=...
```

### Histórico
```
GET  /api/history/package/{trackingNumber}
GET  /api/history/compartment/{compartmentId}
GET  /api/history/courier/{courierId}?startDate=...&endDate=...
GET  /api/history/locker/{lockerId}?startDate=...&endDate=...
GET  /api/history/operations?startDate=...&endDate=...&type=DEPOSIT
```

---

## 10. Consideraciones Técnicas

### 10.1 Performance
- Usar índices en columnas de fecha
- Implementar paginación para consultas grandes
- Caché de reportes frecuentes (Redis)
- Vista materializada para estadísticas diarias

### 10.2 Seguridad
- Autenticación JWT requerida
- Roles: ADMIN puede ver todos los reportes, COURIER solo sus propios datos
- Validación de rangos de fechas
- Rate limiting en endpoints de reportes

### 10.3 Escalabilidad
- Procesamiento asíncrono para reportes pesados
- Generación de reportes en background
- Notificación cuando el reporte esté listo
- Almacenamiento temporal de reportes generados

---

## 11. Ejemplo de Respuesta

### Reporte de Depósitos
```json
{
  "reportType": "DEPOSITS_BY_PERIOD",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "data": {
    "daily": [
      {"date": "2024-01-01", "count": 15},
      {"date": "2024-01-02", "count": 23},
      {"date": "2024-01-03", "count": 18}
    ],
    "byLocker": [
      {"lockerId": 1, "lockerName": "Locker Centro", "count": 120},
      {"lockerId": 2, "lockerName": "Locker Norte", "count": 95}
    ],
    "byCourier": [
      {"courierId": 1, "courierName": "Juan Pérez", "count": 85},
      {"courierId": 2, "courierName": "María González", "count": 130}
    ]
  },
  "summary": {
    "totalDeposits": 215,
    "averagePerDay": 6.9,
    "peakDay": "2024-01-15",
    "peakDayCount": 28
  },
  "generatedAt": "2024-02-18T19:30:00"
}
```

### Histórico de Paquete
```json
[
  {
    "id": 1,
    "operationType": "DEPOSIT",
    "entityType": "PACKAGE",
    "entityId": 123,
    "description": "Paquete depositado en compartimento 5",
    "userType": "COURIER",
    "userName": "Juan Pérez",
    "timestamp": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "operationType": "CODE_GENERATED",
    "entityType": "RETRIEVAL_CODE",
    "entityId": 456,
    "description": "Código de retiro generado: RCSV3K9P",
    "userType": "SYSTEM",
    "userName": null,
    "timestamp": "2024-01-15T10:30:05"
  },
  {
    "id": 3,
    "operationType": "RETRIEVAL",
    "entityType": "PACKAGE",
    "entityId": 123,
    "description": "Paquete retirado exitosamente",
    "userType": "CLIENT",
    "userName": "Carlos Rodríguez",
    "timestamp": "2024-01-16T14:20:00"
  }
]
```

---

## 12. Tiempo Estimado Total

- **Fase 1:** 2-3 días
- **Fase 2:** 3-4 días
- **Fase 3:** 2-3 días
- **Fase 4:** 3-4 días
- **Fase 5:** 2-3 días
- **Fase 6:** 2-3 días

**Total: 14-20 días de desarrollo**

---

## 13. Dependencias Adicionales

```xml
<!-- Para exportación PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>

<!-- Para exportación CSV -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.7.1</version>
</dependency>

<!-- Para JSONB en PostgreSQL -->
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-utils-hibernate-60</artifactId>
    <version>3.5.1</version>
</dependency>
```

---

## 14. Próximos Pasos

1. Revisar y aprobar el plan
2. Crear branch `feature/reports-history`
3. Implementar Fase 1
4. Hacer commit y push
5. Continuar con fases siguientes
6. Testing completo
7. Documentación en Swagger
8. Merge a main

---

**Documento creado:** 2024-02-18  
**Versión:** 1.0  
**Estado:** Propuesta inicial
