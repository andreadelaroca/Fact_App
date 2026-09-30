package ni.edu.uam.fact_app.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.uam.fact_app.config.DatabaseConnection;
import ni.edu.uam.fact_app.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto";
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

    public void guardar() {
        String sql = "INSERT INTO producto(id, codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            Producto producto = new Producto();
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getCodigo());
            ps.setString(3, producto.getNombre());
            //Validación id categoría
            if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
                ps.setInt(3, producto.getCategoria().getId());
            }
            else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setBigDecimal(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getExistencia());
            ps.setString(7, producto.getRutaImagen());
            ps.setBoolean(8, producto.isActivo());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, categoria_id = ?, precio_venta = ?, existencia = ?, ruta_imagen = ?, activo = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(4, producto.getExistencia());
            ps.setString(5, producto.getRutaImagen());
            ps.setBoolean(6, producto.isActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id) {
            String sql = "DELETE FROM producto WHERE id = ?";
            try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
