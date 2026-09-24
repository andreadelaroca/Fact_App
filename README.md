# FactApp - Sistema de Gestión de Productos y Facturación

FactApp es una aplicación de escritorio desarrollada para la administración y control de inventario de productos. El sistema permite gestionar un catálogo completo con categorías, validaciones financieras, control de existencias e integración de imágenes locales mediante una interfaz gráfica construida en JavaFX.

## Tecnologías Utilizadas

* **Lenguaje:** Java 21
* **Interfaz Gráfica:** JavaFX (FXML)
* **Librerías:** Lombok

## Funcionalidades Principales

* **Menú de Navegación:** Transiciones entre vistas mediante la clase utilitaria `SceneManager`.
* **Gestión de Productos (CRUD Completo):**
  * **Creación:** Registro de productos con código, nombre, categoría, precio de venta, existencia inicial, estado (activo/inactivo) e imagen asociable.
  * **Edición:** Carga interactiva de datos existentes en los campos de texto para actualización en tiempo real.
  * **Eliminación:** Borrado de productos del catálogo visual.
* **Búsqueda y Filtros:** Filtrado de la tabla (`TableView`) en tiempo real mediante búsquedas por código de producto.
* **Validación de Datos:**
  * Control de campos obligatorios en los formularios.
  * Verificación de rangos (precios $> 0$ y existencias no negativas).
  * Control de excepciones numéricas (`NumberFormatException`).
* **Carga de Archivos:** Integración de imágenes de vista previa (`FileChooser`) compatibles con formatos `.png`, `.jpg` y `.jpeg`.

## Estructura del Proyecto

```
src/main/java/ni/edu/uam/fact_app/
├── controller/
│   ├── MenuPrincipalController.java   # Control de navegación y menú principal
│   └── ProductoController.java        # Lógica de la vista de gestión de productos
├── model/
│   ├── Categoria.java                 # Modelo de categoría
│   └── Producto.java                  # Modelo del producto (Lombok @Data)
└── util/
    └── SceneManager.java              # Carga dinámica de escenarios FXML

src/main/resources/ni/edu/uam/fact_app/
├── fxml/
│   ├── menu-principal.fxml            # Vista FXML del menú
│   └── producto-view.fxml             # Vista FXML de la tabla y formulario
└── icons/                             # Recursos gráficos
