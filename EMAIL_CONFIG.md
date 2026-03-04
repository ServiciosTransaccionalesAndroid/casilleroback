# 📧 Configuración de Email en Railway

## ⚠️ Problema Actual

Railway bloquea conexiones SMTP salientes por seguridad. El sistema funciona sin email, pero no se envían notificaciones.

---

## ✅ Soluciones

### Opción 1: Usar SendGrid (RECOMENDADO)

SendGrid es gratuito hasta 100 emails/día y funciona en Railway.

#### 1. Crear cuenta en SendGrid
- Ir a: https://sendgrid.com
- Crear cuenta gratuita
- Verificar email

#### 2. Crear API Key
- Dashboard → Settings → API Keys
- Create API Key
- Copiar la key (solo se muestra una vez)

#### 3. Configurar en Railway

```bash
SPRING_MAIL_HOST=smtp.sendgrid.net
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=apikey
SPRING_MAIL_PASSWORD=tu-sendgrid-api-key
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

---

### Opción 2: Usar Mailgun

Mailgun ofrece 5,000 emails/mes gratis.

#### 1. Crear cuenta
- Ir a: https://www.mailgun.com
- Crear cuenta

#### 2. Obtener credenciales
- Dashboard → Sending → Domain settings
- Copiar SMTP credentials

#### 3. Configurar en Railway

```bash
SPRING_MAIL_HOST=smtp.mailgun.org
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu-username@mailgun.org
SPRING_MAIL_PASSWORD=tu-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

---

### Opción 3: Deshabilitar Email (Temporal)

Si no necesitas email por ahora:

```bash
# No agregues variables de email en Railway
# El sistema funcionará sin enviar correos
```

El código ya está preparado para funcionar sin email.

---

## 🧪 Probar Email

### Endpoint de prueba

```bash
POST /api/packages/{trackingNumber}/resend-code
```

Si el email está configurado correctamente, el destinatario recibirá el correo.

---

## 📊 Estado Actual

- ✅ Sistema funciona sin email
- ⚠️ Notificaciones deshabilitadas
- ✅ Códigos se generan correctamente
- ✅ API responde con código + PIN

---

## 🔍 Verificar Logs

En Railway → Logs, busca:

### Email funcionando:
```
Email sent successfully to cliente@email.com
```

### Email fallando:
```
Error sending email: Mail server connection failed
Email notifications disabled
```

---

## 💡 Alternativa: Webhook

Si no quieres configurar email, puedes:

1. Obtener código + PIN desde la API
2. Enviar por WhatsApp/SMS usando otro servicio
3. Mostrar en pantalla al mensajero

### Ejemplo:

```bash
POST /api/deposits
Response:
{
  "retrievalCode": "RCSV2X4Y",
  "secretPin": "123456"
}

# Enviar por WhatsApp API
POST https://api.whatsapp.com/send
{
  "to": "+573001234567",
  "message": "Tu código: RCSV2X4Y, PIN: 123456"
}
```

---

## ✅ Recomendación

**Para producción:** Usar SendGrid (gratis, confiable, fácil)

**Para desarrollo:** Deshabilitar email temporalmente

---

## 🚀 Aplicar Cambios

```bash
git add .
git commit -m "Make email optional and more resilient"
git push
```

El sistema ya funciona sin email. Solo agrega las variables cuando quieras habilitarlo.
