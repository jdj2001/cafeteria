/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import accesoDatosObjetos.Conexion;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

//import cafeteriagrupo.pkg6_v1.Principal.jblUsuario;
/**
 *
 * @author Juan Alvarado
 */
public class Login extends javax.swing.JFrame {

    String usuario, pass, rol, sentenciaSQL, sentenciaSQL2, codRol, estatus, estatusValidar, rolValidar;
    Connection con = null;
    private static String correoUsu;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon icono;
    ImageIcon imagen;

    public String modeloCorreo = "^[a-zA-Z0-9]+[@]+[a-zA-Z0-9]+[.]+[a-zA-Z0-9]+$";

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
        this.ConectarBD();
        //this.correoUsu=user;
        //jblUsuario.setText(correoUsu);
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

    /*public void validarUsuario() {
        String usuario = this.txtUsuario.getText();
        String pass = this.txtContrasenia.getText();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese el usuario y la contraseña.");
        } else {
            try {
                String sql = "SELECT Correo, contraseña FROM usuarios "
                        + "WHERE Correo='" + usuario + "' AND contraseña='" + pass + "' AND estatus LIKE 'Activo'";

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);

                if (rs.next()) {
                    try {
                        String sentenciaSQL2 = "SELECT r.descripcion FROM rol r INNER JOIN usuarios u ON r.id=u.rol WHERE u.correo = ?";

                        PreparedStatement ps2 = con.prepareStatement(sentenciaSQL2);
                        ps2.setString(1, usuario);
                        ResultSet rs2 = ps2.executeQuery();
                        rol = rs2.getString("r.descripcion");
                        if (rs2.next() && rol.equals("Ventas")) {
                            historialAcceso(usuario);
                            JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
                            //setVisible(false);
                            this.setVisible(false);
                            SaldoInicial dialog = new SaldoInicial(owner, true, usuario);
                            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            dialog.setVisible(true);

                        } else {
                            historialAcceso(usuario);
                            this.setVisible(false);
                            new Principal(usuario, 0).setVisible(true);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //
                } else {
                    JOptionPane.showMessageDialog(null, "USUARIO INACTIVO O NO EXISTE");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }*/

    public void validarUsuario() {
        String usuario = this.txtUsuario.getText();
        String pass = this.txtContrasenia.getText();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese el usuario y la contraseña.");
        } else {
            try {
                // Consulta para validar el usuario y contraseña
                String sql = "SELECT Correo, contraseña FROM usuarios "
                        + "WHERE Correo='" + usuario + "' AND contraseña='" + pass + "' AND estatus LIKE 'Activo'";

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);

                if (rs.next()) {
                    try {
                        // Consulta para obtener el rol del usuario
                        String sentenciaSQL = "SELECT r.descripcion FROM rol r INNER JOIN usuarios u ON r.id=u.rol WHERE u.correo = ?";
                        PreparedStatement ps = con.prepareStatement(sentenciaSQL);
                        ps.setString(1, usuario);
                        ResultSet rs2 = ps.executeQuery();

                        if (rs2.next()) {
                            rol = rs2.getString("r.descripcion");

                            if (rol.equals("Ventas")) {
                                historialAcceso(usuario);
                                JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
                                this.setVisible(false);
                                SaldoInicial dialog = new SaldoInicial(owner, true, usuario);
                                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                                dialog.setVisible(true);
                            } else {
                                historialAcceso(usuario);
                                this.setVisible(false);
                                new Principal(usuario, 0).setVisible(true);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "USUARIO INACTIVO O NO EXISTE");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    public void historialAcceso(String nombreUsuario) {
        String usuarioLogeado = nombreUsuario;
        int idUsuario = 0;
        try {
            //ConectarBD();//METODO ANTERIOR
            //sentenciaSQL = "SELECT id FROM usuarios WHERE correo=" + txtUsuario.getText().trim();
            String sentenciaSQL = "SELECT id FROM usuarios WHERE correo = ?";
            PreparedStatement ps2 = con.prepareStatement(sentenciaSQL);
            ps2.setString(1, usuarioLogeado);

            ResultSet rs = ps2.executeQuery();
            if (rs.next()) {
                // Si el ResultSet tiene al menos un resultado, obtenemos el valor de la columna "id".
                idUsuario = rs.getInt("id");
            }

            sentenciaSQL = "INSERT INTO historialaccesos (id,usuario,fecha)VALUES(?,?,?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setInt(2, idUsuario);
            LocalDateTime fechaHoraActual = LocalDateTime.now();
            java.sql.Timestamp fechaHoraSQL = java.sql.Timestamp.valueOf(fechaHoraActual);
            ps.setTimestamp(3, fechaHoraSQL);
            ps.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            //JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
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
        jLabel6 = new javax.swing.JLabel();
        txtContrasenia = new javax.swing.JPasswordField();
        btnRegistrarse = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnContraseña = new javax.swing.JButton();
        btnIngresar = new javax.swing.JButton();
        jblFondo = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(141, 123, 104));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(241, 222, 201));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(96, 54, 1));
        jLabel1.setText("Ingresar");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 110, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Contraseña");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 270, 157, -1));

        txtContrasenia.setBackground(new java.awt.Color(241, 222, 201));
        jPanel2.add(txtContrasenia, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 270, 230, -1));

        btnRegistrarse.setBackground(new java.awt.Color(54, 21, 0));
        btnRegistrarse.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnRegistrarse.setForeground(new java.awt.Color(227, 202, 165));
        btnRegistrarse.setText("Registrarse");
        btnRegistrarse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarseActionPerformed(evt);
            }
        });
        jPanel2.add(btnRegistrarse, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 340, -1, -1));

        btnSalir.setBackground(new java.awt.Color(54, 21, 0));
        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(227, 202, 165));
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        jPanel2.add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 340, 110, -1));

        btnContraseña.setBackground(new java.awt.Color(54, 21, 0));
        btnContraseña.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnContraseña.setForeground(new java.awt.Color(227, 202, 165));
        btnContraseña.setText("¿Olvidó su contraseña?");
        btnContraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContraseñaActionPerformed(evt);
            }
        });
        jPanel2.add(btnContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, 340, -1));

        btnIngresar.setBackground(new java.awt.Color(54, 21, 0));
        btnIngresar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnIngresar.setForeground(new java.awt.Color(227, 202, 165));
        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });
        jPanel2.add(btnIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 340, 100, -1));

        jblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconos.png"))); // NOI18N
        jPanel2.add(jblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 700, 170));

        jblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo.png"))); // NOI18N
        jPanel2.add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 170));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Correo");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 210, 157, -1));

        txtUsuario.setBackground(new java.awt.Color(241, 222, 201));
        txtUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });
        jPanel2.add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 210, 230, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 700, 610));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        this.dispose();

    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContraseñaActionPerformed
        setVisible(false);
        new ContraOlvidada("").setVisible(true);
    }//GEN-LAST:event_btnContraseñaActionPerformed

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        this.validarUsuario();
    }//GEN-LAST:event_btnIngresarActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void btnRegistrarseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarseActionPerformed
        /*setVisible(false);
        new RegistrarUsuario().setVisible(true);*/
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        setVisible(false);

        RegistrarUsuarios dialog = new RegistrarUsuarios(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnRegistrarseActionPerformed

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContraseña;
    private javax.swing.JButton btnIngresar;
    private javax.swing.JButton btnRegistrarse;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jblFondo;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JPasswordField txtContrasenia;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
