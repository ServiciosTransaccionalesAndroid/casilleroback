# 🚀 Guía de Escalabilidad - Sistema de Emails

## Estado Actual

✅ **Implementado:**
- Envío asíncrono con `@Async`
- Thread pool configurado (5-10 threads)
- No bloquea el API principal

## Problemas de Escalabilidad

### 1. **Límites de Gmail**
- **Gratuito:** 500 emails/día
- **Google Workspace:** 2000 emails/día
- **Solución:** Usar servicio profesional

### 2. **Sin Retry**
- Si falla el envío, se pierde
- **Solución:** Implementar cola de mensajes

### 3. **Sin Rate Limiting**
- Puede exceder límites de Gmail
- **Solución:** Implementar throttling

### 4. **QR Generado Cada Vez**
- Consume CPU
- **Solución:** Cachear QR generados

---

## Soluciones para Producción

### Opción 1: AWS SES (Recomendado)

**Ventajas:**
- 62,000 emails/mes GRATIS
- $0.10 por cada 1,000 emails adicionales
- Alta disponibilidad
- Métricas incluidas

**Implementación:**
```yaml
# application-prod.yml
spring:
  mail:
    host: email-smtp.us-east-1.amazonaws.com
    port: 587
    username: ${AWS_SES_USERNAME}
    password: ${AWS_SES_PASSWORD}
```

**Costo estimado:**
- 0-62,000 emails/mes: **GRATIS**
- 100,000 emails/mes: **$3.80**
- 1,000,000 emails/mes: **$100**

### Opción 2: SendGrid

**Ventajas:**
- 100 emails/día GRATIS
- $19.95/mes para 50,000 emails
- API simple
- Plantillas HTML

**Implementación:**
```java
// Usar SendGrid API en lugar de SMTP
@Service
public class SendGridEmailService {
    private final SendGrid sendGrid;
    
    public void sendEmail(...) {
        Mail mail = new Mail(from, subject, to, content);
        sendGrid.api(mail);
    }
}
```

**Costo estimado:**
- 0-100 emails/día: **GRATIS**
- 50,000 emails/mes: **$19.95**
- 100,000 emails/mes: **$89.95**

### Opción 3: Cola de Mensajes (Escalable)

**Arquitectura:**
```
API → RabbitMQ/SQS → Email Worker → SMTP
```

**Ventajas:**
- Retry automático
- Rate limiting
- Priorización
- Escalado horizontal

**Implementación con RabbitMQ:**

#### 1. Agregar dependencia
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### 2. Configuración
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

#### 3. Producer (enviar a cola)
```java
@Service
public class EmailQueueService {
    private final RabbitTemplate rabbitTemplate;
    
    public void queueEmail(EmailMessage message) {
        rabbitTemplate.convertAndSend("email-queue", message);
    }
}
```

#### 4. Consumer (procesar cola)
```java
@Service
public class EmailConsumer {
    @RabbitListener(queues = "email-queue")
    public void processEmail(EmailMessage message) {
        emailService.sendEmail(message);
    }
}
```

**Costo:**
- RabbitMQ self-hosted: **GRATIS**
- AWS SQS: $0.40 por millón de requests
- CloudAMQP: $19/mes (plan básico)

---

## Comparación de Opciones

| Opción | Costo/mes | Emails/mes | Complejidad | Escalabilidad |
|--------|-----------|------------|-------------|---------------|
| Gmail | GRATIS | 500 | Baja | ❌ Muy limitado |
| AWS SES | $0-100 | Ilimitado | Media | ✅ Excelente |
| SendGrid | $0-90 | 100-100k | Baja | ✅ Buena |
| RabbitMQ + SES | $20-120 | Ilimitado | Alta | ✅ Excelente |

---

## Recomendación por Escenario

### Desarrollo/Testing
✅ **Gmail** (actual)
- Gratis
- Fácil de configurar
- Suficiente para pruebas

### Startup (< 10,000 emails/mes)
✅ **AWS SES**
- Gratis hasta 62,000
- Fácil de configurar
- Escalable

### Producción (10,000 - 100,000 emails/mes)
✅ **AWS SES + Async**
- Costo bajo ($3-10/mes)
- Alta disponibilidad
- Métricas incluidas

### Producción Grande (> 100,000 emails/mes)
✅ **RabbitMQ + AWS SES**
- Retry automático
- Rate limiting
- Escalado horizontal
- Alta disponibilidad

---

## Implementación Recomendada (Fase 1)

### 1. Migrar a AWS SES

```yaml
# application-prod.yml
spring:
  mail:
    host: email-smtp.us-east-1.amazonaws.com
    port: 587
    username: ${AWS_SES_SMTP_USERNAME}
    password: ${AWS_SES_SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 2. Mantener @Async (ya implementado)

```java
@Async
public void sendRetrievalCodeEmail(...) {
    // Código actual
}
```

### 3. Agregar Retry

```java
@Async
@Retryable(
    value = {MailException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000)
)
public void sendRetrievalCodeEmail(...) {
    // Código actual
}
```

### 4. Agregar Métricas

```java
@Async
public void sendRetrievalCodeEmail(...) {
    try {
        mailSender.send(message);
        metricsService.incrementEmailsSent();
    } catch (Exception e) {
        metricsService.incrementEmailsFailed();
        throw e;
    }
}
```

---

## Implementación Recomendada (Fase 2)

### Agregar Cola de Mensajes

```java
// 1. Crear DTO
public record EmailMessage(
    String to,
    String subject,
    String body,
    byte[] qrCode
) {}

// 2. Producer
@Service
public class EmailQueueService {
    private final RabbitTemplate rabbitTemplate;
    
    public void queueEmail(EmailMessage message) {
        rabbitTemplate.convertAndSend("email-queue", message);
    }
}

// 3. Consumer
@Service
public class EmailConsumer {
    @RabbitListener(queues = "email-queue")
    @Retryable(maxAttempts = 3)
    public void processEmail(EmailMessage message) {
        emailService.sendEmail(message);
    }
}
```

---

## Caché de QR Codes

```java
@Service
public class QRCodeService {
    private final LoadingCache<String, byte[]> qrCache;
    
    public QRCodeService() {
        this.qrCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(48, TimeUnit.HOURS)
            .build(new CacheLoader<String, byte[]>() {
                public byte[] load(String code) {
                    return generateQRCodeInternal(code);
                }
            });
    }
    
    public byte[] generateQRCode(String code, int width, int height) {
        return qrCache.get(code);
    }
}
```

---

## Monitoreo

### Métricas a Monitorear

1. **Emails enviados/hora**
2. **Tasa de fallos**
3. **Tiempo de envío promedio**
4. **Cola de emails pendientes**
5. **Límites de API alcanzados**

### Herramientas

- **Prometheus + Grafana**: Métricas en tiempo real
- **AWS CloudWatch**: Si usas SES
- **SendGrid Dashboard**: Si usas SendGrid

---

## Checklist de Producción

- [x] Envío asíncrono implementado
- [ ] Migrar a AWS SES o SendGrid
- [ ] Implementar retry con @Retryable
- [ ] Agregar rate limiting
- [ ] Implementar caché de QR
- [ ] Agregar métricas
- [ ] Configurar alertas
- [ ] Implementar cola de mensajes (opcional)
- [ ] Configurar monitoreo
- [ ] Documentar límites y costos

---

## Costos Proyectados

### Escenario: 50,000 emails/mes

| Servicio | Costo/mes |
|----------|-----------|
| AWS SES | GRATIS (< 62k) |
| RabbitMQ (CloudAMQP) | $19 |
| Monitoring (CloudWatch) | $5 |
| **Total** | **$24/mes** |

### Escenario: 500,000 emails/mes

| Servicio | Costo/mes |
|----------|-----------|
| AWS SES | $44 |
| RabbitMQ (CloudAMQP) | $99 |
| Monitoring | $10 |
| **Total** | **$153/mes** |

---

## Conclusión

**Para empezar:**
1. ✅ Mantener Gmail + @Async (ya implementado)
2. Migrar a AWS SES cuando superes 500 emails/día
3. Agregar RabbitMQ cuando superes 10,000 emails/mes

**Implementación actual es suficiente para:**
- Desarrollo
- Testing
- MVP
- Hasta ~500 emails/día

**Necesitas mejorar cuando:**
- Superes 500 emails/día
- Necesites garantías de entrega
- Requieras métricas detalladas
- Escales a múltiples lockers
