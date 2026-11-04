-- *** Script Proyecto ERP Frescura Total **

-- Script de Creación de Base de Datos: erp_frescura_total
-- El estudiante puede agregar elementos a la base de datos si considera que hace falta la modelo actual


DROP DATABASE IF EXISTS erp_frescura_total;
CREATE DATABASE erp_frescura_total CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp_frescura_total;

-- TABLAS DE CATÁLOGO (LOOKUP TABLES)

CREATE TABLE estados_lote (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE tipos_cliente (
    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE estados_venta (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB;

-- INFORMACIÓN DE LA EMPRESA

CREATE TABLE informacion_empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre_empresa VARCHAR(255) NOT NULL,
    nit VARCHAR(20) NOT NULL UNIQUE,
    direccion TEXT NOT NULL,
    ciudad VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100)
) ENGINE=InnoDB;

-- MÓDULO: PROVEEDORES

CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nit VARCHAR(20) NOT NULL UNIQUE,
    persona_contacto VARCHAR(100),
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- MÓDULO: PRODUCTOS

CREATE TABLE categorias_producto (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    id_categoria INT NOT NULL,
    unidad_medida VARCHAR(20) NOT NULL COMMENT 'Ej: kg, litro, unidad',
    precio_base DECIMAL(10, 2),
    FOREIGN KEY (id_categoria) REFERENCES categorias_producto(id_categoria)
) ENGINE=InnoDB;

CREATE TABLE productos_proveedor (
    id_proveedor INT NOT NULL,
    id_producto INT NOT NULL,
    PRIMARY KEY (id_proveedor, id_producto),
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE CASCADE
) ENGINE=InnoDB;

-- MÓDULO: INVENTARIO Y CALIDAD

CREATE TABLE lotes_inventario (
    id_lote INT AUTO_INCREMENT PRIMARY KEY,
    numero_lote VARCHAR(50) NOT NULL UNIQUE,
    id_producto INT NOT NULL,
    id_proveedor INT NOT NULL,
    fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NOT NULL,
    cantidad_inicial DECIMAL(10, 2) NOT NULL,
    cantidad DECIMAL(10, 2) NOT NULL,
    precio_compra DECIMAL(10, 2) NOT NULL,
    precio_venta DECIMAL(10, 2) NOT NULL,
    pasillo VARCHAR(50),
    estante VARCHAR(50),
    id_estado INT NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    FOREIGN KEY (id_estado) REFERENCES estados_lote(id_estado),
    INDEX idx_fecha_vencimiento (fecha_vencimiento),
    INDEX idx_estado (id_estado),
    INDEX idx_producto (id_producto)
) ENGINE=InnoDB;

CREATE TABLE registro_cuarentena (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_lote INT NOT NULL,
    motivo TEXT NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resuelta BOOLEAN DEFAULT FALSE,
    fecha_resolucion DATETIME NULL,
    notas_resolucion TEXT,
    FOREIGN KEY (id_lote) REFERENCES lotes_inventario(id_lote),
    INDEX idx_resuelta (resuelta)
) ENGINE=InnoDB;

CREATE TABLE inspecciones_calidad (
    id_inspeccion INT AUTO_INCREMENT PRIMARY KEY,
    id_lote INT NOT NULL,
    fecha_inspeccion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resultado ENUM('Aprobado', 'Rechazado') NOT NULL,
    nombre_inspector VARCHAR(100) NOT NULL,
    motivo_rechazo TEXT COMMENT 'Motivo detallado si el resultado es Rechazado',
    notas TEXT,
    FOREIGN KEY (id_lote) REFERENCES lotes_inventario(id_lote),
    INDEX idx_resultado (resultado)
) ENGINE=InnoDB;

CREATE TABLE registro_mermas (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_lote INT NOT NULL,
    id_inspeccion INT COMMENT 'Inspección que generó esta merma (si aplica)',
    cantidad_perdida DECIMAL(10, 2) NOT NULL,
    motivo TEXT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_registro VARCHAR(100),
    FOREIGN KEY (id_lote) REFERENCES lotes_inventario(id_lote),
    FOREIGN KEY (id_inspeccion) REFERENCES inspecciones_calidad(id_inspeccion) ON DELETE SET NULL,
    INDEX idx_fecha (fecha_registro)
) ENGINE=InnoDB;

-- MÓDULO: PROMOCIONES

CREATE TABLE promociones (
    id_promocion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    tasa_descuento DECIMAL(5, 2) NOT NULL COMMENT 'Ej: 0.50 para 50%',
    aplica_estado_lote INT,
    aplica_categoria_producto INT,
    activa BOOLEAN DEFAULT TRUE,
    fecha_inicio DATE,
    fecha_fin DATE,
    FOREIGN KEY (aplica_estado_lote) REFERENCES estados_lote(id_estado),
    FOREIGN KEY (aplica_categoria_producto) REFERENCES categorias_producto(id_categoria),
    INDEX idx_activa (activa)
) ENGINE=InnoDB;

-- MÓDULO: CLIENTES Y VENTAS

CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion TEXT,
    id_tipo INT NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tipo) REFERENCES tipos_cliente(id_tipo)
) ENGINE=InnoDB;

CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    numero_venta VARCHAR(50) NOT NULL UNIQUE,
    id_cliente INT NOT NULL,
    fecha_venta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12, 2) NOT NULL,
    total_descuento DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_impuesto DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    monto_total DECIMAL(12, 2) NOT NULL,
    id_estado INT NOT NULL,
    usuario_registro VARCHAR(100) COMMENT 'Usuario que registró la venta',
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_estado) REFERENCES estados_venta(id_estado),
    INDEX idx_estado (id_estado),
    INDEX idx_fecha (fecha_venta)
) ENGINE=InnoDB;

CREATE TABLE detalles_venta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_lote INT NOT NULL,
    cantidad_vendida DECIMAL(10, 2) NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    descuento_aplicado DECIMAL(5, 2) DEFAULT 0.00,
    monto_descuento DECIMAL(10, 2) DEFAULT 0.00,
    tasa_impuesto DECIMAL(5, 2) DEFAULT 0.00,
    monto_impuesto DECIMAL(10, 2) DEFAULT 0.00,
    subtotal DECIMAL(10, 2) NOT NULL,
    total_linea DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_lote) REFERENCES lotes_inventario(id_lote)
) ENGINE=InnoDB;

CREATE TABLE facturas (
    id_factura INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    cufe VARCHAR(255) NOT NULL UNIQUE COMMENT 'Código simulado para factura electrónica',
    enlace_qr VARCHAR(255) COMMENT 'URL o datos para generar código QR',
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12, 2) NOT NULL,
    total_descuento DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    monto_impuesto DECIMAL(12, 2) NOT NULL,
    monto_total DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta),
    INDEX idx_numero (numero_factura)
) ENGINE=InnoDB;

-- MÓDULO: LOGÍSTICA Y ENTREGAS

CREATE TABLE conductores (
    id_conductor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    numero_licencia VARCHAR(50) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE vehiculos (
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(20) NOT NULL UNIQUE,
    modelo VARCHAR(100),
    capacidad DECIMAL(10, 2) COMMENT 'Capacidad en kg o unidades',
    estado ENUM('Activo', 'Inactivo', 'Mantenimiento') DEFAULT 'Activo'
) ENGINE=InnoDB;

CREATE TABLE rutas_entrega (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    nombre_ruta VARCHAR(255) NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100) COMMENT 'Usuario logístico que creó la ruta',
    hora_salida_estimada DATETIME,
    hora_llegada_estimada DATETIME,
    hora_salida_real DATETIME,
    hora_llegada_real DATETIME,
    estado ENUM('Planificada', 'En Transito', 'Completada', 'Cancelada') DEFAULT 'Planificada',
    INDEX idx_estado (estado),
    INDEX idx_fecha_creacion (fecha_creacion)
) ENGINE=InnoDB;

CREATE TABLE asignaciones_entrega (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_ruta INT NOT NULL,
    id_vehiculo INT NOT NULL,
    id_conductor INT NOT NULL,
    fecha_asignacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    usuario_asignacion VARCHAR(100) COMMENT 'Usuario que realizó la asignación',
    notas TEXT,
    FOREIGN KEY (id_ruta) REFERENCES rutas_entrega(id_ruta),
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo),
    FOREIGN KEY (id_conductor) REFERENCES conductores(id_conductor),
    INDEX idx_ruta (id_ruta)
) ENGINE=InnoDB;

CREATE TABLE ventas_ruta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ruta INT NOT NULL,
    id_venta INT NOT NULL,
    orden_entrega INT COMMENT 'Orden de entrega en la ruta',
    direccion_destino TEXT NOT NULL,
    fecha_entrega DATETIME,
    recibido_por VARCHAR(255),
    observaciones TEXT,
    FOREIGN KEY (id_ruta) REFERENCES rutas_entrega(id_ruta),
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta),
    UNIQUE KEY (id_ruta, id_venta),
    INDEX idx_ruta (id_ruta),
    INDEX idx_venta (id_venta)
) ENGINE=InnoDB;

-- DATOS INICIALES (SOLO CATÁLOGOS ESENCIALES)

INSERT INTO estados_lote (nombre, descripcion) VALUES
('Disponible', 'Lote disponible para venta'),
('Proximo a Vencer', 'Lote próximo a vencer (7 días o menos)'),
('Vencido', 'Lote vencido, no apto para venta'),
('En Cuarentena', 'Lote en cuarentena pendiente de inspección');

INSERT INTO tipos_cliente (nombre, descripcion) VALUES
('Mayorista', 'Cliente que compra en grandes cantidades'),
('Minorista', 'Cliente que compra en pequeñas cantidades');

INSERT INTO estados_venta (nombre, descripcion) VALUES
('Registrada', 'Venta registrada, pendiente de preparación'),
('En Preparacion', 'Pedido en preparación para despacho'),
('En Transito', 'Pedido en tránsito hacia el cliente'),
('Entregada', 'Pedido entregado al cliente'),
('Cancelada', 'Venta cancelada');

INSERT INTO informacion_empresa (nombre_empresa, nit, direccion, ciudad, telefono, email) VALUES
('Frescura Total S.A.S.', '900123456-7', 'Calle 123 #45-67', 'Bogotá', '(601) 234-5678', 'contacto@frescuratotal.com');

INSERT INTO categorias_producto (nombre, descripcion) VALUES
('Frutas', 'Frutas frescas'),
('Verduras', 'Verduras y hortalizas'),
('Lacteos', 'Productos lácteos'),
('Carnes', 'Carnes y derivados'),
('Granos', 'Granos y cereales'),
('Bebidas', 'Bebidas y líquidos'),
('Enlatados', 'Productos enlatados'),
('Panaderia', 'Productos de panadería');

-- ============================================================
-- DATOS DE PRUEBA DEL ERP FRESCURA TOTAL
-- ============================================================
-- Registros ficticios para pruebas funcionales del sistema.
-- Los datos mantienen las relaciones entre las tablas.
-- Minimo 50 registros por tabla principal.
-- ============================================================


-- ============================================================
-- MODULO 2: PROVEEDORES Y COMPRAS
-- TABLA: proveedores
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO proveedores
(nombre, nit, persona_contacto, email, telefono, direccion, activo)
VALUES
('Frutas del Oriente S.A.S.', '901100001-1', 'Carlos Ramirez', 'contacto@frutasoriente.example.com', '3001000001', 'Bucaramanga, Santander', TRUE),
('Distribuidora Agricola Santander', '901100002-2', 'Maria Gomez', 'ventas@agricolasantander.example.com', '3001000002', 'Giron, Santander', TRUE),
('Verduras Frescas del Campo', '901100003-3', 'Andres Martinez', 'contacto@verdurascampo.example.com', '3001000003', 'Piedecuesta, Santander', TRUE),
('Lacteos La Sabana', '901100004-4', 'Laura Hernandez', 'ventas@lacteossabana.example.com', '3001000004', 'Bogota, Cundinamarca', TRUE),
('Carnes Premium del Norte', '901100005-5', 'Jorge Rodriguez', 'contacto@carnesnorte.example.com', '3001000005', 'Cucuta, Norte de Santander', TRUE),
('Granos de Colombia S.A.S.', '901100006-6', 'Diana Lopez', 'ventas@granoscolombia.example.com', '3001000006', 'Ibague, Tolima', TRUE),
('Bebidas Naturales Andinas', '901100007-7', 'Felipe Torres', 'contacto@bebidasandinas.example.com', '3001000007', 'Tunja, Boyaca', TRUE),
('Conservas del Valle', '901100008-8', 'Paola Castro', 'ventas@conservasvalle.example.com', '3001000008', 'Cali, Valle del Cauca', TRUE),
('Panaderia Industrial Central', '901100009-9', 'Ricardo Morales', 'contacto@panaderiacentral.example.com', '3001000009', 'Medellin, Antioquia', TRUE),
('Agropecuaria Los Pinos', '901100010-1', 'Natalia Vargas', 'ventas@agropinos.example.com', '3001000010', 'Floridablanca, Santander', TRUE),

('Cultivos La Esperanza', '901100011-2', 'Pedro Rojas', 'contacto@cultivosesperanza.example.com', '3001000011', 'Lebrija, Santander', TRUE),
('Frutales de Santander', '901100012-3', 'Sandra Diaz', 'ventas@frutalessantander.example.com', '3001000012', 'Socorro, Santander', TRUE),
('Hortalizas del Altiplano', '901100013-4', 'Oscar Gutierrez', 'contacto@hortalizasaltiplano.example.com', '3001000013', 'Duitama, Boyaca', TRUE),
('Lacteos del Campo', '901100014-5', 'Camila Moreno', 'ventas@lacteoscampo.example.com', '3001000014', 'Chiquinquira, Boyaca', TRUE),
('Avicola El Progreso', '901100015-6', 'Miguel Sanchez', 'contacto@avicolaprogreso.example.com', '3001000015', 'Bucaramanga, Santander', TRUE),
('Distribuidora de Cereales Andinos', '901100016-7', 'Andrea Silva', 'ventas@cerealesandinos.example.com', '3001000016', 'Pasto, Narino', TRUE),
('Jugos y Pulpas Tropicales', '901100017-8', 'Daniel Perez', 'contacto@pulpastropicales.example.com', '3001000017', 'Barranquilla, Atlantico', TRUE),
('Alimentos Enlatados del Caribe', '901100018-9', 'Valentina Ruiz', 'ventas@enlatadoscaribe.example.com', '3001000018', 'Cartagena, Bolivar', TRUE),
('Molinos La Pradera', '901100019-1', 'Sebastian Ortiz', 'contacto@molinospradera.example.com', '3001000019', 'Villavicencio, Meta', TRUE),
('Finca Productiva El Roble', '901100020-2', 'Monica Jimenez', 'ventas@fincaelroble.example.com', '3001000020', 'San Gil, Santander', TRUE),

('Agricola Campo Verde', '901100021-3', 'Alejandro Medina', 'contacto@campoverde.example.com', '3001000021', 'Rionegro, Antioquia', TRUE),
('Frutas Tropicales del Magdalena', '901100022-4', 'Juliana Herrera', 'ventas@frutasmagdalena.example.com', '3001000022', 'Santa Marta, Magdalena', TRUE),
('Verduras Organicas del Sur', '901100023-5', 'Cristian Vega', 'contacto@organicassur.example.com', '3001000023', 'Popayan, Cauca', TRUE),
('Quesos Artesanales Boyaca', '901100024-6', 'Marcela Navarro', 'ventas@quesosboyaca.example.com', '3001000024', 'Sogamoso, Boyaca', TRUE),
('Carnicos La Hacienda', '901100025-7', 'Hector Molina', 'contacto@carnicoshacienda.example.com', '3001000025', 'Monteria, Cordoba', TRUE),
('Arroces del Tolima', '901100026-8', 'Patricia Romero', 'ventas@arrocestolima.example.com', '3001000026', 'Espinal, Tolima', TRUE),
('Agua y Bebidas Cristalinas', '901100027-9', 'Esteban Castillo', 'contacto@bebidascristalinas.example.com', '3001000027', 'Manizales, Caldas', TRUE),
('Conservas Naturales del Pacifico', '901100028-1', 'Gabriela Suarez', 'ventas@conservaspacifico.example.com', '3001000028', 'Buenaventura, Valle del Cauca', TRUE),
('Panificadora Los Trigales', '901100029-2', 'Juan Cardenas', 'contacto@lostrigales.example.com', '3001000029', 'Pereira, Risaralda', TRUE),
('Productores Unidos del Campo', '901100030-3', 'Lucia Mendoza', 'ventas@productoresunidos.example.com', '3001000030', 'Barbosa, Santander', TRUE),

('Agroindustrias La Colina', '901100031-4', 'Fernando Pineda', 'contacto@agrocolina.example.com', '3001000031', 'Zapatoca, Santander', TRUE),
('Frutas Selectas Nacionales', '901100032-5', 'Carolina Acosta', 'ventas@frutasselectas.example.com', '3001000032', 'Armenia, Quindio', TRUE),
('Hortalizas del Oriente Colombiano', '901100033-6', 'Roberto Salazar', 'contacto@hortalizasoriente.example.com', '3001000033', 'Malaga, Santander', TRUE),
('Distribuidora Lactea del Norte', '901100034-7', 'Isabel Duarte', 'ventas@lacteanorte.example.com', '3001000034', 'Pamplona, Norte de Santander', TRUE),
('Proteinas del Caribe', '901100035-8', 'David Guerrero', 'contacto@proteinascaribe.example.com', '3001000035', 'Sincelejo, Sucre', TRUE),
('Legumbres y Semillas del Valle', '901100036-9', 'Daniela Fuentes', 'ventas@legumbresvalle.example.com', '3001000036', 'Palmira, Valle del Cauca', TRUE),
('Refrescos Naturales La Montana', '901100037-1', 'Mauricio Leon', 'contacto@refrescosmontana.example.com', '3001000037', 'Fusagasuga, Cundinamarca', TRUE),
('Alimentos Procesados Andinos', '901100038-2', 'Tatiana Reyes', 'ventas@procesadosandinos.example.com', '3001000038', 'Bogota, Cundinamarca', TRUE),
('Harinas y Panificados del Sur', '901100039-3', 'Wilson Rangel', 'contacto@harinassur.example.com', '3001000039', 'Neiva, Huila', TRUE),
('Cosechas del Magdalena Medio', '901100040-4', 'Adriana Arias', 'ventas@cosechasmagdalena.example.com', '3001000040', 'Barrancabermeja, Santander', TRUE),

('Agroexportadora El Paraiso', '901100041-5', 'Nelson Quintero', 'contacto@agroparaiso.example.com', '3001000041', 'Apartado, Antioquia', TRUE),
('Frutas y Citricos La Rivera', '901100042-6', 'Claudia Villamizar', 'ventas@citricosrivera.example.com', '3001000042', 'Rionegro, Santander', TRUE),
('Cultivos Organicos La Primavera', '901100043-7', 'Santiago Beltran', 'contacto@cultivosprimavera.example.com', '3001000043', 'Villa de Leyva, Boyaca', TRUE),
('Lacteos Frescura Andina', '901100044-8', 'Angie Sandoval', 'ventas@frescuraandina.example.com', '3001000044', 'Ubate, Cundinamarca', TRUE),
('Distribuidora Carnica Nacional', '901100045-9', 'Kevin Camargo', 'contacto@carnicanacional.example.com', '3001000045', 'Bello, Antioquia', TRUE),
('Semillas y Granos El Agricultor', '901100046-1', 'Rosa Bautista', 'ventas@granosagricultor.example.com', '3001000046', 'Yopal, Casanare', TRUE),
('Pulpas y Bebidas del Tropico', '901100047-2', 'Edwin Villalobos', 'contacto@bebidastropico.example.com', '3001000047', 'Valledupar, Cesar', TRUE),
('Conservas Alimenticias del Oriente', '901100048-3', 'Lina Calderon', 'ventas@conservasoriente.example.com', '3001000048', 'Ocana, Norte de Santander', TRUE),
('Productos de Panaderia La Espiga', '901100049-4', 'Ivan Escobar', 'contacto@panaderiaespiga.example.com', '3001000049', 'Dosquebradas, Risaralda', TRUE),
('Central de Abastos Frescos', '901100050-5', 'Beatriz Caceres', 'ventas@abastosfrescos.example.com', '3001000050', 'Bucaramanga, Santander', TRUE);

-- ============================================================
-- MODULO 1: INVENTARIOS Y CALIDAD
-- TABLA: productos
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO productos
(nombre, descripcion, id_categoria, unidad_medida, precio_base)
VALUES
-- FRUTAS (1-10)
('Manzana Roja', 'Manzana roja fresca nacional', 1, 'kg', 6500.00),
('Manzana Verde', 'Manzana verde fresca', 1, 'kg', 7200.00),
('Banano', 'Banano fresco de primera calidad', 1, 'kg', 3800.00),
('Naranja Valencia', 'Naranja fresca para consumo y jugos', 1, 'kg', 4200.00),
('Mandarina', 'Mandarina dulce nacional', 1, 'kg', 4500.00),
('Mango Tommy', 'Mango fresco de temporada', 1, 'kg', 5500.00),
('Pina', 'Pina fresca seleccionada', 1, 'unidad', 7500.00),
('Papaya', 'Papaya madura de primera calidad', 1, 'kg', 4800.00),
('Fresa', 'Fresa fresca seleccionada', 1, 'kg', 12000.00),
('Uva Verde', 'Uva verde fresca', 1, 'kg', 14500.00),

-- VERDURAS (11-20)
('Zanahoria', 'Zanahoria fresca nacional', 2, 'kg', 3200.00),
('Tomate Chonto', 'Tomate chonto fresco', 2, 'kg', 4200.00),
('Cebolla Cabezona', 'Cebolla cabezona blanca', 2, 'kg', 3800.00),
('Cebolla Larga', 'Cebolla larga fresca', 2, 'kg', 3500.00),
('Papa Pastusa', 'Papa pastusa seleccionada', 2, 'kg', 3400.00),
('Papa Criolla', 'Papa criolla fresca', 2, 'kg', 5800.00),
('Lechuga Batavia', 'Lechuga batavia fresca', 2, 'unidad', 4200.00),
('Pepino Cohombro', 'Pepino cohombro fresco', 2, 'kg', 3600.00),
('Brocoli', 'Brocoli fresco seleccionado', 2, 'kg', 6500.00),
('Espinaca', 'Espinaca fresca en manojo', 2, 'unidad', 3500.00),

-- LACTEOS (21-26)
('Leche Entera', 'Leche entera pasteurizada de un litro', 3, 'litro', 4800.00),
('Leche Deslactosada', 'Leche deslactosada de un litro', 3, 'litro', 5500.00),
('Queso Campesino', 'Queso campesino fresco', 3, 'kg', 18500.00),
('Queso Mozzarella', 'Queso mozzarella fresco', 3, 'kg', 24500.00),
('Yogur Natural', 'Yogur natural de un litro', 3, 'litro', 8500.00),
('Mantequilla', 'Mantequilla de mesa en presentacion de 250 gramos', 3, 'unidad', 9200.00),

-- CARNES (27-32)
('Pechuga de Pollo', 'Pechuga de pollo fresca sin hueso', 4, 'kg', 18500.00),
('Muslo de Pollo', 'Muslo de pollo fresco', 4, 'kg', 12500.00),
('Carne de Res Molida', 'Carne de res molida fresca', 4, 'kg', 22500.00),
('Lomo de Res', 'Corte de lomo de res fresco', 4, 'kg', 38500.00),
('Lomo de Cerdo', 'Lomo de cerdo fresco', 4, 'kg', 21500.00),
('Costilla de Cerdo', 'Costilla de cerdo fresca', 4, 'kg', 19500.00),

-- GRANOS (33-38)
('Arroz Blanco', 'Arroz blanco de primera calidad', 5, 'kg', 4800.00),
('Frijol Rojo', 'Frijol rojo seco seleccionado', 5, 'kg', 9200.00),
('Lenteja', 'Lenteja seca seleccionada', 5, 'kg', 7500.00),
('Garbanzo', 'Garbanzo seco de primera calidad', 5, 'kg', 8500.00),
('Maiz Amarillo', 'Maiz amarillo seco', 5, 'kg', 4200.00),
('Avena en Hojuelas', 'Avena natural en hojuelas', 5, 'kg', 7800.00),

-- BEBIDAS (39-42)
('Agua Mineral', 'Agua mineral sin gas de 600 ml', 6, 'unidad', 2500.00),
('Jugo de Naranja', 'Jugo de naranja envasado de un litro', 6, 'litro', 6500.00),
('Jugo de Mango', 'Jugo de mango envasado de un litro', 6, 'litro', 6200.00),
('Agua con Gas', 'Agua mineral con gas de 600 ml', 6, 'unidad', 2800.00),

-- ENLATADOS (43-46)
('Atun en Agua', 'Atun enlatado en agua de 170 gramos', 7, 'unidad', 7800.00),
('Atun en Aceite', 'Atun enlatado en aceite de 170 gramos', 7, 'unidad', 8200.00),
('Maiz Dulce Enlatado', 'Maiz dulce en conserva de 300 gramos', 7, 'unidad', 5800.00),
('Arvejas Enlatadas', 'Arvejas en conserva de 300 gramos', 7, 'unidad', 5200.00),

-- PANADERIA (47-50)
('Pan Tajado Blanco', 'Pan tajado blanco de 500 gramos', 8, 'unidad', 7500.00),
('Pan Integral', 'Pan integral tajado de 500 gramos', 8, 'unidad', 8500.00),
('Pan Hamburguesa', 'Paquete de seis panes para hamburguesa', 8, 'unidad', 7200.00),
('Pan Perro Caliente', 'Paquete de seis panes para perro caliente', 8, 'unidad', 6800.00);


-- ============================================================
-- MODULO 3: VENTAS Y FACTURACION
-- TABLA: clientes
-- CANTIDAD: 50 registros de prueba
-- 25 mayoristas y 25 minoristas
-- ============================================================

INSERT INTO clientes
(nombre, documento, email, telefono, direccion, id_tipo)
VALUES

-- CLIENTES MAYORISTAS (1-25)
('Supermercado La Canasta S.A.S.', '901200001-1', 'compras@lacanasta.example.com', '3102000001', 'Carrera 15 # 35-20, Bucaramanga', 1),
('Restaurante El Buen Sabor', '901200002-2', 'compras@buensabor.example.com', '3102000002', 'Calle 45 # 20-15, Bucaramanga', 1),
('Minimercado Los Pinos', '901200003-3', 'ventas@minipinos.example.com', '3102000003', 'Carrera 27 # 10-40, Floridablanca', 1),
('Distribuidora Alimentaria del Norte', '901200004-4', 'contacto@alimentarianorte.example.com', '3102000004', 'Calle 10 # 8-30, Cucuta', 1),
('Restaurante Campestre La Hacienda', '901200005-5', 'compras@campestrehacienda.example.com', '3102000005', 'Via Piedecuesta km 5, Santander', 1),
('Supermercado Economico Santander', '901200006-6', 'ventas@economicosantander.example.com', '3102000006', 'Carrera 33 # 48-12, Bucaramanga', 1),
('Hotel y Restaurante El Mirador', '901200007-7', 'compras@hotelmirador.example.com', '3102000007', 'Calle 12 # 5-18, San Gil', 1),
('Tienda Mayorista El Progreso', '901200008-8', 'contacto@mayoristaprogreso.example.com', '3102000008', 'Carrera 8 # 14-25, Giron', 1),
('Restaurante Sabores Colombianos', '901200009-9', 'compras@saborescolombianos.example.com', '3102000009', 'Calle 60 # 25-35, Bucaramanga', 1),
('Supermercado Familiar Central', '901200010-1', 'ventas@familiarcentral.example.com', '3102000010', 'Carrera 17 # 42-11, Piedecuesta', 1),

('Comercializadora de Alimentos Andina', '901200011-2', 'contacto@alimentosandina.example.com', '3102000011', 'Zona Industrial, Bogota', 1),
('Restaurante La Parrilla Santandereana', '901200012-3', 'compras@parrillasantander.example.com', '3102000012', 'Calle 36 # 21-45, Bucaramanga', 1),
('Supermercado El Ahorro', '901200013-4', 'ventas@superahorro.example.com', '3102000013', 'Carrera 5 # 18-30, Barrancabermeja', 1),
('Distribuciones Comerciales del Valle', '901200014-5', 'compras@comercialvalle.example.com', '3102000014', 'Calle 25 # 9-40, Cali', 1),
('Restaurante La Cocina de Mama', '901200015-6', 'contacto@cocinamama.example.com', '3102000015', 'Carrera 30 # 15-28, Bucaramanga', 1),
('Mercado Campesino La Esperanza', '901200016-7', 'ventas@mercadoesperanza.example.com', '3102000016', 'Calle 8 # 12-33, Lebrija', 1),
('Hotel Colonial Santander', '901200017-8', 'compras@colonialsantander.example.com', '3102000017', 'Carrera 10 # 7-15, Socorro', 1),
('Restaurante Delicias del Oriente', '901200018-9', 'contacto@deliciasoriente.example.com', '3102000018', 'Calle 52 # 32-20, Bucaramanga', 1),
('Supermercado Gran Familia', '901200019-1', 'ventas@granfamilia.example.com', '3102000019', 'Carrera 25 # 40-30, Floridablanca', 1),
('Distribuidora Mercantil del Campo', '901200020-2', 'compras@mercantilcampo.example.com', '3102000020', 'Calle 15 # 8-12, Tunja', 1),

('Restaurante El Fogon Tradicional', '901200021-3', 'contacto@fogontradicional.example.com', '3102000021', 'Carrera 22 # 34-16, Bucaramanga', 1),
('Supermercado La Despensa', '901200022-4', 'ventas@ladespensa.example.com', '3102000022', 'Calle 30 # 16-25, Giron', 1),
('Abastos y Suministros Nacionales', '901200023-5', 'compras@abastosnacionales.example.com', '3102000023', 'Zona Industrial, Medellin', 1),
('Restaurante Gourmet La Casona', '901200024-6', 'contacto@gourmetcasona.example.com', '3102000024', 'Carrera 35 # 48-30, Bucaramanga', 1),
('Supermercado San Rafael', '901200025-7', 'ventas@sanrafael.example.com', '3102000025', 'Calle 18 # 12-42, Piedecuesta', 1),

-- CLIENTES MINORISTAS (26-50)
('Juan Carlos Perez', '1002000026', 'juan.perez@example.com', '3203000026', 'Calle 10 # 15-22, Bucaramanga', 2),
('Maria Fernanda Gomez', '1002000027', 'maria.gomez@example.com', '3203000027', 'Carrera 20 # 30-18, Floridablanca', 2),
('Andres Felipe Rodriguez', '1002000028', 'andres.rodriguez@example.com', '3203000028', 'Calle 25 # 8-14, Giron', 2),
('Laura Sofia Martinez', '1002000029', 'laura.martinez@example.com', '3203000029', 'Carrera 14 # 40-26, Bucaramanga', 2),
('Carlos Alberto Ramirez', '1002000030', 'carlos.ramirez@example.com', '3203000030', 'Calle 18 # 22-30, Piedecuesta', 2),
('Diana Carolina Lopez', '1002000031', 'diana.lopez@example.com', '3203000031', 'Carrera 9 # 12-18, San Gil', 2),
('Felipe Andres Torres', '1002000032', 'felipe.torres@example.com', '3203000032', 'Calle 35 # 17-20, Bucaramanga', 2),
('Paola Andrea Castro', '1002000033', 'paola.castro@example.com', '3203000033', 'Carrera 28 # 45-12, Floridablanca', 2),
('Ricardo Jose Morales', '1002000034', 'ricardo.morales@example.com', '3203000034', 'Calle 12 # 6-24, Giron', 2),
('Natalia Andrea Vargas', '1002000035', 'natalia.vargas@example.com', '3203000035', 'Carrera 16 # 25-30, Bucaramanga', 2),

('Pedro Antonio Rojas', '1002000036', 'pedro.rojas@example.com', '3203000036', 'Calle 7 # 11-15, Lebrija', 2),
('Sandra Milena Diaz', '1002000037', 'sandra.diaz@example.com', '3203000037', 'Carrera 12 # 18-20, Socorro', 2),
('Oscar Javier Gutierrez', '1002000038', 'oscar.gutierrez@example.com', '3203000038', 'Calle 22 # 14-30, Bucaramanga', 2),
('Camila Andrea Moreno', '1002000039', 'camila.moreno@example.com', '3203000039', 'Carrera 5 # 9-18, Piedecuesta', 2),
('Miguel Angel Sanchez', '1002000040', 'miguel.sanchez@example.com', '3203000040', 'Calle 40 # 28-15, Floridablanca', 2),
('Andrea Carolina Silva', '1002000041', 'andrea.silva@example.com', '3203000041', 'Carrera 21 # 38-26, Bucaramanga', 2),
('Daniel Esteban Perez', '1002000042', 'daniel.perez@example.com', '3203000042', 'Calle 16 # 10-35, Giron', 2),
('Valentina Ruiz Herrera', '1002000043', 'valentina.ruiz@example.com', '3203000043', 'Carrera 18 # 25-20, Bucaramanga', 2),
('Sebastian Ortiz Medina', '1002000044', 'sebastian.ortiz@example.com', '3203000044', 'Calle 30 # 12-28, Floridablanca', 2),
('Monica Patricia Jimenez', '1002000045', 'monica.jimenez@example.com', '3203000045', 'Carrera 11 # 7-15, San Gil', 2),

('Alejandro Medina Vargas', '1002000046', 'alejandro.medina@example.com', '3203000046', 'Calle 48 # 25-18, Bucaramanga', 2),
('Juliana Herrera Castro', '1002000047', 'juliana.herrera@example.com', '3203000047', 'Carrera 8 # 15-26, Giron', 2),
('Cristian Vega Ramirez', '1002000048', 'cristian.vega@example.com', '3203000048', 'Calle 20 # 17-30, Piedecuesta', 2),
('Marcela Navarro Diaz', '1002000049', 'marcela.navarro@example.com', '3203000049', 'Carrera 32 # 42-15, Floridablanca', 2),
('Hector Molina Rojas', '1002000050', 'hector.molina@example.com', '3203000050', 'Calle 55 # 18-22, Bucaramanga', 2);



-- ============================================================
-- MODULO 1: INVENTARIOS Y CALIDAD
-- TABLA: lotes_inventario
-- CANTIDAD: 50 registros de prueba
-- ============================================================
-- Estado 1: Disponible
-- Cantidades expresadas segun la unidad de cada producto.
-- Precios ficticios en pesos colombianos (COP).
-- ============================================================

INSERT INTO lotes_inventario
(numero_lote, id_producto, id_proveedor, fecha_vencimiento,
 cantidad_inicial, cantidad, precio_compra, precio_venta,
 pasillo, estante, id_estado)
VALUES

-- FRUTAS (1-10)
('FT-2026-001', 1, 1, '2026-12-15', 100, 100, 4200, 6500, 'A', '01', 1),
('FT-2026-002', 2, 2, '2026-12-16', 80, 80, 4800, 7200, 'A', '02', 1),
('FT-2026-003', 3, 3, '2026-11-25', 120, 120, 2400, 3800, 'A', '03', 1),
('FT-2026-004', 4, 4, '2026-12-10', 150, 150, 2800, 4200, 'A', '04', 1),
('FT-2026-005', 5, 5, '2026-12-12', 90, 90, 3000, 4500, 'A', '05', 1),
('FT-2026-006', 6, 6, '2026-12-14', 75, 75, 3600, 5500, 'A', '06', 1),
('FT-2026-007', 7, 7, '2026-12-18', 60, 60, 5000, 7500, 'A', '07', 1),
('FT-2026-008', 8, 8, '2026-12-20', 85, 85, 3200, 4800, 'A', '08', 1),
('FT-2026-009', 9, 9, '2026-11-20', 50, 50, 8500, 12000, 'A', '09', 1),
('FT-2026-010', 10, 10, '2026-12-22', 70, 70, 10000, 14500, 'A', '10', 1),

-- VERDURAS (11-20)
('FT-2026-011', 11, 11, '2026-12-10', 180, 180, 2100, 3200, 'B', '01', 1),
('FT-2026-012', 12, 12, '2026-11-25', 140, 140, 2800, 4200, 'B', '02', 1),
('FT-2026-013', 13, 13, '2027-01-15', 160, 160, 2500, 3800, 'B', '03', 1),
('FT-2026-014', 14, 14, '2026-11-22', 100, 100, 2200, 3500, 'B', '04', 1),
('FT-2026-015', 15, 15, '2027-01-20', 200, 200, 2200, 3400, 'B', '05', 1),
('FT-2026-016', 16, 16, '2026-12-15', 130, 130, 3800, 5800, 'B', '06', 1),
('FT-2026-017', 17, 17, '2026-11-20', 90, 90, 2700, 4200, 'B', '07', 1),
('FT-2026-018', 18, 18, '2026-12-05', 110, 110, 2400, 3600, 'B', '08', 1),
('FT-2026-019', 19, 19, '2026-11-21', 80, 80, 4200, 6500, 'B', '09', 1),
('FT-2026-020', 20, 20, '2026-11-19', 95, 95, 2200, 3500, 'B', '10', 1),

-- LACTEOS (21-26)
('FT-2026-021', 21, 21, '2026-12-20', 150, 150, 3200, 4800, 'C', '01', 1),
('FT-2026-022', 22, 22, '2026-12-22', 120, 120, 3700, 5500, 'C', '02', 1),
('FT-2026-023', 23, 23, '2026-12-18', 60, 60, 12500, 18500, 'C', '03', 1),
('FT-2026-024', 24, 24, '2026-12-25', 55, 55, 17000, 24500, 'C', '04', 1),
('FT-2026-025', 25, 25, '2026-12-10', 100, 100, 5800, 8500, 'C', '05', 1),
('FT-2026-026', 26, 26, '2027-02-15', 90, 90, 6200, 9200, 'C', '06', 1),

-- CARNES (27-32)
('FT-2026-027', 27, 27, '2026-11-18', 85, 85, 12500, 18500, 'D', '01', 1),
('FT-2026-028', 28, 28, '2026-11-19', 110, 110, 8500, 12500, 'D', '02', 1),
('FT-2026-029', 29, 29, '2026-11-18', 75, 75, 15500, 22500, 'D', '03', 1),
('FT-2026-030', 30, 30, '2026-11-20', 50, 50, 27000, 38500, 'D', '04', 1),
('FT-2026-031', 31, 31, '2026-11-19', 80, 80, 14500, 21500, 'D', '05', 1),
('FT-2026-032', 32, 32, '2026-11-21', 95, 95, 13000, 19500, 'D', '06', 1),

-- GRANOS (33-38)
('FT-2026-033', 33, 33, '2027-10-15', 300, 300, 3200, 4800, 'E', '01', 1),
('FT-2026-034', 34, 34, '2027-09-20', 200, 200, 6200, 9200, 'E', '02', 1),
('FT-2026-035', 35, 35, '2027-08-15', 180, 180, 5000, 7500, 'E', '03', 1),
('FT-2026-036', 36, 36, '2027-09-10', 150, 150, 5700, 8500, 'E', '04', 1),
('FT-2026-037', 37, 37, '2027-07-20', 220, 220, 2800, 4200, 'E', '05', 1),
('FT-2026-038', 38, 38, '2027-10-10', 140, 140, 5200, 7800, 'E', '06', 1),

-- BEBIDAS (39-42)
('FT-2026-039', 39, 39, '2027-08-15', 250, 250, 1600, 2500, 'F', '01', 1),
('FT-2026-040', 40, 40, '2027-03-20', 160, 160, 4300, 6500, 'F', '02', 1),
('FT-2026-041', 41, 41, '2027-03-25', 150, 150, 4100, 6200, 'F', '03', 1),
('FT-2026-042', 42, 42, '2027-08-20', 200, 200, 1800, 2800, 'F', '04', 1),

-- ENLATADOS (43-46)
('FT-2026-043', 43, 43, '2028-05-15', 140, 140, 5200, 7800, 'G', '01', 1),
('FT-2026-044', 44, 44, '2028-06-20', 135, 135, 5500, 8200, 'G', '02', 1),
('FT-2026-045', 45, 45, '2028-04-15', 120, 120, 3900, 5800, 'G', '03', 1),
('FT-2026-046', 46, 46, '2028-05-20', 130, 130, 3500, 5200, 'G', '04', 1),

-- PANADERIA (47-50)
('FT-2026-047', 47, 47, '2026-11-15', 100, 100, 5000, 7500, 'H', '01', 1),
('FT-2026-048', 48, 48, '2026-11-16', 95, 95, 5700, 8500, 'H', '02', 1),
('FT-2026-049', 49, 49, '2026-11-15', 110, 110, 4800, 7200, 'H', '03', 1),
('FT-2026-050', 50, 50, '2026-11-16', 105, 105, 4500, 6800, 'H', '04', 1);


-- ============================================================
-- MODULO 3: VENTAS Y FACTURACION
-- TABLA: ventas
-- CANTIDAD: 50 registros de prueba
-- ============================================================
-- Estado 1: Registrada
-- Descuentos e impuestos: 0.00
-- Usuario de registro: admin
-- Valores expresados en COP
-- ============================================================

INSERT INTO ventas
(numero_venta, id_cliente, subtotal, total_descuento,
 total_impuesto, monto_total, id_estado, usuario_registro)
VALUES

-- VENTAS 1-10
('VTA-FT-2026-001', 1, 13000.00, 0.00, 0.00, 13000.00, 1, 'admin'),
('VTA-FT-2026-002', 2, 14400.00, 0.00, 0.00, 14400.00, 1, 'admin'),
('VTA-FT-2026-003', 3, 7600.00, 0.00, 0.00, 7600.00, 1, 'admin'),
('VTA-FT-2026-004', 4, 8400.00, 0.00, 0.00, 8400.00, 1, 'admin'),
('VTA-FT-2026-005', 5, 9000.00, 0.00, 0.00, 9000.00, 1, 'admin'),
('VTA-FT-2026-006', 6, 11000.00, 0.00, 0.00, 11000.00, 1, 'admin'),
('VTA-FT-2026-007', 7, 15000.00, 0.00, 0.00, 15000.00, 1, 'admin'),
('VTA-FT-2026-008', 8, 9600.00, 0.00, 0.00, 9600.00, 1, 'admin'),
('VTA-FT-2026-009', 9, 24000.00, 0.00, 0.00, 24000.00, 1, 'admin'),
('VTA-FT-2026-010', 10, 29000.00, 0.00, 0.00, 29000.00, 1, 'admin'),

-- VENTAS 11-20
('VTA-FT-2026-011', 11, 6400.00, 0.00, 0.00, 6400.00, 1, 'admin'),
('VTA-FT-2026-012', 12, 8400.00, 0.00, 0.00, 8400.00, 1, 'admin'),
('VTA-FT-2026-013', 13, 7600.00, 0.00, 0.00, 7600.00, 1, 'admin'),
('VTA-FT-2026-014', 14, 7000.00, 0.00, 0.00, 7000.00, 1, 'admin'),
('VTA-FT-2026-015', 15, 6800.00, 0.00, 0.00, 6800.00, 1, 'admin'),
('VTA-FT-2026-016', 16, 11600.00, 0.00, 0.00, 11600.00, 1, 'admin'),
('VTA-FT-2026-017', 17, 8400.00, 0.00, 0.00, 8400.00, 1, 'admin'),
('VTA-FT-2026-018', 18, 7200.00, 0.00, 0.00, 7200.00, 1, 'admin'),
('VTA-FT-2026-019', 19, 13000.00, 0.00, 0.00, 13000.00, 1, 'admin'),
('VTA-FT-2026-020', 20, 7000.00, 0.00, 0.00, 7000.00, 1, 'admin'),

-- VENTAS 21-30
('VTA-FT-2026-021', 21, 9600.00, 0.00, 0.00, 9600.00, 1, 'admin'),
('VTA-FT-2026-022', 22, 11000.00, 0.00, 0.00, 11000.00, 1, 'admin'),
('VTA-FT-2026-023', 23, 37000.00, 0.00, 0.00, 37000.00, 1, 'admin'),
('VTA-FT-2026-024', 24, 49000.00, 0.00, 0.00, 49000.00, 1, 'admin'),
('VTA-FT-2026-025', 25, 17000.00, 0.00, 0.00, 17000.00, 1, 'admin'),
('VTA-FT-2026-026', 26, 18400.00, 0.00, 0.00, 18400.00, 1, 'admin'),
('VTA-FT-2026-027', 27, 37000.00, 0.00, 0.00, 37000.00, 1, 'admin'),
('VTA-FT-2026-028', 28, 25000.00, 0.00, 0.00, 25000.00, 1, 'admin'),
('VTA-FT-2026-029', 29, 45000.00, 0.00, 0.00, 45000.00, 1, 'admin'),
('VTA-FT-2026-030', 30, 77000.00, 0.00, 0.00, 77000.00, 1, 'admin'),

-- VENTAS 31-40
('VTA-FT-2026-031', 31, 43000.00, 0.00, 0.00, 43000.00, 1, 'admin'),
('VTA-FT-2026-032', 32, 39000.00, 0.00, 0.00, 39000.00, 1, 'admin'),
('VTA-FT-2026-033', 33, 9600.00, 0.00, 0.00, 9600.00, 1, 'admin'),
('VTA-FT-2026-034', 34, 18400.00, 0.00, 0.00, 18400.00, 1, 'admin'),
('VTA-FT-2026-035', 35, 15000.00, 0.00, 0.00, 15000.00, 1, 'admin'),
('VTA-FT-2026-036', 36, 17000.00, 0.00, 0.00, 17000.00, 1, 'admin'),
('VTA-FT-2026-037', 37, 8400.00, 0.00, 0.00, 8400.00, 1, 'admin'),
('VTA-FT-2026-038', 38, 15600.00, 0.00, 0.00, 15600.00, 1, 'admin'),
('VTA-FT-2026-039', 39, 5000.00, 0.00, 0.00, 5000.00, 1, 'admin'),
('VTA-FT-2026-040', 40, 13000.00, 0.00, 0.00, 13000.00, 1, 'admin'),

-- VENTAS 41-50
('VTA-FT-2026-041', 41, 12400.00, 0.00, 0.00, 12400.00, 1, 'admin'),
('VTA-FT-2026-042', 42, 5600.00, 0.00, 0.00, 5600.00, 1, 'admin'),
('VTA-FT-2026-043', 43, 15600.00, 0.00, 0.00, 15600.00, 1, 'admin'),
('VTA-FT-2026-044', 44, 16400.00, 0.00, 0.00, 16400.00, 1, 'admin'),
('VTA-FT-2026-045', 45, 11600.00, 0.00, 0.00, 11600.00, 1, 'admin'),
('VTA-FT-2026-046', 46, 10400.00, 0.00, 0.00, 10400.00, 1, 'admin'),
('VTA-FT-2026-047', 47, 15000.00, 0.00, 0.00, 15000.00, 1, 'admin'),
('VTA-FT-2026-048', 48, 17000.00, 0.00, 0.00, 17000.00, 1, 'admin'),
('VTA-FT-2026-049', 49, 14400.00, 0.00, 0.00, 14400.00, 1, 'admin'),
('VTA-FT-2026-050', 50, 13600.00, 0.00, 0.00, 13600.00, 1, 'admin');


-- ============================================================
-- MODULO 3: VENTAS Y FACTURACION
-- TABLA: detalles_venta
-- CANTIDAD: 50 registros de prueba
-- ============================================================
-- Cada venta contiene un producto de un lote.
-- Cantidad vendida: 2
-- Descuento: 0.00
-- Impuesto: 0.00
-- Subtotal = cantidad_vendida * precio_unitario
-- Total linea = subtotal
-- ============================================================

INSERT INTO detalles_venta
(id_venta, id_lote, cantidad_vendida, precio_unitario,
 descuento_aplicado, monto_descuento, tasa_impuesto,
 monto_impuesto, subtotal, total_linea)
VALUES

-- DETALLES 1-10: FRUTAS
(1, 1, 2, 6500.00, 0, 0, 0, 0, 13000.00, 13000.00),
(2, 2, 2, 7200.00, 0, 0, 0, 0, 14400.00, 14400.00),
(3, 3, 2, 3800.00, 0, 0, 0, 0, 7600.00, 7600.00),
(4, 4, 2, 4200.00, 0, 0, 0, 0, 8400.00, 8400.00),
(5, 5, 2, 4500.00, 0, 0, 0, 0, 9000.00, 9000.00),
(6, 6, 2, 5500.00, 0, 0, 0, 0, 11000.00, 11000.00),
(7, 7, 2, 7500.00, 0, 0, 0, 0, 15000.00, 15000.00),
(8, 8, 2, 4800.00, 0, 0, 0, 0, 9600.00, 9600.00),
(9, 9, 2, 12000.00, 0, 0, 0, 0, 24000.00, 24000.00),
(10, 10, 2, 14500.00, 0, 0, 0, 0, 29000.00, 29000.00),

-- DETALLES 11-20: VERDURAS
(11, 11, 2, 3200.00, 0, 0, 0, 0, 6400.00, 6400.00),
(12, 12, 2, 4200.00, 0, 0, 0, 0, 8400.00, 8400.00),
(13, 13, 2, 3800.00, 0, 0, 0, 0, 7600.00, 7600.00),
(14, 14, 2, 3500.00, 0, 0, 0, 0, 7000.00, 7000.00),
(15, 15, 2, 3400.00, 0, 0, 0, 0, 6800.00, 6800.00),
(16, 16, 2, 5800.00, 0, 0, 0, 0, 11600.00, 11600.00),
(17, 17, 2, 4200.00, 0, 0, 0, 0, 8400.00, 8400.00),
(18, 18, 2, 3600.00, 0, 0, 0, 0, 7200.00, 7200.00),
(19, 19, 2, 6500.00, 0, 0, 0, 0, 13000.00, 13000.00),
(20, 20, 2, 3500.00, 0, 0, 0, 0, 7000.00, 7000.00),

-- DETALLES 21-26: LACTEOS
(21, 21, 2, 4800.00, 0, 0, 0, 0, 9600.00, 9600.00),
(22, 22, 2, 5500.00, 0, 0, 0, 0, 11000.00, 11000.00),
(23, 23, 2, 18500.00, 0, 0, 0, 0, 37000.00, 37000.00),
(24, 24, 2, 24500.00, 0, 0, 0, 0, 49000.00, 49000.00),
(25, 25, 2, 8500.00, 0, 0, 0, 0, 17000.00, 17000.00),
(26, 26, 2, 9200.00, 0, 0, 0, 0, 18400.00, 18400.00),

-- DETALLES 27-32: CARNES
(27, 27, 2, 18500.00, 0, 0, 0, 0, 37000.00, 37000.00),
(28, 28, 2, 12500.00, 0, 0, 0, 0, 25000.00, 25000.00),
(29, 29, 2, 22500.00, 0, 0, 0, 0, 45000.00, 45000.00),
(30, 30, 2, 38500.00, 0, 0, 0, 0, 77000.00, 77000.00),
(31, 31, 2, 21500.00, 0, 0, 0, 0, 43000.00, 43000.00),
(32, 32, 2, 19500.00, 0, 0, 0, 0, 39000.00, 39000.00),

-- DETALLES 33-38: GRANOS
(33, 33, 2, 4800.00, 0, 0, 0, 0, 9600.00, 9600.00),
(34, 34, 2, 9200.00, 0, 0, 0, 0, 18400.00, 18400.00),
(35, 35, 2, 7500.00, 0, 0, 0, 0, 15000.00, 15000.00),
(36, 36, 2, 8500.00, 0, 0, 0, 0, 17000.00, 17000.00),
(37, 37, 2, 4200.00, 0, 0, 0, 0, 8400.00, 8400.00),
(38, 38, 2, 7800.00, 0, 0, 0, 0, 15600.00, 15600.00),

-- DETALLES 39-42: BEBIDAS
(39, 39, 2, 2500.00, 0, 0, 0, 0, 5000.00, 5000.00),
(40, 40, 2, 6500.00, 0, 0, 0, 0, 13000.00, 13000.00),
(41, 41, 2, 6200.00, 0, 0, 0, 0, 12400.00, 12400.00),
(42, 42, 2, 2800.00, 0, 0, 0, 0, 5600.00, 5600.00),

-- DETALLES 43-46: ENLATADOS
(43, 43, 2, 7800.00, 0, 0, 0, 0, 15600.00, 15600.00),
(44, 44, 2, 8200.00, 0, 0, 0, 0, 16400.00, 16400.00),
(45, 45, 2, 5800.00, 0, 0, 0, 0, 11600.00, 11600.00),
(46, 46, 2, 5200.00, 0, 0, 0, 0, 10400.00, 10400.00),

-- DETALLES 47-50: PANADERIA
(47, 47, 2, 7500.00, 0, 0, 0, 0, 15000.00, 15000.00),
(48, 48, 2, 8500.00, 0, 0, 0, 0, 17000.00, 17000.00),
(49, 49, 2, 7200.00, 0, 0, 0, 0, 14400.00, 14400.00),
(50, 50, 2, 6800.00, 0, 0, 0, 0, 13600.00, 13600.00);

-- ============================================================
-- MODULO 3: VENTAS Y FACTURACION
-- TABLA: facturas
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO facturas
(id_venta, numero_factura, cufe, enlace_qr,
 subtotal, total_descuento, monto_impuesto, monto_total)
VALUES

(1, 'FAC-FT-2026-001', 'CUFE-SIM-FT-2026-001', 'QR-FT-2026-001', 13000, 0, 0, 13000),
(2, 'FAC-FT-2026-002', 'CUFE-SIM-FT-2026-002', 'QR-FT-2026-002', 14400, 0, 0, 14400),
(3, 'FAC-FT-2026-003', 'CUFE-SIM-FT-2026-003', 'QR-FT-2026-003', 7600, 0, 0, 7600),
(4, 'FAC-FT-2026-004', 'CUFE-SIM-FT-2026-004', 'QR-FT-2026-004', 8400, 0, 0, 8400),
(5, 'FAC-FT-2026-005', 'CUFE-SIM-FT-2026-005', 'QR-FT-2026-005', 9000, 0, 0, 9000),
(6, 'FAC-FT-2026-006', 'CUFE-SIM-FT-2026-006', 'QR-FT-2026-006', 11000, 0, 0, 11000),
(7, 'FAC-FT-2026-007', 'CUFE-SIM-FT-2026-007', 'QR-FT-2026-007', 15000, 0, 0, 15000),
(8, 'FAC-FT-2026-008', 'CUFE-SIM-FT-2026-008', 'QR-FT-2026-008', 9600, 0, 0, 9600),
(9, 'FAC-FT-2026-009', 'CUFE-SIM-FT-2026-009', 'QR-FT-2026-009', 24000, 0, 0, 24000),
(10, 'FAC-FT-2026-010', 'CUFE-SIM-FT-2026-010', 'QR-FT-2026-010', 29000, 0, 0, 29000),

(11, 'FAC-FT-2026-011', 'CUFE-SIM-FT-2026-011', 'QR-FT-2026-011', 6400, 0, 0, 6400),
(12, 'FAC-FT-2026-012', 'CUFE-SIM-FT-2026-012', 'QR-FT-2026-012', 8400, 0, 0, 8400),
(13, 'FAC-FT-2026-013', 'CUFE-SIM-FT-2026-013', 'QR-FT-2026-013', 7600, 0, 0, 7600),
(14, 'FAC-FT-2026-014', 'CUFE-SIM-FT-2026-014', 'QR-FT-2026-014', 7000, 0, 0, 7000),
(15, 'FAC-FT-2026-015', 'CUFE-SIM-FT-2026-015', 'QR-FT-2026-015', 6800, 0, 0, 6800),
(16, 'FAC-FT-2026-016', 'CUFE-SIM-FT-2026-016', 'QR-FT-2026-016', 11600, 0, 0, 11600),
(17, 'FAC-FT-2026-017', 'CUFE-SIM-FT-2026-017', 'QR-FT-2026-017', 8400, 0, 0, 8400),
(18, 'FAC-FT-2026-018', 'CUFE-SIM-FT-2026-018', 'QR-FT-2026-018', 7200, 0, 0, 7200),
(19, 'FAC-FT-2026-019', 'CUFE-SIM-FT-2026-019', 'QR-FT-2026-019', 13000, 0, 0, 13000),
(20, 'FAC-FT-2026-020', 'CUFE-SIM-FT-2026-020', 'QR-FT-2026-020', 7000, 0, 0, 7000),

(21, 'FAC-FT-2026-021', 'CUFE-SIM-FT-2026-021', 'QR-FT-2026-021', 9600, 0, 0, 9600),
(22, 'FAC-FT-2026-022', 'CUFE-SIM-FT-2026-022', 'QR-FT-2026-022', 11000, 0, 0, 11000),
(23, 'FAC-FT-2026-023', 'CUFE-SIM-FT-2026-023', 'QR-FT-2026-023', 37000, 0, 0, 37000),
(24, 'FAC-FT-2026-024', 'CUFE-SIM-FT-2026-024', 'QR-FT-2026-024', 49000, 0, 0, 49000),
(25, 'FAC-FT-2026-025', 'CUFE-SIM-FT-2026-025', 'QR-FT-2026-025', 17000, 0, 0, 17000),
(26, 'FAC-FT-2026-026', 'CUFE-SIM-FT-2026-026', 'QR-FT-2026-026', 18400, 0, 0, 18400),
(27, 'FAC-FT-2026-027', 'CUFE-SIM-FT-2026-027', 'QR-FT-2026-027', 37000, 0, 0, 37000),
(28, 'FAC-FT-2026-028', 'CUFE-SIM-FT-2026-028', 'QR-FT-2026-028', 25000, 0, 0, 25000),
(29, 'FAC-FT-2026-029', 'CUFE-SIM-FT-2026-029', 'QR-FT-2026-029', 45000, 0, 0, 45000),
(30, 'FAC-FT-2026-030', 'CUFE-SIM-FT-2026-030', 'QR-FT-2026-030', 77000, 0, 0, 77000),

(31, 'FAC-FT-2026-031', 'CUFE-SIM-FT-2026-031', 'QR-FT-2026-031', 43000, 0, 0, 43000),
(32, 'FAC-FT-2026-032', 'CUFE-SIM-FT-2026-032', 'QR-FT-2026-032', 39000, 0, 0, 39000),
(33, 'FAC-FT-2026-033', 'CUFE-SIM-FT-2026-033', 'QR-FT-2026-033', 9600, 0, 0, 9600),
(34, 'FAC-FT-2026-034', 'CUFE-SIM-FT-2026-034', 'QR-FT-2026-034', 18400, 0, 0, 18400),
(35, 'FAC-FT-2026-035', 'CUFE-SIM-FT-2026-035', 'QR-FT-2026-035', 15000, 0, 0, 15000),
(36, 'FAC-FT-2026-036', 'CUFE-SIM-FT-2026-036', 'QR-FT-2026-036', 17000, 0, 0, 17000),
(37, 'FAC-FT-2026-037', 'CUFE-SIM-FT-2026-037', 'QR-FT-2026-037', 8400, 0, 0, 8400),
(38, 'FAC-FT-2026-038', 'CUFE-SIM-FT-2026-038', 'QR-FT-2026-038', 15600, 0, 0, 15600),
(39, 'FAC-FT-2026-039', 'CUFE-SIM-FT-2026-039', 'QR-FT-2026-039', 5000, 0, 0, 5000),
(40, 'FAC-FT-2026-040', 'CUFE-SIM-FT-2026-040', 'QR-FT-2026-040', 13000, 0, 0, 13000),

(41, 'FAC-FT-2026-041', 'CUFE-SIM-FT-2026-041', 'QR-FT-2026-041', 12400, 0, 0, 12400),
(42, 'FAC-FT-2026-042', 'CUFE-SIM-FT-2026-042', 'QR-FT-2026-042', 5600, 0, 0, 5600),
(43, 'FAC-FT-2026-043', 'CUFE-SIM-FT-2026-043', 'QR-FT-2026-043', 15600, 0, 0, 15600),
(44, 'FAC-FT-2026-044', 'CUFE-SIM-FT-2026-044', 'QR-FT-2026-044', 16400, 0, 0, 16400),
(45, 'FAC-FT-2026-045', 'CUFE-SIM-FT-2026-045', 'QR-FT-2026-045', 11600, 0, 0, 11600),
(46, 'FAC-FT-2026-046', 'CUFE-SIM-FT-2026-046', 'QR-FT-2026-046', 10400, 0, 0, 10400),
(47, 'FAC-FT-2026-047', 'CUFE-SIM-FT-2026-047', 'QR-FT-2026-047', 15000, 0, 0, 15000),
(48, 'FAC-FT-2026-048', 'CUFE-SIM-FT-2026-048', 'QR-FT-2026-048', 17000, 0, 0, 17000),
(49, 'FAC-FT-2026-049', 'CUFE-SIM-FT-2026-049', 'QR-FT-2026-049', 14400, 0, 0, 14400),
(50, 'FAC-FT-2026-050', 'CUFE-SIM-FT-2026-050', 'QR-FT-2026-050', 13600, 0, 0, 13600);

-- ============================================================
-- MODULO 2: PROVEEDORES Y COMPRAS
-- TABLA: productos_proveedor
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO productos_proveedor (id_proveedor, id_producto)
VALUES

-- FRUTAS
(1, 1),
(12, 2),
(22, 3),
(32, 4),
(42, 5),
(1, 6),
(12, 7),
(22, 8),
(32, 9),
(42, 10),

-- VERDURAS
(3, 11),
(13, 12),
(23, 13),
(33, 14),
(43, 15),
(3, 16),
(13, 17),
(23, 18),
(33, 19),
(43, 20),

-- LACTEOS
(4, 21),
(14, 22),
(24, 23),
(34, 24),
(44, 25),
(4, 26),

-- CARNES
(5, 27),
(15, 28),
(25, 29),
(35, 30),
(45, 31),
(5, 32),

-- GRANOS
(6, 33),
(16, 34),
(26, 35),
(36, 36),
(46, 37),
(6, 38),

-- BEBIDAS
(7, 39),
(17, 40),
(27, 41),
(37, 42),

-- ENLATADOS
(8, 43),
(18, 44),
(28, 45),
(38, 46),

-- PANADERIA
(9, 47),
(19, 48),
(39, 49),
(49, 50);


-- ============================================================
-- MODULO 4: LOGISTICA Y ENTREGAS
-- TABLA: conductores
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO conductores
(nombre, numero_licencia, telefono, email, activo)
VALUES

('Carlos Alberto Ramirez', 'LIC-FT-2026-001', '3104000001', 'carlos.ramirez@example.com', TRUE),
('Juan David Martinez', 'LIC-FT-2026-002', '3104000002', 'juan.martinez@example.com', TRUE),
('Andres Felipe Gomez', 'LIC-FT-2026-003', '3104000003', 'andres.gomez@example.com', TRUE),
('Miguel Angel Torres', 'LIC-FT-2026-004', '3104000004', 'miguel.torres@example.com', TRUE),
('Jorge Enrique Rodriguez', 'LIC-FT-2026-005', '3104000005', 'jorge.rodriguez@example.com', TRUE),
('Luis Fernando Castro', 'LIC-FT-2026-006', '3104000006', 'luis.castro@example.com', TRUE),
('Pedro Antonio Vargas', 'LIC-FT-2026-007', '3104000007', 'pedro.vargas@example.com', TRUE),
('Ricardo Jose Morales', 'LIC-FT-2026-008', '3104000008', 'ricardo.morales@example.com', TRUE),
('Sebastian Andres Ortiz', 'LIC-FT-2026-009', '3104000009', 'sebastian.ortiz@example.com', TRUE),
('Daniel Esteban Perez', 'LIC-FT-2026-010', '3104000010', 'daniel.perez@example.com', TRUE),

('Oscar Javier Gutierrez', 'LIC-FT-2026-011', '3104000011', 'oscar.gutierrez@example.com', TRUE),
('Felipe Andres Rojas', 'LIC-FT-2026-012', '3104000012', 'felipe.rojas@example.com', TRUE),
('Alejandro Medina', 'LIC-FT-2026-013', '3104000013', 'alejandro.medina@example.com', TRUE),
('Cristian David Vega', 'LIC-FT-2026-014', '3104000014', 'cristian.vega@example.com', TRUE),
('Hector Fabio Molina', 'LIC-FT-2026-015', '3104000015', 'hector.molina@example.com', TRUE),
('Mauricio Leon', 'LIC-FT-2026-016', '3104000016', 'mauricio.leon@example.com', TRUE),
('Fernando Pineda', 'LIC-FT-2026-017', '3104000017', 'fernando.pineda@example.com', TRUE),
('Roberto Salazar', 'LIC-FT-2026-018', '3104000018', 'roberto.salazar@example.com', TRUE),
('David Guerrero', 'LIC-FT-2026-019', '3104000019', 'david.guerrero@example.com', TRUE),
('Wilson Rangel', 'LIC-FT-2026-020', '3104000020', 'wilson.rangel@example.com', TRUE),

('Santiago Beltran', 'LIC-FT-2026-021', '3104000021', 'santiago.beltran@example.com', TRUE),
('Kevin Camargo', 'LIC-FT-2026-022', '3104000022', 'kevin.camargo@example.com', TRUE),
('Edwin Villalobos', 'LIC-FT-2026-023', '3104000023', 'edwin.villalobos@example.com', TRUE),
('Ivan Escobar', 'LIC-FT-2026-024', '3104000024', 'ivan.escobar@example.com', TRUE),
('Nelson Quintero', 'LIC-FT-2026-025', '3104000025', 'nelson.quintero@example.com', TRUE),
('Esteban Castillo', 'LIC-FT-2026-026', '3104000026', 'esteban.castillo@example.com', TRUE),
('Javier Alonso Duarte', 'LIC-FT-2026-027', '3104000027', 'javier.duarte@example.com', TRUE),
('Camilo Andres Silva', 'LIC-FT-2026-028', '3104000028', 'camilo.silva@example.com', TRUE),
('Diego Fernando Navarro', 'LIC-FT-2026-029', '3104000029', 'diego.navarro@example.com', TRUE),
('Julian Alberto Herrera', 'LIC-FT-2026-030', '3104000030', 'julian.herrera@example.com', TRUE),

('Mario Alberto Suarez', 'LIC-FT-2026-031', '3104000031', 'mario.suarez@example.com', TRUE),
('Victor Manuel Cardenas', 'LIC-FT-2026-032', '3104000032', 'victor.cardenas@example.com', TRUE),
('Rafael Antonio Mendoza', 'LIC-FT-2026-033', '3104000033', 'rafael.mendoza@example.com', TRUE),
('Jose Orlando Arias', 'LIC-FT-2026-034', '3104000034', 'jose.arias@example.com', TRUE),
('Alberto Enrique Fuentes', 'LIC-FT-2026-035', '3104000035', 'alberto.fuentes@example.com', TRUE),
('Nicolas Andres Pardo', 'LIC-FT-2026-036', '3104000036', 'nicolas.pardo@example.com', TRUE),
('Fabian Eduardo Moreno', 'LIC-FT-2026-037', '3104000037', 'fabian.moreno@example.com', TRUE),
('German Augusto Lopez', 'LIC-FT-2026-038', '3104000038', 'german.lopez@example.com', TRUE),
('Henry Alexander Diaz', 'LIC-FT-2026-039', '3104000039', 'henry.diaz@example.com', TRUE),
('Leonardo Andres Acosta', 'LIC-FT-2026-040', '3104000040', 'leonardo.acosta@example.com', TRUE),

('Freddy Alberto Bautista', 'LIC-FT-2026-041', '3104000041', 'freddy.bautista@example.com', TRUE),
('Orlando Javier Sandoval', 'LIC-FT-2026-042', '3104000042', 'orlando.sandoval@example.com', TRUE),
('Jaime Enrique Rueda', 'LIC-FT-2026-043', '3104000043', 'jaime.rueda@example.com', TRUE),
('Francisco Javier Serrano', 'LIC-FT-2026-044', '3104000044', 'francisco.serrano@example.com', TRUE),
('Manuel Antonio Caceres', 'LIC-FT-2026-045', '3104000045', 'manuel.caceres@example.com', TRUE),
('William Andres Sanchez', 'LIC-FT-2026-046', '3104000046', 'william.sanchez@example.com', TRUE),
('Alexander Jose Romero', 'LIC-FT-2026-047', '3104000047', 'alexander.romero@example.com', TRUE),
('Cesar Augusto Jimenez', 'LIC-FT-2026-048', '3104000048', 'cesar.jimenez@example.com', FALSE),
('Eduardo Felipe Molina', 'LIC-FT-2026-049', '3104000049', 'eduardo.molina@example.com', FALSE),
('Alfonso Enrique Rojas', 'LIC-FT-2026-050', '3104000050', 'alfonso.rojas@example.com', FALSE);


-- ============================================================
-- MODULO 4: LOGISTICA Y ENTREGAS
-- TABLA: vehiculos
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO vehiculos
(placa, modelo, capacidad, estado)
VALUES

-- VEHICULOS 1-10
('FTX101', 'Chevrolet NHR 2022', 2500.00, 'Activo'),
('FTX102', 'Chevrolet NKR 2023', 3500.00, 'Activo'),
('FTX103', 'Chevrolet NPR 2021', 4500.00, 'Activo'),
('FTX104', 'JAC HFC 2022', 3000.00, 'Activo'),
('FTX105', 'JAC HFC 2024', 3500.00, 'Activo'),
('FTX106', 'Hyundai HD65 2020', 3000.00, 'Activo'),
('FTX107', 'Hyundai HD78 2022', 4500.00, 'Activo'),
('FTX108', 'Foton Aumark 2023', 4000.00, 'Activo'),
('FTX109', 'Foton BJ 2021', 3000.00, 'Activo'),
('FTX110', 'Hino 300 2024', 5000.00, 'Activo'),

-- VEHICULOS 11-20
('FTX111', 'Hino 300 2022', 4500.00, 'Activo'),
('FTX112', 'Hino 500 2023', 7500.00, 'Activo'),
('FTX113', 'Isuzu NQR 2021', 5000.00, 'Activo'),
('FTX114', 'Isuzu NPR 2022', 4500.00, 'Activo'),
('FTX115', 'Mitsubishi Fuso Canter 2024', 3500.00, 'Activo'),
('FTX116', 'Mitsubishi Fuso Canter 2022', 4000.00, 'Activo'),
('FTX117', 'Mercedes-Benz Atego 2023', 8000.00, 'Activo'),
('FTX118', 'Mercedes-Benz Accelo 2021', 6000.00, 'Activo'),
('FTX119', 'Volkswagen Delivery 2022', 5000.00, 'Activo'),
('FTX120', 'Volkswagen Worker 2020', 7000.00, 'Activo'),

-- VEHICULOS 21-30
('FTX121', 'Chevrolet NHR 2024', 2500.00, 'Activo'),
('FTX122', 'Chevrolet NKR 2021', 3500.00, 'Activo'),
('FTX123', 'JAC X200 2023', 1800.00, 'Activo'),
('FTX124', 'JAC X200 2024', 1800.00, 'Activo'),
('FTX125', 'Foton Aumark S 2023', 4500.00, 'Activo'),
('FTX126', 'Hyundai Mighty 2022', 4000.00, 'Activo'),
('FTX127', 'Hyundai Mighty 2024', 4500.00, 'Activo'),
('FTX128', 'Hino 300 2021', 5000.00, 'Activo'),
('FTX129', 'Isuzu NQR 2024', 5500.00, 'Activo'),
('FTX130', 'Mitsubishi Fuso Canter 2023', 3500.00, 'Activo'),

-- VEHICULOS 31-40
('FTX131', 'Mercedes-Benz Atego 2022', 7500.00, 'Activo'),
('FTX132', 'Volkswagen Delivery 2023', 5000.00, 'Activo'),
('FTX133', 'Chevrolet NPR 2024', 4500.00, 'Activo'),
('FTX134', 'JAC HFC 2021', 3000.00, 'Activo'),
('FTX135', 'Foton BJ 2024', 3500.00, 'Activo'),
('FTX136', 'Hino 500 2022', 8000.00, 'Activo'),
('FTX137', 'Isuzu NPR 2023', 4500.00, 'Activo'),
('FTX138', 'Hyundai HD78 2021', 4500.00, 'Activo'),
('FTX139', 'Chevrolet NKR 2020', 3500.00, 'Activo'),
('FTX140', 'Volkswagen Worker 2022', 7000.00, 'Activo'),

-- VEHICULOS 41-50
('FTX141', 'Chevrolet NHR 2020', 2500.00, 'Activo'),
('FTX142', 'JAC X200 2022', 1800.00, 'Activo'),
('FTX143', 'Foton Aumark 2021', 4000.00, 'Activo'),
('FTX144', 'Hino 300 2020', 4500.00, 'Activo'),
('FTX145', 'Hyundai Mighty 2021', 4000.00, 'Activo'),
('FTX146', 'Isuzu NQR 2020', 5000.00, 'Activo'),
('FTX147', 'Mercedes-Benz Accelo 2020', 6000.00, 'Mantenimiento'),
('FTX148', 'Chevrolet NPR 2019', 4500.00, 'Mantenimiento'),
('FTX149', 'JAC HFC 2019', 3000.00, 'Inactivo'),
('FTX150', 'Foton BJ 2020', 3000.00, 'Inactivo');


-- ============================================================
-- MODULO 4: LOGISTICA Y ENTREGAS
-- TABLA: rutas_entrega
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO rutas_entrega
(nombre_ruta, usuario_creacion, hora_salida_estimada,
 hora_llegada_estimada, hora_salida_real, hora_llegada_real, estado)
VALUES

-- RUTAS 1-10
('Ruta Bucaramanga Centro 001', 'admin', '2026-11-05 07:00:00', '2026-11-05 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca 002', 'admin', '2026-11-05 08:00:00', '2026-11-05 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron 003', 'admin', '2026-11-05 09:00:00', '2026-11-05 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta 004', 'admin', '2026-11-05 10:00:00', '2026-11-05 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Lebrija 005', 'admin', '2026-11-05 11:00:00', '2026-11-05 14:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Norte 006', 'admin', '2026-11-06 07:00:00', '2026-11-06 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Sur 007', 'admin', '2026-11-06 08:00:00', '2026-11-06 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca Canaveral 008', 'admin', '2026-11-06 09:00:00', '2026-11-06 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron Industrial 009', 'admin', '2026-11-06 10:00:00', '2026-11-06 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta Centro 010', 'admin', '2026-11-06 11:00:00', '2026-11-06 14:00:00', NULL, NULL, 'Planificada'),

-- RUTAS 11-20
('Ruta San Gil 011', 'admin', '2026-11-07 06:00:00', '2026-11-07 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Socorro 012', 'admin', '2026-11-07 07:00:00', '2026-11-07 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Barbosa 013', 'admin', '2026-11-07 06:00:00', '2026-11-07 14:00:00', NULL, NULL, 'Planificada'),
('Ruta Barrancabermeja 014', 'admin', '2026-11-07 05:00:00', '2026-11-07 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Malaga 015', 'admin', '2026-11-07 05:00:00', '2026-11-07 15:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Cabecera 016', 'admin', '2026-11-08 07:00:00', '2026-11-08 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Provenza 017', 'admin', '2026-11-08 08:00:00', '2026-11-08 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca Lagos 018', 'admin', '2026-11-08 09:00:00', '2026-11-08 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron Centro 019', 'admin', '2026-11-08 10:00:00', '2026-11-08 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta Barroblanco 020', 'admin', '2026-11-08 11:00:00', '2026-11-08 14:00:00', NULL, NULL, 'Planificada'),

-- RUTAS 21-30
('Ruta Bucaramanga Occidental 021', 'admin', '2026-11-09 07:00:00', '2026-11-09 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Oriental 022', 'admin', '2026-11-09 08:00:00', '2026-11-09 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca La Cumbre 023', 'admin', '2026-11-09 09:00:00', '2026-11-09 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron El Poblado 024', 'admin', '2026-11-09 10:00:00', '2026-11-09 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta La Rioja 025', 'admin', '2026-11-09 11:00:00', '2026-11-09 14:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Universitaria 026', 'admin', '2026-11-10 07:00:00', '2026-11-10 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Ciudadela 027', 'admin', '2026-11-10 08:00:00', '2026-11-10 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca Ruitoque 028', 'admin', '2026-11-10 09:00:00', '2026-11-10 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron Rincon 029', 'admin', '2026-11-10 10:00:00', '2026-11-10 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta San Francisco 030', 'admin', '2026-11-10 11:00:00', '2026-11-10 14:00:00', NULL, NULL, 'Planificada'),

-- RUTAS 31-40
('Ruta Bucaramanga Centro 031', 'admin', '2026-11-11 07:00:00', '2026-11-11 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca 032', 'admin', '2026-11-11 08:00:00', '2026-11-11 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron 033', 'admin', '2026-11-11 09:00:00', '2026-11-11 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta 034', 'admin', '2026-11-11 10:00:00', '2026-11-11 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Lebrija 035', 'admin', '2026-11-11 11:00:00', '2026-11-11 14:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Norte 036', 'admin', '2026-11-12 07:00:00', '2026-11-12 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Sur 037', 'admin', '2026-11-12 08:00:00', '2026-11-12 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca 038', 'admin', '2026-11-12 09:00:00', '2026-11-12 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron Industrial 039', 'admin', '2026-11-12 10:00:00', '2026-11-12 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta Centro 040', 'admin', '2026-11-12 11:00:00', '2026-11-12 14:00:00', NULL, NULL, 'Planificada'),

-- RUTAS 41-50
('Ruta San Gil 041', 'admin', '2026-11-13 06:00:00', '2026-11-13 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Socorro 042', 'admin', '2026-11-13 07:00:00', '2026-11-13 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Barbosa 043', 'admin', '2026-11-13 06:00:00', '2026-11-13 14:00:00', NULL, NULL, 'Planificada'),
('Ruta Barrancabermeja 044', 'admin', '2026-11-13 05:00:00', '2026-11-13 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Malaga 045', 'admin', '2026-11-13 05:00:00', '2026-11-13 15:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Cabecera 046', 'admin', '2026-11-14 07:00:00', '2026-11-14 10:00:00', NULL, NULL, 'Planificada'),
('Ruta Bucaramanga Provenza 047', 'admin', '2026-11-14 08:00:00', '2026-11-14 11:00:00', NULL, NULL, 'Planificada'),
('Ruta Floridablanca Lagos 048', 'admin', '2026-11-14 09:00:00', '2026-11-14 12:00:00', NULL, NULL, 'Planificada'),
('Ruta Giron Centro 049', 'admin', '2026-11-14 10:00:00', '2026-11-14 13:00:00', NULL, NULL, 'Planificada'),
('Ruta Piedecuesta Barroblanco 050', 'admin', '2026-11-14 11:00:00', '2026-11-14 14:00:00', NULL, NULL, 'Planificada');


-- ============================================================
-- MODULO 4: LOGISTICA Y ENTREGAS
-- TABLA: asignaciones_entrega
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO asignaciones_entrega
(id_ruta, id_vehiculo, id_conductor, usuario_asignacion, notas)
VALUES

-- ASIGNACIONES 1-10
(1, 1, 1, 'admin', 'Entrega programada en Bucaramanga Centro'),
(2, 2, 2, 'admin', 'Entrega programada en Floridablanca'),
(3, 3, 3, 'admin', 'Entrega programada en Giron'),
(4, 4, 4, 'admin', 'Entrega programada en Piedecuesta'),
(5, 5, 5, 'admin', 'Entrega programada en Lebrija'),
(6, 6, 6, 'admin', 'Entrega programada en Bucaramanga Norte'),
(7, 7, 7, 'admin', 'Entrega programada en Bucaramanga Sur'),
(8, 8, 8, 'admin', 'Entrega programada en Canaveral'),
(9, 9, 9, 'admin', 'Entrega programada en Giron Industrial'),
(10, 10, 10, 'admin', 'Entrega programada en Piedecuesta Centro'),

-- ASIGNACIONES 11-20
(11, 11, 11, 'admin', 'Entrega programada en San Gil'),
(12, 12, 12, 'admin', 'Entrega programada en Socorro'),
(13, 13, 13, 'admin', 'Entrega programada en Barbosa'),
(14, 14, 14, 'admin', 'Entrega programada en Barrancabermeja'),
(15, 15, 15, 'admin', 'Entrega programada en Malaga'),
(16, 16, 16, 'admin', 'Entrega programada en Cabecera'),
(17, 17, 17, 'admin', 'Entrega programada en Provenza'),
(18, 18, 18, 'admin', 'Entrega programada en Lagos'),
(19, 19, 19, 'admin', 'Entrega programada en Giron Centro'),
(20, 20, 20, 'admin', 'Entrega programada en Barroblanco'),

-- ASIGNACIONES 21-30
(21, 21, 21, 'admin', 'Entrega programada en Bucaramanga Occidental'),
(22, 22, 22, 'admin', 'Entrega programada en Bucaramanga Oriental'),
(23, 23, 23, 'admin', 'Entrega programada en La Cumbre'),
(24, 24, 24, 'admin', 'Entrega programada en El Poblado'),
(25, 25, 25, 'admin', 'Entrega programada en La Rioja'),
(26, 26, 26, 'admin', 'Entrega programada en zona Universitaria'),
(27, 27, 27, 'admin', 'Entrega programada en Ciudadela'),
(28, 28, 28, 'admin', 'Entrega programada en Ruitoque'),
(29, 29, 29, 'admin', 'Entrega programada en Giron Rincon'),
(30, 30, 30, 'admin', 'Entrega programada en San Francisco'),

-- ASIGNACIONES 31-40
(31, 31, 31, 'admin', 'Segunda jornada de entregas en Bucaramanga Centro'),
(32, 32, 32, 'admin', 'Segunda jornada de entregas en Floridablanca'),
(33, 33, 33, 'admin', 'Segunda jornada de entregas en Giron'),
(34, 34, 34, 'admin', 'Segunda jornada de entregas en Piedecuesta'),
(35, 35, 35, 'admin', 'Segunda jornada de entregas en Lebrija'),
(36, 36, 36, 'admin', 'Segunda jornada de entregas en Bucaramanga Norte'),
(37, 37, 37, 'admin', 'Segunda jornada de entregas en Bucaramanga Sur'),
(38, 38, 38, 'admin', 'Segunda jornada de entregas en Floridablanca'),
(39, 39, 39, 'admin', 'Segunda jornada de entregas en Giron Industrial'),
(40, 40, 40, 'admin', 'Segunda jornada de entregas en Piedecuesta Centro'),

-- ASIGNACIONES 41-50
(41, 41, 41, 'admin', 'Segunda jornada de entregas en San Gil'),
(42, 42, 42, 'admin', 'Segunda jornada de entregas en Socorro'),
(43, 43, 43, 'admin', 'Segunda jornada de entregas en Barbosa'),
(44, 44, 44, 'admin', 'Segunda jornada de entregas en Barrancabermeja'),
(45, 45, 45, 'admin', 'Segunda jornada de entregas en Malaga'),
(46, 46, 46, 'admin', 'Segunda jornada de entregas en Cabecera'),
(47, 1, 1, 'admin', 'Nueva asignacion del vehiculo y conductor para Provenza'),
(48, 2, 2, 'admin', 'Nueva asignacion del vehiculo y conductor para Lagos'),
(49, 3, 3, 'admin', 'Nueva asignacion del vehiculo y conductor para Giron Centro'),
(50, 4, 4, 'admin', 'Nueva asignacion del vehiculo y conductor para Barroblanco');


-- ============================================================
-- MODULO 4: LOGISTICA Y ENTREGAS
-- TABLA: ventas_ruta
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO ventas_ruta
(id_ruta, id_venta, orden_entrega, direccion_destino,
 fecha_entrega, recibido_por, observaciones)
VALUES

-- ENTREGAS 1-10
(1, 1, 1, 'Carrera 15 # 35-20, Bucaramanga', NULL, NULL, 'Entrega programada'),
(2, 2, 1, 'Calle 45 # 20-15, Bucaramanga', NULL, NULL, 'Entrega programada'),
(3, 3, 1, 'Carrera 27 # 10-40, Floridablanca', NULL, NULL, 'Entrega programada'),
(4, 4, 1, 'Calle 10 # 8-30, Cucuta', NULL, NULL, 'Entrega programada'),
(5, 5, 1, 'Via Piedecuesta km 5, Santander', NULL, NULL, 'Entrega programada'),
(6, 6, 1, 'Carrera 33 # 48-12, Bucaramanga', NULL, NULL, 'Entrega programada'),
(7, 7, 1, 'Calle 12 # 5-18, San Gil', NULL, NULL, 'Entrega programada'),
(8, 8, 1, 'Carrera 8 # 14-25, Giron', NULL, NULL, 'Entrega programada'),
(9, 9, 1, 'Calle 60 # 25-35, Bucaramanga', NULL, NULL, 'Entrega programada'),
(10, 10, 1, 'Carrera 17 # 42-11, Piedecuesta', NULL, NULL, 'Entrega programada'),

-- ENTREGAS 11-20
(11, 11, 1, 'Zona Industrial, Bogota', NULL, NULL, 'Entrega programada'),
(12, 12, 1, 'Calle 36 # 21-45, Bucaramanga', NULL, NULL, 'Entrega programada'),
(13, 13, 1, 'Carrera 5 # 18-30, Barrancabermeja', NULL, NULL, 'Entrega programada'),
(14, 14, 1, 'Calle 25 # 9-40, Cali', NULL, NULL, 'Entrega programada'),
(15, 15, 1, 'Carrera 30 # 15-28, Bucaramanga', NULL, NULL, 'Entrega programada'),
(16, 16, 1, 'Calle 8 # 12-33, Lebrija', NULL, NULL, 'Entrega programada'),
(17, 17, 1, 'Carrera 10 # 7-15, Socorro', NULL, NULL, 'Entrega programada'),
(18, 18, 1, 'Calle 52 # 32-20, Bucaramanga', NULL, NULL, 'Entrega programada'),
(19, 19, 1, 'Carrera 25 # 40-30, Floridablanca', NULL, NULL, 'Entrega programada'),
(20, 20, 1, 'Calle 15 # 8-12, Tunja', NULL, NULL, 'Entrega programada'),

-- ENTREGAS 21-30
(21, 21, 1, 'Carrera 22 # 34-16, Bucaramanga', NULL, NULL, 'Entrega programada'),
(22, 22, 1, 'Calle 30 # 16-25, Giron', NULL, NULL, 'Entrega programada'),
(23, 23, 1, 'Zona Industrial, Medellin', NULL, NULL, 'Entrega programada'),
(24, 24, 1, 'Carrera 35 # 48-30, Bucaramanga', NULL, NULL, 'Entrega programada'),
(25, 25, 1, 'Calle 18 # 12-42, Piedecuesta', NULL, NULL, 'Entrega programada'),
(26, 26, 1, 'Calle 10 # 15-22, Bucaramanga', NULL, NULL, 'Entrega programada'),
(27, 27, 1, 'Carrera 20 # 30-18, Floridablanca', NULL, NULL, 'Entrega programada'),
(28, 28, 1, 'Calle 25 # 8-14, Giron', NULL, NULL, 'Entrega programada'),
(29, 29, 1, 'Carrera 14 # 40-26, Bucaramanga', NULL, NULL, 'Entrega programada'),
(30, 30, 1, 'Calle 18 # 22-30, Piedecuesta', NULL, NULL, 'Entrega programada'),

-- ENTREGAS 31-40
(31, 31, 1, 'Carrera 9 # 12-18, San Gil', NULL, NULL, 'Entrega programada'),
(32, 32, 1, 'Calle 35 # 17-20, Bucaramanga', NULL, NULL, 'Entrega programada'),
(33, 33, 1, 'Carrera 28 # 45-12, Floridablanca', NULL, NULL, 'Entrega programada'),
(34, 34, 1, 'Calle 12 # 6-24, Giron', NULL, NULL, 'Entrega programada'),
(35, 35, 1, 'Carrera 16 # 25-30, Bucaramanga', NULL, NULL, 'Entrega programada'),
(36, 36, 1, 'Calle 7 # 11-15, Lebrija', NULL, NULL, 'Entrega programada'),
(37, 37, 1, 'Carrera 12 # 18-20, Socorro', NULL, NULL, 'Entrega programada'),
(38, 38, 1, 'Calle 22 # 14-30, Bucaramanga', NULL, NULL, 'Entrega programada'),
(39, 39, 1, 'Carrera 5 # 9-18, Piedecuesta', NULL, NULL, 'Entrega programada'),
(40, 40, 1, 'Calle 40 # 28-15, Floridablanca', NULL, NULL, 'Entrega programada'),

-- ENTREGAS 41-50
(41, 41, 1, 'Carrera 21 # 38-26, Bucaramanga', NULL, NULL, 'Entrega programada'),
(42, 42, 1, 'Calle 16 # 10-35, Giron', NULL, NULL, 'Entrega programada'),
(43, 43, 1, 'Carrera 18 # 25-20, Bucaramanga', NULL, NULL, 'Entrega programada'),
(44, 44, 1, 'Calle 30 # 12-28, Floridablanca', NULL, NULL, 'Entrega programada'),
(45, 45, 1, 'Carrera 11 # 7-15, San Gil', NULL, NULL, 'Entrega programada'),
(46, 46, 1, 'Calle 48 # 25-18, Bucaramanga', NULL, NULL, 'Entrega programada'),
(47, 47, 1, 'Carrera 8 # 15-26, Giron', NULL, NULL, 'Entrega programada'),
(48, 48, 1, 'Calle 20 # 17-30, Piedecuesta', NULL, NULL, 'Entrega programada'),
(49, 49, 1, 'Carrera 32 # 42-15, Floridablanca', NULL, NULL, 'Entrega programada'),
(50, 50, 1, 'Calle 55 # 18-22, Bucaramanga', NULL, NULL, 'Entrega programada');


-- ============================================================
-- MODULO 3: VENTAS Y FACTURACION
-- TABLA: promociones
-- CANTIDAD: 50 registros de prueba
-- ============================================================

INSERT INTO promociones
(nombre, descripcion, tasa_descuento, aplica_estado_lote,
 aplica_categoria_producto, activa, fecha_inicio, fecha_fin)
VALUES

-- PROMOCIONES 1-10: FRUTAS
('Descuento Manzanas', 'Promocion de frutas frescas', 0.10, 1, 1, TRUE, '2026-11-01', '2026-11-30'),
('Frutas de Temporada', 'Descuento en frutas seleccionadas', 0.15, 1, 1, TRUE, '2026-11-01', '2026-12-15'),
('Frutas Proximas a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 1, TRUE, '2026-11-01', '2026-12-31'),
('Festival de Frutas', 'Promocion especial de frutas', 0.20, 1, 1, TRUE, '2026-11-10', '2026-11-20'),
('Frutas Frescura Total', 'Oferta de frutas nacionales', 0.12, 1, 1, TRUE, '2026-11-01', '2026-11-25'),
('Frutas Fin de Semana', 'Descuento especial en frutas', 0.18, 1, 1, TRUE, '2026-11-07', '2026-11-15'),
('Frutas Mayoristas', 'Promocion comercial para frutas', 0.08, 1, 1, TRUE, '2026-11-01', '2026-12-31'),
('Frutas de Diciembre', 'Descuento de temporada navidena', 0.15, 1, 1, TRUE, '2026-12-01', '2026-12-31'),
('Liquidacion Frutas', 'Oferta de frutas proximas al vencimiento', 0.40, 2, 1, TRUE, '2026-11-01', '2026-12-31'),
('Promocion Frutas Especial', 'Oferta especial de frutas', 0.10, 1, 1, FALSE, '2026-11-01', '2026-11-30'),

-- PROMOCIONES 11-20: VERDURAS
('Verduras Frescas', 'Descuento en verduras seleccionadas', 0.10, 1, 2, TRUE, '2026-11-01', '2026-11-30'),
('Verduras de Temporada', 'Oferta de verduras nacionales', 0.15, 1, 2, TRUE, '2026-11-01', '2026-12-15'),
('Verduras Proximas a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 2, TRUE, '2026-11-01', '2026-12-31'),
('Festival de Verduras', 'Promocion de productos vegetales', 0.20, 1, 2, TRUE, '2026-11-10', '2026-11-20'),
('Verduras del Campo', 'Descuento en productos del campo', 0.12, 1, 2, TRUE, '2026-11-01', '2026-11-25'),
('Verduras Fin de Semana', 'Oferta especial de verduras', 0.18, 1, 2, TRUE, '2026-11-07', '2026-11-15'),
('Verduras Mayoristas', 'Promocion de verduras para distribuidores', 0.08, 1, 2, TRUE, '2026-11-01', '2026-12-31'),
('Verduras de Diciembre', 'Oferta especial de diciembre', 0.15, 1, 2, TRUE, '2026-12-01', '2026-12-31'),
('Liquidacion Verduras', 'Descuento de verduras proximas a vencer', 0.40, 2, 2, TRUE, '2026-11-01', '2026-12-31'),
('Promocion Verduras Especial', 'Promocion temporal de verduras', 0.10, 1, 2, FALSE, '2026-11-01', '2026-11-30'),

-- PROMOCIONES 21-26: LACTEOS
('Lacteos Frescos', 'Descuento en lacteos seleccionados', 0.10, 1, 3, TRUE, '2026-11-01', '2026-11-30'),
('Lacteos de Temporada', 'Oferta especial de productos lacteos', 0.15, 1, 3, TRUE, '2026-11-01', '2026-12-15'),
('Lacteos Proximos a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 3, TRUE, '2026-11-01', '2026-12-31'),
('Festival de Lacteos', 'Promocion comercial de lacteos', 0.20, 1, 3, TRUE, '2026-11-10', '2026-11-20'),
('Lacteos Mayoristas', 'Oferta de productos lacteos', 0.12, 1, 3, TRUE, '2026-11-01', '2026-12-31'),
('Liquidacion Lacteos', 'Descuento en lacteos proximos a vencer', 0.40, 2, 3, TRUE, '2026-11-01', '2026-12-31'),

-- PROMOCIONES 27-32: CARNES
('Carnes Frescas', 'Descuento en carnes seleccionadas', 0.10, 1, 4, TRUE, '2026-11-01', '2026-11-30'),
('Carnes de Temporada', 'Oferta especial de productos carnicos', 0.15, 1, 4, TRUE, '2026-11-01', '2026-12-15'),
('Carnes Proximas a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 4, TRUE, '2026-11-01', '2026-12-31'),
('Festival de Carnes', 'Promocion comercial de carnes', 0.20, 1, 4, TRUE, '2026-11-10', '2026-11-20'),
('Carnes Mayoristas', 'Descuento especial para distribuidores', 0.12, 1, 4, TRUE, '2026-11-01', '2026-12-31'),
('Liquidacion Carnes', 'Descuento en carnes proximas a vencer', 0.40, 2, 4, TRUE, '2026-11-01', '2026-12-31'),

-- PROMOCIONES 33-38: GRANOS
('Granos Seleccionados', 'Descuento en granos nacionales', 0.10, 1, 5, TRUE, '2026-11-01', '2026-11-30'),
('Festival de Granos', 'Oferta especial de granos', 0.15, 1, 5, TRUE, '2026-11-01', '2026-12-15'),
('Granos Proximos a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 5, TRUE, '2026-11-01', '2026-12-31'),
('Granos Mayoristas', 'Oferta para compras de granos', 0.20, 1, 5, TRUE, '2026-11-10', '2026-11-20'),
('Granos de Diciembre', 'Promocion especial de granos', 0.12, 1, 5, TRUE, '2026-12-01', '2026-12-31'),
('Liquidacion Granos', 'Descuento en granos proximos a vencer', 0.40, 2, 5, TRUE, '2026-11-01', '2026-12-31'),

-- PROMOCIONES 39-42: BEBIDAS
('Bebidas Naturales', 'Descuento en bebidas seleccionadas', 0.10, 1, 6, TRUE, '2026-11-01', '2026-11-30'),
('Festival de Bebidas', 'Oferta especial de bebidas', 0.15, 1, 6, TRUE, '2026-11-01', '2026-12-15'),
('Bebidas Proximas a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 6, TRUE, '2026-11-01', '2026-12-31'),
('Liquidacion Bebidas', 'Descuento por liquidacion de inventario', 0.40, 2, 6, TRUE, '2026-11-01', '2026-12-31'),

-- PROMOCIONES 43-46: ENLATADOS
('Enlatados Seleccionados', 'Descuento en productos enlatados', 0.10, 1, 7, TRUE, '2026-11-01', '2026-11-30'),
('Festival de Enlatados', 'Oferta especial de conservas', 0.15, 1, 7, TRUE, '2026-11-01', '2026-12-15'),
('Enlatados Proximos a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 7, TRUE, '2026-11-01', '2026-12-31'),
('Liquidacion Enlatados', 'Descuento en conservas proximas a vencer', 0.40, 2, 7, TRUE, '2026-11-01', '2026-12-31'),

-- PROMOCIONES 47-50: PANADERIA
('Panaderia Fresca', 'Descuento en productos de panaderia', 0.10, 1, 8, TRUE, '2026-11-01', '2026-11-30'),
('Festival de Panaderia', 'Oferta especial de productos horneados', 0.15, 1, 8, TRUE, '2026-11-01', '2026-12-15'),
('Panaderia Proxima a Vencer', 'Descuento por vencimiento cercano', 0.50, 2, 8, TRUE, '2026-11-01', '2026-12-31'),
('Liquidacion Panaderia', 'Descuento en productos proximos a vencer', 0.40, 2, 8, TRUE, '2026-11-01', '2026-12-31');
