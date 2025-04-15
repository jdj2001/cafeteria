/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaInventario;
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
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author Juan Alvarado
 */
public class inventario extends javax.swing.JDialog {

    String descripcion, sentenciaSQL;

    int codigo, stockMin, stockMax, cantidad, cantidadIngresar;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon imagen;
    Icon icono;
    private static String correoUsu;
    public Double utilidad2;
    ResultSet rs = null; //PERMITE TENER METODOS PARA OBTENER INFO DE LAS DIFERENTES COLUMNAS DE UNA FILA=TIPO RESULT SET VARIABE
    DefaultTableModel modelo, modeloInventario; //PARA EL MÓDELO DE NUESTRA TABLA
    Object datosProductos[] = new Object[9];
    Object datosInventario[] = new Object[3];

    /**
     * Creates new form inventario
     */
    public inventario(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        this.setLocationRelativeTo(null);
        this.ConectarBD();
        this.correoUsu = user;
        btnGuardar.setEnabled(false);

        jblUsuario.setText(correoUsu);
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
        txtDescripcion.setText("");
        txtCantidadIngresada.setText("");
        txtCantidadActual.setText("");
        txtMinimo.setText("");
        txtMaximo.setText("");
        //txtNombre.requestFocus();//

        int fila = tblProductos.getRowCount();//TRAYENDO X CANTIDAD DE REGISTROS. CUANTAS FILAS TENEMOS EN JTABLE
        //CICLO INVERSO. EMPIEZA EN EL MAYOR HASTA MENOR. AGARRA DE ABAJO HASTA ARRIBA. VA IR REMOVIENDOLAS
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);//FILA X HASTA LLEGAR A CERO
        }
        int fila2 = tblInventario.getRowCount();//TRAYENDO X CANTIDAD DE REGISTROS. CUANTAS FILAS TENEMOS EN JTABLE
        //CICLO INVERSO. EMPIEZA EN EL MAYOR HASTA MENOR. AGARRA DE ABAJO HASTA ARRIBA. VA IR REMOVIENDOLAS
        for (int i2 = fila2 - 1; i2 >= 0; i2--) {
            modeloInventario.removeRow(i2);//FILA X HASTA LLEGAR A CERO
        }
    }

    public void insertarInventario() {
        codigo = Integer.parseInt(txtCodigo.getText());
        cantidad = Integer.parseInt(txtCantidadIngresada.getText());
        int min = Integer.parseInt(txtMinimo.getText());
        int max = Integer.parseInt(txtMaximo.getText());
        descripcion = txtDescripcion.getText();

        try {
            // Verificar si el ID del producto ya existe en el inventario
            String consultaExistencia = "SELECT idProducto FROM inventario WHERE idProducto = ?";
            PreparedStatement psConsulta = con.prepareStatement(consultaExistencia);
            psConsulta.setInt(1, codigo);
            ResultSet rs = psConsulta.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "El ID del producto ya existe en el inventario.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (cantidad >= min && cantidad <= max) {
                    sentenciaSQL = "INSERT INTO inventario (idProducto, descripcion, cantidad) VALUES(?,?,?)";
                    ps = con.prepareStatement(sentenciaSQL);
                    ps.setInt(1, codigo);
                    ps.setString(2, descripcion);
                    ps.setInt(3, cantidad);
                    ps.execute();
                    JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
                    this.limpiar();
                    leerProducto();
                } else {
                    JOptionPane.showMessageDialog(null, "La cantidad ingresada no cumple con los límites establecidos para el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void leerProducto() {
        sentenciaSQL = "SELECT P.id,P.descripcion,C.descripcion,P.costo,P.utilidad,P.precioVenta,P.CantidadMinima,P.CantidadMaxima,PR.descripcion "
                + "FROM producto P INNER JOIN categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE P.estado LIKE 'Activo'";
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

    public void leerInventario() {
        //this.limpiar();
        /*sentenciaSQL = "SELECT P.id,P.descripcion,C.Nombre,P.costo,P.utilidad,P.precioVenta,PR.nombre FROM producto P INNER JOIN "
                + "categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE P.estado LIKE 'Activo'";*/
        sentenciaSQL = "SELECT idProducto,descripcion,cantidad FROM inventario";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modeloInventario = (DefaultTableModel) tblInventario.getModel();
            while (rs.next()) {
                datosInventario[0] = (rs.getString(1));
                datosInventario[1] = (rs.getString(2));
                datosInventario[2] = (rs.getString(3));
                // datosProductos[7] = (rs.getString(8));
                //datosProductos[8] = (rs.getString(9));
                modeloInventario.addRow(datosInventario);
            }
            tblInventario.setModel(modeloInventario);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    public void actualizarInventario() {
        String cantidadActualTexto = txtCantidadActual.getText();
        if (cantidadActualTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "PRODUCTO NO EXISTE EN EL INVENTARIO. NO SE PUEDE ACTUALIZAR.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Salir del método si la cantidad actual está vacía
        }

        codigo = Integer.parseInt(txtCodigo.getText());
        cantidad = Integer.parseInt(cantidadActualTexto);
        cantidadIngresar = Integer.parseInt(txtCantidadIngresada.getText());
        descripcion = txtDescripcion.getText();
        stockMin = Integer.parseInt(txtMinimo.getText());
        stockMax = Integer.parseInt(txtMaximo.getText());

        if ((cantidad+cantidadIngresar) >= stockMin && (cantidad+cantidadIngresar) <= stockMax) {
            try {
                sentenciaSQL = "UPDATE inventario SET descripcion=?, cantidad=? WHERE idProducto=?";
                ps = con.prepareStatement(sentenciaSQL);
                ps.setString(1, descripcion);
                ps.setDouble(2, cantidadIngresar+cantidad);
                ps.setInt(3, codigo);
                ps.execute();
                JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            } catch (SQLException ex) {
                Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR DATOS" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "La cantidad ingresada no cumple con los límites establecidos para el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarInventario() {
        try {
            int cantidadActual = Integer.parseInt(txtCantidadActual.getText().trim());

            if (cantidadActual == 0) {
                // Actualizar el estado del registro en el inventario
                sentenciaSQL = "UPDATE producto SET estado='Inactivo' WHERE id=" + txtCodigo.getText().trim();
                ps = con.prepareStatement(sentenciaSQL);
                ps.execute();
                JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            } else {
                JOptionPane.showMessageDialog(null, "No se puede eliminar el producto del inventario. Aún tiene existencia.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void validarCampos() {
        String nombre = txtDescripcion.getText();
        String cantidad = txtCantidadIngresada.getText();
        String codigo = txtCodigo.getText();
        String min = txtMinimo.getText();
        String max = txtMaximo.getText();

        if (!codigo.equals("") && !nombre.equals("") && !cantidad.equals("")) {
            btnGuardar.setEnabled(true);
        } else {
            btnGuardar.setEnabled(false);
        }
    }

    public void buscar() {
        String criterio = "";
        boolean buscarPorCodigo = false;
        boolean buscarPorNombre = false;

        if (!txtCodigo.getText().isEmpty()) {
            criterio = txtCodigo.getText();
            buscarPorCodigo = true;
        } else if (!txtDescripcion.getText().isEmpty()) {
            criterio = txtDescripcion.getText();
            buscarPorNombre = true;
        }

        if (buscarPorCodigo || buscarPorNombre) {
            try {
                String query2 = "SELECT I.idProducto,I.descripcion,I.cantidad, P.id,P.descripcion,P.costo,P.utilidad,P.precioVenta,P.CantidadMinima,P.CantidadMaxima,C.descripcion"
                        + " FROM inventario I INNER JOIN producto P ON I.idProducto = P.id INNER JOIN categoria C ON P.idCategoria=C.id WHERE ";
                String query = "SELECT I.idProducto,I.descripcion,I.cantidad,P.id,P.descripcion,C.descripcion,P.costo,P.utilidad,P.precioVenta,P.CantidadMinima,P.CantidadMaxima,PR.descripcion "
                        + "FROM Inventario I INNER JOIN producto P ON I.idProducto=P.id INNER JOIN categoria C ON P.idCategoria=C.id INNER JOIN proveedor PR ON P.idProveedor=PR.id WHERE ";
                if (buscarPorCodigo) {
                    query += "I.idProducto = ?";
                } else if (buscarPorNombre) {
                    query += "I.descripcion = ?";
                }
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, criterio);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    txtCodigo.setText(rs.getString("I.idProducto"));
                    txtDescripcion.setText(rs.getString("I.descripcion"));
                    txtCantidadActual.setText(rs.getString("I.cantidad"));
                    txtMinimo.setText(rs.getString("P.CantidadMinima"));
                    txtMaximo.setText(rs.getString("P.CantidadMaxima"));

                    modeloInventario = (DefaultTableModel) tblInventario.getModel();
                    modeloInventario.setRowCount(0);
                    datosInventario[0] = rs.getString(1);
                    datosInventario[1] = rs.getString(2);
                    datosInventario[2] = rs.getString(3);

                    modeloInventario.addRow(datosInventario);

                    modelo = (DefaultTableModel) tblProductos.getModel();
                    modelo.setRowCount(0);
                    datosProductos[0] = rs.getString(4);
                    datosProductos[1] = rs.getString(5);
                    datosProductos[2] = rs.getString(6);
                    datosProductos[3] = rs.getString(7);
                    datosProductos[4] = rs.getString(8);
                    datosProductos[5] = rs.getString(9);
                    datosProductos[6] = rs.getString(10);
                    datosProductos[7] = rs.getString(11);
                    datosProductos[8] = rs.getString(12);

                    modelo.addRow(datosProductos);

                } else {
                    JOptionPane.showMessageDialog(null, "NO EXISTE TAL PRODUCTO EN EL INVENTARIO");
                }
                tblInventario.setModel(modeloInventario);
                tblProductos.setModel(modelo);
            } catch (SQLException ex) {
                System.out.println("ERROR" + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese un código o nombre para realizar la búsqueda.");
        }
    }

    public void buscarProducto() {
        String criterio = "";
        boolean buscarPorCodigo = false;
        boolean buscarPorNombre = false;

        if (!txtCodigo.getText().isEmpty()) {
            criterio = txtCodigo.getText();
            buscarPorCodigo = true;
        } else if (!txtDescripcion.getText().isEmpty()) {
            criterio = txtDescripcion.getText();
            buscarPorNombre = true;
        }

        if (buscarPorCodigo || buscarPorNombre) {
            try {
                String query = "SELECT id,descripcion,costo,utilidad,precio,stockMinimo,stockMaximo,tipoProducto,categoria FROM producto WHERE ";
                if (buscarPorCodigo) {
                    query += "id = ?";
                } else if (buscarPorNombre) {
                    query += "descripcion = ?";
                }
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, criterio);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {

                    txtCodigo.setText(rs.getString("id"));
                    txtDescripcion.setText(rs.getString("descripcion"));
                    String tipoProducto = rs.getString("tipoProducto");
                    txtMinimo.setText(rs.getString("stockMinimo"));
                    txtMaximo.setText(rs.getString("stockMaximo"));

                    modelo = (DefaultTableModel) tblProductos.getModel();
                    modelo.setRowCount(0);
                    //Object[] datosClientes = new Object[4];
                    datosProductos[0] = rs.getString(1);
                    datosProductos[1] = rs.getString(2);
                    datosProductos[2] = rs.getString(3);
                    datosProductos[3] = rs.getString(4);
                    datosProductos[4] = rs.getString(5);
                    datosProductos[5] = rs.getString(6);
                    datosProductos[6] = rs.getString(7);

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

        btnCerrar = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton();
        txtCodigo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtCantidadIngresada = new javax.swing.JTextField();
        btnLeer = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblInventario = new javax.swing.JTable();
        btnActualizar = new javax.swing.JButton();
        txtCantidadActual = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtMinimo = new javax.swing.JTextField();
        txtMaximo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnBuscarDescripcion = new javax.swing.JButton();
        btnBuscarCoidgo = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        jblLogo = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1610, 0, 50, 50));

        btnAyuda.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(1540, 0, 50, 50));

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
        getContentPane().add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 170, 250, 30));

        jLabel15.setBackground(new java.awt.Color(241, 222, 201));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setText("Código");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 170, 80, 30));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("USUARIO:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 730, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1370, 730, 270, 30));

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
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 470, 150, 40));

        jLabel16.setBackground(new java.awt.Color(241, 222, 201));
        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel16.setText("Cantidad a Ingresar");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 350, 170, 30));

        txtCantidadIngresada.setBackground(new java.awt.Color(241, 222, 201));
        txtCantidadIngresada.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCantidadIngresada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadIngresadaActionPerformed(evt);
            }
        });
        txtCantidadIngresada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadIngresadaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadIngresadaKeyTyped(evt);
            }
        });
        getContentPane().add(txtCantidadIngresada, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 350, 60, 30));

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
        getContentPane().add(btnLeer, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 470, 160, 40));

        tblInventario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblInventario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "DESCRIPCION", "CANTIDAD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInventario.setColumnSelectionAllowed(true);
        tblInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblInventarioMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblInventario);
        tblInventario.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 450, 720, 240));

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
        getContentPane().add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 420, 160, 40));

        txtCantidadActual.setEditable(false);
        txtCantidadActual.setBackground(new java.awt.Color(241, 222, 201));
        txtCantidadActual.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCantidadActual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActualActionPerformed(evt);
            }
        });
        txtCantidadActual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadActualKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadActualKeyTyped(evt);
            }
        });
        getContentPane().add(txtCantidadActual, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 290, 60, 30));

        jLabel13.setBackground(new java.awt.Color(241, 222, 201));
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel13.setText("Cantidad Actual");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 290, 140, 30));

        txtMinimo.setEditable(false);
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
        getContentPane().add(txtMinimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 290, 60, 30));

        txtMaximo.setEditable(false);
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
        getContentPane().add(txtMaximo, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 350, 60, 30));

        jLabel11.setBackground(new java.awt.Color(241, 222, 201));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel11.setText("Cantidad Máxima");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 350, 150, 30));

        jLabel12.setBackground(new java.awt.Color(241, 222, 201));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setText("Cantidad Mínima");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 290, 140, 30));

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
        getContentPane().add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 420, 150, 40));

        btnBuscarDescripcion.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscarDescripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscarDescripcion.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscarDescripcion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crearOrden.png"))); // NOI18N
        btnBuscarDescripcion.setBorderPainted(false);
        btnBuscarDescripcion.setContentAreaFilled(false);
        btnBuscarDescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarDescripcionActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscarDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 230, 50, 30));

        btnBuscarCoidgo.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscarCoidgo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscarCoidgo.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscarCoidgo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crearOrden.png"))); // NOI18N
        btnBuscarCoidgo.setBorderPainted(false);
        btnBuscarCoidgo.setContentAreaFilled(false);
        btnBuscarCoidgo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCoidgoActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscarCoidgo, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 170, 50, 30));

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
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 420, 150, 40));

        jLabel7.setBackground(new java.awt.Color(241, 222, 201));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Descripción");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 230, 100, 30));

        txtDescripcion.setBackground(new java.awt.Color(241, 222, 201));
        txtDescripcion.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtDescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescripcionActionPerformed(evt);
            }
        });
        txtDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyTyped(evt);
            }
        });
        getContentPane().add(txtDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 230, 250, 30));

        tblProductos.setBackground(new java.awt.Color(227, 202, 165));
        tblProductos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(54, 21, 0), 2, true));
        tblProductos.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblProductos.setForeground(new java.awt.Color(54, 21, 0));
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD", "DESC", "CATEGO", "COSTO", "UTILIDAD", "PRECIO", "MIN", "MAX", "PROV"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setColumnSelectionAllowed(true);
        tblProductos.setGridColor(new java.awt.Color(241, 222, 201));
        tblProductos.setSelectionForeground(new java.awt.Color(241, 222, 201));
        tblProductos.getTableHeader().setReorderingAllowed(false);
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblProductos);
        tblProductos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 100, 720, 290));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1660, 780));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtCodigoKeyReleased

    private void txtCodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoKeyTyped

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        this.eliminarInventario();
        this.limpiar();
        this.leerProducto();
        this.leerInventario();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        limpiar();
        leerProducto();
        leerInventario();
    }//GEN-LAST:event_btnLeerActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        this.actualizarInventario();
        this.limpiar();
        this.leerProducto();
        this.leerInventario();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtCantidadActualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActualActionPerformed

    private void txtCantidadActualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadActualKeyReleased

    }//GEN-LAST:event_txtCantidadActualKeyReleased

    private void txtCantidadActualKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadActualKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActualKeyTyped

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

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.insertarInventario();
        //this.limpiar();
        //this.leerProducto();
        //this.leerInventario();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBuscarDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarDescripcionActionPerformed
        buscar();
    }//GEN-LAST:event_btnBuscarDescripcionActionPerformed

    private void btnBuscarCoidgoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCoidgoActionPerformed
        buscar();
    }//GEN-LAST:event_btnBuscarCoidgoActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        this.limpiar();
        this.leerProducto();
        this.leerInventario();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescripcionActionPerformed

    private void txtDescripcionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtDescripcionKeyReleased

    private void txtDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyTyped
        char c = evt.getKeyChar();

        // Validar la longitud del texto actual en la caja de texto
        if (txtDescripcion.getText().length() == 50) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE)
                || !String.valueOf(c).matches("[\\p{L}\\s]+")) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }
    }//GEN-LAST:event_txtDescripcionKeyTyped

    private void tblInventarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblInventarioMouseClicked
        int fila = tblInventario.getSelectedRow();
        String codigoProductoSeleccionado = tblInventario.getValueAt(fila, 0).toString();

        txtCodigo.setText(tblInventario.getValueAt(fila, 0).toString());
        txtDescripcion.setText(tblInventario.getValueAt(fila, 1).toString());
        txtCantidadActual.setText(tblInventario.getValueAt(fila, 2).toString());

        for (int i = 0; i < modelo.getRowCount(); i++) {
            String codigoProductoActual = modelo.getValueAt(i, 0).toString();
            if (codigoProductoActual.equals(codigoProductoSeleccionado)) {
                txtMinimo.setText(modelo.getValueAt(i, 6).toString());
                txtMaximo.setText(modelo.getValueAt(i, 7).toString());
                break; // Salir del bucle una vez que se encuentre el producto
            }
        }
        btnGuardar.setEnabled(false);
    }//GEN-LAST:event_tblInventarioMouseClicked

    private void txtCantidadIngresadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadIngresadaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadIngresadaActionPerformed

    private void txtCantidadIngresadaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadIngresadaKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtCantidadIngresadaKeyReleased

    private void txtCantidadIngresadaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadIngresadaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadIngresadaKeyTyped

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        //this.leerProducto();
        //this.leerInventario();
    }//GEN-LAST:event_formComponentShown

    private void tblProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductosMouseClicked
        int fila = tblProductos.getSelectedRow();//QUÉ FILA SELECCIONO, DIO CLIC
        txtCodigo.setText(tblProductos.getValueAt(fila, 0).toString());//CERO ES DONDE ESTARA EL CODIGO.
        txtDescripcion.setText(tblProductos.getValueAt(fila, 1).toString());
        txtMinimo.setText(tblProductos.getValueAt(fila, 6).toString());
        txtMaximo.setText(tblProductos.getValueAt(fila, 7).toString());
    }//GEN-LAST:event_tblProductosMouseClicked

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame inventario = null;
        AyudaInventario dialog2 = new AyudaInventario(inventario, true);
        dialog2.setVisible(true);


    }//GEN-LAST:event_btnAyudaActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

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
            java.util.logging.Logger.getLogger(inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(inventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                inventario dialog = new inventario(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnBuscarCoidgo;
    private javax.swing.JButton btnBuscarDescripcion;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLeer;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JTable tblInventario;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCantidadActual;
    private javax.swing.JTextField txtCantidadIngresada;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtMaximo;
    private javax.swing.JTextField txtMinimo;
    // End of variables declaration//GEN-END:variables
}
