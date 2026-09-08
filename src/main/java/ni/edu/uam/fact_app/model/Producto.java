package ni.edu.uam.fact_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class Producto {
    private Integer id;
    private String nombre;
    private Categoria categoria;
    private BigDecimal precioVenta;
    private int existencia;
    private int cantidad;
    private String rutaImagen;
    private boolean activo;
}
