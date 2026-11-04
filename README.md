# ERP para la Bodega de Alimentos "Frescura Total"

## Descripción

**Frescura Total ERP** es una aplicación de consola desarrollada en Java para apoyar la administración de una bodega distribuidora de alimentos perecederos. Centraliza los procesos de inventario, control de calidad, proveedores, ventas, facturación y entregas, con el propósito de reducir pérdidas por vencimiento, mejorar la trazabilidad de los lotes y mantener coordinadas las operaciones comerciales y logísticas. Utiliza MySQL para persistencia de datos y una arquitectura MVC organizada por componentes, complementada con el patrón DAO.

## Autores

- **[Brayan Steven RIaño Fuentes]**
- **[Juan David Perez Anaya]**

> Reemplazar los campos anteriores por los nombres completos antes de la entrega.

## Requerimientos previos

- **Java Development Kit (JDK) 17**.
- **Apache NetBeans IDE 12 o superior**, con soporte para Maven.
- **Apache Maven** (integrado en NetBeans o instalado por separado para ejecutar comandos desde terminal).
- **MySQL Server 8.0**.
- **Git**, para clonar el repositorio.
- Opcional: **DBeaver** o MySQL Workbench para administrar la base de datos; **Docker**, si se desea ejecutar MySQL en un contenedor.

El proyecto declara sus dependencias en `pom.xml`, entre ellas MySQL Connector/J, `dotenv-java`, Apache PDFBox y ZXing. Maven se encarga de descargarlas.

## Configuración de la base de datos

### 1. Iniciar MySQL

Inicia una instancia local de MySQL 8.0 y comprueba que tienes un usuario con permisos para crear bases de datos y tablas.

### 2. Ejecutar el script SQL

El archivo **`docs/database.sql`** contiene las instrucciones para crear la base de datos `erp_frescura_total`, definir las tablas y cargar los registros de prueba incluidos en el script.

**Advertencia:** el script comienza con `DROP DATABASE IF EXISTS erp_frescura_total;`. Si ya existe una base de datos con ese nombre, se eliminará junto con su contenido. Haz una copia de seguridad antes de ejecutarlo.

Desde un cliente gráfico como DBeaver o MySQL Workbench, abre `docs/database.sql` y ejecútalo completo. Como alternativa, desde la terminal, ubicado en la raíz del proyecto:

```bash
mysql -u root -p < docs/database.sql
```

Introduce la contraseña de MySQL cuando se solicite. Para comprobar la creación de las tablas:

```sql
USE erp_frescura_total;
SHOW TABLES;
```

### 3. Configurar las credenciales

La conexión se gestiona en `src/main/java/com/frescuratotal/erp/config/DatabaseConnection.java`, que utiliza la biblioteca `dotenv-java` para leer las variables del archivo **`.env`**.

En la raíz del proyecto, crea `.env` a partir de `.env.example` y configura tus credenciales:

```dotenv
DB_URL="jdbc:mysql://localhost:3306/erp_frescura_total"
DB_USER="root"
DB_PASSWORD="TU_CONTRASENA_MYSQL"
```

Si MySQL se ejecuta en Docker con el puerto `3307` publicado en tu equipo, cambia la URL a:

```dotenv
DB_URL="jdbc:mysql://localhost:3307/erp_frescura_total"
```

El puerto debe coincidir con el de tu instalación. **No subas `.env` ni contraseñas reales al repositorio.** Mantén únicamente `.env.example` como referencia.

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/juandavidperezA/frescura-total-erp.git
cd frescura-total-erp
```

Utiliza la rama `main` para la versión de entrega. Si el repositorio es privado, necesitas acceso autorizado.

### 2. Abrir en Apache NetBeans

1. Abre Apache NetBeans.
2. Selecciona **File > Open Project**.
3. Selecciona la carpeta `frescura-total-erp`, que contiene `pom.xml`.
4. Espera a que Maven descargue las dependencias.
5. Verifica que el proyecto utilice JDK 17 y que MySQL esté en ejecución.

### 3. Compilar

En NetBeans, selecciona **Run > Clean and Build Project** (o la opción equivalente del menú contextual del proyecto). Verifica que el proceso termine sin errores.

### 4. Ejecutar

Selecciona **Run > Run Project** o presiona **F6**. El punto de entrada configurado es `com.frescuratotal.erp.FrescuraTotalErp`.

La aplicación inicia en consola, comprueba la conexión con MySQL y presenta el menú principal para acceder a los módulos.

### Ejecución alternativa desde terminal

Si tienes Maven disponible en el `PATH`:

```bash
mvn clean package
mvn exec:java
```

La compilación Maven genera archivos en `target/`, no en `dist/`. El JAR generado por la configuración actual no incluye necesariamente todas las dependencias; por ello, **se recomienda ejecutar desde NetBeans o con `mvn exec:java`** en lugar de utilizar directamente `java -jar`.

## Módulos del sistema

| Módulo | Responsabilidad |
| --- | --- |
| **Inventarios y calidad** | Registro y consulta de lotes, cantidades, fechas de vencimiento, estados, cuarentena, inspecciones y mermas. |
| **Proveedores y compras** | Gestión de proveedores, asociación con productos y consulta del historial de recepción de mercancía. |
| **Ventas y facturación** | Gestión de clientes y ventas, detalle de productos y generación de comprobantes de facturación. |
| **Logística y entregas** | Preparación y seguimiento de pedidos, asignación de entregas, conductores, vehículos y rutas. |

## Estructura del proyecto y arquitectura

La aplicación utiliza **Modelo-Vista-Controlador (MVC) por componentes**. Cada módulo separa, según su responsabilidad, las entidades de negocio (`model`), la interacción con el usuario (`view`), la coordinación de operaciones (`controller`) y la persistencia (`dao`). Algunas funciones también incluyen validadores (`validation`).

Estructura representativa del código:

```text
frescura-total-erp/
├── docs/
│   ├── database.sql
│   ├── diagrama_er.png             # Artefacto exigido para la entrega
│   └── diagrama_clases.png        # Artefacto exigido para la entrega
├── src/main/java/
│   ├── com/frescuratotal/erp/
│   │   ├── FrescuraTotalErp.java   # Punto de entrada y menú principal
│   │   ├── config/
│   │   │   └── DatabaseConnection.java
│   │   ├── inventory/
│   │   │   ├── model/
│   │   │   ├── dao/
│   │   │   ├── controller/
│   │   │   ├── view/
│   │   │   └── validation/
│   │   └── suppliers/
│   │       ├── model/
│   │       ├── dao/
│   │       ├── controller/
│   │       └── view/
│   ├── sales/                      # Componente de ventas
│   ├── logistics/                  # Componente de logística
│   └── logging/                    # Registro de acciones
├── .env.example
├── pom.xml
└── README.md
```

> La estructura anterior es ilustrativa. Los diagramas indicados son artefactos requeridos; confirma que ambos archivos estén presentes en `docs/` antes de la entrega.

El flujo principal es **Vista → Controlador → DAO → MySQL**. Las vistas reciben opciones y datos del usuario, los controladores coordinan las operaciones, los DAO ejecutan consultas SQL mediante JDBC y los modelos representan la información del dominio.

## Principios SOLID y patrones de diseño

### 1. Responsabilidad única (SRP)

La separación de responsabilidades se observa en clases como `InventoryLot`, `InventoryLotValidator`, `InventoryLotDAO`, `InventoryController` e `InventoryView`. El modelo representa los datos del lote; el validador comprueba las reglas de entrada; el DAO realiza las operaciones SQL; el controlador coordina las solicitudes y la vista interactúa con el usuario. Esta división facilita localizar errores y modificar una parte sin concentrar toda la lógica en una sola clase.

### 2. Patrón DAO (Data Access Object)

Las clases de persistencia, como `InventoryLotDAO` y `SupplierDAO`, encapsulan el acceso a MySQL mediante JDBC y sentencias SQL parametrizadas. Por ejemplo, `InventoryLotDAO` registra lotes y consulta sus datos sin obligar a la vista a conocer la estructura de las consultas. Este patrón reduce el acoplamiento entre la interfaz y la base de datos, favorece el mantenimiento y permite reutilizar operaciones desde los controladores.

### 3. Arquitectura MVC por componentes

El proyecto organiza sus funcionalidades de negocio en componentes de inventario, proveedores, ventas y logística. Cada componente distribuye las tareas entre modelos, vistas y controladores, evitando que la lógica de presentación y las operaciones de persistencia se mezclen. La clase `FrescuraTotalErp` actúa como punto de entrada e integra los menús de los módulos.

### 4. Configuración centralizada de conexiones

`DatabaseConnection` centraliza la creación de conexiones JDBC y obtiene la URL, el usuario y la contraseña desde variables definidas en `.env`. Esto evita duplicar credenciales en las clases DAO y facilita ejecutar la aplicación en diferentes equipos cambiando únicamente la configuración local. **No se presenta como un patrón Singleton**, ya que el método `getConnection()` obtiene conexiones nuevas y no mantiene una instancia única compartida.

## Documentación técnica

El directorio `docs/` debe contener:

- **`database.sql`**: creación de la base de datos, tablas y registros de prueba.
- **`diagrama_er.png`**: diagrama entidad-relación con atributos y cardinalidades.
- **`diagrama_clases.png`**: diagrama UML de las principales entidades del dominio y sus relaciones.

## Tecnologías utilizadas

Java 17 · Apache Maven · MySQL 8.0 · JDBC · Apache NetBeans · dotenv-java · Apache PDFBox · ZXing · Git y GitHub.

## Consideraciones

Este es un proyecto académico. Los documentos de facturación generados por la aplicación no deben confundirse con facturas electrónicas oficialmente validadas por la DIAN. Los datos de ejemplo se utilizan con fines de prueba y demostración.
