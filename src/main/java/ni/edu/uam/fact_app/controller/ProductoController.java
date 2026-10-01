package ni.edu.uam.fact_app.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.fact_app.dao.CategoriaDAO;
import ni.edu.uam.fact_app.dao.ProductoDAO;
import ni.edu.uam.fact_app.model.Categoria;
import ni.edu.uam.fact_app.model.Producto;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public class ProductoController {
    @FXML private TextField txtId, txtCodigo, txtNombre, txtPrecio, txtExistencia, txtCodigoBuscar;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colPrecio;
    @FXML private TableColumn<Producto, String> colExistencia;
    @FXML private TableColumn<Producto, String> colActivo;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private String rutaImagen;
    private Producto productoEditable;

    @FXML
    private void initialize() {
        cmbCategoria.setItems(categorias);
        tblProductos.setItems(productos);
        chkActivo.setSelected(true);
        txtId.setEditable(false); // el id lo asigna la base de datos

        // Mostrar el nombre de la categoría en el ComboBox
        cmbCategoria.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categoria c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombre());
            }
        });
        cmbCategoria.setButtonCell(cmbCategoria.getCellFactory().call(null));

        colId.setCellValueFactory(c -> texto(c.getValue().getId()));
        colCodigo.setCellValueFactory(c -> texto(c.getValue().getCodigo()));
        colNombre.setCellValueFactory(c -> texto(c.getValue().getNombre()));
        colCategoria.setCellValueFactory(c -> texto(
                c.getValue().getCategoria() == null ? "" : c.getValue().getCategoria().getNombre()));
        colPrecio.setCellValueFactory(c -> texto(c.getValue().getPrecioVenta()));
        colExistencia.setCellValueFactory(c -> texto(c.getValue().getExistencia()));
        colActivo.setCellValueFactory(c -> texto(c.getValue().isActivo() ? "Sí" : "No"));

        cargarCategorias();
        cargarProductos();
    }

    private SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(valor == null ? "" : valor.toString());
    }

    private void cargarCategorias() {
        categorias.setAll(categoriaDAO.listar().stream()
                .filter(Categoria::isActiva)
                .toList());
    }

    private void cargarProductos() {
        productos.setAll(productoDAO.listar());
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {
        if (txtCodigo.getText().isBlank()
                || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank()
                || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }

        BigDecimal precio;
        int existencia;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
            return;
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0 || existencia < 0) {
            mensaje(Alert.AlertType.WARNING, "Precio mayor que cero y existencia no negativa.");
            return;
        }

        String codigo = txtCodigo.getText().trim();
        boolean codigoRepetido = productos.stream().anyMatch(p ->
                p.getCodigo().equalsIgnoreCase(codigo)
                        && (productoEditable == null || !p.getId().equals(productoEditable.getId())));
        if (codigoRepetido) {
            mensaje(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
            return;
        }

        Producto p = (productoEditable == null) ? new Producto() : productoEditable;
        p.setCodigo(codigo);
        p.setNombre(txtNombre.getText().trim());
        p.setCategoria(cmbCategoria.getValue().getId());
        p.setPrecioVenta(precio);
        p.setExistencia(existencia);
        p.setRutaImagen(rutaImagen);
        p.setActivo(chkActivo.isSelected());

        boolean ok;
        if (productoEditable == null) {
            ok = productoDAO.guardar(p);
            if (ok) mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
        } else {
            ok = productoDAO.actualizar(p);
            if (ok) mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
        }

        if (!ok) {
            mensaje(Alert.AlertType.ERROR, "No se pudo guardar el producto en la base de datos.");
        }

        cargarProductos(); // recarga desde la BD para reflejar el estado real
        limpiar();
    }

    @FXML
    private void editar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto de la tabla para editar.");
            return;
        }
        productoEditable = seleccionado;
        txtId.setText(String.valueOf(seleccionado.getId()));
        txtCodigo.setText(seleccionado.getCodigo());
        txtNombre.setText(seleccionado.getNombre());
        txtPrecio.setText(seleccionado.getPrecioVenta().toString());
        txtExistencia.setText(String.valueOf(seleccionado.getExistencia()));
        chkActivo.setSelected(seleccionado.isActivo());

        // Seleccionar en el ComboBox la misma instancia que está en la lista (comparando por id)
        Categoria cat = seleccionado.getCategoria();
        cmbCategoria.setValue(cat == null ? null : categorias.stream()
                .filter(c -> c.getId().equals(cat.getId()))
                .findFirst()
                .orElse(null));

        rutaImagen = seleccionado.getRutaImagen();
        imgProducto.setImage(rutaImagen != null ? new Image(rutaImagen) : null);
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto para eliminar.");
            return;
        }

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el producto \"" + seleccionado.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.YES) {
            return;
        }

        if (productoDAO.eliminar(seleccionado.getId())) {
            mensaje(Alert.AlertType.INFORMATION, "Producto eliminado correctamente.");
        } else {
            mensaje(Alert.AlertType.ERROR,
                    "No se pudo eliminar el producto (puede estar usado en una factura).");
        }
        cargarProductos();
        limpiar();
    }

    @FXML
    private void buscarPorCodigo() {
        String codigoBusqueda = txtCodigoBuscar.getText().trim();
        if (codigoBusqueda.isEmpty()) {
            tblProductos.setItems(productos);
            return;
        }
        tblProductos.setItems(productos.filtered(
                p -> p.getCodigo().equalsIgnoreCase(codigoBusqueda)));
    }

    @FXML
    private void mostrar() {
        txtCodigoBuscar.clear();
        cargarProductos();
        tblProductos.setItems(productos);
    }

    @FXML
    private void abrirMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ni/edu/uam/fact_app/fxml/menu-principal.fxml"));
            Stage stage = (Stage) txtCodigo.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
        } catch (IOException e) {
            mensaje(Alert.AlertType.ERROR, "Error al cargar el menú principal.");
        }
    }

    @FXML
    private void salir() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtId.clear();
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
        productoEditable = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}