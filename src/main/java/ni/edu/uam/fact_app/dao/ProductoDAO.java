package ni.edu.uam.fact_app.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.uam.fact_app.config.DatabaseConnection;
import ni.edu.uam.fact_app.model.Producto;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                ) {
            while (resultSet.next()) {
                Producto producto = new Producto();
                producto.setId(resultSet.getInt("id"));
                producto.setCodigo(resultSet.getString("codigo"));
                producto.setNombre(resultSet.getString("nombre"));
                producto.setCategoria(resultSet.getInt("categoria_id"));
                producto.setPrecioVenta(resultSet.getBigDecimal("precio_venta"));
                producto.setExistencia(resultSet.getInt("existencia"));
                producto.setRutaImagen(resultSet.getString("ruta_imagen"));
                producto.setActivo(resultSet.getBoolean("activo"));
                productos.add(producto);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }
}
