package ni.edu.uam.fact_app.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.uam.fact_app.config.DatabaseConnection;
import ni.edu.uam.fact_app.model.Categoria;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria";
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(resultSet.getInt("id"));
                categoria.setNombre(resultSet.getString("nombre"));
                categoria.setActiva(resultSet.getBoolean("activa"));
                categorias.add(categoria);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    public void guardar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, categoria.isActiva());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
