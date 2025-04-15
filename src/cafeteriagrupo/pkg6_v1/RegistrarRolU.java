/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaRegistrarRol;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;

/**
 *
 * @author Juan Alvarado
 */
public class RegistrarRolU extends javax.swing.JDialog {
    private static String correoUsu;
    ImageIcon imagen;
    String sentenciaSQL, codRol, estatus;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ImageIcon icono;
    public String modeloNombre = "^[a-zA-Z]";
    public String modeloDescripcion = "^[a-zA-Z]+[.]";

    /**
     * Creates new form RegistrarRolU
     */
    public RegistrarRolU(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        this.btnGuardar.setEnabled(false);
        this.setLocationRelativeTo(null);
        this.correoUsu = user;
        //this.ConectarBD();
        jblUsuario.setText(correoUsu);
        /*if (correoUsu.equals("AdminCafe.2001@gmail.com")) {
            btnActualizar.setVisible(true);
            btnEliminar.setVisible(true);
            btnBuscar.setVisible(true);
        } else {
            btnEliminar.setVisible(false);
            btnActualizar.setVisible(false);
            btnBuscar.setVisible(false);
        }*/
        //txtCodigo.setText("0");//ES AUTO INCREMENTABLE, SE PONE 0 Y SE MANDA PORQUE NO IMPORTA CUAL SE MANDE, PORQUE ES AUTO
        cambioImagen("iconos.png", jblFondo);
        cambioImagen("logo.png", jblLogo);
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

    public void crearRol() {
        if (txtDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de crear un rol.");
            return;
        }
        //SE DEBE METER EN UN BLOQUE TRY-CATCH
        try {
            this.ConectarBD();//METODO ANTERIOR
            sentenciaSQL = "INSERT INTO rol (descripcion)VALUES(?)";
            ps = con.prepareStatement(sentenciaSQL);
            //ps.setString(1, txtCodigo.getText());
            ps.setString(1, txtDescripcion.getText());

            ps.execute();
            //salida PARA CONFIRMAR DATOS INGRESADOS CORRECTAMENTE
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORREXTAMENTE");

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(RegistrarRolU.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
        }
    }
    public void eliminarRol() {
        if (txtDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar un rol.");
            return;
        }
        try {
            //ConectarBD();
            sentenciaSQL="DELETE FROM rol WHERE id=" + txtCodigo.getText().trim();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }
    public void actualizarRol() {
        if (txtDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de actualizar un rol.");
            return;
        }
        //this.limpiar();
        sentenciaSQL = "SELECT id,descripcion FROM rol";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                txtCodigo.setText(rs.getString(1));//POSICION CERO VA IR LA COLUMNA NUMERO 1. PRIMER DATO COMO ENTERO PERO COMO EL ARREGLO ES DE TIPO OBJETO, SE PUEDE UTILIZAR GET INT/STRING
                txtDescripcion.setText(rs.getString(2));//TIPO DE DATO Y NUMERO DE COLUMNA  
                txtCodigo.setEditable(false);
            }
            //AGREGAR MODELO A LA TABLA
            
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    public void limpiar() {
        txtDescripcion.setText("");
        btnGuardar.setEnabled(false);
    }

    public void validarCampos() {
        String descripcion = txtDescripcion.getText();

        if (!descripcion.equals("")) {
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
                String query = "SELECT * FROM rol WHERE ";
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
                } else {
                    JOptionPane.showMessageDialog(null, "NO EXISTE TAL SERVICIO");
                }
                btnGuardar.setVisible(false);
            } catch (SQLException ex) {
                System.out.println("ERROR" + ex.getMessage() + ex.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jblFondo = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jblUsuario = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        btnBuscar1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(141, 123, 104));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(241, 222, 201));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(96, 54, 1));
        jLabel1.setText("REGISTRAR ROL");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Descripción");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 290, 110, -1));

        btnGuardar.setBackground(new java.awt.Color(54, 21, 0));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(227, 202, 165));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jPanel2.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 380, 90, -1));

        btnLimpiar.setBackground(new java.awt.Color(54, 21, 0));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(227, 202, 165));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        jPanel2.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 380, 90, -1));

        jblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconos.png"))); // NOI18N
        jPanel2.add(jblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 460, 740, 120));

        jblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo.png"))); // NOI18N
        jPanel2.add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 160));

        txtDescripcion.setBackground(new java.awt.Color(241, 222, 201));
        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDescripcion.setRows(5);
        txtDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(txtDescripcion);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 260, 310, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Código");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 210, 157, -1));

        txtCodigo.setBackground(new java.awt.Color(241, 222, 201));
        txtCodigo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        jPanel2.add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 210, 310, -1));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jblUsuario.setForeground(new java.awt.Color(96, 54, 1));
        jPanel2.add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 210, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(96, 54, 1));
        jLabel11.setText("USUARIO:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 90, 30));

        btnEliminar.setBackground(new java.awt.Color(54, 21, 0));
        btnEliminar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(227, 202, 165));
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        jPanel2.add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 420, 100, -1));

        btnActualizar.setBackground(new java.awt.Color(54, 21, 0));
        btnActualizar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(227, 202, 165));
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        jPanel2.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 420, 100, -1));

        btnAyuda.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        jPanel2.add(btnAyuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 0, 50, 50));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        jPanel2.add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 50, 50));

        btnBuscar1.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar1.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuscar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 200, 40, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 740, 580));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 828, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.crearRol();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        this.limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtDescripcionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtDescripcionKeyReleased

    private void txtDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyTyped
        char c = evt.getKeyChar();

// Validar la longitud del texto actual en la caja de texto
        if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE
                || c == ',' || c == '.' || c == ';')) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }

    }//GEN-LAST:event_txtDescripcionKeyTyped

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarRol();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarRol();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame RegistrarRol = null;
        AyudaRegistrarRol dialog2 = new AyudaRegistrarRol(RegistrarRol, true);
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyudaActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

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
            java.util.logging.Logger.getLogger(RegistrarRolU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrarRolU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrarRolU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrarRolU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistrarRolU dialog = new RegistrarRolU(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnBuscar1;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblFondo;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextArea txtDescripcion;
    // End of variables declaration//GEN-END:variables
}
