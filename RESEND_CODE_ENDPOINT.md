# 📧 Endpoint: Reenviar Código de Retiro

## Descripción
Permite reenviar el correo con el código de retiro al cliente cuando lo ha perdido o no lo recibió.

---

## 📍 Endpoint

```
POST /api/packages/{trackingNumber}/resend-code
```

---

## 🔑 Parámetros

### Path Parameter
- **trackingNumber** (string, requerido): Número de guía del paquete

---

## ✅ Ejemplo de Uso

### Request

```bash
POST /api/packages/PKG123/resend-code
```

### Response Exitosa (200 OK)

```json
"Retrieval code email sent successfully"
```

---

## ❌ Errores Posibles

### 1. Paquete no encontrado
```json
{
  "error": "Package not found: PKG123"
}
```

### 2. Paquete no está en locker
```json
{
  "error": "Package is not in locker. Current status: EN_TRANSITO"
}
```

### 3. No hay código activo
```json
{
  "error": "No active retrieval code found for package: PKG123"
}
```

### 4. Depósito no encontrado
```json
{
  "error": "Deposit not found for package: PKG123"
}
```

---

## 📋 Validaciones

El endpoint valida que:
1. ✅ El paquete existe
2. ✅ El paquete está en estado `EN_LOCKER`
3. ✅ Existe un código de retiro activo (no usado)
4. ✅ El paquete tiene un depósito asociado

---

## 📧 Contenido del Email

El correo incluye:
- 🔑 Código de retiro (ej: `RCSV2X4Y`)
- 🔢 PIN secreto de 6 dígitos
- 📍 Ubicación del locker
- 🗓️ Fecha de expiración
- 📱 Instrucciones de retiro
- 🖼️ Código QR adjunto

---

## 🔄 Casos de Uso

### 1. Cliente perdió el correo
```bash
curl -X POST https://tu-api.railway.app/api/packages/PKG123/resend-code
```

### 2. Correo no llegó
```bash
curl -X POST https://tu-api.railway.app/api/packages/PKG456/resend-code
```

### 3. Cliente eliminó el correo por error
```bash
curl -X POST https://tu-api.railway.app/api/packages/PKG789/resend-code
```

---

## 🎯 Flujo Completo

```
1. Cliente llama al soporte: "Perdí mi código"
   ↓
2. Soporte solicita número de guía
   ↓
3. Soporte ejecuta: POST /api/packages/{trackingNumber}/resend-code
   ↓
4. Sistema valida el paquete
   ↓
5. Sistema reenvía el correo con código + PIN + QR
   ↓
6. Cliente recibe el correo
   ↓
7. Cliente puede retirar su paquete
```

---

## 🔐 Seguridad

- ✅ Solo reenvía si el paquete está en locker
- ✅ Solo reenvía códigos activos (no usados)
- ✅ No genera nuevo código (usa el existente)
- ✅ Envía al email registrado del destinatario

---

## 🧪 Testing

### Test 1: Reenvío exitoso
```bash
# 1. Depositar paquete
POST /api/deposits
{
  "trackingNumber": "TEST001",
  "lockerId": 1,
  "courierEmployeeId": "EMP001"
}

# 2. Reenviar código
POST /api/packages/TEST001/resend-code

# Resultado: Email reenviado ✅
```

### Test 2: Paquete no en locker
```bash
# Paquete en tránsito
POST /api/packages/TEST002/resend-code

# Error: "Package is not in locker" ❌
```

### Test 3: Código ya usado
```bash
# 1. Retirar paquete
POST /api/retrievals
{
  "code": "RCSV2X4Y",
  "secretPin": "123456"
}

# 2. Intentar reenviar
POST /api/packages/TEST001/resend-code

# Error: "No active retrieval code found" ❌
```

---

## 📊 Estados del Paquete

| Estado | ¿Puede reenviar? | Razón |
|--------|------------------|-------|
| EN_TRANSITO | ❌ | No está en locker |
| EN_LOCKER | ✅ | Código activo disponible |
| ENTREGADO | ❌ | Ya fue retirado |
| EXPIRADO | ❌ | Código expirado |

---

## 🚀 Integración Frontend

### JavaScript/React
```javascript
const resendCode = async (trackingNumber) => {
  try {
    const response = await fetch(
      `${API_URL}/api/packages/${trackingNumber}/resend-code`,
      { method: 'POST' }
    );
    
    if (response.ok) {
      alert('Código reenviado exitosamente');
    } else {
      const error = await response.json();
      alert(`Error: ${error.error}`);
    }
  } catch (error) {
    console.error('Error:', error);
  }
};

// Uso
resendCode('PKG123');
```

### Python
```python
import requests

def resend_code(tracking_number):
    url = f"{API_URL}/api/packages/{tracking_number}/resend-code"
    response = requests.post(url)
    
    if response.status_code == 200:
        print("Código reenviado exitosamente")
    else:
        print(f"Error: {response.json()['error']}")

# Uso
resend_code('PKG123')
```

---

## 📞 Soporte

### Preguntas Frecuentes

**P: ¿Cuántas veces puedo reenviar el código?**  
R: Ilimitadas veces mientras el código esté activo.

**P: ¿Se genera un nuevo código al reenviar?**  
R: No, se reenvía el mismo código existente.

**P: ¿Cambia la fecha de expiración?**  
R: No, mantiene la fecha original.

**P: ¿Puedo reenviar después de que expire?**  
R: No, el código debe estar activo.

---

## ✅ Checklist de Implementación

- [x] Endpoint creado
- [x] Validaciones implementadas
- [x] Query para buscar código activo
- [x] Integración con EmailService
- [x] Manejo de errores
- [x] Documentación Swagger
- [x] Tests unitarios

---

**Última actualización:** Enero 2025  
**Versión:** 1.1.0
