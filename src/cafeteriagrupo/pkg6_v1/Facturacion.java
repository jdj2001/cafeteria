/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;

import accesoDatosObjetos.Conexion;
import accesoDatosObjetos.Productos;
import ayuda.AyudaFacturacion;
import java.awt.Color;
//import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.util.regex.Pattern;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import javax.swing.JTextField;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.event.TableModelEvent;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;
import java.awt.Desktop;

import java.awt.print.PrinterJob;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

//import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
//import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.properties.TextAlignment;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.text.TabStop;
import com.itextpdf.layout.element.TabStop;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TabAlignment;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

//=======
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;

/**
 *
 * @author Juan Alvarado
 */
public class Facturacion extends javax.swing.JDialog {

    double totalTarjeta = 0.0; // Almacena el total adicional por tarjeta de crédito
    boolean sePagaConTarjeta = false; // Indica si la venta se pagará con tarjeta de crédito

    private static String correoUsu;
    int cantProducto = 0, cantidadDisponibleJtable = 0;
    // HashMap para almacenar las cantidades disponibles de cada producto en el jTable
    /*private HashMap<String, Integer> cantidadesDisponiblesEnFactura = new HashMap<>();
// HashMap para almacenar las cantidades disponibles de cada producto en el inventario
    private HashMap<String, Integer> cantidadDisponibleProductos = new HashMap<>();
    private List<Productos> productosEnFactura = new ArrayList<>();*/

    private HashMap<String, Integer> cantidadesDisponiblesEnFactura = new HashMap<>();
    private HashMap<String, Integer> cantidadDisponibleProductos = new HashMap<>();
    private List<Productos> productosEnFactura = new ArrayList<>();

    String camb, efec, DNI, impuesto, impuestoF, precioF, totalF, cantidadF, nombreProducto, telefono, sentenciaSQL;
    //int cantidadC;
    Double cambio = 0.0, efectivo = 0.0, GRANTOTAL = 0.0, impuesto2, impuestoBebidas = 0.15, impuestoComida = 0.18, impuestoPostre = 0.25, precio2, total, GranTotal, Total;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon imagen;
    Icon icono;
    public Double utilidad2;
    ResultSet rs = null; //PERMITE TENER METODOS PARA OBTENER INFO DE LAS DIFERENTES COLUMNAS DE UNA FILA=TIPO RESULT SET VARIABE
    DefaultTableModel modelo = new DefaultTableModel(); //PARA EL MÓDELO DE NUESTRA TABLA
    DefaultTableModel modeloOrden = new DefaultTableModel();
    Object NombreProducto[] = new Object[3];
    Object datosOrden[] = new Object[11];
    private Double granTotal = 0.0;

    private boolean pagoTarjetaCredito = false;
    // Declarar una variable para almacenar las filas confirmadas
    private Set<Integer> filasConfirmadas = new HashSet<>();
    public String modeloTelefono = "^[0-9]*$";
    
    

    //LLEVAR CONTROL DE LAS CANTIDADES RESTADAS DE LOS PRODUCTOS AQUI Y NO EN EL INVENTARIO
    /*int cantidadDisponibleEnFactura = 0;
    int cantidadDisponibleEnProducto = 0;*/
    /**
     * Creates new form Facturacion
     */
    public Facturacion(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        this.ConectarBD();
        
        this.validarFactura();
        CargarCategoria();
        correoUsu = user;
        jblUsuario.setText(correoUsu);
        cambioImagen("dibujos.png", fondo);
        cambioImagen("logoSinFondo.png", jblLogo);
        txtEfectivo.setEnabled(false);
        txtRTN.setEnabled(false);
        txtNombreCliente.setEnabled(false);
        btnVerificarEfectivo.setEnabled(false);
        btnCerrarVenta.setEnabled(false);
        btnAñadirOrden.setEnabled(false);
        btnCrearFactura.setEnabled(false);
        btnImprimir.setEnabled(false);
    }

    public void validarFactura() {
        int opcionFacturaRTN = 0;
        int opcion = JOptionPane.showOptionDialog(this, "¿La factura se hará con RTN?", "Pregunta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (opcion == JOptionPane.NO_OPTION) {
            String nombreCliente = consumidorFinal();
            txtNombreCliente.setText(nombreCliente);
            System.out.println(consumidorFinal());

        } else if (opcion == JOptionPane.YES_OPTION) {
            String rtn = JOptionPane.showInputDialog(this, "Ingrese el RTN del cliente:");
            existeCliente(rtn);

            if (existeCliente(rtn)) {

                String nombreCliente = obtenerNombreCliente(rtn);
                txtNombreCliente.setText(nombreCliente);
                txtRTN.setText(rtn);
            } else if (rtn.length() < 14 || rtn.length() > 14 || !rtn.matches(modeloTelefono)) {
                JOptionPane.showMessageDialog(null, "RTN INCORRECTO");
                return;
            } else {

                opcionFacturaRTN = JOptionPane.showOptionDialog(
                        this,
                        "El cliente no existe en la base de datos. ¿Desea intentar de nuevo con el DNI o realizar la factura con RTN?",
                        "Error",
                        JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"SIN RTN", "CON RTN"}, null);

                if (opcionFacturaRTN == JOptionPane.YES_OPTION || opcionFacturaRTN == JOptionPane.NO_OPTION) {
                    validarFactura();
                }
            }
        }
        tblFactura.setEnabled(true);
        tblNombreProducto.setEnabled(true);
        jspCantidad.setEnabled(true);
        btnAñadirOrden.setVisible(true);
        cboCategoria.setEnabled(true);
        btnVerificarEfectivo.setVisible(true);
    }

    public void limpiar() {
        //txtNombreProducto.setText("");
        txtNombreCliente.setText("");
        txtRTN.setText("");
        //txtCodigo.setText("");
        //txtTotal.setText("");
        txtEfectivo.setText("");
        txtCambio.setText("");
        txtGranTotal.setText("");
        /*txtImpuesto.setText("0.0");
        txtPrecio.setText("0.0");
        txtSubtotal.setText("0.0");
        txtTotal.setText("0.0");
        jspCantidad.setValue(1);*/
        cboCategoria.setSelectedIndex(0);
        /*int fila = tblNombreProducto.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);//FILA X HASTA LLEGAR A CERO
        }*/
        int fila2 = tblFactura.getRowCount();
        for (int i2 = fila2 - 1; i2 >= 0; i2--) {
            modeloOrden.removeRow(i2);//FILA X HASTA LLEGAR A CERO
        }
        /*btnVerificarEfectivo.setEnabled(false);
        btnCrearFactura.setEnabled(false);
        btnCerrarVenta.setEnabled(false);*/
        btnImprimir.setEnabled(false);
    }

    public boolean existeCliente(String rtn) {
        boolean existe = false;
        try {
            String sql = "SELECT COUNT(*) FROM clientes WHERE RTN = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, rtn);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                existe = count > 0;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        return existe;
    }

    public String obtenerNombreCliente(String rtn) {
        String nombreCliente = "";
        try {
            String sql = "SELECT nombreCliente FROM clientes WHERE RTN = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, rtn);
            rs = ps.executeQuery();
            if (rs.next()) {
                nombreCliente = rs.getString("nombreCliente");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        return nombreCliente;
    }

    public String consumidorFinal() {
        String nombreCliente = "";
        //int id = 3;
        String sql = "SELECT nombreCliente FROM clientes WHERE id = 2";
        try {
            ps = con.prepareStatement(sql);
            //ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                //String nombreCliente="";
                nombreCliente = rs.getString("nombreCliente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        return nombreCliente;
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        int width = img.getWidth();
        int height = img.getHeight();
        if (width > 0 && height > 0) {
            imagen = new ImageIcon("src/imagenes/" + nombreImagen);
            icono = new ImageIcon(imagen.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT));
            img.setIcon(icono);
        } else {
            // Aquí puedes agregar un mensaje de depuración o manejar el caso cuando el tamaño del JLabel es cero.
        }
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void restarCantidadInventario(String DescripcionProducto, int CantidadActutal) throws SQLException {
        sentenciaSQL = "UPDATE inventario SET cantidad = cantidad - ? WHERE descripcion = ?";
        ps = con.prepareStatement(sentenciaSQL);
        ps.setInt(1, CantidadActutal);
        ps.setString(2, DescripcionProducto);
        ps.executeUpdate();
    }

    public void crearFactura() {
        try {
            for (int i = 0; i < modeloOrden.getRowCount(); i++) {
                if (!filasConfirmadas.contains(i)) {
                    continue;
                }
                String cliente = String.valueOf(modeloOrden.getValueAt(i, 0));
                String RTN = String.valueOf(modeloOrden.getValueAt(i, 1));
                String codigoProducto = String.valueOf(modeloOrden.getValueAt(i, 2));
                String nombreProducto = String.valueOf(modeloOrden.getValueAt(i, 3));
                double precio = Double.parseDouble(String.valueOf(modeloOrden.getValueAt(i, 4)));
                int cantidadEntero = Integer.parseInt(String.valueOf(modeloOrden.getValueAt(i, 5)));
                String cantidad = String.valueOf(modeloOrden.getValueAt(i, 5));
                double impuesto = Double.parseDouble(String.valueOf(modeloOrden.getValueAt(i, 6)));
                double tarjeta = Double.parseDouble(String.valueOf(modeloOrden.getValueAt(i, 7)));
                double subtotal = Double.parseDouble(String.valueOf(modeloOrden.getValueAt(i, 8)));
                double total = Double.parseDouble(String.valueOf(modeloOrden.getValueAt(i, 9)));

                Date fecha = (Date) modeloOrden.getValueAt(i, 10);
                try {
                    int cantidadAgregar = Integer.parseInt(cantidad);
                    restarCantidadInventario(nombreProducto, cantidadAgregar);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al obtener el stock del producto: " + ex.getMessage());
                    return;
                }

                try {
                    sentenciaSQL = "INSERT INTO factura (NombreCliente, RTN,codigoProducto, producto, precio, cantidad, impuesto,tarjeta,subtotal, total, fecha) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                    ps = con.prepareStatement(sentenciaSQL);
                    //ps.setInt(1, 0);
                    ps.setString(1, cliente);
                    ps.setString(2, RTN);
                    ps.setString(3, codigoProducto);
                    ps.setString(4, nombreProducto);
                    ps.setDouble(5, precio);
                    ps.setInt(6, cantidadEntero);
                    ps.setDouble(7, impuesto);
                    ps.setDouble(8, tarjeta);
                    ps.setDouble(9, subtotal);
                    ps.setDouble(10, total);
                    ps.setDate(11, fecha);
                    ps.execute();
                    btnImprimir.setEnabled(true);
                    btnCrearFactura.setEnabled(false);
                    btnCrearFactura.setEnabled(false);
                    //JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
                } catch (SQLException ex) {
                    Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS: " + ex.getMessage());
                }
            }
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS: " + ex.getMessage());
        }
    }

    // Método para actualizar las cantidades disponibles en la factura y en el inventario
    private void actualizarCantidadesDisponibles(String codigoProducto, int cantidadAgregar) throws SQLException {
        int cantidadDisponibleEnFactura = cantidadesDisponiblesEnFactura.getOrDefault(codigoProducto, 0);
        cantidadesDisponiblesEnFactura.put(codigoProducto, cantidadDisponibleEnFactura + cantidadAgregar);

        int stockProducto = obtenerStockProducto(codigoProducto);
        int cantidadDisponibleEnProducto = cantidadDisponibleProductos.getOrDefault(codigoProducto, stockProducto - cantidadDisponibleEnFactura);

        if (cantidadAgregar < 0) {
            // Si estamos eliminando productos de la factura, actualizamos la cantidad disponible en el inventario
            cantidadDisponibleEnProducto += Math.abs(cantidadAgregar);
        } else {
            // Si estamos agregando productos a la factura, verificamos si hay suficientes unidades disponibles en el inventario
            if (stockProducto - cantidadDisponibleEnFactura < 0) {
                // Si no hay suficientes unidades, establecemos la cantidad disponible en el inventario como 0
                cantidadDisponibleEnProducto = 0;
            } else {
                cantidadDisponibleEnProducto = stockProducto - cantidadDisponibleEnFactura;
            }
        }

        cantidadDisponibleProductos.put(codigoProducto, cantidadDisponibleEnProducto);
    }

    public void agregarFilaTabla() {
        modeloOrden = (DefaultTableModel) tblFactura.getModel();
        String RTN = txtRTN.getText();
        String codigoProducto = txtCodigo.getText(); // Supongo que aquí está la clave única del producto en la tabla inventario
        String nombreProducto = txtNombreProducto.getText();
        String precio = txtPrecio.getText();
        String cantidad = jspCantidad.getValue().toString();
        String impuesto = txtImpuesto.getText();
        String subtotal = txtSubtotal.getText();
        String total = txtTotal.getText();
        String cliente = txtNombreCliente.getText();
        LocalDate fechaActual = LocalDate.now();
        Date fechaSQL = Date.valueOf(fechaActual);

        double precio3 = Double.parseDouble(precio);
        int cantidadAgregar = Integer.parseInt(cantidad);
        //double subtotal2 = precio3 * cantidadAgregar;
        //txtSubtotal.setText(String.valueOf(subtotal2));
        //subtotal = txtSubtotal.getText().trim();
        try {
            // Verificar disponibilidad del producto en inventario
            int cantidadDisponibleEnFactura = cantidadesDisponiblesEnFactura.getOrDefault(codigoProducto, 0);

            int stockProducto = obtenerStockProducto(codigoProducto);

            int stockMinimoProducto = obtenerStockMinimoProducto();
            if ((stockProducto - cantidadDisponibleEnFactura - cantidadAgregar) == stockMinimoProducto) {
                JOptionPane.showMessageDialog(null, "EL STOCK HA LLEGADO AL MÍNIMO");
            }
            if ((cantidadDisponibleEnFactura + cantidadAgregar) > stockProducto) {
                JOptionPane.showMessageDialog(null, "No hay suficientes unidades del producto en inventario.\n EL INVENTARIO NO PUEDE QUEDAR EN NÚMEROS NEGATIVOS");
                return; // No se agrega la fila al jTable
            } else if ((cantidadDisponibleEnFactura + cantidadAgregar) == stockProducto) {
                JOptionPane.showMessageDialog(null, "Ya no hay productos disponibles.\n Escoja otro producto o cree la factura!");
                //jspCantidad.setValue(0);
                //jspCantidad.setEnabled(false);
                //btnAñadirOrden.setEnabled(false);
                //return;
            } else if ((/*cantidadDisponibleEnFactura +*/cantidadAgregar) < stockProducto) {
                JOptionPane.showMessageDialog(null, "Añadido! Aun quedan " + (stockProducto - cantidadDisponibleEnFactura - cantidadAgregar) + " unidades disponibles de este producto");//AQUI
            }

            // Agregar la fila al jTable
            datosOrden[0] = cliente;
            datosOrden[1] = RTN;
            datosOrden[2] = codigoProducto;
            datosOrden[3] = nombreProducto;
            datosOrden[4] = precio;
            datosOrden[5] = cantidad;
            datosOrden[6] = impuesto;
            datosOrden[7] = 0.0;
            datosOrden[8] = subtotal;
            datosOrden[9] = total;
            datosOrden[10] = fechaSQL;

            modeloOrden.addRow(datosOrden);
            tblFactura.setModel(modeloOrden);

            double totalActual = Double.parseDouble(total);
            GRANTOTAL = GRANTOTAL + totalActual;
            txtGranTotal.setText(String.valueOf(GRANTOTAL));

            int fila = modeloOrden.getRowCount() - 1;
            filasConfirmadas.add(fila);

            // Habilitar botón de cerrar venta si hay filas confirmadas
            btnCerrarVenta.setEnabled(!filasConfirmadas.isEmpty());

            // Actualizar cantidades disponibles en la factura y en el inventario
            cantidadesDisponiblesEnFactura.put(codigoProducto, (cantidadDisponibleEnFactura + cantidadAgregar));
            cantidadDisponibleProductos.put(codigoProducto, (stockProducto - cantidadDisponibleEnFactura));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener el stock del producto: " + ex.getMessage());
            return;
        }
    }

    public void cerrarVenta() {
        int opcionCerrarVenta = JOptionPane.showOptionDialog(this, "¿CERRAR LA VENTA?", "Pregunta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (opcionCerrarVenta == JOptionPane.YES_OPTION) {
            int opcionTarjetaCredito = JOptionPane.showOptionDialog(this, "¿Se pagará con tarjeta de crédito?", "Pregunta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (opcionTarjetaCredito == JOptionPane.YES_OPTION) {
                GRANTOTAL = 0.0;
                for (int fila : filasConfirmadas) {
                    double precioAntesTarjeta = Double.parseDouble(modeloOrden.getValueAt(fila, 4).toString());
                    int cantidadAntesTarjeta = Integer.parseInt(modeloOrden.getValueAt(fila, 5).toString());
                    double impuestoAntesTarjeta = Double.parseDouble(modeloOrden.getValueAt(fila, 6).toString());
                    double subtotalAntesTarjeta = Double.parseDouble(modeloOrden.getValueAt(fila, 8).toString());
                    double totalAntesTarjeta = Double.parseDouble(modeloOrden.getValueAt(fila, 9).toString());
                    String productoNombre = (String) cboCategoria.getSelectedItem();

                    //CON TARJETA
                    double tarjeta;
                    double precioDespuesTarjeta = 0.0;
                    double impuestoDespuesTarjeta = 0.0;
                    double subtotalDespuesTarjeta = 0.0;
                    double totalDespuesTarjeta = 0.0;

                    String nuevoPrecio;
                    String nuevoSubTotal;
                    String nuevoTotal;
                    String Tarjeta;
                    String nuevoImpuesto;

                    if (productoNombre.equals("Postres/Bocadillos")) {
                        tarjeta = precioAntesTarjeta * 0.05;
                        precioDespuesTarjeta = (tarjeta + precioAntesTarjeta);
                        impuestoDespuesTarjeta = (impuestoPostre * precioDespuesTarjeta);
                        subtotalDespuesTarjeta = (precioDespuesTarjeta * cantidadAntesTarjeta);
                        totalDespuesTarjeta = (subtotalDespuesTarjeta + impuestoDespuesTarjeta);

                        nuevoPrecio = String.valueOf(precioDespuesTarjeta);
                        nuevoSubTotal = String.valueOf(subtotalDespuesTarjeta);
                        nuevoTotal = String.valueOf(totalDespuesTarjeta);
                        Tarjeta = String.valueOf(tarjeta);
                        nuevoImpuesto = String.valueOf(impuestoDespuesTarjeta);
                        txtPrecio.setText(nuevoPrecio);
                        txtImpuesto.setText(String.valueOf(impuestoDespuesTarjeta));
                        txtSubtotal.setText(nuevoSubTotal);
                        txtTotal.setText(String.valueOf(totalDespuesTarjeta));

                        modeloOrden.setValueAt(nuevoPrecio, fila, 4);
                        modeloOrden.setValueAt(nuevoImpuesto, fila, 6);
                        modeloOrden.setValueAt(Tarjeta, fila, 7);
                        modeloOrden.setValueAt(nuevoSubTotal, fila, 8);
                        modeloOrden.setValueAt(nuevoTotal, fila, 9);

                    } else if (productoNombre.equals("Bebidas")) {
                        tarjeta = precioAntesTarjeta * 0.05;
                        precioDespuesTarjeta = tarjeta + precioAntesTarjeta;
                        impuestoDespuesTarjeta = impuestoBebidas * precioDespuesTarjeta;
                        subtotalDespuesTarjeta = precioDespuesTarjeta * cantidadAntesTarjeta;
                        totalDespuesTarjeta = subtotalDespuesTarjeta + (impuestoDespuesTarjeta /*+ precioDespuesTarjeta*/);

                        nuevoPrecio = String.valueOf(precioDespuesTarjeta);
                        nuevoSubTotal = String.valueOf(subtotalDespuesTarjeta);
                        nuevoTotal = String.valueOf(totalDespuesTarjeta);
                        Tarjeta = String.valueOf(tarjeta);
                        nuevoImpuesto = String.valueOf(impuestoDespuesTarjeta);
                        txtPrecio.setText(nuevoPrecio);
                        txtImpuesto.setText(String.valueOf(impuestoDespuesTarjeta));
                        txtSubtotal.setText(nuevoSubTotal);
                        txtTotal.setText(String.valueOf(totalDespuesTarjeta));

                        modeloOrden.setValueAt(nuevoPrecio, fila, 4);
                        modeloOrden.setValueAt(nuevoImpuesto, fila, 6);
                        modeloOrden.setValueAt(Tarjeta, fila, 7);
                        modeloOrden.setValueAt(nuevoSubTotal, fila, 8);
                        modeloOrden.setValueAt(nuevoTotal, fila, 9);

                    } else if (productoNombre.equals("Comida")) {
                        tarjeta = precioAntesTarjeta * 0.05;
                        precioDespuesTarjeta = tarjeta + precioAntesTarjeta;
                        impuestoDespuesTarjeta = impuestoComida * precioDespuesTarjeta;
                        subtotalDespuesTarjeta = precioDespuesTarjeta * cantidadAntesTarjeta;
                        totalDespuesTarjeta = subtotalDespuesTarjeta + (impuestoDespuesTarjeta /*+ precioDespuesTarjeta*/);

                        nuevoPrecio = String.valueOf(precioDespuesTarjeta);
                        nuevoSubTotal = String.valueOf(subtotalDespuesTarjeta);
                        nuevoTotal = String.valueOf(totalDespuesTarjeta);
                        Tarjeta = String.valueOf(tarjeta);
                        nuevoImpuesto = String.valueOf(impuestoDespuesTarjeta);
                        txtPrecio.setText(nuevoPrecio);
                        txtImpuesto.setText(String.valueOf(impuestoDespuesTarjeta));
                        txtSubtotal.setText(nuevoSubTotal);
                        txtTotal.setText(String.valueOf(totalDespuesTarjeta));

                        modeloOrden.setValueAt(nuevoPrecio, fila, 4);
                        modeloOrden.setValueAt(nuevoImpuesto, fila, 6);
                        modeloOrden.setValueAt(Tarjeta, fila, 7);
                        modeloOrden.setValueAt(nuevoSubTotal, fila, 8);
                        modeloOrden.setValueAt(nuevoTotal, fila, 9);
                    }

                    GRANTOTAL = GRANTOTAL + totalDespuesTarjeta;
                    txtGranTotal.setText(String.valueOf(GRANTOTAL));
                    JOptionPane.showMessageDialog(null, "VENTA CERRDA!");
                    btnCrearFactura.setEnabled(true);
                }
                tblFactura.setEnabled(false);
                tblNombreProducto.setEnabled(false);
                btnAñadirOrden.setEnabled(false);
            } else if (opcionTarjetaCredito == JOptionPane.NO_OPTION) {
                JOptionPane.showMessageDialog(null, "Ingrese el efectivo");
                //limpiar();
                txtEfectivo.setEnabled(true);
                if (!txtEfectivo.getText().isEmpty() || !txtEfectivo.equals("") || !txtEfectivo.equals("0") || !txtEfectivo.equals("0.0")) {
                    btnVerificarEfectivo.setEnabled(true);
                }
            }
            tblFactura.setEnabled(false);
            tblNombreProducto.setEnabled(false);
            btnAñadirOrden.setVisible(false);
            btnCerrarVenta.setEnabled(false);
            cboCategoria.setEnabled(false);
            jspCantidad.setEnabled(false);
        } else if (opcionCerrarVenta == JOptionPane.NO_OPTION) {
            tblFactura.setEnabled(true);
            tblNombreProducto.setEnabled(true);
            btnAñadirOrden.setVisible(true);
            btnCerrarVenta.setEnabled(true);
            cboCategoria.setEnabled(true);
        }
        //btnAñadirOrden.setVisible(false);
    }

    public void cambio() {
        double TotalPagar = (Double.parseDouble(txtGranTotal.getText()));
        efec = txtEfectivo.getText();
        double efectivoCliente = Double.parseDouble(efec);

        if (efectivoCliente < TotalPagar) {
            JOptionPane.showMessageDialog(null, "CANTIDAD INSUFICIENTE");
            return;
        } else if (efectivoCliente == TotalPagar) {
            JOptionPane.showMessageDialog(null, "¡GRACIAS POR SU COMPRA!");
            txtEfectivo.setEditable(false);
            txtCambio.setText(String.valueOf(0));
            btnCrearFactura.setEnabled(true);
            btnVerificarEfectivo.setVisible(false);
        } else if (efectivoCliente > TotalPagar) {
            JOptionPane.showMessageDialog(null, "¡GRACIAS POR SU COMPRA!");
            txtEfectivo.setEditable(false);
            cambio = efectivoCliente - TotalPagar;
            txtCambio.setText(String.valueOf(cambio));
            btnCrearFactura.setEnabled(true);
            btnVerificarEfectivo.setVisible(false);
        }
    }

    public int obtenerStockProducto(String DescripcionProducto) throws SQLException {
        sentenciaSQL = "SELECT cantidad FROM inventario WHERE idProducto = ?";
        ps = con.prepareStatement(sentenciaSQL);
        ps.setString(1, DescripcionProducto);
        rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("cantidad");
        }
        //int cantInventario = rs.getInt("cantidad");
        return 0;
    }

    public int obtenerStockMinimoProducto() throws SQLException {
        String productoMin = (String) txtCodigo.getText();
        sentenciaSQL = "SELECT CantidadMinima FROM producto WHERE id = ?";
        ps = con.prepareStatement(sentenciaSQL);
        ps.setString(1, productoMin);
        rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("CantidadMinima");
        }
        return 0;
    }

    public void eliminarFilaTabla(int filaSeleccionada) throws SQLException {
        String codigoProducto = tblFactura.getValueAt(filaSeleccionada, 2).toString();
        int cantidadEliminar = Integer.parseInt(tblFactura.getValueAt(filaSeleccionada, 5).toString());
        double subtotalEliminar = Double.parseDouble(tblFactura.getValueAt(filaSeleccionada, 9).toString());

        // Actualizar las cantidades disponibles en la factura y en el inventario
        actualizarCantidadesDisponibles(codigoProducto, -cantidadEliminar);

        double totalActual = Double.parseDouble(txtGranTotal.getText());
        totalActual -= subtotalEliminar;
        txtGranTotal.setText(String.valueOf(totalActual));
        GRANTOTAL = totalActual;

        // Eliminar la fila de la tabla
        modeloOrden = (DefaultTableModel) tblFactura.getModel();
        modeloOrden.removeRow(filaSeleccionada);

        // Habilitar/deshabilitar botón de cerrar venta según si hay filas confirmadas o no
        btnCerrarVenta.setEnabled(!filasConfirmadas.isEmpty());

        // Eliminar el producto de la lista productosEnFactura
        Productos productoEnFactura = null;
        for (Productos producto : productosEnFactura) {
            if (producto.getCodigoProducto().equals(codigoProducto)) {
                productoEnFactura = producto;
                break;
            }
        }
        if (productoEnFactura != null) {
            productosEnFactura.remove(productoEnFactura);
        }
    }

    public void CargarCategoria() {
        cboCategoria.removeAllItems();
        /*cboCategoria.setSelectedIndex(0);
        this.cboCategoria.removeItem("CATEGORÍAS:");
        this.cboCategoria.addItem("CATEGORÍAS:");*/
        String Csql2 = "SELECT id, descripcion FROM categoria WHERE estado LIKE 'Activo'";
        try {
            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery(Csql2);
            //cboCategoria.setSelectedIndex(0);
            //cboCategoria.removeAllItems();
            this.cboCategoria.addItem("CATEGORÍAS:");
            cboCategoria.setSelectedIndex(0);
            while (rs2.next()) {
                this.cboCategoria.addItem(rs2.getString("descripcion"));
            } // FIN DEL WHILE

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void leerCategoria1() {
        //this.limpiar();
        /*sentenciaSQL = "SELECT P.id,P.descripcion,C.Nombre,P.costo,P.utilidad,P.precioVenta,PR.nombre FROM producto P INNER JOIN "
                + "categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE P.estado LIKE 'Activo'";*/
        sentenciaSQL = "SELECT I.idProducto,I.descripcion,P.precioVenta FROM inventario I INNER JOIN producto P ON I.idProducto = P.id "
                + "INNER JOIN categoria C ON P.idCategoria=C.id WHERE C.descripcion LIKE 'Postres/Bocadillos'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblNombreProducto.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                NombreProducto[0] = (rs.getString(1));
                NombreProducto[1] = (rs.getString(2));
                NombreProducto[2] = (rs.getString(3));
                // datosProductos[7] = (rs.getString(8));
                //datosProductos[8] = (rs.getString(9));
                modelo.addRow(NombreProducto);
            }
            //AGREGAR MODELO A LA TABLA
            tblNombreProducto.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    public void leerCategoria2() {
        //this.limpiar();
        /*sentenciaSQL = "SELECT P.id,P.descripcion,C.Nombre,P.costo,P.utilidad,P.precioVenta,PR.nombre FROM producto P INNER JOIN "
                + "categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE P.estado LIKE 'Activo'";*/
        sentenciaSQL = "SELECT I.idProducto,I.descripcion,P.precioVenta FROM inventario I INNER JOIN producto P ON I.idProducto = P.id "
                + "INNER JOIN categoria C ON P.idCategoria=C.id WHERE C.descripcion LIKE 'Bebidas'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblNombreProducto.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                NombreProducto[0] = (rs.getString(1));
                NombreProducto[1] = (rs.getString(2));
                NombreProducto[2] = (rs.getString(3));
                // datosProductos[7] = (rs.getString(8));
                //datosProductos[8] = (rs.getString(9));
                modelo.addRow(NombreProducto);
            }
            //AGREGAR MODELO A LA TABLA
            tblNombreProducto.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    public void leerCategoria3() {
        //this.limpiar();
        /*sentenciaSQL = "SELECT P.id,P.descripcion,C.Nombre,P.costo,P.utilidad,P.precioVenta,PR.nombre FROM producto P INNER JOIN "
                + "categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE P.estado LIKE 'Activo'";*/
        sentenciaSQL = "SELECT I.idProducto,I.descripcion,P.precioVenta FROM inventario I INNER JOIN producto P ON I.idProducto = P.id "
                + "INNER JOIN categoria C ON P.idCategoria=C.id WHERE C.descripcion LIKE 'Comida'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblNombreProducto.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                NombreProducto[0] = (rs.getString(1));
                NombreProducto[1] = (rs.getString(2));
                NombreProducto[2] = (rs.getString(3));
                // datosProductos[7] = (rs.getString(8));
                //datosProductos[8] = (rs.getString(9));
                modelo.addRow(NombreProducto);
            }
            //AGREGAR MODELO A LA TABLA
            tblNombreProducto.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    public void validarCampos() {
        String campo1 = txtNombreCliente.getText();
        String campo2 = txtNombreProducto.getText();
        String campo3 = txtTotal.getText();
        String campo4 = txtImpuesto.getText();
        String campo5 = txtPrecio.getText();
        String campo6 = txtRTN.getText();
        int valorSpinner = (int) jspCantidad.getValue();
        boolean campoRTNValido = campo6.matches("\\d{14}");
        boolean camposLetrasValidos = campo1.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+") && campo2.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");

        boolean camposNumerosValidos = !campo3.isEmpty() && !campo4.isEmpty()
                && !campo5.isEmpty() && (campo6.isEmpty() || campo6.length() == 14)
                && !campo3.equals("0.0") && !campo4.equals("0.0")
                && !campo5.equals("0.0");
        boolean spinnerValido = valorSpinner >= 1;

        // Validar que solo se ingrese uno de los campos RTN o DNI y el otro esté vacío
        //boolean soloUnCampoValido = (campoRTNValido && campo7.isEmpty()) || (campoDNIValido && campo6.isEmpty());
        boolean todosCamposValidos = camposLetrasValidos && camposNumerosValidos && spinnerValido;

        btnAñadirOrden.setEnabled(todosCamposValidos);

        if (todosCamposValidos) {
            double precio3 = Double.parseDouble(campo5);
            double impuesto2 = calcularImpuesto(precio3);
            int cantidad = (int) jspCantidad.getValue();
            double total = (precio3 + impuesto2) * cantidad;
            txtTotal.setText(String.valueOf(total));
            btnAñadirOrden.setEnabled(true);
            //btnCrearFactura.setEnabled(true);
        } else {
            //txtTotal.setText("0.0");
        }
    }

    public double calcularImpuesto(double precio) {
        String tipo = (String) cboCategoria.getSelectedItem();
        double impuesto = 0.0;
        //precio = Double.parseDouble(txtPrecio.getText());
        if (tipo.equals("Postres/Bocadillos")) {
            impuesto = impuestoPostre * precio;
            txtImpuesto.setText(String.valueOf(impuesto));
        } else if (tipo.equals("Bebidas")) {
            impuesto = impuestoBebidas * precio;
            txtImpuesto.setText(String.valueOf(impuesto));
        } else if (tipo.equals("Comida")) {
            impuesto = impuestoComida * precio;
            txtImpuesto.setText(String.valueOf(impuesto));
        }

        return impuesto;
    }

    public void filtrarProducto(String producto, String categoria) {
        modelo = (DefaultTableModel) tblNombreProducto.getModel();
        modelo.setRowCount(0);

        try {
            /*String query = "SELECT P.id, P.descripcion, P.precioVenta FROM producto P "
                    + "INNER JOIN categoria C ON P.idCategoria=C.id WHERE P.descripcion LIKE ? AND C.descripcion LIKE ?";*/
            String query = "SELECT I.idProducto,I.descripcion,P.precioVenta FROM inventario I INNER JOIN producto P ON I.idProducto = P.id "
                    + "INNER JOIN categoria C ON P.idCategoria=C.id WHERE I.descripcion LIKE ? AND C.descripcion LIKE ?";
            PreparedStatement stmt = con.prepareStatement(query);
            String likeFilter = "%" + producto + "%";
            stmt.setString(1, likeFilter);
            stmt.setString(2, categoria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID Producto: " + rs.getString("I.idProducto"));
                System.out.println("Descripción: " + rs.getString("I.descripcion"));
                System.out.println("Precio Venta: " + rs.getString("P.precioVenta"));

                Object[] fila = {
                    rs.getString("I.idProducto"),
                    rs.getString("I.descripcion"),
                    rs.getString("P.precioVenta")
                };
                modelo.addRow(fila);
            }
            stmt.close();
            rs.close();

        } catch (SQLException ex) {
            System.out.println("Error al obtener usuarios: " + ex.getMessage());
        }
    }

    /*public void imprimirFacturaTabla() {
        String carpetaFacturas = "facturas";
        File carpeta = new File(carpetaFacturas);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        String nombreArchivo = carpetaFacturas + File.separator + "factura_" + System.currentTimeMillis() + ".pdf";
        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            // Título predeterminado
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f);
            Paragraph titulo = new Paragraph("CAFETERÍA Grupo #6", fontTitulo);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(titulo);

            // Campos de texto (solo una vez)
            Font fontCampo = FontFactory.getFont(FontFactory.HELVETICA, 12f);
            documento.add(new Paragraph("\nNombre Cliente: " + txtNombreCliente.getText(), fontCampo));
            documento.add(new Paragraph("RTN: " + txtRTN.getText(), fontCampo));
            documento.add(new Paragraph("Efectivo: " + txtEfectivo.getText(), fontCampo));
            documento.add(new Paragraph("Cambio: " + txtCambio.getText() + "\n\n", fontCampo));

            // jTable de 11 columnas con varias filas
            // Supongamos que tienes un modelo de jTable llamado 'modeloOrden'
            PdfPTable table = new PdfPTable(9); // Columnas 2 a 10 (índices 1 a 9)
            table.setWidthPercentage(110);

            // Agregar encabezados de columnas (solo una vez)
            String[] headers = {"CÓDIGO", "NOMPRE PRODUCTO", "PRECIO", "CANTIDAD", "IMPUESTO", "TARJETA", "SUBTOTAL", "TOTAL", "FECHA"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(header));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Agregar filas al jTable (usando 'modeloOrden')
            DecimalFormat df = new DecimalFormat("#.00"); // Formato para redondear a 2 decimales
            for (int i = 0; i < modeloOrden.getRowCount(); i++) {
                for (int j = 2; j <= 10; j++) { // Columnas 2 a 10 (índices 1 a 9)
                    Object value = modeloOrden.getValueAt(i, j);
                    String formattedValue;
                    if (value instanceof Double) {
                        formattedValue = df.format((Double) value);
                    } else {
                        formattedValue = value.toString();
                    }
                    PdfPCell cell = new PdfPCell(new Phrase(formattedValue, fontCampo));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
            }

            // Agregar la tabla al documento
            documento.add(table);

            documento.close();
            System.out.println("PDF generado exitosamente. Ruta: " + nombreArchivo);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/
 /*public void imprimirFactura() {
        String carpetaFacturas = "facturas";
        File carpeta = new File(carpetaFacturas);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        String nombreArchivo = carpetaFacturas + File.separator + "factura_" + System.currentTimeMillis() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(nombreArchivo);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document documento = new Document(pdfDocument);

            // Título predeterminado
            Paragraph titulo = new Paragraph("CAFETERÍA Grupo #6")
                    .setTextAlignment(TextAlignment.CENTER);
            documento.add(titulo);

            // Campos de texto (solo una vez)
            String nombreCliente = txtNombreCliente.getText();
            String rtn = txtRTN.getText();
            String efectivo = txtEfectivo.getText();
            String cambio = txtCambio.getText();

            Paragraph clienteParrafo = new Paragraph("Nombre Cliente: " + nombreCliente)
                    .setTextAlignment(TextAlignment.LEFT);
            documento.add(clienteParrafo);

            Paragraph rtnParrafo = new Paragraph("RTN: " + rtn)
                    .setTextAlignment(TextAlignment.LEFT);
            documento.add(rtnParrafo);

            Paragraph efectivoParrafo = new Paragraph("Efectivo: " + efectivo)
                    .setTextAlignment(TextAlignment.LEFT);
            documento.add(efectivoParrafo);

            Paragraph cambioParrafo = new Paragraph("Cambio: " + cambio)
                    .setTextAlignment(TextAlignment.LEFT);
            documento.add(cambioParrafo);

            // Agregar los datos del jTable (usando 'modeloOrden')
            String headersTexto = String.format("%-10s %-20s %-10s %-10s %-10s %-10s %-10s %-10s",
                    "CÓDIGO", "NOMBRE PRODUCTO", "PRECIO", "CANTIDAD", "IMPUESTO", "TARJETA", "SUBTOTAL", "TOTAL");
            Paragraph headersParrafo = new Paragraph(headersTexto)
                    .setFontSize(8) // Ajustar el tamaño de la fuente
                    .setTextAlignment(TextAlignment.LEFT);
            documento.add(headersParrafo);

            DecimalFormat df = new DecimalFormat("#.00"); // Formato para redondear a 2 decimales

            // Obtener la longitud máxima de cada columna del jTable
            int[] columnWidths = new int[]{
                10, 20, 10, 10, 10, 10, 10, 10
            };
            for (int i = 0; i < modeloOrden.getRowCount(); i++) {
                for (int j = 2; j < 10; j++) { // Comenzar desde la columna 2 (CÓDIGO) hasta la columna 9 (TOTAL)
                    String cellValue = modeloOrden.getValueAt(i, j).toString();
                    int cellLength = cellValue.length();
                    if (cellLength > columnWidths[j - 2]) {
                        columnWidths[j - 2] = cellLength;
                    }
                }
            }

            // Crear los tabuladores con sus posiciones y alineaciones en función de la longitud máxima de cada columna
            TabStop[] tabStops = new TabStop[8];
            for (int i = 0; i < 8; i++) {
                tabStops[i] = new TabStop((columnWidths[i] + 1) * 10, TabAlignment.LEFT);
            }

            for (int i = 0; i < modeloOrden.getRowCount(); i++) {
                String codigo = modeloOrden.getValueAt(i, 2).toString();
                String producto = modeloOrden.getValueAt(i, 3).toString();
                String precio = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 4).toString()));
                String cantidad = modeloOrden.getValueAt(i, 5).toString();
                String impuesto = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 6).toString()));
                String tarjeta = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 7).toString()));
                String subtotal = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 8).toString()));
                String total = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 9).toString()));

                String filaTexto = String.format("%-10s \t%-20s \t%-10s \t%-10s \t%-10s \t%-10s \t%-10s \t%-10s",
                        codigo, producto, precio, cantidad, impuesto, tarjeta, subtotal, total);

                Paragraph filaParrafo = new Paragraph(filaTexto)
                        .setFontSize(8) // Ajustar el tamaño de la fuente
                        .setTextAlignment(TextAlignment.LEFT)
                        .addTabStops(tabStops);
                documento.add(filaParrafo);
            }

            documento.close();
            System.out.println("PDF generado exitosamente. Ruta: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    public void imprimirFactura() {
        String carpetaFacturas = "facturas";
        File carpeta = new File(carpetaFacturas);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        String nombreArchivo = carpetaFacturas + File.separator + "factura_" + System.currentTimeMillis() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(nombreArchivo);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document documento = new Document(pdfDocument);

            // Logo de la cafetería
            Image logoCafeteria = new Image(ImageDataFactory.create("src/imagenes/logoSinFondo.png"));
            logoCafeteria.scaleToFit(150, 150);
            logoCafeteria.setHorizontalAlignment(HorizontalAlignment.CENTER);
            documento.add(logoCafeteria);

            // Título de la factura
            Paragraph titulo = new Paragraph("CAFETERÍA Grupo #6")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold();
            documento.add(titulo);

            // Campos de texto (solo una vez)
            String nombreCliente = txtNombreCliente.getText();
            String rtn = txtRTN.getText();
            String efectivo = txtEfectivo.getText();
            String cambio = txtCambio.getText();
            String grantotal = txtGranTotal.getText();

            Paragraph clienteParrafo = new Paragraph("Nombre Cliente: " + nombreCliente)
                    .setFontSize(12)
                    .setMarginLeft(10);
            documento.add(clienteParrafo);

            Paragraph rtnParrafo = new Paragraph("RTN: " + rtn)
                    .setFontSize(12)
                    .setMarginLeft(10);
            documento.add(rtnParrafo);

            Paragraph efectivoParrafo = new Paragraph("Efectivo: " + efectivo)
                    .setFontSize(12)
                    .setMarginLeft(10);
            documento.add(efectivoParrafo);

            Paragraph cambioParrafo = new Paragraph("Cambio: " + cambio)
                    .setFontSize(12)
                    .setMarginLeft(10);
            documento.add(cambioParrafo);
            
            Paragraph totalParrafo = new Paragraph("TOTAL: " + grantotal)
                    .setFontSize(12)
                    .setMarginLeft(10);
            documento.add(totalParrafo);

            // Agregar los datos del jTable (usando 'modeloOrden')
            String headersTexto = String.format("%-10s %-20s %-10s %-10s %-10s %-10s %-10s %-10s",
                    "CÓDIGO", "NOMBRE PRODUCTO", "PRECIO", "CANTIDAD", "IMPUESTO", "TARJETA", "SUBTOTAL", "TOTAL");
            Paragraph headersParrafo = new Paragraph(headersTexto)
                    .setFontSize(8)
                    .setMarginLeft(10)
                    .setMarginTop(10);
            documento.add(headersParrafo);

            DecimalFormat df = new DecimalFormat("#.00"); // Formato para redondear a 2 decimales

            for (int i = 0; i < modeloOrden.getRowCount(); i++) {
                String codigo = modeloOrden.getValueAt(i, 2).toString();
                String producto = modeloOrden.getValueAt(i, 3).toString();
                String precio = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 4).toString()));
                String cantidad = modeloOrden.getValueAt(i, 5).toString();
                String impuesto = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 6).toString()));
                String tarjeta = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 7).toString()));
                String subtotal = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 8).toString()));
                String total = df.format(Double.parseDouble(modeloOrden.getValueAt(i, 9).toString()));

                String filaTexto = String.format("%-10s %-20s %-10s %-10s %-10s %-10s %-10s %-10s",
                        codigo, producto, precio, cantidad, impuesto, tarjeta, subtotal, total);

                Paragraph filaParrafo = new Paragraph(filaTexto)
                        .setFontSize(8)
                        .setMarginLeft(10);
                documento.add(filaParrafo);
            }

            documento.close();
            System.out.println("PDF generado exitosamente. Ruta: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtNombreCliente = new javax.swing.JTextField();
        btnImprimir = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jspCantidad = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtImpuesto = new javax.swing.JTextField();
        txtCodigo = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNombreProducto = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFactura = new javax.swing.JTable();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtEfectivo = new javax.swing.JTextField();
        txtCambio = new javax.swing.JTextField();
        btnAñadirOrden = new javax.swing.JButton();
        btnCrearFactura = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtGranTotal = new javax.swing.JTextField();
        btnCerrarVenta = new javax.swing.JButton();
        btnVerificarEfectivo = new javax.swing.JButton();
        txtRTN = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtBuscarProducto = new javax.swing.JTextField();
        btnCerrar = new javax.swing.JButton();
        btnAyuda1 = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setBackground(new java.awt.Color(241, 222, 201));
        txtNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtNombreCliente.setEnabled(false);
        txtNombreCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreClienteActionPerformed(evt);
            }
        });
        txtNombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyTyped(evt);
            }
        });
        getContentPane().add(txtNombreCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 250, 250, 30));

        btnImprimir.setBackground(new java.awt.Color(54, 21, 0));
        btnImprimir.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnImprimir.setForeground(new java.awt.Color(227, 202, 165));
        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/recibo_factura.png"))); // NOI18N
        btnImprimir.setText("Imprimir Factura");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });
        getContentPane().add(btnImprimir, new org.netbeans.lib.awtextra.AbsoluteConstraints(1640, 850, 230, 40));

        jLabel15.setBackground(new java.awt.Color(241, 222, 201));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setText("Nombre Cliente");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 250, 140, 30));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("USUARIO:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 10, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 10, 190, 30));

        jLabel7.setBackground(new java.awt.Color(241, 222, 201));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("RTN");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 300, 100, 30));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel17.setText("Nombre Producto");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 400, 150, 30));

        txtNombreProducto.setEditable(false);
        txtNombreProducto.setBackground(new java.awt.Color(241, 222, 201));
        txtNombreProducto.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtNombreProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreProductoKeyReleased(evt);
            }
        });
        getContentPane().add(txtNombreProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 400, 250, 30));

        txtPrecio.setEditable(false);
        txtPrecio.setBackground(new java.awt.Color(241, 222, 201));
        txtPrecio.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPrecioKeyReleased(evt);
            }
        });
        getContentPane().add(txtPrecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 250, 230, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setText("Precio");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 250, 100, 30));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("Cantidad");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 300, 100, 30));

        jspCantidad.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jspCantidad.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jspCantidad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(241, 222, 201), 2, true));
        jspCantidad.setFocusable(false);
        jspCantidad.setRequestFocusEnabled(false);
        jspCantidad.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jspCantidadStateChanged(evt);
            }
        });
        jspCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jspCantidadKeyReleased(evt);
            }
        });
        getContentPane().add(jspCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 300, 230, 30));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel9.setText("Total");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 250, 100, 30));

        txtTotal.setEditable(false);
        txtTotal.setBackground(new java.awt.Color(241, 222, 201));
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotalKeyReleased(evt);
            }
        });
        getContentPane().add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1540, 250, 250, 30));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel18.setText("Impuesto");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 350, 100, 30));

        txtImpuesto.setEditable(false);
        txtImpuesto.setBackground(new java.awt.Color(241, 222, 201));
        txtImpuesto.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtImpuesto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImpuestoKeyReleased(evt);
            }
        });
        getContentPane().add(txtImpuesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 350, 230, 30));

        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new java.awt.Color(241, 222, 201));
        txtCodigo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
        });
        getContentPane().add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 350, 250, 30));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel19.setText("Código Producto");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 350, 140, 30));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setText("Subtotal");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 400, 100, 30));

        txtSubtotal.setEditable(false);
        txtSubtotal.setBackground(new java.awt.Color(241, 222, 201));
        txtSubtotal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtSubtotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSubtotalKeyReleased(evt);
            }
        });
        getContentPane().add(txtSubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 400, 230, 30));

        tblNombreProducto.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblNombreProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "PRODUCTO", "PRECIO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNombreProducto.getTableHeader().setReorderingAllowed(false);
        tblNombreProducto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblNombreProductoFocusGained(evt);
            }
        });
        tblNombreProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNombreProductoMouseClicked(evt);
            }
        });
        tblNombreProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblNombreProductoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblNombreProductoKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblNombreProducto);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 590, 590, 240));

        tblFactura.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CLIENTE", "RTN", "CÓDIGO", "PRODUCTO", "PRECIO", "CANTIDAD", "IMPUESTO", "TARJETA", "SUBTOTAL", "TOTAL", "FECHA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblFactura.getTableHeader().setReorderingAllowed(false);
        tblFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFacturaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblFactura);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 590, 1240, 240));

        cboCategoria.setBackground(new java.awt.Color(227, 202, 165));
        cboCategoria.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        cboCategoria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboCategoriaItemStateChanged(evt);
            }
        });
        cboCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cboCategoriaMouseClicked(evt);
            }
        });
        cboCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoriaActionPerformed(evt);
            }
        });
        cboCategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboCategoriaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cboCategoriaKeyReleased(evt);
            }
        });
        getContentPane().add(cboCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 490, 300, 30));

        jLabel16.setBackground(new java.awt.Color(241, 222, 201));
        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel16.setText("Efectivo");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 300, 100, 30));

        jLabel11.setBackground(new java.awt.Color(241, 222, 201));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel11.setText("Cambio");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 350, 100, 30));

        txtEfectivo.setBackground(new java.awt.Color(241, 222, 201));
        txtEfectivo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtEfectivo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtEfectivoFocusGained(evt);
            }
        });
        txtEfectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEfectivoActionPerformed(evt);
            }
        });
        txtEfectivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEfectivoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEfectivoKeyTyped(evt);
            }
        });
        getContentPane().add(txtEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1540, 300, 250, 30));

        txtCambio.setEditable(false);
        txtCambio.setBackground(new java.awt.Color(241, 222, 201));
        txtCambio.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCambioActionPerformed(evt);
            }
        });
        txtCambio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCambioKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCambioKeyTyped(evt);
            }
        });
        getContentPane().add(txtCambio, new org.netbeans.lib.awtextra.AbsoluteConstraints(1540, 350, 250, 30));

        btnAñadirOrden.setBackground(new java.awt.Color(54, 21, 0));
        btnAñadirOrden.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnAñadirOrden.setForeground(new java.awt.Color(227, 202, 165));
        btnAñadirOrden.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crear.png"))); // NOI18N
        btnAñadirOrden.setText("Añadir Orden");
        btnAñadirOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirOrdenActionPerformed(evt);
            }
        });
        getContentPane().add(btnAñadirOrden, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 460, 200, 40));

        btnCrearFactura.setBackground(new java.awt.Color(54, 21, 0));
        btnCrearFactura.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnCrearFactura.setForeground(new java.awt.Color(227, 202, 165));
        btnCrearFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crearFactura.png"))); // NOI18N
        btnCrearFactura.setText("Crear Factura");
        btnCrearFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearFacturaActionPerformed(evt);
            }
        });
        getContentPane().add(btnCrearFactura, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 520, 230, 50));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setText("Gran Total");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 400, 100, 30));

        txtGranTotal.setEditable(false);
        txtGranTotal.setBackground(new java.awt.Color(241, 222, 201));
        txtGranTotal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtGranTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGranTotalActionPerformed(evt);
            }
        });
        txtGranTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGranTotalKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtGranTotalKeyTyped(evt);
            }
        });
        getContentPane().add(txtGranTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1540, 400, 250, 30));

        btnCerrarVenta.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrarVenta.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnCerrarVenta.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrarVenta.png"))); // NOI18N
        btnCerrarVenta.setText("Cerrar Venta");
        btnCerrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarVentaActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrarVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 510, 200, 40));

        btnVerificarEfectivo.setBackground(new java.awt.Color(54, 21, 0));
        btnVerificarEfectivo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnVerificarEfectivo.setForeground(new java.awt.Color(227, 202, 165));
        btnVerificarEfectivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/efectivo.png"))); // NOI18N
        btnVerificarEfectivo.setText("Verificar Efectivo");
        btnVerificarEfectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarEfectivoActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerificarEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1400, 460, 230, 40));

        txtRTN.setEditable(false);
        txtRTN.setBackground(new java.awt.Color(241, 222, 201));
        txtRTN.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##############"))));
        txtRTN.setEnabled(false);
        getContentPane().add(txtRTN, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 300, 250, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("FACTURACIÓN");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 110, -1, -1));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel20.setText("Buscar Producto");
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 540, 150, 30));

        txtBuscarProducto.setBackground(new java.awt.Color(241, 222, 201));
        txtBuscarProducto.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductoActionPerformed(evt);
            }
        });
        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyReleased(evt);
            }
        });
        getContentPane().add(txtBuscarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 540, 250, 30));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1840, 0, 50, 50));

        btnAyuda1.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda1.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1760, 0, 50, 50));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1890, 910));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreClienteActionPerformed

    private void txtNombreClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtNombreClienteKeyReleased

    private void txtNombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreClienteKeyTyped

    private void jspCantidadStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jspCantidadStateChanged
        //validarCampos();
        int cantidadC = (int) jspCantidad.getValue();

        if (cantidadC < 0) {
            JOptionPane.showMessageDialog(null, "NO CANTIDADES NEGATIVAS");
            jspCantidad.setValue(1);
            txtTotal.setText(String.valueOf(0.0));
        } else if (cantidadC > 1) {
            validarCampos();
            //btnAñadirOrden.setEnabled(true);
            double precio3 = Double.parseDouble(txtPrecio.getText().trim());
            double impuesto2 = calcularImpuesto(precio3);
            int cantidad = (int) jspCantidad.getValue();
            double subtotal = precio3 * cantidad;
            txtSubtotal.setText(String.valueOf(subtotal));
            double total = (precio3 + impuesto2) * cantidad;
            txtTotal.setText(String.valueOf(total));
        } else if (cantidadC == 1) {
            //validarCampos();
            double precio3 = Double.parseDouble(txtPrecio.getText().trim());
            double impuesto2 = calcularImpuesto(precio3);
            int cantidad = (int) jspCantidad.getValue();
            double subtotal = precio3 * cantidad;
            txtSubtotal.setText(String.valueOf(subtotal));
            double total = (precio3 + impuesto2) * cantidad;
            txtTotal.setText(String.valueOf(total));
        }
        validarCampos();
    }//GEN-LAST:event_jspCantidadStateChanged

    private void tblNombreProductoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNombreProductoMouseClicked
        //validarCampos();
        int fila = tblNombreProducto.getSelectedRow();
        txtCodigo.setText(tblNombreProducto.getValueAt(fila, 0).toString());
        txtNombreProducto.setText(tblNombreProducto.getValueAt(fila, 1).toString());
        txtPrecio.setText(tblNombreProducto.getValueAt(fila, 2).toString());
        //validarCampos();
        int cantidadC = (int) jspCantidad.getValue();

        if (cantidadC < 0) {
            JOptionPane.showMessageDialog(null, "NO CANTIDADES NEGATIVAS");
            jspCantidad.setValue(1);
            txtTotal.setText(String.valueOf(0));
        } else if (cantidadC > 1) {
            //validarCampos();
            //btnAñadirOrden.setEnabled(true);
            double precio3 = Double.parseDouble(txtPrecio.getText().trim());
            double impuesto2 = calcularImpuesto(precio3);
            int cantidad = (int) jspCantidad.getValue();
            double subtotal = precio3 * cantidad;
            double total = (precio3 + impuesto2) * cantidad;
            txtSubtotal.setText(String.valueOf(subtotal));
            txtTotal.setText(String.valueOf(total));
            btnAñadirOrden.setEnabled(true);
        } else if (cantidadC == 1) {
            //validarCampos();
            //btnAñadirOrden.setEnabled(true);
            double precio3 = Double.parseDouble(txtPrecio.getText().trim());
            double impuesto2 = calcularImpuesto(precio3);
            int cantidad = (int) jspCantidad.getValue();
            double subtotal = precio3 * cantidad;
            txtSubtotal.setText(String.valueOf(subtotal));
            double total = (precio3 + impuesto2) * cantidad;
            txtTotal.setText(String.valueOf(total));
            btnAñadirOrden.setEnabled(true);
        }
        validarCampos();
    }//GEN-LAST:event_tblNombreProductoMouseClicked

    private void cboCategoriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboCategoriaItemStateChanged
        validarCampos();
        String tipo = (String) cboCategoria.getSelectedItem();

        //Double impuesto2;
        //Double precio2 = 0.0;
        if (tipo.equals("Postres/Bocadillos")) {
            //DefaultTableModel modelo = new DefaultTableModel();
            int fila = tblNombreProducto.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            modelo.setRowCount(0);
            this.leerCategoria1();
            txtNombreProducto.setText("");
            txtCodigo.setText("");
            txtImpuesto.setText("0.0");
            txtPrecio.setText("0.0");
            txtSubtotal.setText("0.0");
            txtTotal.setText("0.0");
            jspCantidad.setValue(1);
        } else if (tipo.equals("Bebidas")) {
            //DefaultTableModel modelo = new DefaultTableModel();
            int fila = tblNombreProducto.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            modelo.setRowCount(0);
            this.leerCategoria2();
            txtNombreProducto.setText("");
            txtCodigo.setText("");
            txtImpuesto.setText("0.0");
            txtPrecio.setText("0.0");
            txtSubtotal.setText("0.0");
            txtTotal.setText("0.0");
            jspCantidad.setValue(1);
        } else if (tipo.equals("Comida")) {
            //DefaultTableModel modelo = new DefaultTableModel();
            int fila = tblNombreProducto.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            modelo.setRowCount(0);
            this.leerCategoria3();
            txtNombreProducto.setText("");
            txtCodigo.setText("");
            txtImpuesto.setText("0.0");
            txtPrecio.setText("0.0");
            txtSubtotal.setText("0.0");
            txtTotal.setText("0.0");
            jspCantidad.setValue(1);
        } else if (tipo.equals("CATEGORÍAS:")) {
            int fila = tblNombreProducto.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            modelo.setRowCount(0);
            txtNombreProducto.setText("");
            txtCodigo.setText("");
            txtImpuesto.setText("0.0");
            txtPrecio.setText("0.0");
            txtSubtotal.setText("0.0");
            txtTotal.setText("0.0");
            jspCantidad.setValue(1);
        }
    }//GEN-LAST:event_cboCategoriaItemStateChanged

    private void cboCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cboCategoriaMouseClicked
        validarCampos();
    }//GEN-LAST:event_cboCategoriaMouseClicked

    private void cboCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCategoriaActionPerformed

    private void cboCategoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboCategoriaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCategoriaKeyPressed

    private void cboCategoriaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboCategoriaKeyReleased

    }//GEN-LAST:event_cboCategoriaKeyReleased

    private void txtEfectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEfectivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEfectivoActionPerformed

    private void txtEfectivoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEfectivoKeyReleased
        if (txtEfectivo.getText().isEmpty()) {
            btnVerificarEfectivo.setEnabled(false);
        } else {
            btnVerificarEfectivo.setEnabled(true);
        }
    }//GEN-LAST:event_txtEfectivoKeyReleased

    private void txtEfectivoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEfectivoKeyTyped
        if (txtEfectivo.getText().isEmpty()) {
            btnVerificarEfectivo.setEnabled(false);
        } else {
            btnVerificarEfectivo.setEnabled(true);
        }

    }//GEN-LAST:event_txtEfectivoKeyTyped

    private void txtCambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCambioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCambioActionPerformed

    private void txtCambioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCambioKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCambioKeyReleased

    private void txtCambioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCambioKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCambioKeyTyped

    private void btnAñadirOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirOrdenActionPerformed
        agregarFilaTabla();
    }//GEN-LAST:event_btnAñadirOrdenActionPerformed

    private void btnCrearFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearFacturaActionPerformed
        crearFactura();
        //limpiar();
    }//GEN-LAST:event_btnCrearFacturaActionPerformed

    private void txtGranTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGranTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGranTotalActionPerformed

    private void txtGranTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGranTotalKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGranTotalKeyReleased

    private void txtGranTotalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGranTotalKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGranTotalKeyTyped

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtCodigoKeyReleased

    private void txtNombreProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreProductoKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtNombreProductoKeyReleased

    private void txtPrecioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtPrecioKeyReleased

    private void jspCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jspCantidadKeyReleased
        validarCampos();
    }//GEN-LAST:event_jspCantidadKeyReleased

    private void txtSubtotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSubtotalKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtSubtotalKeyReleased

    private void txtTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtTotalKeyReleased

    private void btnCerrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarVentaActionPerformed
        cerrarVenta();
        if (txtEfectivo.getText().isEmpty()) {
            btnVerificarEfectivo.setEnabled(false);
        } else {
            btnVerificarEfectivo.setEnabled(true);
        }
    }//GEN-LAST:event_btnCerrarVentaActionPerformed

    private void btnVerificarEfectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerificarEfectivoActionPerformed
        cambio();
    }//GEN-LAST:event_btnVerificarEfectivoActionPerformed

    private void tblFacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFacturaMouseClicked
        int fila = tblFactura.getSelectedRow();
        if (fila != -1) {
            //modeloOrden.removeRow(fila);

            try {
                // Obtener el código y la cantidad del producto eliminado
                /*String codigoProducto = (String) modeloOrden.getValueAt(fila, 2); // Asumo que el código está en la columna 2
                int cantidadEliminada = Integer.parseInt(modeloOrden.getValueAt(fila, 5).toString()); // Asumo que la cantidad está en la columna 5 (index 4 en base 0)

                // Actualizar el HashMap de cantidadesDisponiblesEnFactura
                int cantidadDisponibleEnFactura = cantidadesDisponiblesEnFactura.getOrDefault(codigoProducto, 0);
                cantidadesDisponiblesEnFactura.put(codigoProducto, (cantidadDisponibleEnFactura - cantidadEliminada));

                // Actualizar el HashMap de cantidadDisponibleProductos
                int stockProducto = obtenerStockProducto(codigoProducto);
                int cantidadDisponibleEnProducto = cantidadDisponibleProductos.getOrDefault(codigoProducto, 0);
                cantidadDisponibleProductos.put(codigoProducto, (stockProducto + cantidadDisponibleEnFactura));
                modeloOrden.removeRow(fila);*/
                eliminarFilaTabla(fila);

            } catch (SQLException ex) {
                Logger.getLogger(Facturacion.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*int filaSeleccionada = tblFactura.getSelectedRow();
        try {
            eliminarFilaTabla(filaSeleccionada);
        } catch (SQLException ex) {
            Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }//GEN-LAST:event_tblFacturaMouseClicked

    private void txtBuscarProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyReleased
        String nombreProducto = txtBuscarProducto.getText();
        String nombreCategoria = (String) cboCategoria.getSelectedItem();
        filtrarProducto(nombreProducto, nombreCategoria);
    }//GEN-LAST:event_txtBuscarProductoKeyReleased

    private void txtBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProductoActionPerformed

    private void tblNombreProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblNombreProductoKeyReleased
        //validarCampos();
    }//GEN-LAST:event_tblNombreProductoKeyReleased

    private void txtImpuestoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpuestoKeyReleased
        //validarCampos();
    }//GEN-LAST:event_txtImpuestoKeyReleased

    private void tblNombreProductoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblNombreProductoKeyPressed
        //validarCampos();
    }//GEN-LAST:event_tblNombreProductoKeyPressed

    private void tblNombreProductoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblNombreProductoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tblNombreProductoFocusGained

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimirFactura();

        /*this.dispose();
        this.setVisible(true);
        this.repaint();*/
        limpiar();
        validarFactura();
        //this.dispose();
        //imprimirFacturaTabla();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void txtEfectivoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEfectivoFocusGained
        if (txtEfectivo.getText().isEmpty()) {
            btnVerificarEfectivo.setEnabled(false);
        } else {
            btnVerificarEfectivo.setEnabled(true);
        }
    }//GEN-LAST:event_txtEfectivoFocusGained

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnAyuda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda1ActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame Facturacion = null;
        AyudaFacturacion dialog2 = new AyudaFacturacion(Facturacion, true);
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyuda1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Facturacion dialog = new Facturacion(new javax.swing.JFrame(), true, correoUsu);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAyuda1;
    private javax.swing.JButton btnAñadirOrden;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCerrarVenta;
    private javax.swing.JButton btnCrearFactura;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnVerificarEfectivo;
    private javax.swing.JComboBox<String> cboCategoria;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JSpinner jspCantidad;
    private javax.swing.JTable tblFactura;
    private javax.swing.JTable tblNombreProducto;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtCambio;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtEfectivo;
    private javax.swing.JTextField txtGranTotal;
    private javax.swing.JTextField txtImpuesto;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JFormattedTextField txtRTN;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
