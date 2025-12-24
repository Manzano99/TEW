package es.tew;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CarritoBean implements Serializable {
    private Map<String, Integer> productos = new HashMap<>();

    public void addProducto(String codigo) {
        if (codigo == null) return;
        productos.put(codigo, productos.getOrDefault(codigo, 0) + 1);
    }

    public Map<String, Integer> getProductos() {
        return productos;
    }

    public void setProductos(Map<String, Integer> productos) {
        this.productos = productos;
    }
}
