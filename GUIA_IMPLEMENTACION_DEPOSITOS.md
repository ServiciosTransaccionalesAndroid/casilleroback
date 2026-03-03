# 📦 Guía de Implementación - Sistema de Depósitos

## Base URL
```
https://casilleroback-production.up.railway.app
```

---

## 🔄 Flujo Completo de Depósito

### Paso 1: Courier Escanea Paquete
El courier escanea el código de barras del paquete para obtener el `trackingNumber`.

### Paso 2: Validar Paquete
**GET** `/api/packages/validate?trackingNumber={number}`

```bash
curl "https://casilleroback-production.up.railway.app/api/packages/validate?trackingNumber=SRV987654321"
```

**Response:**
```json
{
  "id": 1,
  "trackingNumber": "SRV987654321",
  "recipient": {
    "id": 1,
    "name": "Carlos Rodríguez",
    "phone": "+573101234567",
    "email": "carlos.rodriguez@email.com"
  },
  "width": 30.00,
  "height": 20.00,
  "depth": 15.00,
  "weight": 2.50,
  "status": "EN_TRANSITO"
}
```

### Paso 3: Registrar Depósito
**POST** `/api/deposits`

**Request (sin compartmentNumber):**
```json
{
  "trackingNumber": "SRV987654321",
  "lockerId": 1,
  "courierEmployeeId": "TEST999"
}
```

**Response:**
```json
{
  "depositId": 1,
  "compartmentNumber": 5,
  "retrievalCode": "RCSVHCW3",
  "secretPin": "290632",
  "expiresAt": "2026-03-05T21:18:08",
  "message": "Deposit registered successfully. Use compartment #5. Retrieval code: RCSVHCW3"
}
```

### Paso 4: Abrir Compartimento
**PUT** `/api/compartments/{id}/door-state`

Primero obtener el ID del compartimento:
```bash
curl "https://casilleroback-production.up.railway.app/api/compartments/locker/1"
```

Luego abrir la puerta del compartimento asignado:
```bash
curl -X PUT https://casilleroback-production.up.railway.app/api/compartments/5/door-state \
  -H "Content-Type: application/json" \
  -d '{"doorState":"ABIERTO"}'
```

### Paso 5: Courier Deposita Paquete
El courier coloca el paquete en el compartimento #5.

### Paso 6: Cerrar Compartimento
```bash
curl -X PUT https://casilleroback-production.up.railway.app/api/compartments/5/door-state \
  -H "Content-Type: application/json" \
  -d '{"doorState":"CERRADO"}'
```

---

## 📱 Implementación App Móvil (React Native / Flutter)

### Composable/Service
```typescript
// services/depositService.ts
export class DepositService {
  private baseURL = 'https://casilleroback-production.up.railway.app';

  async validatePackage(trackingNumber: string) {
    const response = await fetch(
      `${this.baseURL}/api/packages/validate?trackingNumber=${trackingNumber}`
    );
    return response.json();
  }

  async createDeposit(data: {
    trackingNumber: string;
    lockerId: number;
    courierEmployeeId: string;
  }) {
    const response = await fetch(`${this.baseURL}/api/deposits`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return response.json();
  }

  async openCompartment(compartmentId: number) {
    const response = await fetch(
      `${this.baseURL}/api/compartments/${compartmentId}/door-state`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ doorState: 'ABIERTO' })
      }
    );
    return response.json();
  }

  async closeCompartment(compartmentId: number) {
    const response = await fetch(
      `${this.baseURL}/api/compartments/${compartmentId}/door-state`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ doorState: 'CERRADO' })
      }
    );
    return response.json();
  }
}
```

### Pantalla de Depósito
```typescript
// screens/DepositScreen.tsx
import { useState } from 'react';
import { DepositService } from '../services/depositService';

export function DepositScreen() {
  const [trackingNumber, setTrackingNumber] = useState('');
  const [packageInfo, setPackageInfo] = useState(null);
  const [depositResult, setDepositResult] = useState(null);
  const [loading, setLoading] = useState(false);
  
  const depositService = new DepositService();
  const courierEmployeeId = 'TEST999'; // Obtener del login
  const lockerId = 1; // Locker actual

  // Paso 1: Escanear y validar paquete
  const handleScanPackage = async (scannedCode: string) => {
    setLoading(true);
    try {
      const pkg = await depositService.validatePackage(scannedCode);
      setPackageInfo(pkg);
      setTrackingNumber(scannedCode);
    } catch (error) {
      alert('Paquete no encontrado');
    } finally {
      setLoading(false);
    }
  };

  // Paso 2: Registrar depósito
  const handleDeposit = async () => {
    setLoading(true);
    try {
      const result = await depositService.createDeposit({
        trackingNumber,
        lockerId,
        courierEmployeeId
      });
      
      setDepositResult(result);
      
      // Paso 3: Abrir compartimento automáticamente
      await depositService.openCompartment(result.compartmentNumber);
      
      alert(`Compartimento #${result.compartmentNumber} abierto. Deposite el paquete.`);
      
    } catch (error) {
      alert('Error al registrar depósito');
    } finally {
      setLoading(false);
    }
  };

  // Paso 4: Confirmar depósito y cerrar
  const handleConfirmDeposit = async () => {
    if (!depositResult) return;
    
    try {
      await depositService.closeCompartment(depositResult.compartmentNumber);
      alert('Depósito completado exitosamente');
      
      // Mostrar código de retiro
      alert(`Código de retiro: ${depositResult.retrievalCode}\nPIN: ${depositResult.secretPin}`);
      
      // Reset
      setPackageInfo(null);
      setDepositResult(null);
      setTrackingNumber('');
      
    } catch (error) {
      alert('Error al cerrar compartimento');
    }
  };

  return (
    <View>
      {/* UI de escaneo, validación y depósito */}
    </View>
  );
}
```

---

## 🎯 Flujo Visual

```
┌─────────────────────────────────────────────────────────────┐
│ 1. COURIER ESCANEA PAQUETE                                  │
│    Tracking: SRV987654321                                   │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. APP VALIDA PAQUETE                                       │
│    GET /api/packages/validate?trackingNumber=SRV987654321   │
│    ✓ Paquete encontrado                                     │
│    ✓ Destinatario: Carlos Rodríguez                         │
│    ✓ Dimensiones: 30x20x15 cm                               │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. COURIER CONFIRMA DEPÓSITO                                │
│    POST /api/deposits                                       │
│    {                                                        │
│      "trackingNumber": "SRV987654321",                      │
│      "lockerId": 1,                                         │
│      "courierEmployeeId": "TEST999"                         │
│    }                                                        │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. BACKEND ASIGNA COMPARTIMENTO                             │
│    ✓ Calcula tamaño necesario: MEDIUM                       │
│    ✓ Busca compartimento disponible                         │
│    ✓ Asigna compartimento #5                                │
│    ✓ Genera código: RCSVHCW3                                │
│    ✓ Envía SMS/Email al destinatario                        │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. APP ABRE COMPARTIMENTO                                   │
│    PUT /api/compartments/5/door-state                       │
│    {"doorState": "ABIERTO"}                                 │
│    🚪 Compartimento #5 se abre                              │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. COURIER DEPOSITA PAQUETE                                 │
│    📦 Coloca paquete en compartimento #5                    │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. APP CIERRA COMPARTIMENTO                                 │
│    PUT /api/compartments/5/door-state                       │
│    {"doorState": "CERRADO"}                                 │
│    🔒 Compartimento #5 se cierra                            │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. DEPÓSITO COMPLETADO                                      │
│    ✓ Paquete en locker                                      │
│    ✓ Cliente recibe código: RCSVHCW3                        │
│    ✓ Cliente recibe PIN: 290632                             │
│    ✓ Válido hasta: 2026-03-05 21:18                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 Asignación Automática de Compartimentos

El backend calcula el tamaño necesario según las dimensiones del paquete:

| Dimensión Máxima | Tamaño Asignado | Compartimentos Disponibles |
|------------------|-----------------|----------------------------|
| ≤ 25 cm | SMALL | 10 por locker |
| 26-40 cm | MEDIUM | 8 por locker |
| > 40 cm | LARGE | 6 por locker |

**Lógica de asignación:**
1. Calcula tamaño requerido según dimensiones
2. Busca compartimento del tamaño exacto disponible
3. Si no hay, busca uno más grande
4. Si no hay disponibles, retorna error

---

## ⚠️ Manejo de Errores

### Error: Paquete no encontrado
```json
{
  "status": 500,
  "message": "Package not found: SRV987654321"
}
```
**Solución:** Verificar tracking number

### Error: Courier no encontrado
```json
{
  "status": 500,
  "message": "Courier not found: TEST999"
}
```
**Solución:** Verificar employeeId del courier

### Error: No hay compartimentos disponibles
```json
{
  "status": 500,
  "message": "No available compartment for package size"
}
```
**Solución:** Locker lleno, usar otro locker

---

## 📊 Estados del Sistema

### Estado del Paquete
- `EN_TRANSITO` → `EN_LOCKER` → `ENTREGADO`

### Estado del Compartimento
- `DISPONIBLE` → `OCUPADO` → `DISPONIBLE`

### Estado de la Puerta
- `CERRADO` → `ABIERTO` → `CERRADO`

---

## 🧪 Testing

### Test Completo
```bash
# 1. Validar paquete
curl "https://casilleroback-production.up.railway.app/api/packages/validate?trackingNumber=SRV987654321"

# 2. Registrar depósito
curl -X POST https://casilleroback-production.up.railway.app/api/deposits \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SRV987654321",
    "lockerId": 1,
    "courierEmployeeId": "TEST999"
  }'

# 3. Abrir compartimento (usar el número retornado)
curl -X PUT https://casilleroback-production.up.railway.app/api/compartments/5/door-state \
  -H "Content-Type: application/json" \
  -d '{"doorState":"ABIERTO"}'

# 4. Cerrar compartimento
curl -X PUT https://casilleroback-production.up.railway.app/api/compartments/5/door-state \
  -H "Content-Type: application/json" \
  -d '{"doorState":"CERRADO"}'
```

---

## 📝 Datos de Prueba

### Couriers Disponibles
| Employee ID | Nombre | PIN |
|-------------|--------|-----|
| TEST999 | Test User | 5555 |
| COUR001 | Juan Pérez | 1234 |
| COUR002 | María González | 1234 |

### Lockers Disponibles
| ID | Nombre | Compartimentos |
|----|--------|----------------|
| 1 | Locker Centro | 24 |
| 2 | Locker Norte | 24 |

### Paquetes de Prueba
Crear paquetes usando el portal admin en `/api/packages`

---

## 🚀 Próximos Pasos

1. Implementar UI de escaneo de código de barras
2. Agregar feedback visual del compartimento asignado
3. Implementar timeout de puerta abierta
4. Agregar foto del paquete depositado
5. Implementar notificaciones push al cliente
