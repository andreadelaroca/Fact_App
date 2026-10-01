package ni.edu.uam.fact_app.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ni.edu.uam.fact_app.dao.CategoriaDAO;
import ni.edu.uam.fact_app.model.Categoria;

import java.io.IOException;
import java.util.Optional;

public class CategoriaController {
    @FXML private TextField txtId, txtNombre, txtBuscar;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Categoria> tblCategoria;
    @FXML private TableColumn<Categoria, String> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colActivo;
    @FXML private Button btnSalir;


    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private Categoria categoriaEditable;

    @FXML
    private void initialize() {
        tblCategoria.setItems(categorias);
        chkActivo.setSelected(true);
        txtId.setEditable(false); // el id lo genera la base de datos

        colId.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getId() == null ? "" : String.valueOf(c.getValue().getId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colActivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "Sí" : "No"));

        cargarCategorias();
    }

    private void cargarCategorias() {
        categorias.setAll(categoriaDAO.listar());
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mensaje(Alert.AlertType.WARNING, "Debe ingresar el nombre de la categoría.");
            return;
        }

        boolean nombreRepetido = categorias.stream().anyMatch(c ->
                c.getNombre().equalsIgnoreCase(nombre)
                        && (categoriaEditable == null || !c.getId().equals(categoriaEditable.getId())));
        if (nombreRepetido) {
            mensaje(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.");
            return;
        }

        boolean ok;
        if (categoriaEditable == null) {
            Categoria c = new Categoria();
            c.setNombre(nombre);
            c.setActiva(chkActivo.isSelected());
            ok = categoriaDAO.guardar(c);
            if (ok) mensaje(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
        } else {
            categoriaEditable.setNombre(nombre);
            categoriaEditable.setActiva(chkActivo.isSelected());
            ok = categoriaDAO.actualizar(categoriaEditable);
            if (ok) mensaje(Alert.AlertType.INFORMATION, "Categoría actualizada correctamente.");
        }

        if (!ok) {
            mensaje(Alert.AlertType.ERROR, "No se pudo guardar la categoría en la base de datos.");
        }

        cargarCategorias();
        limpiar();
    }

    @FXML
    private void editar() {
        Categoria seleccionada = tblCategoria.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione una categoría de la tabla para editar.");
            return;
        }
        categoriaEditable = seleccionada;
        txtId.setText(String.valueOf(seleccionada.getId()));
        txtNombre.setText(seleccionada.getNombre());
        chkActivo.setSelected(seleccionada.isActiva());
    }

    @FXML
    private void eliminar() {
        Categoria seleccionada = tblCategoria.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione una categoría para eliminar.");
            return;
        }

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar la categoría \"" + seleccionada.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.YES) {
            return;
        }

        if (categoriaDAO.eliminar(seleccionada.getId())) {
            mensaje(Alert.AlertType.INFORMATION, "Categoría eliminada correctamente.");
        } else {
            mensaje(Alert.AlertType.ERROR,
                    "No se pudo eliminar la categoría. Puede tener productos asociados; "
                            + "en ese caso márquela como inactiva.");
        }
        cargarCategorias();
        limpiar();
    }

    @FXML
    private void buscar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            tblCategoria.setItems(categorias);
            return;
        }
        tblCategoria.setItems(categorias.filtered(
                c -> c.getNombre().toLowerCase().contains(texto)));
    }

    @FXML
    private void mostrar() {
        txtBuscar.clear();
        cargarCategorias();
        tblCategoria.setItems(categorias);
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        chkActivo.setSelected(true);
        categoriaEditable = null;
        tblCategoria.getSelectionModel().clearSelection();
    }

    @FXML
    private void abrirMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ni/edu/uam/fact_app/fxml/menu-principal.fxml"));
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
        } catch (IOException e) {
            mensaje(Alert.AlertType.ERROR, "Error al cargar el menú principal.");
        }
    }

    @FXML
    private void salir() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}