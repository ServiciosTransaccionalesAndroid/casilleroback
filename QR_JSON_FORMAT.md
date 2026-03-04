# 📱 Formato QR Code - JSON

## 🎯 Formato Actual

El QR code ahora contiene un JSON con toda la información necesaria para el retiro:

```json
{
  "code": "RCSV2X4Y",
  "pin": "123456",
  "tracking": "PKG123"
}
```

---

## 📋 Campos del JSON

| Campo | Tipo | Descripción | Ejemplo |
|-------|------|-------------|---------|
| `code` | string | Código de retiro | `RCSV2X4Y` |
| `pin` | string | PIN secreto de 6 dígitos | `123456` |
| `tracking` | string | Número de guía del paquete | `PKG123` |

---

## 🔍 Cómo Escanear

### Desde App Móvil (JavaScript/React Native)

```javascript
import { BarCodeScanner } from 'expo-barcode-scanner';

const handleBarCodeScanned = ({ data }) => {
  try {
    const qrData = JSON.parse(data);
    console.log('Código:', qrData.code);
    console.log('PIN:', qrData.pin);
    console.log('Tracking:', qrData.tracking);
    
    // Usar para retiro automático
    processRetrieval(qrData.code, qrData.pin);
  } catch (error) {
    console.error('QR inválido');
  }
};
```

### Desde Web (HTML5)

```javascript
import QrScanner from 'qr-scanner';

const scanner = new QrScanner(
  videoElement,
  result => {
    const qrData = JSON.parse(result.data);
    document.getElementById('code').value = qrData.code;
    document.getElementById('pin').value = qrData.pin;
  }
);
```

### Desde Python

```python
from pyzbar.pyzbar import decode
from PIL import Image
import json

# Leer QR desde imagen
img = Image.open('qr_code.png')
decoded = decode(img)

if decoded:
    qr_data = json.loads(decoded[0].data.decode('utf-8'))
    print(f"Código: {qr_data['code']}")
    print(f"PIN: {qr_data['pin']}")
    print(f"Tracking: {qr_data['tracking']}")
```

---

## 🚀 Endpoints

### Generar QR con JSON

```bash
GET /api/qr/retrieval-code/{code}?width=400&height=400
```

**Respuesta:** Imagen PNG del QR con JSON embebido

---

## 📧 En el Email

El cliente recibe:
1. **Correo HTML** con código y PIN visibles
2. **QR adjunto** (PNG) que contiene el JSON completo
3. **Instrucciones** de cómo usar el QR

---

## 🎯 Ventajas del Formato JSON

✅ **Un solo escaneo** - Obtiene código + PIN  
✅ **Retiro automático** - No necesita escribir nada  
✅ **Trazabilidad** - Incluye número de guía  
✅ **Validación** - Fácil de parsear y validar  
✅ **Extensible** - Se pueden agregar más campos

---

## 🔄 Flujo de Retiro con QR

```
1. Cliente recibe email con QR
   ↓
2. Va al locker
   ↓
3. Escanea QR con la app/pantalla del locker
   ↓
4. Sistema parsea JSON automáticamente
   ↓
5. Valida código + PIN
   ↓
6. Abre compartimento
   ↓
7. Cliente retira paquete
```

---

## 🧪 Testing

### Test 1: Generar QR
```bash
POST /api/deposits
{
  "trackingNumber": "TEST001",
  "lockerId": 1,
  "courierEmployeeId": "EMP001"
}

# Email incluye QR con JSON:
# {"code":"RCSV2X4Y","pin":"123456","tracking":"TEST001"}
```

### Test 2: Escanear y Usar
```bash
# Escanear QR → Obtener JSON
# Parsear JSON → Extraer code y pin
# Llamar API:

POST /api/retrievals
{
  "code": "RCSV2X4Y",
  "secretPin": "123456"
}
```

---

## 📱 Ejemplo de App Móvil

```javascript
// React Native
import { Camera } from 'expo-camera';

function QRScanner() {
  const handleQRScanned = async ({ data }) => {
    try {
      const { code, pin, tracking } = JSON.parse(data);
      
      // Retiro automático
      const response = await fetch(`${API_URL}/api/retrievals`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code, secretPin: pin })
      });
      
      if (response.ok) {
        alert('¡Paquete retirado exitosamente!');
      }
    } catch (error) {
      alert('QR inválido');
    }
  };
  
  return <Camera onBarCodeScanned={handleQRScanned} />;
}
```

---

## 🔐 Seguridad

- ✅ QR contiene PIN → Solo el destinatario puede usarlo
- ✅ Código expira en 48 horas
- ✅ Un solo uso
- ✅ JSON no es encriptado (no es necesario, el PIN ya es la seguridad)

---

## 📊 Comparación

| Formato | Ventajas | Desventajas |
|---------|----------|-------------|
| **Solo código** | Simple | Requiere escribir PIN manualmente |
| **JSON** ✅ | Automático, completo | QR más grande |
| **URL** | Abre app directamente | Requiere conexión |

---

**Última actualización:** Enero 2025  
**Versión:** 1.2.0
