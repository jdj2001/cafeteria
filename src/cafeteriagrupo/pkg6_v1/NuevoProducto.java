/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaProductos;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.util.regex.Pattern;

import java.sql.Date;
import java.time.LocalDate;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Juan Alvarado
 */
public class NuevoProducto extends javax.swing.JDialog {

    String codCategoria, codProveedor, sentenciaSQL;
    Double costo;
    Double utilidad = 0.0, utilidadTotal = 0.0, utilidadBebidas = 0.4, utilidadComida = 0.45, UtilidadPostresBocadillos = 0.5;
    Double utilidad2 = 0.0;
    Double precio;
    Double precio2;
    Double costo2 = 0.0;
    int stockMin = 0, stockMax = 0;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;

    private static String correoUsu;
    ResultSet rs = null; //PERMITE TENER METODOS PARA OBTENER INFO DE LAS DIFERENTES COLUMNAS DE UNA FILA=TIPO RESULT SET VARIABE
    DefaultTableModel modelo; //PARA EL MÓDELO DE NUESTRA TABLA
    Object datosProductos[] = new Object[9];
    ImageIcon imagen;
    ImageIcon icono;

    /**
     * Creates new form NuevoProducto
     */
    public NuevoProducto(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        this.setLocationRelativeTo(null);
        this.ConectarBD();
        this.CargarProveedor();
        this.correoUsu = user;
        btnGuardar.setEnabled(false);
        jblUsuario.setText(correoUsu);
        //txtCodigo.setText("");
        cambioImagen("dibujos.png", fondo);
        cambioImagen("logoSinFondo.png", jblLogo);

    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCosto.setText("");
        cboCategoria.setSelectedIndex(0);
        cboProveedor.setSelectedIndex(0);
        txtPrecio.setText("");
        //cboProveedor.removeAllItems();
        txtMinimo.setText("");
        txtMaximo.setText("");
        //txtNombre.requestFocus();//

        int fila = tblProductos.getRowCount();//TRAYENDO X CANTIDAD DE REGISTROS. CUANTAS FILAS TENEMOS EN JTABLE
        //CICLO INVERSO. EMPIEZA EN EL MAYOR HASTA MENOR. AGARRA DE ABAJO HASTA ARRIBA. VA IR REMOVIENDOLAS
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);//FILA X HASTA LLEGAR A CERO
        }
        btnGuardar.setVisible(true);
    }

    public void CargarProveedor() {
        cboProveedor.removeAllItems();
        cboCategoria.removeAllItems();
        /*cboCategoria.setSelectedIndex(0);
        this.cboCategoria.removeItem("CATEGORÍAS:");
        this.cboCategoria.addItem("CATEGORÍAS:");*/
        String Csql = "SELECT id, descripcion FROM proveedor";
        String Csql2 = "SELECT id, descripcion FROM categoria WHERE estado LIKE 'Activo'";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(Csql); //EJECUTA UNA CONSULTA

            this.cboProveedor.addItem("PROVEEDORES:");
            cboProveedor.setSelectedIndex(0);
            while (rs.next()) {
                this.cboProveedor.addItem(rs.getString("id") + " | " + rs.getString("descripcion"));
            } // FIN DEL WHILE

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery(Csql2);
            //cboCategoria.setSelectedIndex(0);
            //cboCategoria.removeAllItems();
            this.cboCategoria.addItem("CATEGORÍAS:");
            cboCategoria.setSelectedIndex(0);
            while (rs2.next()) {
                this.cboCategoria.addItem(rs2.getString("id") + " | " + rs2.getString("descripcion"));
            } // FIN DEL WHILE

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void proveedorJTable() {
        //cboProveedor.removeAllItems();
        cboProveedor.setSelectedIndex(0);
        String Csql = "SELECT P.idProveedor,PR.descripcion FROM producto P INNER JOIN proveedor PR ON P.idProveedor = PR.id WHERE P.id =" + txtCodigo.getText().trim();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(Csql); //EJECUTA UNA CONSULTA
            while (rs.next()) {
                this.cboProveedor.setSelectedItem(rs.getString("P.idProveedor") + " | " + rs.getString("PR.descripcion"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void categoriaJTable() {
        //cboCategoria.removeAllItems();
        cboCategoria.setSelectedIndex(0);
        String Csql = "SELECT P.idCategoria,C.descripcion FROM producto P INNER JOIN categoria C ON P.idCategoria = C.id WHERE P.id =" + txtCodigo.getText().trim();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(Csql); //EJECUTA UNA CONSULTA
            while (rs.next()) {
                this.cboCategoria.setSelectedItem(rs.getString("P.idCategoria") + " | " + rs.getString("C.descripcion"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void crearProducto() {
        codProveedor = (String) cboProveedor.getSelectedItem();
        int posicion = codProveedor.indexOf("|");
        String idProveedor = codProveedor.substring(0, posicion).trim();
        //NOMBRE PROVEEDOR
        String nomProveedor = (String) cboProveedor.getSelectedItem();
        int posicion2 = nomProveedor.indexOf("|");
        // Extraer la subcadena desde la posición del carácter | más uno hasta el final
        String nombreProveedor = nomProveedor.substring(posicion + 1);
        //CATEGORIA
        codCategoria = (String) cboCategoria.getSelectedItem();
        // Obtener la posición del carácter |
        int posicion3 = codCategoria.indexOf("|");
        // Extraer la subcadena desde el inicio hasta la posición del carácter |
        String idCategoria = codCategoria.substring(0, posicion3).trim();
        //NOMBRE CATEGORIA
        // Obtener el valor seleccionado en el cboProveedor
        String nomCategoria = (String) cboCategoria.getSelectedItem();
        // Obtener la posición del carácter |
        int posicion4 = nomCategoria.indexOf("|");
        // Extraer la subcadena desde la posición del carácter | más uno hasta el final
        String nombreCategoria = nomCategoria.substring(posicion3 + 1);
        //----------------
        costo = Double.parseDouble(txtCosto.getText());
        //utilidad = utilidad2;
        precio = Double.parseDouble(txtPrecio.getText());
        utilidad2 = utilidad2;
        stockMin = Integer.parseInt(txtMinimo.getText());
        stockMax = Integer.parseInt(txtMaximo.getText());
        String categoria = (String) cboCategoria.getSelectedItem();

        if (stockMin > stockMax || stockMax < stockMin) {
            JOptionPane.showMessageDialog(null, "El valor mínimo no puede ser mayor al valor máximo y maximo no puede ser menor que mínimo");
            return;
        }
        if (categoria.equals("CATEGORIA:")) {
            categoria = "";
        }
        if (txtNombre.getText().isEmpty() || cboProveedor.getSelectedIndex() == 0  || cboCategoria.getSelectedIndex() == 0  || txtCosto.getText().isEmpty()
                || txtMinimo.getText().isEmpty() || txtMaximo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de crear el producto");
            return;
        }
        try {
            //ConectarBD();//METODO ANTERIOR
            sentenciaSQL = "INSERT INTO producto (id,descripcion,idProveedor,idCategoria,costo,utilidad,precioVenta,CantidadMinima,CantidadMaxima,estado,fechaCreacion)VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setString(2, txtNombre.getText());
            /*ps.setInt(3, Integer.parseInt(idProveedor));
            ps.setInt(4, Integer.parseInt(idCategoria));*/
            ps.setString(3, idProveedor);
            ps.setString(4, idCategoria);
            ps.setDouble(5, costo);
            ps.setDouble(6, utilidad2);
            ps.setDouble(7, precio);
            ps.setInt(8, stockMin);
            ps.setInt(9, stockMax);
            ps.setString(10, "Activo");
            LocalDate fechaActual = LocalDate.now();
            Date fechaSQL = Date.valueOf(fechaActual);
            ps.setDate(11, fechaSQL);

            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(NuevoProducto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
        }
    }

    public void leerProducto() {
        //this.limpiar();
        sentenciaSQL = "SELECT P.id,P.descripcion,C.descripcion,P.costo,P.utilidad,P.precioVenta,P.CantidadMinima,P.CantidadMaxima,PR.descripcion FROM producto P INNER JOIN "
                + "categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE P.estado LIKE 'Activo'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblProductos.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                datosProductos[0] = (rs.getString(1));
                datosProductos[1] = (rs.getString(2));
                datosProductos[2] = (rs.getString(3));
                datosProductos[3] = (rs.getString(4));
                datosProductos[4] = (rs.getString(5));
                datosProductos[5] = (rs.getString(6));
                datosProductos[6] = (rs.getString(7));
                datosProductos[7] = (rs.getString(8));
                datosProductos[8] = (rs.getString(9));
                // datosProductos[7] = (rs.getString(8));
                //datosProductos[8] = (rs.getString(9));
                modelo.addRow(datosProductos);//AGREGARLE LAS FILAS AL MODELO    
            }
            //AGREGAR MODELO A LA TABLA
            tblProductos.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    // NO SE MODIFICA EL CODIGO, SINO EL PARAMETRO. TAMBIÉN DEBE LLEVAR ID DE REGISTRO, NO SOLO IDENTIDAD
    public void actualizarProducto() {
        if (txtNombre.getText().isEmpty() || cboProveedor.getSelectedIndex() == 0  || cboCategoria.getSelectedIndex() == 0  || txtCosto.getText().isEmpty()
                || txtMinimo.getText().isEmpty() || txtMaximo.getText().isEmpty() || txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de actualizar el producto");
            return;
        }
        /*costo = Double.parseDouble(txtCosto.getText());
        utilidadTotal = utilidadTotal + (utilidad * costo);
        precio = costo + utilidadTotal;
        stockMin = Integer.parseInt(txtMinimo.getText());
        stockMax = Integer.parseInt(txtMaximo.getText());*/

        costo = Double.parseDouble(txtCosto.getText());

        precio = Double.parseDouble(txtPrecio.getText());

        stockMin = Integer.parseInt(txtMinimo.getText());
        stockMax = Integer.parseInt(txtMaximo.getText());
        codProveedor = (String) cboProveedor.getSelectedItem();
        int posicion = codProveedor.indexOf("|");
        String idProveedor = codProveedor.substring(0, posicion).trim();
        String nomProveedor = (String) cboProveedor.getSelectedItem();
        int posicion2 = nomProveedor.indexOf("|");
        String nombreProveedor = nomProveedor.substring(posicion + 1);
        //CATEGORIA
        codCategoria = (String) cboCategoria.getSelectedItem();
        int posicion3 = codCategoria.indexOf("|");
        String idCategoria = codCategoria.substring(0, posicion3).trim();
        String nomCategoria = (String) cboCategoria.getSelectedItem();
        int posicion4 = nomCategoria.indexOf("|");
        String nombreCategoria = nomCategoria.substring(posicion3 + 1);
        try {
            sentenciaSQL = "UPDATE producto SET descripcion=?,idProveedor=?,idCategoria=?,costo=?,utilidad=?,"
                    + "precioVenta=?,CantidadMinima=?,CantidadMaxima=? WHERE id=?";
            ps = con.prepareStatement(sentenciaSQL);
            //EL NUMERO SIGNIFICA EL PRIMER PARÁMETRO, NO DE COLUMNA. DE AQUI ES DONDE OBTIENE DE LA CAJA DE TEXTO Y LO MANDA AL PARAMETRO
            ps.setString(1, txtNombre.getText());
            ps.setInt(2, Integer.parseInt(idProveedor));
            ps.setInt(3, Integer.parseInt(idCategoria));
            ps.setDouble(4, costo);
            ps.setDouble(5, utilidad2);
            ps.setDouble(6, precio);
            ps.setInt(7, stockMin);
            ps.setInt(8, stockMax);
            ps.setString(9, txtCodigo.getText());
            // Obtener la fecha actual
            /*LocalDate fechaActual = LocalDate.now();
            Date fechaSQL = Date.valueOf(fechaActual);
            ps.setDate(10, fechaSQL);*/
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(NuevoProducto.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR DATOS" + ex.getMessage());
        }
    }

    public void eliminarProducto() {
        if (txtNombre.getText().isEmpty() || cboProveedor.getSelectedIndex() == 0  || cboCategoria.getSelectedIndex() == 0  || txtCosto.getText().isEmpty()
                || txtMinimo.getText().isEmpty() || txtMaximo.getText().isEmpty() || txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar el producto");
            return;
        }
        try {
            // Obtener el stock del producto en el inventario
            int stockProducto = obtenerStockProducto(txtNombre.getText().trim());

            // Verificar si hay existencia en el inventario
            if (stockProducto > 0) {
                JOptionPane.showMessageDialog(null, "No se puede eliminar el producto, todavía hay existencia en el inventario.");
                return; // No se elimina el producto
            } else {
                // Si no hay existencia en el inventario, proceder con la eliminación
                sentenciaSQL = "UPDATE producto SET estado='Inactivo' WHERE id=?" /*+ txtCodigo.getText().trim()*/;
                ps = con.prepareStatement(sentenciaSQL);
                ps.setString(1, txtCodigo.getText().trim());
                ps.execute();
                JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public int obtenerStockProducto(String DescripcionProducto) throws SQLException {
        sentenciaSQL = "SELECT cantidad FROM inventario WHERE descripcion = ?";
        ps = con.prepareStatement(sentenciaSQL);
        ps.setString(1, DescripcionProducto);
        rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("cantidad");
        }
        return 0;
    }

    public void validarCampos() {
        String nombre = txtNombre.getText();
        String costo = txtCosto.getText();
        String precio = txtPrecio.getText();
        String min = txtMinimo.getText();
        String max = txtMaximo.getText();
        String categoria = (String) cboCategoria.getSelectedItem();
        String proveedor = (String) cboProveedor.getSelectedItem();

        if (!nombre.equals("") && !costo.equals("") && !precio.equals("")
                && !min.equals("") && !max.equals("") && !categoria.equals("CATEGORÍAS:")
                && !proveedor.equals("PROVEEDORES:")) {
            btnGuardar.setEnabled(true);
        } else {
            btnGuardar.setEnabled(false);
        }
    }

    public void buscar() {
        String criterio = "";
        //String tipoProducto = (String) cboTipoProducto.getSelectedItem();
        String categoria = (String) cboCategoria.getSelectedItem();
        boolean buscarPorCodigo = false;
        boolean buscarPorNombre = false;

        if (!txtCodigo.getText().isEmpty()) {
            criterio = txtCodigo.getText();
            buscarPorCodigo = true;
        } else if (!txtNombre.getText().isEmpty()) {
            criterio = txtNombre.getText();
            buscarPorNombre = true;
        }

        if (buscarPorCodigo || buscarPorNombre) {
            try {
                String query = "SELECT P.id,P.descripcion,C.descripcion,P.costo,P.utilidad,P.precioVenta,P.CantidadMinima,P.CantidadMaxima,PR.descripcion FROM producto P INNER JOIN "
                        + "categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE ";
                if (buscarPorCodigo) {
                    query += "P.id = ?";
                } else if (buscarPorNombre) {
                    query += "P.descripcion = ?";
                }
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, criterio);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Si se encuentra el cliente, cargar sus datos en los campos correspondientes

                    txtCodigo.setText(rs.getString("P.id"));
                    txtNombre.setText(rs.getString("P.descripcion"));
                    txtCosto.setText(rs.getString("P.costo"));
                    txtPrecio.setText(rs.getString("P.precioVenta"));
                    categoriaJTable();
                    proveedorJTable();
                    txtMinimo.setText(rs.getString("P.CantidadMinima"));
                    txtMaximo.setText(rs.getString("P.CantidadMaxima"));

                    modelo = (DefaultTableModel) tblProductos.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
                    modelo.setRowCount(0);
                    //Object[] datosClientes = new Object[4];
                    datosProductos[0] = rs.getString(1);
                    datosProductos[1] = rs.getString(2);
                    datosProductos[2] = rs.getString(3);
                    datosProductos[3] = rs.getString(4);
                    datosProductos[4] = rs.getString(5);
                    datosProductos[5] = rs.getString(6);
                    datosProductos[6] = rs.getString(7);
                    datosProductos[7] = rs.getString(8);
                    datosProductos[8] = rs.getString(9);

                    modelo.addRow(datosProductos);

                    //tblProveedores.setModel(modelo);
                    //this.leerClientes();
                } else {
                    JOptionPane.showMessageDialog(null, "NO EXISTE TAL PRODUCTO");
                }
                tblProductos.setModel(modelo);
            } catch (SQLException ex) {
                System.out.println("ERROR" + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese un código o nombre para realizar la búsqueda.");
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

        txtCodigo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        btnLeer = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        txtCosto = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtMinimo = new javax.swing.JTextField();
        txtMaximo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboProveedor = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnAyuda1 = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnBuscar1 = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtCodigo.setBackground(new java.awt.Color(241, 222, 201));
        txtCodigo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoKeyTyped(evt);
            }
        });
        getContentPane().add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 310, 250, 30));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setText("Código");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 310, 80, 30));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("USUARIO:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 760, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 760, 200, 30));

        btnEliminar.setBackground(new java.awt.Color(54, 21, 0));
        btnEliminar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(227, 202, 165));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/eliminar.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 660, 150, 40));

        btnLeer.setBackground(new java.awt.Color(54, 21, 0));
        btnLeer.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnLeer.setForeground(new java.awt.Color(227, 202, 165));
        btnLeer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/leer.png"))); // NOI18N
        btnLeer.setText("Leer");
        btnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeerActionPerformed(evt);
            }
        });
        getContentPane().add(btnLeer, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 660, 160, 40));

        btnActualizar.setBackground(new java.awt.Color(54, 21, 0));
        btnActualizar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(227, 202, 165));
        btnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/actualizar.png"))); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        getContentPane().add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 610, 160, 40));

        tblProductos.setBackground(new java.awt.Color(227, 202, 165));
        tblProductos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(54, 21, 0), 2, true));
        tblProductos.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblProductos.setForeground(new java.awt.Color(54, 21, 0));
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "DESC", "CATEGORÍA", "COSTO", "UTILIDAD", "PRECIO", "MIN", "MAX", "PROVEEDOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setColumnSelectionAllowed(true);
        tblProductos.setGridColor(new java.awt.Color(241, 222, 201));
        tblProductos.setSelectionForeground(new java.awt.Color(241, 222, 201));
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProductos);
        tblProductos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 280, 760, 420));

        txtCosto.setBackground(new java.awt.Color(241, 222, 201));
        txtCosto.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCostoActionPerformed(evt);
            }
        });
        txtCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCostoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCostoKeyTyped(evt);
            }
        });
        getContentPane().add(txtCosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 490, 60, 30));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel13.setText("Costo");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 490, 60, 30));

        txtMinimo.setBackground(new java.awt.Color(241, 222, 201));
        txtMinimo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtMinimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMinimoActionPerformed(evt);
            }
        });
        txtMinimo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMinimoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMinimoKeyTyped(evt);
            }
        });
        getContentPane().add(txtMinimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 430, 60, 30));

        txtMaximo.setBackground(new java.awt.Color(241, 222, 201));
        txtMaximo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtMaximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaximoActionPerformed(evt);
            }
        });
        txtMaximo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMaximoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMaximoKeyTyped(evt);
            }
        });
        getContentPane().add(txtMaximo, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 490, 60, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel11.setText("Cantidad Máxima");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 490, 150, 30));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setText("Cantidad Mínima");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 430, 140, 30));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setText("Proveedor");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 560, 90, 20));

        cboProveedor.setBackground(new java.awt.Color(227, 202, 165));
        cboProveedor.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        cboProveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboProveedorItemStateChanged(evt);
            }
        });
        cboProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProveedorActionPerformed(evt);
            }
        });
        cboProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboProveedorKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cboProveedorKeyReleased(evt);
            }
        });
        getContentPane().add(cboProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 550, 250, 30));

        btnGuardar.setBackground(new java.awt.Color(54, 21, 0));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(227, 202, 165));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crear.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        getContentPane().add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 610, 150, 40));

        btnLimpiar.setBackground(new java.awt.Color(54, 21, 0));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(227, 202, 165));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/limpiar.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 610, 150, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Descripción");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 100, 30));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("Categoría");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, 80, 30));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel9.setText("Precio");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 490, 60, 30));

        txtNombre.setBackground(new java.awt.Color(241, 222, 201));
        txtNombre.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreKeyTyped(evt);
            }
        });
        getContentPane().add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 250, 30));

        txtPrecio.setBackground(new java.awt.Color(241, 222, 201));
        txtPrecio.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioActionPerformed(evt);
            }
        });
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPrecioKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrecioKeyTyped(evt);
            }
        });
        getContentPane().add(txtPrecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 490, 60, 30));

        cboCategoria.setBackground(new java.awt.Color(227, 202, 165));
        cboCategoria.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        cboCategoria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboCategoriaItemStateChanged(evt);
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
        getContentPane().add(cboCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 430, 250, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("PRODUCTOS");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 80, -1, -1));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1450, 0, 50, 50));

        btnAyuda1.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda1.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1370, 0, 50, 50));

        btnBuscar.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 40, 40));

        btnBuscar1.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar1.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 360, 40, 40));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1500, 810));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.crearProducto();
        limpiar();
        this.leerProducto();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        this.limpiar();
        this.leerProducto();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtNombreKeyReleased

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        char c = evt.getKeyChar();

        // Validar la longitud del texto actual en la caja de texto
        if (txtNombre.getText().length() == 50) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE)
                || !String.valueOf(c).matches("[\\p{L}\\s]+")) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }
    }//GEN-LAST:event_txtNombreKeyTyped

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void txtPrecioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtPrecioKeyReleased

    private void txtPrecioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioKeyTyped
        char c = evt.getKeyChar();

// Validar la longitud del texto actual en la caja de texto
        if (txtPrecio.getText().length() == 4) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isDigit(c) || c == '.' || c == KeyEvent.VK_BACK_SPACE)) {
            evt.consume(); // Si el carácter no es un número o un punto decimal, ignorarlo
        }

    }//GEN-LAST:event_txtPrecioKeyTyped

    private void cboCategoriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboCategoriaItemStateChanged
        this.validarCampos();
        String catego = (String) cboCategoria.getSelectedItem();
        if (catego.equals("CATEGORÍAS:")) {
            txtCosto.setEnabled(false);
        } else if (!catego.equals("CATEGORÍAS:")) {
            txtCosto.setEnabled(true);
            int posicion3 = catego.indexOf("|");

            String nombreCategoria;
            if (posicion3 >= 0) {
                nombreCategoria = catego.substring(posicion3 + 1).trim();
            } else {
                // Si no se encuentra el carácter "|", establecemos un valor predeterminado
                nombreCategoria = "CATEGORÍAS:";
            }

            //this.validarCampos();
            String costoString = this.txtCosto.getText().trim();
            double costo2 = 0.0;

            try {
                costo2 = Double.parseDouble(costoString);
            } catch (NumberFormatException e) {
                costo2 = 0.0;
            }

            if (nombreCategoria.equals("Postres/Bocadillos")) {
                utilidad2 = UtilidadPostresBocadillos * costo2;
            } else if (nombreCategoria.equals("Bebidas")) {
                utilidad2 = utilidadBebidas * costo2;
            } else if (nombreCategoria.equals("Comida")) {
                utilidad2 = utilidadComida * costo2;
            }

            precio2 = costo2 + utilidad2;
            txtPrecio.setText(String.valueOf(precio2));
        }
    }//GEN-LAST:event_cboCategoriaItemStateChanged

    private void cboCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoriaActionPerformed
        this.validarCampos();
    }//GEN-LAST:event_cboCategoriaActionPerformed

    private void cboCategoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboCategoriaKeyPressed
        this.validarCampos();
    }//GEN-LAST:event_cboCategoriaKeyPressed

    private void cboCategoriaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboCategoriaKeyReleased
        //this.validarCampos();
    }//GEN-LAST:event_cboCategoriaKeyReleased

    private void cboProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboProveedorItemStateChanged
        this.validarCampos();
    }//GEN-LAST:event_cboProveedorItemStateChanged

    private void cboProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboProveedorActionPerformed

    private void cboProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboProveedorKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboProveedorKeyPressed

    private void cboProveedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboProveedorKeyReleased
        validarCampos();
    }//GEN-LAST:event_cboProveedorKeyReleased

    private void txtMinimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinimoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMinimoActionPerformed

    private void txtMinimoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMinimoKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtMinimoKeyReleased

    private void txtMinimoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMinimoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMinimoKeyTyped

    private void txtMaximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaximoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaximoActionPerformed

    private void txtMaximoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMaximoKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtMaximoKeyReleased

    private void txtMaximoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMaximoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaximoKeyTyped

    private void txtCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCostoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCostoActionPerformed

    private void txtCostoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCostoKeyReleased
        this.validarCampos();
        String catego = (String) cboCategoria.getSelectedItem();
        int posicion3 = catego.indexOf("|");
// Extraer la subcadena desde el inicio hasta la posición del carácter |
        String idCategoria = catego.substring(0, posicion3).trim();
//NOMBRE CATEGORIA
// Obtener el valor seleccionado en el cboCategoria
        String nomCategoria = (String) cboCategoria.getSelectedItem();
        int posicion4 = nomCategoria.indexOf("|");
// Extraer la subcadena desde la posición del carácter | más uno hasta el final
        String nombreCategoria = nomCategoria.substring(posicion4 + 1).trim();
        //this.validarCampos();

        String costoString = this.txtCosto.getText().trim();
        double costo2 = 0.0;

        try {
            costo2 = Double.parseDouble(costoString);
        } catch (NumberFormatException e) {
            costo2 = 0.0;
        }

        if (nombreCategoria.equals("Postres/Bocadillos")) {
            utilidad2 = UtilidadPostresBocadillos * costo2;
        } else if (nombreCategoria.equals("Bebidas")) {
            utilidad2 = utilidadBebidas * costo2;
        } else if (nombreCategoria.equals("Comida")) {
            utilidad2 = utilidadComida * costo2;
        }

        precio2 = costo2 + utilidad2;
        txtPrecio.setText(String.valueOf(precio2));
    }//GEN-LAST:event_txtCostoKeyReleased

    private void txtCostoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCostoKeyTyped
        String catego = (String) cboCategoria.getSelectedItem();
        //this.validarCampos();

        if (catego.equals("Postres/Bocadillos")) {
            String costoString = this.txtCosto.getText().trim();
            try {
                costo2 = Double.parseDouble(costoString);
            } catch (NumberFormatException e) {
                costo2 = 0.0;
            }
            utilidad2 = UtilidadPostresBocadillos * costo2;
            precio2 = costo2 + utilidad2;
            utilidad2 = utilidad2;

            if (costoString.equals("0") || costoString.equals("") || costoString.equals(".")) {
                //costo2 = 0.0;
                txtPrecio.setText("0");
            } else {

                txtPrecio.setText(String.valueOf(precio2));
            }

        } else if (catego.equals("Bebidas")) {
            String costoString = this.txtCosto.getText().trim();
            try {
                costo2 = Double.parseDouble(costoString);
            } catch (NumberFormatException e) {
                costo2 = 0.0;
            }

            //costo2 = Double.parseDouble(txtCosto.getText());
            //txtCosto.setEnabled(true);
            // Ropa de dama
            utilidad2 = utilidadBebidas * costo2;
            precio2 = costo2 + utilidad2;
            utilidad2 = utilidad2;

            if (costoString.equals("0") || costoString.equals("") || costoString.equals(".")) {
                //costo2 = 0.0;
                txtPrecio.setText("0");
            } else {

                txtPrecio.setText(String.valueOf(precio2));
            }
        } else if (catego.equals("Comida")) {
            String costoString = this.txtCosto.getText().trim();
            try {
                costo2 = Double.parseDouble(costoString);
            } catch (NumberFormatException e) {
                costo2 = 0.0;
            }

            //costo2 = Double.parseDouble(txtCosto.getText());
            //txtCosto.setEnabled(true);
            // Ropa de dama
            utilidad2 = utilidadComida * costo2;
            precio2 = costo2 + utilidad2;
            utilidad2 = utilidad2;

            if (costoString.equals("0") || costoString.equals("") || costoString.equals(".")) {
                //costo2 = 0.0;
                txtPrecio.setText("0");
            } else {
                txtPrecio.setText(String.valueOf(precio2));
            }
        }
    }//GEN-LAST:event_txtCostoKeyTyped

    private void tblProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductosMouseClicked
        int fila = tblProductos.getSelectedRow();
        txtCodigo.setText(tblProductos.getValueAt(fila, 0).toString());
        txtNombre.setText(tblProductos.getValueAt(fila, 1).toString());
        txtCosto.setText(tblProductos.getValueAt(fila, 3).toString());
        txtPrecio.setText(tblProductos.getValueAt(fila, 5).toString());
        txtMinimo.setText(tblProductos.getValueAt(fila, 6).toString());
        txtMaximo.setText(tblProductos.getValueAt(fila, 7).toString());
        categoriaJTable();
        proveedorJTable();
        btnGuardar.setVisible(false);
        //SwingUtilities.invokeLater(this::CargarCategoria);
        //SwingUtilities.invokeLater(this::CargarProveedor);
        System.out.println("ID del producto: " + txtCodigo.getText().trim());


    }//GEN-LAST:event_tblProductosMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarProducto();
        limpiar();
        leerProducto();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoKeyReleased

    private void txtCodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoKeyTyped

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarProducto();
        limpiar();
        leerProducto();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        limpiar();
        leerProducto();
    }//GEN-LAST:event_btnLeerActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnAyuda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda1ActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame NuevoProducto = null;
        AyudaProductos dialog2 = new AyudaProductos(NuevoProducto, true);
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyuda1ActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscar();

    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnBuscar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar1ActionPerformed
        buscar();

    }//GEN-LAST:event_btnBuscar1ActionPerformed

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
            java.util.logging.Logger.getLogger(NuevoProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NuevoProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NuevoProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NuevoProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NuevoProducto dialog = new NuevoProducto(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAyuda1;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscar1;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLeer;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cboCategoria;
    private javax.swing.JComboBox<String> cboProveedor;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextField txtMaximo;
    private javax.swing.JTextField txtMinimo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
