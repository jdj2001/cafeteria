/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accesoDatosObjetos;

import java.sql.Date;

/**
 *
 * @author Juan Alvarado
 */
public class Productos {
    private String codigoProducto;
    private String nombreProducto; // Agregar el atributo nombreProducto
    private int cantidad;

    public Productos(String codigoProducto, String nombreProducto, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto; // Inicializar el atributo nombreProducto
        this.cantidad = cantidad;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}


