# E-commerce Fullstack - CodigoDelSur

Aplicación de comercio electrónico desarrollada como desafío técnico para el proceso de selección de **CodigoDelSur**.

El proyecto implementa un backend en **Spring Boot 4.1.2** con persistencia en **PostgreSQL**, junto a una aplicación web responsiva construida con **React 19 + React Router DOM 7 + Vite 8**.

---

## 🚀 Funcionalidades Principales

- **Autenticación y Autorización (JWT)**:
  - Registro e inicio de sesión con contraseñas encriptadas con BCrypt.
  - Roles `CUSTOMER` y `ADMIN`.
  - Control de acceso por rol en endpoints y rutas del cliente.
- **Catálogo de Productos**:
  - Listado de productos activos, búsqueda por nombre y página de detalle.
- **Carrito de Compras**:
  - Carrito persistente en base de datos asociado al usuario autenticado.
  - Operaciones para agregar productos, actualizar cantidades y eliminar ítems con cálculo de subtotales y totales.
- **Checkout y Pasarela de Pagos (Stripe Test Mode)**:
  - Creación de sesiones de Stripe Checkout.
  - Procesamiento asíncrono y seguro de pagos mediante **Stripe Webhooks** con validación de firma criptográfica (`Stripe-Signature`).
  - Generación de órdenes con congelamiento de precios
  - Vaciado automático del carrito una vez confirmado el pago.
- **Historial y Detalle de Órdenes**:
  - Consulta de compras anteriores y seguimiento de estados (`PENDIENTE`, `PAGADA`).
- **Panel Administrativo (`/admin`)**:
  - Gestión de productos (creación, edición y activación/desactivación).
  - Subida y reemplazo de imágenes locales con validación de tipo MIME (`JPEG`, `PNG`, `WEBP`) y límite de tamaño (máx. 2 MB).
- **Base de Datos y Migraciones**:
  - Control de versiones del esquema y datos iniciales gestionados con **Flyway**.
  - Base de datos relacional PostgreSQL orquestada con **Docker Compose**.

---

## 🛠️ Stack Tecnológico

### Backend
- **Java 21** / **Spring Boot 4.1.2**
- **Spring Data JPA / Hibernate**
- **Spring Security** con **JWT (Auth0 `java-jwt`)**
- **PostgreSQL 16** + **Flyway**
- **Stripe Java SDK**
- **SpringDoc OpenAPI (Swagger UI)**

### Frontend
- **React 19**
- **React Router DOM 7**
- **Vite 8**
- **CSS3 Vanilla** (diseño responsivo)
- **Playwright** (tests End-to-End)

---

## 📋 Requisitos Previos

- **Docker** y **Docker Compose**
- **Java 21 JDK**
- **Node.js 20.19+ o 22.12+** y **npm**
- **Cuenta de Stripe en Modo Test**: Necesaria para obtener las claves de API y probar los pagos.
- **Stripe CLI**: Necesaria para recibir los webhooks de Stripe en el entorno local y completar el flujo de pago.

---

## ⚙️ Configuración y Puesta en Marcha

### 1. Base de Datos (Docker)
El archivo `docker-compose.yml` mapea el puerto `5433` al contenedor de PostgreSQL:
```bash
docker compose up -d
```

### 2. Configuración de Stripe (Requerido)
Para que el checkout y la confirmación de pagos funcionen, debés contar con tus claves de Stripe en modo prueba.

Podés configurarlas como variables de entorno o colocarlas directamente en `backend/src/main/resources/application.properties`:
```properties
stripe.secret-key=sk_test_tu_clave_secreta
stripe.webhook-secret=whsec_tu_clave_webhook
```

> **Para recibir webhooks de Stripe en local**, descargá e iniciá sesión en la Stripe CLI y ejecutá:
> ```bash
> stripe listen --events checkout.session.completed,checkout.session.expired --forward-to localhost:8080/webhooks/stripe
> ```
> El CLI imprimirá tu `webhook signing secret` (`whsec_...`), el cual debés colocar en la propiedad `stripe.webhook-secret`.
>
> **Importante:** la Stripe CLI debe permanecer ejecutándose durante las pruebas de pago. Sin ella, Stripe no puede notificar directamente a `localhost`, por lo que la orden puede permanecer en estado `PENDIENTE`.

### 3. Ejecutar el Backend
```bash
cd backend
./mvnw spring-boot:run
```
El servidor iniciará en `http://localhost:8080`. Flyway ejecutará automáticamente las migraciones y cargará el usuario administrador y los productos de prueba.

### 4. Ejecutar el Frontend
En otra terminal:
```bash
cd frontend
npm install
npm run dev
```
La aplicación web estará disponible en `http://localhost:5173`.

---

## 📖 Documentación de la API (Swagger UI)

Con el backend levantado, la documentación interactiva OpenAPI/Swagger está disponible en:
👉 **`http://localhost:8080/swagger-ui.html`**

### Niveles de Acceso por Endpoint

| Método | Endpoint | Nivel de Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| **POST** | `/auth/login` | 🌐 **Público** | Inicia sesión y retorna token JWT |
| **POST** | `/auth/register` | 🌐 **Público** | Registra un nuevo usuario (`CUSTOMER`) |
| **POST** | `/auth/logout` | 🌐 **Público** | Cierre de sesión en el cliente; no invalida el token en el servidor |
| **GET** | `/productos` | 🌐 **Público** | Lista productos activos del catálogo |
| **GET** | `/productos/{id}` | 🌐 **Público** | Detalle de un producto |
| **GET** | `/productos/buscar` | 🌐 **Público** | Búsqueda por query param `?nombre=` |
| **GET** | `/imagenes/{nombre}` | 🌐 **Público** | Retorna la imagen almacenada |
| **POST** | `/webhooks/stripe` | 🌐 **Público (Stripe)** | Valida firma `Stripe-Signature` y confirma pago |
| **GET** | `/carrito` | 🔒 **Autenticado** | Obtiene el carrito del usuario logueado |
| **POST** | `/carrito/items` | 🔒 **Autenticado** | Agrega un producto al carrito |
| **PUT** | `/carrito/items/{idItem}` | 🔒 **Autenticado** | Modifica cantidad de un ítem |
| **DELETE** | `/carrito/items/{idItem}` | 🔒 **Autenticado** | Elimina un ítem del carrito |
| **POST** | `/checkout` | 🔒 **Autenticado** | Crea orden en `PENDIENTE` y sesión Stripe |
| **GET** | `/ordenes` | 🔒 **Autenticado** | Lista el historial de órdenes del usuario |
| **GET** | `/ordenes/{id}` | 🔒 **Autenticado** | Detalle de una orden específica |
| **GET** | `/admin/productos` | 👑 **Solo ADMIN** | Lista todos los productos (activos e inactivos) |
| **POST** | `/admin/productos` | 👑 **Solo ADMIN** | Crea un nuevo producto |
| **PUT** | `/admin/productos/{id}` | 👑 **Solo ADMIN** | Actualiza datos de un producto |
| **POST** | `/admin/productos/{id}/imagen` | 👑 **Solo ADMIN** | Sube o reemplaza la imagen (multipart/form-data) |

**Nota sobre el cierre de sesión:** el endpoint `/auth/logout` es público y el cierre de sesión descarta el token en el cliente. Como la autenticación es stateless, el token no se invalida en el servidor y sigue siendo válido hasta que vence (2 horas).

---

## 🧪 Tests End-to-End (Playwright)

El proyecto incluye una suite de pruebas E2E con **Playwright** que cubre los flujos críticos de la aplicación interactuando con un navegador real.

### Cobertura de los tests

| Archivo | Flujos cubiertos |
| :--- | :--- |
| `e2e/auth.test.js` | Registro exitoso, usuario duplicado, validaciones de formato, login exitoso y fallido, y cierre de sesión |
| `e2e/catalogo.test.js` | Carga de tarjetas de productos y navegación al detalle |
| `e2e/carrito.test.js` | Registro, agregado de producto al carrito, modificación de cantidad, actualización del precio y eliminación |
| `e2e/admin.test.js` | Acceso sin autenticación, login de administrador, creación de producto y alternancia de estado activo/inactivo |

> El flujo de pago con Stripe Checkout está excluido de los tests E2E de forma intencional, ya que involucra una redirección a un servidor externo y la recepción asíncrona de un Webhook, lo cual introduce dependencias de red e indeterminismo fuera del control del test.

### Cómo correr los tests

> **Requisito previo**: El backend y el frontend deben estar corriendo antes de ejecutar los tests.

La primera vez, desde `frontend`, ejecutá:
```bash
npx playwright install
```

Luego, con el backend y el frontend en ejecución, corré los tests desde `frontend`:
```bash
npx playwright test
```

Para correr los tests con la interfaz gráfica interactiva de Playwright (modo visual):
```bash
npx playwright test --ui
```

### Tests unitarios del backend

Desde el directorio `backend`, ejecutá:
```bash
./mvnw test
```

**Datos utilizados por los tests:** las pruebas asumen que hay productos cargados (la migración de demostración) y crean usuarios y un producto de prueba que permanecen en la base de datos.

Para recrear la base de datos desde cero, desde la raíz del proyecto ejecutá:
```bash
docker compose down -v
docker compose up -d
```

> **Advertencia:** `docker compose down -v` elimina los volúmenes y los datos persistidos de la base de datos.

---

## 💳 Probar un pago

- **Moneda:** USD.
- **Pago exitoso:** `4242 4242 4242 4242`, cualquier fecha futura y cualquier CVC.
- **Pago rechazado:** `4000 0000 0000 0002`, cualquier fecha futura y cualquier CVC.

Estas son tarjetas de prueba de Stripe; no utilices datos de tarjetas reales. Stripe rechaza cobros inferiores al mínimo admitido para la moneda y el método de pago (habitualmente **0,50 USD** para pagos con tarjeta en USD).

Para comprobar el flujo completo, mantené activa la Stripe CLI y verificá que el webhook llegue al backend. La orden debe pasar de `PENDIENTE` a `PAGADA` cuando se procese correctamente la confirmación del pago.

---

## 🧩 Decisiones de diseño

- **El backend manda en precios y totales:** el cliente solo envía ids y cantidades; subtotal y total se calculan en el servidor.
- **Precio congelado:** `orden_detalle` guarda el precio de cada producto al momento de la compra.
- **Orden `PENDIENTE` → `PAGADA` por webhook:** la redirección de Stripe no confirma el pago; se valida la firma del webhook y el procesamiento es idempotente.
- **Sin borrado de productos:** se desactivan para no perder el historial de órdenes ni romper carritos.
- **Carrito persistente en la base**, creado al primer uso.
- **Imágenes en disco detrás de la interfaz `StorageService`:** las imágenes no se suben al repo, así que los productos de ejemplo vienen sin foto (se ve una imagen por defecto) y se pueden cargar desde `/admin`.
- **Roles:** el admin no ve carrito ni órdenes en la interfaz, priorizando la gestión de la tienda.

---

## ⚠️ Limitaciones conocidas

- Las órdenes abandonadas quedan `PENDIENTE` hasta que la sesión de Stripe expira (24 h); la cancelación automática requiere que el webhook correspondiente se reciba y procese correctamente.
- El logout no invalida el token en el servidor; el JWT sigue siendo válido hasta que vence (2 horas).
- El token JWT se guarda en `localStorage`.
- Los tests E2E dejan usuarios y un producto de prueba en la base de datos.
- Las imágenes se almacenan localmente y no se versionan en Git.

---

## 👤 Credenciales Iniciales (Seed Data)

| Rol | Email | Contraseña | Enfoque en la aplicación |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@ecommerce.com` | `admin123` | Gestión de catálogo, precios e imágenes en `/admin`. *(No incluye acciones de compra en la interfaz)* |
| **CUSTOMER** | Registrar nuevo desde `/registro` | Mínimo 8 caracteres | Flujo de compra completo: catálogo, carrito, checkout Stripe y mis órdenes |

> **Nota sobre roles en la interfaz**: Por diseño de experiencia de usuario, las cuentas con rol `ADMIN` no muestran los botones de compra ni los accesos al carrito u órdenes en el menú de navegación, priorizando el acceso al `/admin`. No obstante, al poseer una sesión válida, dichas rutas siguen siendo accesibles mediante enlace directo (`/carrito`, `/ordenes`), lo cual no constituye una amenaza de seguridad al tratarse de un rol de confianza operando bajo su propio contexto de usuario. Para probar el flujo de compra natural y completo, se recomienda registrar e iniciar sesión con un usuario de rol cliente.
