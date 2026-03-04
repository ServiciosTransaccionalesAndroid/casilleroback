# 📄 Generación de Reportes PDF - Documentación Frontend

## 📊 Endpoints Disponibles (JSON)

Los siguientes endpoints retornan datos en formato JSON que el frontend debe convertir a PDF:

| Endpoint | Auth | Descripción |
|----------|------|-------------|
| `GET /api/dashboard/stats` | Público | Estadísticas generales |
| `GET /api/activity/deposits` | ADMIN | Historial de depósitos |
| `GET /api/activity/retrievals` | ADMIN | Historial de retiros |

---

## 🎨 Implementación en Frontend (Nuxt 3)

### 1. Instalar Dependencias

```bash
npm install jspdf jspdf-autotable
```

### 2. Composable para Generar PDFs

```typescript
// composables/useReports.ts
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

export const useReports = () => {
  const { api } = useApi()

  // Generar PDF de Depósitos
  const generateDepositsPDF = async () => {
    try {
      // Obtener datos
      const deposits = await api('/api/activity/deposits')
      
      // Crear PDF
      const doc = new jsPDF()
      
      // Título
      doc.setFontSize(18)
      doc.text('Reporte de Depósitos', 14, 20)
      
      // Fecha del reporte
      doc.setFontSize(10)
      doc.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 28)
      doc.text(`Total de depósitos: ${deposits.length}`, 14, 34)
      
      // Tabla
      autoTable(doc, {
        startY: 40,
        head: [['Tracking', 'Destinatario', 'Courier', 'Compartimento', 'Fecha']],
        body: deposits.map(d => [
          d.trackingNumber,
          d.recipientName,
          d.courierName,
          `#${d.compartmentNumber}`,
          new Date(d.depositTimestamp).toLocaleString('es-CO')
        ]),
        styles: { fontSize: 8 },
        headStyles: { fillColor: [41, 128, 185] }
      })
      
      // Descargar
      doc.save(`depositos-${new Date().toISOString().split('T')[0]}.pdf`)
    } catch (error) {
      console.error('Error generando PDF:', error)
      alert('Error al generar el reporte')
    }
  }

  // Generar PDF de Retiros
  const generateRetrievalsPDF = async () => {
    try {
      const retrievals = await api('/api/activity/retrievals')
      
      const doc = new jsPDF()
      
      doc.setFontSize(18)
      doc.text('Reporte de Retiros', 14, 20)
      
      doc.setFontSize(10)
      doc.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 28)
      doc.text(`Total de retiros: ${retrievals.length}`, 14, 34)
      
      autoTable(doc, {
        startY: 40,
        head: [['Tracking', 'Destinatario', 'Compartimento', 'Fecha']],
        body: retrievals.map(r => [
          r.trackingNumber,
          r.recipientName,
          `#${r.compartmentNumber}`,
          new Date(r.retrievalTimestamp).toLocaleString('es-CO')
        ]),
        styles: { fontSize: 8 },
        headStyles: { fillColor: [46, 204, 113] }
      })
      
      doc.save(`retiros-${new Date().toISOString().split('T')[0]}.pdf`)
    } catch (error) {
      console.error('Error generando PDF:', error)
      alert('Error al generar el reporte')
    }
  }

  // Generar PDF Completo (Depósitos + Retiros)
  const generateActivityPDF = async () => {
    try {
      const [deposits, retrievals] = await Promise.all([
        api('/api/activity/deposits'),
        api('/api/activity/retrievals')
      ])
      
      const doc = new jsPDF()
      
      // Título principal
      doc.setFontSize(20)
      doc.text('Reporte de Actividad', 14, 20)
      
      doc.setFontSize(10)
      doc.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 28)
      
      // Sección Depósitos
      doc.setFontSize(14)
      doc.text('Depósitos', 14, 40)
      
      autoTable(doc, {
        startY: 45,
        head: [['Tracking', 'Destinatario', 'Courier', 'Compartimento', 'Fecha']],
        body: deposits.map(d => [
          d.trackingNumber,
          d.recipientName,
          d.courierName,
          `#${d.compartmentNumber}`,
          new Date(d.depositTimestamp).toLocaleString('es-CO')
        ]),
        styles: { fontSize: 8 },
        headStyles: { fillColor: [41, 128, 185] }
      })
      
      // Sección Retiros
      const finalY = (doc as any).lastAutoTable.finalY + 10
      doc.setFontSize(14)
      doc.text('Retiros', 14, finalY)
      
      autoTable(doc, {
        startY: finalY + 5,
        head: [['Tracking', 'Destinatario', 'Compartimento', 'Fecha']],
        body: retrievals.map(r => [
          r.trackingNumber,
          r.recipientName,
          `#${r.compartmentNumber}`,
          new Date(r.retrievalTimestamp).toLocaleString('es-CO')
        ]),
        styles: { fontSize: 8 },
        headStyles: { fillColor: [46, 204, 113] }
      })
      
      doc.save(`actividad-${new Date().toISOString().split('T')[0]}.pdf`)
    } catch (error) {
      console.error('Error generando PDF:', error)
      alert('Error al generar el reporte')
    }
  }

  // Generar PDF de Dashboard
  const generateDashboardPDF = async () => {
    try {
      const stats = await $fetch('/api/dashboard/stats')
      
      const doc = new jsPDF()
      
      doc.setFontSize(20)
      doc.text('Dashboard - Estadísticas', 14, 20)
      
      doc.setFontSize(10)
      doc.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 28)
      
      // Actividad del día
      doc.setFontSize(14)
      doc.text('Actividad del Día', 14, 40)
      
      doc.setFontSize(10)
      doc.text(`Depósitos: ${stats.todayActivity.totalDeposits}`, 20, 48)
      doc.text(`Retiros: ${stats.todayActivity.totalRetrievals}`, 20, 54)
      doc.text(`Pendientes: ${stats.todayActivity.pendingRetrievals}`, 20, 60)
      
      // Estado de Compartimentos
      doc.setFontSize(14)
      doc.text('Estado de Compartimentos', 14, 72)
      
      autoTable(doc, {
        startY: 78,
        head: [['Estado', 'Cantidad']],
        body: [
          ['Total', stats.compartmentStats.total],
          ['Disponibles', stats.compartmentStats.available],
          ['Ocupados', stats.compartmentStats.occupied],
          ['Mantenimiento', stats.compartmentStats.maintenance]
        ],
        styles: { fontSize: 10 },
        headStyles: { fillColor: [52, 73, 94] }
      })
      
      // Condición Física
      const finalY = (doc as any).lastAutoTable.finalY + 10
      doc.setFontSize(14)
      doc.text('Condición Física', 14, finalY)
      
      autoTable(doc, {
        startY: finalY + 5,
        head: [['Condición', 'Cantidad']],
        body: [
          ['Buen Estado', stats.compartmentStats.conditionBreakdown.buenEstado],
          ['Mal Estado', stats.compartmentStats.conditionBreakdown.malEstado],
          ['Requiere Mantenimiento', stats.compartmentStats.conditionBreakdown.requiereMantenimiento]
        ],
        styles: { fontSize: 10 },
        headStyles: { fillColor: [52, 73, 94] }
      })
      
      doc.save(`dashboard-${new Date().toISOString().split('T')[0]}.pdf`)
    } catch (error) {
      console.error('Error generando PDF:', error)
      alert('Error al generar el reporte')
    }
  }

  return {
    generateDepositsPDF,
    generateRetrievalsPDF,
    generateActivityPDF,
    generateDashboardPDF
  }
}
```

### 3. Uso en Componentes

```vue
<template>
  <div class="reports-page">
    <h1>Reportes</h1>
    
    <div class="buttons-grid">
      <button @click="generateDepositsPDF" class="btn-primary">
        📦 Descargar Reporte de Depósitos
      </button>
      
      <button @click="generateRetrievalsPDF" class="btn-success">
        📤 Descargar Reporte de Retiros
      </button>
      
      <button @click="generateActivityPDF" class="btn-info">
        📊 Descargar Reporte Completo
      </button>
      
      <button @click="generateDashboardPDF" class="btn-secondary">
        📈 Descargar Dashboard
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
const {
  generateDepositsPDF,
  generateRetrievalsPDF,
  generateActivityPDF,
  generateDashboardPDF
} = useReports()
</script>

<style scoped>
.buttons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-top: 2rem;
}

button {
  padding: 1rem;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

button:hover {
  transform: translateY(-2px);
}

.btn-primary { background: #3498db; color: white; }
.btn-success { background: #2ecc71; color: white; }
.btn-info { background: #9b59b6; color: white; }
.btn-secondary { background: #34495e; color: white; }
</style>
```

---

## 📋 Ejemplo con Filtros de Fecha

```typescript
// composables/useReports.ts (versión con filtros)
export const useReports = () => {
  const { api } = useApi()

  const generateDepositsPDFWithFilters = async (
    startDate?: Date,
    endDate?: Date
  ) => {
    try {
      // Obtener todos los depósitos
      let deposits = await api('/api/activity/deposits')
      
      // Filtrar por fecha si se proporcionan
      if (startDate || endDate) {
        deposits = deposits.filter(d => {
          const depositDate = new Date(d.depositTimestamp)
          if (startDate && depositDate < startDate) return false
          if (endDate && depositDate > endDate) return false
          return true
        })
      }
      
      const doc = new jsPDF()
      
      doc.setFontSize(18)
      doc.text('Reporte de Depósitos', 14, 20)
      
      doc.setFontSize(10)
      if (startDate && endDate) {
        doc.text(
          `Período: ${startDate.toLocaleDateString('es-CO')} - ${endDate.toLocaleDateString('es-CO')}`,
          14, 28
        )
      } else {
        doc.text(`Fecha: ${new Date().toLocaleDateString('es-CO')}`, 14, 28)
      }
      doc.text(`Total: ${deposits.length} depósitos`, 14, 34)
      
      autoTable(doc, {
        startY: 40,
        head: [['Tracking', 'Destinatario', 'Courier', 'Compartimento', 'Fecha']],
        body: deposits.map(d => [
          d.trackingNumber,
          d.recipientName,
          d.courierName,
          `#${d.compartmentNumber}`,
          new Date(d.depositTimestamp).toLocaleString('es-CO')
        ]),
        styles: { fontSize: 8 },
        headStyles: { fillColor: [41, 128, 185] }
      })
      
      doc.save(`depositos-${new Date().toISOString().split('T')[0]}.pdf`)
    } catch (error) {
      console.error('Error:', error)
      alert('Error al generar el reporte')
    }
  }

  return { generateDepositsPDFWithFilters }
}
```

**Uso con filtros:**
```vue
<template>
  <div>
    <input v-model="startDate" type="date" />
    <input v-model="endDate" type="date" />
    <button @click="download">Descargar Reporte</button>
  </div>
</template>

<script setup lang="ts">
const { generateDepositsPDFWithFilters } = useReports()
const startDate = ref('')
const endDate = ref('')

const download = () => {
  generateDepositsPDFWithFilters(
    startDate.value ? new Date(startDate.value) : undefined,
    endDate.value ? new Date(endDate.value) : undefined
  )
}
</script>
```

---

## 🎨 Personalización del PDF

### Agregar Logo

```typescript
// Agregar logo al PDF
const addLogo = (doc: jsPDF) => {
  // Opción 1: Desde URL
  const logoUrl = 'https://example.com/logo.png'
  doc.addImage(logoUrl, 'PNG', 160, 10, 30, 30)
  
  // Opción 2: Desde base64
  const logoBase64 = 'data:image/png;base64,...'
  doc.addImage(logoBase64, 'PNG', 160, 10, 30, 30)
}
```

### Agregar Pie de Página

```typescript
const addFooter = (doc: jsPDF) => {
  const pageCount = doc.getNumberOfPages()
  
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i)
    doc.setFontSize(8)
    doc.text(
      `Página ${i} de ${pageCount}`,
      doc.internal.pageSize.width / 2,
      doc.internal.pageSize.height - 10,
      { align: 'center' }
    )
    doc.text(
      'Servientrega - Sistema de Lockers',
      14,
      doc.internal.pageSize.height - 10
    )
  }
}
```

---

## 📊 Resumen

**Endpoints JSON disponibles:**
- ✅ `GET /api/dashboard/stats` - Estadísticas
- ✅ `GET /api/activity/deposits` - Depósitos
- ✅ `GET /api/activity/retrievals` - Retiros

**El frontend debe:**
1. Obtener datos JSON de los endpoints
2. Usar `jsPDF` + `jspdf-autotable` para generar PDFs
3. Aplicar filtros y formato según necesidad
4. Descargar el PDF generado

**No se requieren cambios en el backend** ✅
