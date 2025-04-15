/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaCategoria;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan Alvarado
 */
public class GestionarCategoria extends javax.swing.JFrame {

    ImageIcon imagen;
    String codigo, categoria, usuario, sentenciaSQL, codRol, estatus, estatusValidar, rolValidar;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon icono;
    private static String correoUsu;
    ResultSet rs = null; //PERMITE TENER METODOS PARA OBTENER INFO DE LAS DIFERENTES COLUMNAS DE UNA FILA=TIPO RESULT SET VARIABE
    DefaultTableModel modelo; //PARA EL MÓDELO DE NUESTRA TABLA
    Object datosCategoria[] = new Object[4];

    /**
     * Creates new form GestionarCategoria
     */
    public GestionarCategoria(String user) {
        initComponents();
        cambioImagen("dibujos.png", jblFondo);
        //cambioImagen("Logo.png", jblLogo);
        //cambioImagen("logo.png", jblLogo);
        this.correoUsu = user;
        jblUsuario.setText(correoUsu);
        this.ConectarBD();
        //this.leerCategoria();
        btnGuardar.setEnabled(false);
        cambioImagen("logoSinFondo.png", jblLogo);
    }
    


    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void crearCategoria() {
        if (txtDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de crear la categoría");
            return;
        }
        //SE DEBE METER EN UN BLOQUE TRY-CATCH PARA LLEGAR A ESTO:
        categoria = (String) this.txtDescripcion.getText();
        try {
            //ConectarBD();//METODO ANTERIOR
            sentenciaSQL = "INSERT INTO categoria (descripcion,estado) VALUES(?,?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setString(1, txtDescripcion.getText());
            ps.setString(2, ("Activo"));

            ps.execute();
            //salida PARA CONFIRMAR DATOS INGRESADOS CORRECTAMENTE
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestionarCategoria.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
        }
    }

    public void leerCategoria() {
        //this.limpiar();
        sentenciaSQL = "SELECT * FROM categoria WHERE estado LIKE 'Activo'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblCategoria.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                datosCategoria[0] = (rs.getString(1));//POSICION CERO VA IR LA COLUMNA NUMERO 1. PRIMER DATO COMO ENTERO PERO COMO EL ARREGLO ES DE TIPO OBJETO, SE PUEDE UTILIZAR GET INT/STRING
                datosCategoria[1] = (rs.getString(2));//TIPO DE DATO Y NUMERO DE COLUMNA
                modelo.addRow(datosCategoria);//AGREGARLE LAS FILAS AL MODELO    
            }
            //AGREGAR MODELO A LA TABLA
            tblCategoria.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    public void actualizarCategoria() {
        if (txtDescripcion.getText().isEmpty() || txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de actualizar la categoría");
            return;
        }
        categoria = (String) this.txtDescripcion.getText();
        try {
            sentenciaSQL = "UPDATE categoria SET descripcion=? WHERE id=?";
            ps = con.prepareStatement(sentenciaSQL);
            //EL NUMERO SIGNIFICA EL PRIMER PARÁMETRO, NO DE COLUMNA. DE AQUI ES DONDE OBTIENE DE LA CAJA DE TEXTO Y LO MANDA AL PARAMETRO
            ps.setString(1, categoria);
            ps.setInt(2, Integer.parseInt(txtCodigo.getText()));
            // Obtener la fecha actual
            /*LocalDate fechaActual = LocalDate.now();
            Date fechaSQL = Date.valueOf(fechaActual);
            ps.setDate(10, fechaSQL);*/
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestionarCategoria.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR DATOS" + ex.getMessage());
        }
    }

    public void eliminarCategoria() {
        if (txtDescripcion.getText().isEmpty() || txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar la categoría");
            return;
        }
        try {
            //ConectarBD();
            //sentenciaSQL="DELETE FROM clientes WHERE cod=" + jtfCodigo.getText().trim();//ELIMINAR COMPLETAMENTE SIN DEJAR RASTRO (NO RECOMENDABLE PARA REGSTROS DE TRANSACCIONES
            //TRAYENDO TEXTFIELD. QITA ESPACIOS INICIALES Y FINALES CON .TRIM. ESTO ACTUALIZA Y COLOCA EL ESTADO COMO INACTIVO DONDE EL ID SEA IGUAL AL TXT
            sentenciaSQL = "UPDATE categoria SET estado='Inactivo' WHERE id=" + txtCodigo.getText().trim();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void validarCampos() {
        categoria = txtDescripcion.getText();
        codigo = txtCodigo.getText();

        if (!categoria.equals("")) {
            btnGuardar.setEnabled(true);
        } else {
            btnGuardar.setEnabled(false);
        }
//&& isValidEmail(correo)
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    public void limpiar() {
        txtDescripcion.setText("");
        txtCodigo.setText("");

        int fila = tblCategoria.getRowCount();//TRAYENDO X CANTIDAD DE REGISTROS. CUANTAS FILAS TENEMOS EN JTABLE
        //CICLO INVERSO. EMPIEZA EN EL MAYOR HASTA MENOR. AGARRA DE ABAJO HASTA ARRIBA. VA IR REMOVIENDOLAS
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);//FILA X HASTA LLEGAR A CERO
        }
        btnGuardar.setVisible(true);
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
                String query = "SELECT * FROM categoria WHERE ";
                if (buscarPorCodigo) {
                    query += "id = ?";
                } else if (buscarPorNombre) {
                    query += "descripcion = ?";
                }
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, criterio);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Si se encuentra el cliente, cargar sus datos en los campos correspondientes

                    txtCodigo.setText(rs.getString("id"));
                    txtDescripcion.setText(rs.getString("descripcion"));

                    modelo = (DefaultTableModel) tblCategoria.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
                    modelo.setRowCount(0);
                    //Object[] datosClientes = new Object[4];
                    datosCategoria[0] = rs.getString(1);
                    datosCategoria[1] = rs.getString(2);

                    modelo.addRow(datosCategoria);

                    //tblProveedores.setModel(modelo);
                    //this.leerClientes();
                } else {
                    JOptionPane.showMessageDialog(null, "NO EXISTE TAL SERVICIO");
                }
                tblCategoria.setModel(modelo);
                btnGuardar.setVisible(false);
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

        btnAyuda1 = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategoria = new javax.swing.JTable();
        txtDescripcion = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        txtCodigo = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        btnBuscar1 = new javax.swing.JButton();
        jblFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(350, 134));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAyuda1.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda1.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 0, 50, 50));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 0, 50, 50));

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
        getContentPane().add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 430, 160, 40));

        tblCategoria.setBackground(new java.awt.Color(227, 202, 165));
        tblCategoria.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(54, 21, 0), 2, true));
        tblCategoria.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblCategoria.setForeground(new java.awt.Color(54, 21, 0));
        tblCategoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "CATEGORÍA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCategoria.setColumnSelectionAllowed(true);
        tblCategoria.setGridColor(new java.awt.Color(241, 222, 201));
        tblCategoria.setSelectionForeground(new java.awt.Color(241, 222, 201));
        tblCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCategoriaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCategoria);
        tblCategoria.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 280, 580, 290));

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
        getContentPane().add(txtDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 360, 280, 40));

        btnGuardar.setBackground(new java.awt.Color(54, 21, 0));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(227, 202, 165));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crear.png"))); // NOI18N
        btnGuardar.setText("Crear");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        getContentPane().add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 430, 140, 40));

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
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 480, 140, 40));

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
        getContentPane().add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, 280, 40));

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
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 480, 160, 40));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("CATEGORÍA DE PRODUCTO");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, -1, -1));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setText("Código");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 310, 80, 30));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Descripción");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 100, 30));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("USUARIO:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 600, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 600, 240, 30));

        btnBuscar.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 300, 40, 40));

        btnBuscar1.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar1.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 360, 40, 40));
        getContentPane().add(jblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1260, 650));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tblCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCategoriaMouseClicked
        int fila = tblCategoria.getSelectedRow();//QUÉ FILA SELECCIONO, DIO CLIC
        txtCodigo.setText(tblCategoria.getValueAt(fila, 0).toString());//CERO ES DONDE ESTARA EL CODIGO.
        txtDescripcion.setText(tblCategoria.getValueAt(fila, 1).toString());
        btnGuardar.setVisible(false);
    }//GEN-LAST:event_tblCategoriaMouseClicked

    private void txtDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescripcionActionPerformed

    private void txtDescripcionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtDescripcionKeyReleased

    private void txtDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyTyped
        char c = evt.getKeyChar();

        // Validar la longitud del texto actual en la caja de texto
        if (txtDescripcion.getText().length() == 70) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE)
                || !String.valueOf(c).matches("[\\p{L}\\s]+")) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }
    }//GEN-LAST:event_txtDescripcionKeyTyped

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.crearCategoria();
        this.limpiar();
        this.leerCategoria();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        this.limpiar();
        this.leerCategoria();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        this.leerCategoria();
    }//GEN-LAST:event_formComponentShown

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarCategoria();
        limpiar();
        leerCategoria();
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

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarCategoria();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnAyuda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda1ActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame GestionarCategoria = null;
        AyudaCategoria dialog2 = new AyudaCategoria(GestionarCategoria, true);
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyuda1ActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

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
            java.util.logging.Logger.getLogger(GestionarCategoria.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionarCategoria.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionarCategoria.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionarCategoria.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionarCategoria(correoUsu).setVisible(true);
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
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblFondo;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JTable tblCategoria;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    // End of variables declaration//GEN-END:variables
}
