# 📊 API Dashboard y Actividad - Documentación

## Base URL
```
https://casilleroback-production.up.railway.app
```

---

## 🔐 Autenticación

| Endpoint | Auth Requerida | Rol |
|----------|----------------|-----|
| `/api/dashboard/stats` | ❌ Público | - |
| `/api/activity/**` | ✅ JWT | ADMIN |

---

## 📈 Dashboard

### GET /api/dashboard/stats

Obtiene estadísticas del día y estado de compartimentos.

**Request:**
```bash
curl https://casilleroback-production.up.railway.app/api/dashboard/stats
```

**Response:**
```json
{
  "todayActivity": {
    "totalDeposits": 15,
    "totalRetrievals": 8,
    "pendingRetrievals": 7
  },
  "compartmentStats": {
    "total": 48,
    "available": 32,
    "occupied": 12,
    "maintenance": 4,
    "statusBreakdown": {
      "disponible": 32,
      "ocupado": 12,
      "abierto": 0,
      "mantenimiento": 4
    },
    "doorBreakdown": {
      "cerrado": 47,
      "abierto": 1
    },
    "conditionBreakdown": {
      "buenEstado": 44,
      "malEstado": 2,
      "requiereMantenimiento": 2
    }
  }
}
```

**Descripción de campos:**

**todayActivity:**
- `totalDeposits`: Depósitos realizados hoy
- `totalRetrievals`: Retiros realizados hoy
- `pendingRetrievals`: Paquetes pendientes de retiro

**compartmentStats:**
- `total`: Total de compartimentos (24 por locker)
- `available`: Compartimentos disponibles
- `occupied`: Compartimentos ocupados
- `maintenance`: Compartimentos en mantenimiento

**statusBreakdown:**
- `disponible`: Vacíos y listos para usar
- `ocupado`: Con paquete depositado
- `abierto`: Puerta abierta (en uso)
- `mantenimiento`: Fuera de servicio

**doorBreakdown:**
- `cerrado`: Puertas cerradas
- `abierto`: Puertas abiertas

**conditionBreakdown:**
- `buenEstado`: Funcionando correctamente
- `malEstado`: Dañados
- `requiereMantenimiento`: Necesitan revisión

---

## 📦 Actividad - Depósitos

### GET /api/activity/deposits

Lista todos los depósitos ordenados por fecha (más recientes primero).

**Request:**
```bash
curl https://casilleroback-production.up.railway.app/api/activity/deposits \
  -H "Authorization: Bearer {admin_token}"
```

**Response:**
```json
[
  {
    "id": 1,
    "trackingNumber": "SRV987654321",
    "recipientName": "Ana Martínez",
    "recipientPhone": "+573109876543",
    "courierName": "Test User",
    "courierEmployeeId": "TEST999",
    "compartmentNumber": 5,
    "lockerName": "Locker Centro",
    "depositTimestamp": "2026-03-03T14:30:00",
    "photoUrl": null
  },
  {
    "id": 2,
    "trackingNumber": "PKG123456789",
    "recipientName": "Carlos Rodríguez",
    "recipientPhone": "+573101234567",
    "courierName": "Juan Pérez",
    "courierEmployeeId": "COUR001",
    "compartmentNumber": 12,
    "lockerName": "Locker Norte",
    "depositTimestamp": "2026-03-03T10:15:00",
    "photoUrl": "https://example.com/photo.jpg"
  }
]
```

### GET /api/activity/deposits/{id}

Obtiene detalles de un depósito específico.

**Request:**
```bash
curl https://casilleroback-production.up.railway.app/api/activity/deposits/1 \
  -H "Authorization: Bearer {admin_token}"
```

**Response:**
```json
{
  "id": 1,
  "trackingNumber": "SRV987654321",
  "recipientName": "Ana Martínez",
  "recipientPhone": "+573109876543",
  "courierName": "Test User",
  "courierEmployeeId": "TEST999",
  "compartmentNumber": 5,
  "lockerName": "Locker Centro",
  "depositTimestamp": "2026-03-03T14:30:00",
  "photoUrl": null
}
```

**Nota:** No se expone el código de retiro ni el PIN secreto por seguridad.

---

## 📤 Actividad - Retiros

### GET /api/activity/retrievals

Lista todos los retiros ordenados por fecha (más recientes primero).

**Request:**
```bash
curl https://casilleroback-production.up.railway.app/api/activity/retrievals \
  -H "Authorization: Bearer {admin_token}"
```

**Response:**
```json
[
  {
    "id": 1,
    "trackingNumber": "PKG123456789",
    "recipientName": "Carlos Rodríguez",
    "recipientPhone": "+573101234567",
    "compartmentNumber": 12,
    "lockerName": "Locker Norte",
    "retrievalTimestamp": "2026-03-03T16:45:00",
    "verified": true
  },
  {
    "id": 2,
    "trackingNumber": "SRV111222333",
    "recipientName": "Pedro Sánchez",
    "recipientPhone": "+573105556677",
    "compartmentNumber": 3,
    "lockerName": "Locker Centro",
    "retrievalTimestamp": "2026-03-03T12:20:00",
    "verified": true
  }
]
```

### GET /api/activity/retrievals/{id}

Obtiene detalles de un retiro específico.

**Request:**
```bash
curl https://casilleroback-production.up.railway.app/api/activity/retrievals/1 \
  -H "Authorization: Bearer {admin_token}"
```

**Response:**
```json
{
  "id": 1,
  "trackingNumber": "PKG123456789",
  "recipientName": "Carlos Rodríguez",
  "recipientPhone": "+573101234567",
  "compartmentNumber": 12,
  "lockerName": "Locker Norte",
  "retrievalTimestamp": "2026-03-03T16:45:00",
  "verified": true
}
```

**Campo `verified`:**
- `true`: Retiro verificado con código y PIN
- `false`: Retiro sin verificación completa

---

## 📄 Códigos QR (PDF)

### GET /api/qr/{code}

Genera y descarga un PDF con el código QR de retiro.

**Request:**
```bash
curl https://casilleroback-production.up.railway.app/api/qr/RCSVHCW3 \
  --output codigo-retiro.pdf
```

**Response:**
- Content-Type: `application/pdf`
- Archivo PDF descargable con:
  - Código QR
  - Código de retiro: RCSVHCW3
  - Información del locker
  - Instrucciones de uso

### Descargar PDF desde Railway

**Opción 1: Desde navegador**
```
https://casilleroback-production.up.railway.app/api/qr/RCSVHCW3
```
El navegador descargará automáticamente el PDF.

**Opción 2: Desde terminal**
```bash
curl https://casilleroback-production.up.railway.app/api/qr/RCSVHCW3 \
  --output codigo-retiro.pdf
```

**Opción 3: Desde JavaScript**
```javascript
async function downloadQR(code) {
  const response = await fetch(
    `https://casilleroback-production.up.railway.app/api/qr/${code}`
  );
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `codigo-retiro-${code}.pdf`;
  a.click();
}

// Uso
downloadQR('RCSVHCW3');
```

**Opción 4: Desde React/Nuxt**
```typescript
// composables/useQR.ts
export const useQR = () => {
  const downloadQR = async (code: string) => {
    const response = await fetch(
      `https://casilleroback-production.up.railway.app/api/qr/${code}`
    );
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `codigo-retiro-${code}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  return { downloadQR };
};
```

---

## 🎨 Implementación en Portal Admin (Nuxt 3)

### Dashboard Component

```vue
<template>
  <div class="dashboard">
    <h1>Dashboard</h1>
    
    <!-- Actividad del día -->
    <div class="stats-grid">
      <div class="stat-card">
        <h3>Depósitos Hoy</h3>
        <p class="stat-value">{{ stats?.todayActivity.totalDeposits }}</p>
      </div>
      
      <div class="stat-card">
        <h3>Retiros Hoy</h3>
        <p class="stat-value">{{ stats?.todayActivity.totalRetrievals }}</p>
      </div>
      
      <div class="stat-card">
        <h3>Pendientes</h3>
        <p class="stat-value">{{ stats?.todayActivity.pendingRetrievals }}</p>
      </div>
    </div>

    <!-- Estado de compartimentos -->
    <div class="compartments-section">
      <h2>Estado de Compartimentos</h2>
      
      <div class="stats-grid">
        <div class="stat-card">
          <h3>Disponibles</h3>
          <p class="stat-value green">{{ stats?.compartmentStats.available }}</p>
        </div>
        
        <div class="stat-card">
          <h3>Ocupados</h3>
          <p class="stat-value blue">{{ stats?.compartmentStats.occupied }}</p>
        </div>
        
        <div class="stat-card">
          <h3>Mantenimiento</h3>
          <p class="stat-value orange">{{ stats?.compartmentStats.maintenance }}</p>
        </div>
      </div>

      <!-- Condición física -->
      <div class="condition-stats">
        <h3>Condición Física</h3>
        <div class="progress-bar">
          <div class="segment good" 
               :style="{width: goodPercentage + '%'}">
            Buen Estado: {{ stats?.compartmentStats.conditionBreakdown.buenEstado }}
          </div>
          <div class="segment bad" 
               :style="{width: badPercentage + '%'}">
            Mal Estado: {{ stats?.compartmentStats.conditionBreakdown.malEstado }}
          </div>
          <div class="segment maintenance" 
               :style="{width: maintenancePercentage + '%'}">
            Requiere Mantenimiento: {{ stats?.compartmentStats.conditionBreakdown.requiereMantenimiento }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { api } = useApi()
const stats = ref(null)

const goodPercentage = computed(() => {
  if (!stats.value) return 0
  return (stats.value.compartmentStats.conditionBreakdown.buenEstado / 
          stats.value.compartmentStats.total) * 100
})

const badPercentage = computed(() => {
  if (!stats.value) return 0
  return (stats.value.compartmentStats.conditionBreakdown.malEstado / 
          stats.value.compartmentStats.total) * 100
})

const maintenancePercentage = computed(() => {
  if (!stats.value) return 0
  return (stats.value.compartmentStats.conditionBreakdown.requiereMantenimiento / 
          stats.value.compartmentStats.total) * 100
})

onMounted(async () => {
  stats.value = await api('/api/dashboard/stats')
})
</script>
```

### Deposits List Component

```vue
<template>
  <div class="deposits-list">
    <h1>Historial de Depósitos</h1>
    
    <table>
      <thead>
        <tr>
          <th>Tracking</th>
          <th>Destinatario</th>
          <th>Courier</th>
          <th>Compartimento</th>
          <th>Locker</th>
          <th>Fecha</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="deposit in deposits" :key="deposit.id">
          <td>{{ deposit.trackingNumber }}</td>
          <td>{{ deposit.recipientName }}</td>
          <td>{{ deposit.courierName }}</td>
          <td>#{{ deposit.compartmentNumber }}</td>
          <td>{{ deposit.lockerName }}</td>
          <td>{{ formatDate(deposit.depositTimestamp) }}</td>
          <td>
            <button @click="viewDetails(deposit.id)">Ver</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
const { api } = useApi()
const deposits = ref([])

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('es-CO')
}

const viewDetails = (id: number) => {
  navigateTo(`/deposits/${id}`)
}

onMounted(async () => {
  deposits.value = await api('/api/activity/deposits')
})
</script>
```

### Retrievals List Component

```vue
<template>
  <div class="retrievals-list">
    <h1>Historial de Retiros</h1>
    
    <table>
      <thead>
        <tr>
          <th>Tracking</th>
          <th>Destinatario</th>
          <th>Compartimento</th>
          <th>Locker</th>
          <th>Fecha</th>
          <th>Verificado</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="retrieval in retrievals" :key="retrieval.id">
          <td>{{ retrieval.trackingNumber }}</td>
          <td>{{ retrieval.recipientName }}</td>
          <td>#{{ retrieval.compartmentNumber }}</td>
          <td>{{ retrieval.lockerName }}</td>
          <td>{{ formatDate(retrieval.retrievalTimestamp) }}</td>
          <td>
            <span :class="retrieval.verified ? 'verified' : 'not-verified'">
              {{ retrieval.verified ? '✓ Sí' : '✗ No' }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
const { api } = useApi()
const retrievals = ref([])

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('es-CO')
}

onMounted(async () => {
  retrievals.value = await api('/api/activity/retrievals')
})
</script>
```

---

## 📊 Resumen de Endpoints

| Endpoint | Método | Auth | Descripción |
|----------|--------|------|-------------|
| `/api/dashboard/stats` | GET | Público | Estadísticas del dashboard |
| `/api/activity/deposits` | GET | ADMIN | Listar todos los depósitos |
| `/api/activity/deposits/{id}` | GET | ADMIN | Ver detalle de depósito |
| `/api/activity/retrievals` | GET | ADMIN | Listar todos los retiros |
| `/api/activity/retrievals/{id}` | GET | ADMIN | Ver detalle de retiro |
| `/api/qr/{code}` | GET | Público | Descargar PDF con QR |

---

## 🔒 Seguridad

**Información NO expuesta:**
- ❌ Código de retiro (solo en PDF para cliente)
- ❌ PIN secreto (solo en PDF para cliente)
- ❌ Contraseñas de couriers
- ❌ Tokens JWT de otros usuarios

**Información SÍ expuesta:**
- ✅ Tracking numbers
- ✅ Nombres de destinatarios
- ✅ Teléfonos de destinatarios
- ✅ Nombres de couriers
- ✅ Números de compartimentos
- ✅ Fechas de operaciones

---

## 🧪 Testing

```bash
# 1. Dashboard stats (público)
curl https://casilleroback-production.up.railway.app/api/dashboard/stats

# 2. Login admin
TOKEN=$(curl -s -X POST https://casilleroback-production.up.railway.app/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@servientrega.com","password":"Admin123!"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 3. Listar depósitos
curl https://casilleroback-production.up.railway.app/api/activity/deposits \
  -H "Authorization: Bearer $TOKEN"

# 4. Listar retiros
curl https://casilleroback-production.up.railway.app/api/activity/retrievals \
  -H "Authorization: Bearer $TOKEN"

# 5. Descargar QR
curl https://casilleroback-production.up.railway.app/api/qr/RCSVHCW3 \
  --output codigo.pdf
```

---

## 📅 Versión

**Implementado:** 2026-03-03  
**Versión:** 1.2.0
