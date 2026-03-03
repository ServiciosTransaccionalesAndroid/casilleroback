# 📦 API - Gestión de Paquetes y Clientes

## Base URL
```
https://casilleroback-production.up.railway.app
```

---

## 👥 Gestión de Clientes (Recipients)

### 1. Crear Cliente
```bash
POST /api/recipients
```

**Request:**
```json
{
  "name": "Juan Pérez",
  "phone": "+573001234567",
  "email": "juan.perez@email.com",
  "address": "Calle 100 #15-20, Bogotá"
}
```

**Response (201):**
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "phone": "+573001234567",
  "email": "juan.perez@email.com",
  "address": "Calle 100 #15-20, Bogotá"
}
```

### 2. Listar Clientes
```bash
GET /api/recipients
```

### 3. Obtener Cliente
```bash
GET /api/recipients/{id}
```

### 4. Actualizar Cliente
```bash
PUT /api/recipients/{id}
```

**Request:**
```json
{
  "name": "Juan Pérez Actualizado",
  "phone": "+573009999999"
}
```

### 5. Eliminar Cliente
```bash
DELETE /api/recipients/{id}
```

---

## 📦 Gestión de Paquetes

### 1. Crear Paquete
```bash
POST /api/packages
```

**Request:**
```json
{
  "trackingNumber": "SRV2024001",
  "recipientId": 1,
  "width": 30.5,
  "height": 20.0,
  "depth": 15.5,
  "weight": 2.5
}
```

**Response (201):**
```json
{
  "id": 1,
  "trackingNumber": "SRV2024001",
  "recipient": {
    "id": 1,
    "name": "Juan Pérez",
    "phone": "+573001234567",
    "email": "juan.perez@email.com",
    "address": "Calle 100 #15-20, Bogotá"
  },
  "width": 30.5,
  "height": 20.0,
  "depth": 15.5,
  "weight": 2.5,
  "status": "EN_TRANSITO"
}
```

### 2. Listar Paquetes
```bash
GET /api/packages
```

### 3. Obtener Paquete
```bash
GET /api/packages/{id}
```

### 4. Actualizar Paquete
```bash
PUT /api/packages/{id}
```

**Request:**
```json
{
  "status": "DEPOSITADO",
  "weight": 3.0
}
```

### 5. Eliminar Paquete
```bash
DELETE /api/packages/{id}
```

---

## 🔄 Flujo Completo: Crear Envío

### Paso 1: Crear Cliente
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/recipients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "María García",
    "phone": "+573201234567",
    "email": "maria.garcia@email.com",
    "address": "Carrera 7 #32-16, Bogotá"
  }'
```

**Respuesta:**
```json
{
  "id": 5,
  "name": "María García",
  "phone": "+573201234567",
  "email": "maria.garcia@email.com",
  "address": "Carrera 7 #32-16, Bogotá"
}
```

### Paso 2: Crear Paquete
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/packages \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SRV2024005",
    "recipientId": 5,
    "width": 25.0,
    "height": 15.0,
    "depth": 10.0,
    "weight": 1.5
  }'
```

**Respuesta:**
```json
{
  "id": 10,
  "trackingNumber": "SRV2024005",
  "recipient": {
    "id": 5,
    "name": "María García",
    "phone": "+573201234567",
    "email": "maria.garcia@email.com",
    "address": "Carrera 7 #32-16, Bogotá"
  },
  "width": 25.0,
  "height": 15.0,
  "depth": 10.0,
  "weight": 1.5,
  "status": "EN_TRANSITO"
}
```

---

## 💻 Implementación en Nuxt 3

### Composable para Clientes

**`composables/useRecipients.ts`**
```typescript
export const useRecipients = () => {
  const { api } = useApi()

  const getRecipients = async () => {
    return await api('/api/recipients')
  }

  const createRecipient = async (data: {
    name: string
    phone: string
    email?: string
    address?: string
  }) => {
    return await api('/api/recipients', {
      method: 'POST',
      body: data
    })
  }

  const updateRecipient = async (id: number, data: any) => {
    return await api(`/api/recipients/${id}`, {
      method: 'PUT',
      body: data
    })
  }

  const deleteRecipient = async (id: number) => {
    return await api(`/api/recipients/${id}`, {
      method: 'DELETE'
    })
  }

  return {
    getRecipients,
    createRecipient,
    updateRecipient,
    deleteRecipient
  }
}
```

### Composable para Paquetes

**`composables/usePackages.ts`**
```typescript
export const usePackages = () => {
  const { api } = useApi()

  const getPackages = async () => {
    return await api('/api/packages')
  }

  const createPackage = async (data: {
    trackingNumber: string
    recipientId: number
    width: number
    height: number
    depth: number
    weight: number
  }) => {
    return await api('/api/packages', {
      method: 'POST',
      body: data
    })
  }

  const updatePackage = async (id: number, data: any) => {
    return await api(`/api/packages/${id}`, {
      method: 'PUT',
      body: data
    })
  }

  const deletePackage = async (id: number) => {
    return await api(`/api/packages/${id}`, {
      method: 'DELETE'
    })
  }

  return {
    getPackages,
    createPackage,
    updatePackage,
    deletePackage
  }
}
```

### Página: Crear Envío

**`pages/envios/crear.vue`**
```vue
<template>
  <div class="max-w-4xl mx-auto p-6">
    <h1 class="text-2xl font-bold mb-6">Crear Nuevo Envío</h1>

    <!-- Paso 1: Seleccionar o Crear Cliente -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <h2 class="text-xl font-semibold mb-4">1. Cliente</h2>
      
      <div class="mb-4">
        <label class="block mb-2">Seleccionar cliente existente</label>
        <select v-model="selectedRecipientId" class="w-full border rounded px-3 py-2">
          <option :value="null">-- Crear nuevo cliente --</option>
          <option v-for="r in recipients" :key="r.id" :value="r.id">
            {{ r.name }} - {{ r.phone }}
          </option>
        </select>
      </div>

      <div v-if="!selectedRecipientId" class="space-y-4">
        <input v-model="newRecipient.name" placeholder="Nombre" class="w-full border rounded px-3 py-2" />
        <input v-model="newRecipient.phone" placeholder="Teléfono" class="w-full border rounded px-3 py-2" />
        <input v-model="newRecipient.email" placeholder="Email" type="email" class="w-full border rounded px-3 py-2" />
        <input v-model="newRecipient.address" placeholder="Dirección" class="w-full border rounded px-3 py-2" />
      </div>
    </div>

    <!-- Paso 2: Datos del Paquete -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <h2 class="text-xl font-semibold mb-4">2. Datos del Paquete</h2>
      
      <div class="grid grid-cols-2 gap-4">
        <input v-model="packageData.trackingNumber" placeholder="Número de guía" class="border rounded px-3 py-2" />
        <input v-model.number="packageData.weight" placeholder="Peso (kg)" type="number" step="0.1" class="border rounded px-3 py-2" />
        <input v-model.number="packageData.width" placeholder="Ancho (cm)" type="number" step="0.1" class="border rounded px-3 py-2" />
        <input v-model.number="packageData.height" placeholder="Alto (cm)" type="number" step="0.1" class="border rounded px-3 py-2" />
        <input v-model.number="packageData.depth" placeholder="Profundidad (cm)" type="number" step="0.1" class="border rounded px-3 py-2" />
      </div>
    </div>

    <!-- Botón Crear -->
    <button
      @click="handleCreate"
      :disabled="loading"
      class="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
    >
      {{ loading ? 'Creando...' : 'Crear Envío' }}
    </button>

    <div v-if="error" class="mt-4 text-red-600">{{ error }}</div>
    <div v-if="success" class="mt-4 text-green-600">¡Envío creado exitosamente!</div>
  </div>
</template>

<script setup lang="ts">
const { getRecipients, createRecipient } = useRecipients()
const { createPackage } = usePackages()

const recipients = ref([])
const selectedRecipientId = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref(false)

const newRecipient = ref({
  name: '',
  phone: '',
  email: '',
  address: ''
})

const packageData = ref({
  trackingNumber: '',
  weight: 0,
  width: 0,
  height: 0,
  depth: 0
})

onMounted(async () => {
  recipients.value = await getRecipients()
})

const handleCreate = async () => {
  loading.value = true
  error.value = ''
  success.value = false

  try {
    let recipientId = selectedRecipientId.value

    // Crear cliente si es necesario
    if (!recipientId) {
      const newR = await createRecipient(newRecipient.value)
      recipientId = newR.id
    }

    // Crear paquete
    await createPackage({
      ...packageData.value,
      recipientId
    })

    success.value = true
    setTimeout(() => navigateTo('/envios'), 2000)
  } catch (err) {
    error.value = 'Error al crear el envío'
  } finally {
    loading.value = false
  }
}
</script>
```

---

## 📊 Estados de Paquetes

| Estado | Descripción |
|--------|-------------|
| EN_TRANSITO | Paquete en camino |
| DEPOSITADO | Paquete en locker |
| ENTREGADO | Paquete retirado |
| DEVUELTO | Paquete devuelto |

---

## ✅ Validaciones

### Cliente
- **name**: Requerido
- **phone**: Requerido
- **email**: Formato email válido (opcional)

### Paquete
- **trackingNumber**: Requerido, único
- **recipientId**: Requerido, debe existir
- **width, height, depth, weight**: Números positivos

---

## 🧪 Testing

```bash
# Crear cliente
curl -X POST https://casilleroback-production.up.railway.app/api/recipients \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","phone":"+573001111111"}'

# Crear paquete
curl -X POST https://casilleroback-production.up.railway.app/api/packages \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber":"TEST001",
    "recipientId":1,
    "width":10,"height":10,"depth":10,"weight":1
  }'

# Listar paquetes
curl https://casilleroback-production.up.railway.app/api/packages
```

---

## 📝 Resumen de Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/recipients` | Crear cliente |
| GET | `/api/recipients` | Listar clientes |
| GET | `/api/recipients/{id}` | Obtener cliente |
| PUT | `/api/recipients/{id}` | Actualizar cliente |
| DELETE | `/api/recipients/{id}` | Eliminar cliente |
| POST | `/api/packages` | Crear paquete |
| GET | `/api/packages` | Listar paquetes |
| GET | `/api/packages/{id}` | Obtener paquete |
| PUT | `/api/packages/{id}` | Actualizar paquete |
| DELETE | `/api/packages/{id}` | Eliminar paquete |
