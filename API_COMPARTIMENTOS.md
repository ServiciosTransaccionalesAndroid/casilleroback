# 📦 API Compartimentos - Gestión de Estados

## Base URL
```
https://casilleroback-production.up.railway.app
```

---

## 🔓 Autenticación

Todos los endpoints son **PÚBLICOS** (sin JWT) para acceso desde hardware del locker.

---

## 📋 Endpoints

### 1. Obtener Compartimento por ID

**GET** `/api/compartments/{id}`

**Respuesta:**
```json
{
  "id": 1,
  "compartmentNumber": 1,
  "size": "SMALL",
  "status": "DISPONIBLE",
  "doorState": "CERRADO",
  "physicalCondition": "BUEN_ESTADO",
  "lockerId": 1
}
```

---

### 2. Listar Compartimentos de un Locker

**GET** `/api/compartments/locker/{lockerId}`

**Ejemplo:**
```bash
curl https://casilleroback-production.up.railway.app/api/compartments/locker/1 \
  -H "Authorization: Bearer {token}"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "compartmentNumber": 1,
    "size": "SMALL",
    "status": "DISPONIBLE",
    "doorState": "CERRADO",
    "physicalCondition": "BUEN_ESTADO",
    "lockerId": 1
  },
  {
    "id": 2,
    "compartmentNumber": 2,
    "size": "SMALL",
    "status": "OCUPADO",
    "doorState": "CERRADO",
    "physicalCondition": "BUEN_ESTADO",
    "lockerId": 1
  }
]
```

---

### 3. Actualizar Estado de Puerta

**PUT** `/api/compartments/{id}/door-state`

**Request:**
```json
{
  "doorState": "ABIERTO"
}
```

**Valores permitidos:**
- `CERRADO`
- `ABIERTO`

**Ejemplo:**
```bash
curl -X PUT https://casilleroback-production.up.railway.app/api/compartments/1/door-state \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"doorState":"ABIERTO"}'
```

**Respuesta:**
```
Door state updated to ABIERTO
```

---

### 4. Actualizar Condición Física

**PUT** `/api/compartments/{id}/physical-condition`

**Request:**
```json
{
  "physicalCondition": "MAL_ESTADO"
}
```

**Valores permitidos:**
- `BUEN_ESTADO`
- `MAL_ESTADO`
- `REQUIERE_MANTENIMIENTO`

**Ejemplo:**
```bash
curl -X PUT https://casilleroback-production.up.railway.app/api/compartments/1/physical-condition \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"physicalCondition":"REQUIERE_MANTENIMIENTO"}'
```

**Respuesta:**
```
Physical condition updated to REQUIERE_MANTENIMIENTO
```

---

## 📊 Estados de Compartimento

### Status (Ocupación)
| Estado | Descripción |
|--------|-------------|
| `DISPONIBLE` | Compartimento vacío y listo para usar |
| `OCUPADO` | Compartimento con paquete |
| `ABIERTO` | Compartimento abierto (en uso) |
| `MANTENIMIENTO` | Fuera de servicio |

### Door State (Puerta)
| Estado | Descripción |
|--------|-------------|
| `CERRADO` | Puerta cerrada |
| `ABIERTO` | Puerta abierta |

### Physical Condition (Condición Física)
| Estado | Descripción |
|--------|-------------|
| `BUEN_ESTADO` | Funcionando correctamente |
| `MAL_ESTADO` | Dañado, no usar |
| `REQUIERE_MANTENIMIENTO` | Necesita revisión |

---

## 🔄 Flujos de Uso

### Flujo 1: Depósito de Paquete
```
1. GET /api/compartments/locker/1 
   → Ver compartimentos disponibles

2. POST /api/deposits
   → Sistema asigna compartimento y cambia status a OCUPADO

3. PUT /api/compartments/{id}/door-state
   → Hardware abre puerta (ABIERTO)

4. PUT /api/compartments/{id}/door-state
   → Hardware cierra puerta (CERRADO)
```

### Flujo 2: Retiro de Paquete
```
1. GET /api/retrievals/validate?code={code}
   → Validar código de retiro

2. PUT /api/compartments/{id}/door-state
   → Abrir puerta (ABIERTO)

3. POST /api/retrievals
   → Registrar retiro

4. PUT /api/compartments/{id}/door-state
   → Cerrar puerta (CERRADO)
```

### Flujo 3: Mantenimiento
```
1. GET /api/compartments/locker/1
   → Revisar todos los compartimentos

2. PUT /api/compartments/{id}/physical-condition
   → Marcar como REQUIERE_MANTENIMIENTO

3. Técnico repara el compartimento

4. PUT /api/compartments/{id}/physical-condition
   → Marcar como BUEN_ESTADO
```

---

## 🎯 Casos de Uso

### Portal Admin
- Ver estado de todos los compartimentos
- Marcar compartimentos para mantenimiento
- Monitorear puertas abiertas

### App Móvil Courier
- Ver compartimentos disponibles antes de depósito
- Verificar que puerta se abrió correctamente

### Locker Hardware
- Reportar estado de puerta (sensor)
- Reportar problemas físicos (sensor)

---

## ⚠️ Validaciones

### Error: Compartimento no encontrado
```json
{
  "status": 500,
  "message": "Compartment not found"
}
```

### Error: Valor inválido
```json
{
  "status": 500,
  "message": "No enum constant com.servientrega.locker.enums.DoorState.INVALIDO"
}
```

### Error: Sin autorización
```json
{
  "status": 401,
  "message": "Unauthorized"
}
```

---

## 📱 Integración Nuxt 3

```typescript
// composables/useCompartments.ts
export const useCompartments = () => {
  const { api } = useApi()

  const getCompartmentsByLocker = async (lockerId: number) => {
    return await api(`/api/compartments/locker/${lockerId}`)
  }

  const updateDoorState = async (id: number, doorState: string) => {
    return await api(`/api/compartments/${id}/door-state`, {
      method: 'PUT',
      body: { doorState }
    })
  }

  const updatePhysicalCondition = async (id: number, physicalCondition: string) => {
    return await api(`/api/compartments/${id}/physical-condition`, {
      method: 'PUT',
      body: { physicalCondition }
    })
  }

  return {
    getCompartmentsByLocker,
    updateDoorState,
    updatePhysicalCondition
  }
}
```

**Uso en componente:**
```vue
<script setup lang="ts">
const { getCompartmentsByLocker, updateDoorState } = useCompartments()

const compartments = ref([])

onMounted(async () => {
  compartments.value = await getCompartmentsByLocker(1)
})

const openDoor = async (id: number) => {
  await updateDoorState(id, 'ABIERTO')
  // Refrescar lista
  compartments.value = await getCompartmentsByLocker(1)
}
</script>
```

---

## 🔑 Permisos

| Endpoint | ADMIN | COURIER |
|----------|-------|---------|
| GET /api/compartments/{id} | ✅ | ✅ |
| GET /api/compartments/locker/{lockerId} | ✅ | ✅ |
| PUT /api/compartments/{id}/door-state | ✅ | ✅ |
| PUT /api/compartments/{id}/physical-condition | ✅ | ✅ |

---

## 📅 Cambios

**V11 - 2026-03-03**
- Agregado `doorState` (CERRADO/ABIERTO)
- Agregado `physicalCondition` (BUEN_ESTADO/MAL_ESTADO/REQUIERE_MANTENIMIENTO)
- Endpoints para actualizar estados
