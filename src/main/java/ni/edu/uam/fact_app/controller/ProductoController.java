package ni.edu.uam.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.fact_app.model.Categoria;
import ni.edu.uam.fact_app.model.Producto;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

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

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();
    private String rutaImagen;
    private Producto productoEditable;

    @FXML
    private void initialize() {
        cmbCategoria.setItems(FXCollections.observableArrayList(
                new Categoria(1, "Alimentos", true),
                new Categoria(2, "Bebidas", true),
                new Categoria(3, "Limpieza", true)));
        tblProductos.setItems(productos);
        chkActivo.setSelected(true);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
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
        try {
            Integer id = Integer.parseInt(txtId.getId());
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0 || existencia < 0) {
                mensaje(Alert.AlertType.WARNING,
                        "Precio mayor que cero y existencia no negativa.");
                return;
            }
            if (productoEditable == null) {
                Producto p = new Producto(
                        id,
                        txtCodigo.getText().trim(),
                        txtNombre.getText().trim(),
                        cmbCategoria.getValue(),
                        precio,
                        existencia,
                        rutaImagen,
                        chkActivo.isSelected());
            productos.add(p);
            mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            }
            else {
                productoEditable.setCodigo(txtCodigo.getText().trim());
                productoEditable.setNombre(txtNombre.getText().trim());
                productoEditable.setCategoria(cmbCategoria.getValue());
                productoEditable.setPrecioVenta(precio);
                productoEditable.setExistencia(existencia);
                productoEditable.setRutaImagen(rutaImagen);
                productoEditable.setActivo(chkActivo.isSelected());

                tblProductos.refresh();
                mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
                productoEditable = null;
            }
            limpiar();

        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
        }
    }

    @FXML
    private void editar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto de la tabla para editar.");
            return;
        }
        productoEditable = seleccionado;
        txtCodigo.setText(seleccionado.getCodigo());
        txtNombre.setText(seleccionado.getNombre());
        cmbCategoria.setValue(seleccionado.getCategoria());
        txtPrecio.setText(seleccionado.getPrecioVenta().toString());
        txtExistencia.setText(String.valueOf(seleccionado.getExistencia()));
        chkActivo.setSelected(seleccionado.isActivo());

        if (seleccionado.getRutaImagen() != null) {
            rutaImagen = seleccionado.getRutaImagen();
            imgProducto.setImage(new Image(rutaImagen));
        } else {
            imgProducto.setImage(null);
        }
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione un producto para eliminar.");
            return;
        }
        productos.remove(seleccionado);
        mensaje(Alert.AlertType.INFORMATION, "Producto eliminado correctamente.");
    }

    @FXML
    private void buscarPorCodigo() {
        String codigoBusqueda = txtCodigoBuscar.getText().trim();
        if (codigoBusqueda.isEmpty()) {
            tblProductos.setItems(productos);
            return;
        }
        ObservableList<Producto> filtrados = productos.filtered(
                p -> p.getCodigo().equalsIgnoreCase(codigoBusqueda)
        );
        tblProductos.setItems(filtrados);
    }

    @FXML
    private void mostrar() {
        txtCodigoBuscar.clear();
        tblProductos.setItems(productos);
    }

    @FXML
    private void abrirMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/edu/uam/fact_app/fxml/menu-principal.fxml"));
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
        txtCodigo.clear(); txtNombre.clear(); txtPrecio.clear(); txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true); imgProducto.setImage(null); rutaImagen = null;
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}