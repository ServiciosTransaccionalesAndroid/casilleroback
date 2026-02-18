# 📦 CÓMO UN CLIENTE RETIRA SU PAQUETE

## 🔄 FLUJO COMPLETO DEL PROCESO

### 1️⃣ MENSAJERO DEPOSITA EL PAQUETE

**Ubicación:** En el locker físico

1. Mensajero llega al locker con el paquete
2. Hace login en la pantalla táctil del locker
3. Escanea el código de barras del paquete (SRV123456789)
4. El locker asigna un compartimento automáticamente
5. El compartimento se abre
6. Mensajero deposita el paquete y cierra la puerta

**Backend registra el depósito:**
```
POST /api/deposits
{
  "trackingNumber": "SRV123456789",
  "lockerId": 1,
  "compartmentId": 5,
  "courierId": 1
}

Respuesta:
{
  "retrievalCode": "A3K7M9P2",  ← CÓDIGO GENERADO
  "expiresAt": "2024-01-19T10:30:00"
}
```

---

### 2️⃣ CLIENTE RECIBE NOTIFICACIÓN

**Automáticamente el backend envía:**

📱 **SMS al cliente:**
```
Servientrega: Tu paquete está en Locker Centro 
(Centro Comercial Andino). 
Código de retiro: A3K7M9P2
Válido hasta: 19/01/2024 10:30
```

📧 **Email al cliente:**
```
Asunto: Tu paquete está listo para retirar

Hola Carlos Rodríguez,

Tu paquete ha llegado y está listo para ser retirado.

Código de retiro: A3K7M9P2
Ubicación: Locker Centro
Dirección: Centro Comercial Andino, Carrera 11 #82-71
Válido hasta: 19/01/2024 10:30

Instrucciones:
1. Dirígete al locker
2. Ingresa tu código en la pantalla
3. El casillero se abrirá automáticamente
4. Retira tu paquete y cierra la puerta

Gracias por usar Servientrega.
```

---

### 3️⃣ CLIENTE VA AL LOCKER

**Ubicación:** En el locker físico

El cliente llega al locker con su código: **A3K7M9P2**

---

### 4️⃣ CLIENTE INGRESA EL CÓDIGO

**En la pantalla táctil del locker:**

```
┌─────────────────────────────┐
│   SERVIENTREGA LOCKER       │
│                             │
│  Ingresa tu código:         │
│  ┌─────────────────┐        │
│  │ A3K7M9P2        │        │
│  └─────────────────┘        │
│                             │
│     [VALIDAR CÓDIGO]        │
└─────────────────────────────┘
```

**El software del locker llama al backend:**
```
GET /api/retrievals/validate?code=A3K7M9P2

Respuesta:
{
  "valid": true,
  "compartmentId": 5,
  "trackingNumber": "SRV123456789",
  "message": "Code is valid"
}
```

---

### 5️⃣ LOCKER ABRE EL COMPARTIMENTO

**Si el código es válido:**

```
┌─────────────────────────────┐
│   ✓ CÓDIGO VÁLIDO           │
│                             │
│  Abriendo compartimento 5   │
│                             │
│  Por favor retira tu        │
│  paquete y cierra la        │
│  puerta.                    │
└─────────────────────────────┘
```

- El compartimento #5 se abre automáticamente
- Cliente retira su paquete
- Cliente cierra la puerta

---

### 6️⃣ LOCKER REGISTRA EL RETIRO

**El software del locker llama al backend:**
```
POST /api/retrievals
{
  "code": "A3K7M9P2",
  "photoUrl": "https://storage.example.com/photo2.jpg"
}

Respuesta:
{
  "retrievalId": 1,
  "timestamp": "2024-01-17T14:20:00",
  "message": "Package retrieved successfully"
}
```

**Backend automáticamente:**
- Marca el código como usado
- Libera el compartimento (DISPONIBLE)
- Actualiza el paquete a ENTREGADO
- Envía confirmación al cliente

---

### 7️⃣ CLIENTE RECIBE CONFIRMACIÓN

📱 **SMS:**
```
Servientrega: Tu paquete SRV123456789 
ha sido entregado exitosamente. 
Gracias por tu preferencia.
```

📧 **Email:**
```
Asunto: Paquete entregado

Hola Carlos Rodríguez,

Tu paquete con número de guía SRV123456789 
ha sido entregado exitosamente.

Fecha y hora: 17/01/2024 14:20

Gracias por confiar en Servientrega.
```

---

## 🎯 RESUMEN VISUAL

```
┌──────────────┐
│  MENSAJERO   │
│  Deposita    │
└──────┬───────┘
       │
       ▼
┌──────────────────────┐
│  BACKEND GENERA      │
│  Código: A3K7M9P2    │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  NOTIFICACIÓN        │
│  SMS + Email         │
│  al cliente          │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  CLIENTE             │
│  Recibe código       │
│  A3K7M9P2            │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  CLIENTE             │
│  Va al locker        │
│  Ingresa código      │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  LOCKER              │
│  Valida código       │
│  Abre compartimento  │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  CLIENTE             │
│  Retira paquete      │
│  Cierra puerta       │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  BACKEND             │
│  Registra retiro     │
│  Envía confirmación  │
└──────────────────────┘
```

---

## 🔑 PUNTOS CLAVE

### Para el Cliente:
1. ✅ **NO necesita tracking number** para retirar
2. ✅ **Solo necesita el código de retiro** (A3K7M9P2)
3. ✅ **Recibe el código por SMS y Email** automáticamente
4. ✅ **No necesita autenticación** (el código es suficiente)
5. ✅ **Tiene 48 horas** para retirar (configurable)

### Tracking Number vs Código de Retiro:

| Concepto | Tracking Number | Código de Retiro |
|----------|----------------|------------------|
| **Ejemplo** | SRV123456789 | A3K7M9P2 |
| **Quién lo usa** | Mensajero | Cliente |
| **Para qué** | Depositar | Retirar |
| **Cuándo se genera** | Al crear el envío | Al depositar en locker |
| **Dónde se obtiene** | Sistema ERP | SMS/Email del cliente |

---

## 🚨 CASOS ESPECIALES

### Código Inválido
```
┌─────────────────────────────┐
│   ✗ CÓDIGO INVÁLIDO         │
│                             │
│  El código ingresado no     │
│  es válido, ha expirado     │
│  o ya fue usado.            │
│                             │
│  Por favor verifica e       │
│  intenta nuevamente.        │
└─────────────────────────────┘
```

### Código Expirado
```
┌─────────────────────────────┐
│   ✗ CÓDIGO EXPIRADO         │
│                             │
│  Tu código expiró el        │
│  19/01/2024 10:30           │
│                             │
│  Contacta a Servientrega    │
│  para más información.      │
└─────────────────────────────┘
```

### Código Ya Usado
```
┌─────────────────────────────┐
│   ✗ CÓDIGO YA USADO         │
│                             │
│  Este código ya fue         │
│  utilizado el 17/01/2024    │
│                             │
│  Si no retiraste tu         │
│  paquete, contacta a        │
│  Servientrega.              │
└─────────────────────────────┘
```

---

## 💡 PREGUNTAS FRECUENTES

### ¿El cliente necesita el tracking number para retirar?
**NO.** Solo necesita el código de retiro que recibe por SMS/Email.

### ¿Cómo obtiene el cliente su código?
Automáticamente por SMS y Email cuando el mensajero deposita el paquete.

### ¿Puede usar el tracking number para retirar?
**NO.** El tracking number (SRV123456789) es diferente al código de retiro (A3K7M9P2).

### ¿Qué pasa si pierde el código?
Debe contactar a Servientrega con su tracking number para obtener el código nuevamente.

### ¿Cuánto tiempo tiene para retirar?
48 horas por defecto (configurable en el sistema).

### ¿Puede otra persona retirar con el código?
Sí, cualquier persona con el código válido puede retirar el paquete.

---

## 🧪 PRUEBA EL FLUJO COMPLETO

### Simular Depósito (Mensajero):
```bash
# Usa Swagger UI: http://localhost:8090/swagger-ui.html
# O consulta códigos existentes en la BD
```

### Simular Retiro (Cliente):
```bash
# 1. Cliente valida su código
curl "http://localhost:8090/api/retrievals/validate?code=A3K7M9P2"

# 2. Cliente retira (locker llama esto automáticamente)
curl -X POST http://localhost:8090/api/retrievals \
  -H "Content-Type: application/json" \
  -d '{"code": "A3K7M9P2"}'
```

---

## 📱 EXPERIENCIA DEL CLIENTE

1. **Recibe SMS:** "Tu código es A3K7M9P2"
2. **Va al locker:** Centro Comercial Andino
3. **Ingresa código:** A3K7M9P2 en la pantalla
4. **Compartimento se abre:** Automáticamente
5. **Retira paquete:** Y cierra la puerta
6. **Recibe confirmación:** SMS de entrega exitosa

**¡Así de simple!** 🎉

---

El cliente **NUNCA necesita saber** el tracking number (SRV123456789).  
Solo necesita el **código de retiro** (A3K7M9P2) que recibe automáticamente.
