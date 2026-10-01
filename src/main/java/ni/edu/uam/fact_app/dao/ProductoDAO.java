package ni.edu.uam.fact_app.dao;

import ni.edu.uam.fact_app.config.DatabaseConnection;
import ni.edu.uam.fact_app.model.Categoria;
import ni.edu.uam.fact_app.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.codigo, p.nombre, p.categoria_id, p.precio_venta, "
                + "p.existencia, p.ruta_imagen, p.activo, "
                + "c.nombre AS categoria_nombre, c.activa AS categoria_activa "
                + "FROM producto p LEFT JOIN categoria c ON p.categoria_id = c.id "
                + "ORDER BY p.id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));

                int categoriaId = rs.getInt("categoria_id");
                if (!rs.wasNull()) {
                    Categoria categoria = new Categoria();
                    categoria.setId(categoriaId);
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    categoria.setActiva(rs.getBoolean("categoria_activa"));
                    producto.setCategoria(categoria.getId());
                }

                producto.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                producto.setExistencia(rs.getInt("existencia"));
                producto.setRutaImagen(rs.getString("ruta_imagen"));
                producto.setActivo(rs.getBoolean("activo"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /** Inserta el producto; el id lo genera la base de datos (AUTO_INCREMENT). */
    public boolean guardar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, "
                + "existencia, ruta_imagen, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            setCategoriaId(ps, 3, producto);
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    producto.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, categoria_id = ?, precio_venta = ?, "
                + "existencia = ?, ruta_imagen = ?, activo = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            setCategoriaId(ps, 3, producto);
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());
            ps.setInt(8, producto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setCategoriaId(PreparedStatement ps, int index, Producto producto) throws SQLException {
        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            ps.setInt(index, producto.getCategoria().getId());
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }
}