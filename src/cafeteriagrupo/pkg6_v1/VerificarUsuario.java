/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaVerificarUsuarios;
import java.awt.Frame;
import java.awt.Image;
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
public class VerificarUsuario extends javax.swing.JDialog {

    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon icono;
    ImageIcon imagen;
    private static String correoUsu;
    ResultSet rs = null; //PERMITE TENER METODOS PARA OBTENER INFO DE LAS DIFERENTES COLUMNAS DE UNA FILA=TIPO RESULT SET VARIABE
    DefaultTableModel modelo; //PARA EL MÓDELO DE NUESTRA TABLA
    Object datosUsuario[] = new Object[8];

    /**
     * Creates new form VerificarUsuario
     */
    public VerificarUsuario(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        correoUsu = user;
        jblUsuario.enable(false);
        jblUsuario.setText(correoUsu);
        this.setLocationRelativeTo(null);
        this.ConectarBD();
        cambioImagen("dibujos.png", fondo);
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        //icono = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void obtenerUsuario(String filtro) {
        modelo = (DefaultTableModel) tblUsuario.getModel();
        modelo.setRowCount(0);

        try {
            String query = "SELECT U.id, U.nombre, U.identidad, U.telefono, U.correo, U.preguntaSeguridad, R.descripcion, U.estatus FROM usuarios U "
                    + "INNER JOIN rol R ON U.rol=R.id WHERE U.correo LIKE ? OR U.nombre LIKE ? OR U.identidad LIKE ?";
            PreparedStatement stmt = con.prepareStatement(query);
            String likeFilter = "%" + filtro + "%";
            stmt.setString(1, likeFilter);
            stmt.setString(2, likeFilter);
            stmt.setString(3, likeFilter);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("identidad"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("preguntaSeguridad"),
                    rs.getString("descripcion"),
                    rs.getString("estatus")
                };
                modelo.addRow(fila);
            }
            stmt.close();
            rs.close();

        } catch (SQLException ex) {
            System.out.println("Error al obtener usuarios: " + ex.getMessage());
        }
    }

    public void buscar() {
        try {
            String query = "SELECT U.id,U.nombre,U.identidad,U.telefono,U.correo,U.preguntaSeguridad,R.descripcion,U.estatus FROM usuarios U"
                    + " INNER JOIN rol R ON U.rol=R.id WHERE U.correo = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, txtBuscar.getText().trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Si se encuentra el cliente, cargar sus datos en los campos correspondientes

                modelo = (DefaultTableModel) tblUsuario.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
                modelo.setRowCount(0);
                //Object[] datosClientes = new Object[4];
                datosUsuario[0] = rs.getString(1);
                datosUsuario[1] = rs.getString(2);
                datosUsuario[2] = rs.getString(3);
                datosUsuario[3] = rs.getString(4);
                datosUsuario[4] = rs.getString(5);
                datosUsuario[5] = rs.getString(6);
                datosUsuario[6] = rs.getString(7);
                datosUsuario[7] = rs.getString(8);
                modelo.addRow(datosUsuario);

                //tblProveedores.setModel(modelo);
                //this.leerClientes();
                tblUsuario.setModel(modelo);
            } else {
                JOptionPane.showMessageDialog(null, "NO EXISTE ESTE USUARIO");
            }

        } catch (SQLException ex) {
            System.out.println("ERROR" + ex.getMessage());
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

        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuario = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton();
        jblUsuario = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtBuscar.setBackground(new java.awt.Color(241, 222, 201));
        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarKeyTyped(evt);
            }
        });
        getContentPane().add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 150, 250, 30));

        tblUsuario.setBackground(new java.awt.Color(227, 202, 165));
        tblUsuario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(54, 21, 0), 2, true));
        tblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblUsuario.setForeground(new java.awt.Color(54, 21, 0));
        tblUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "NOMBRE", "IDENTIDAD", "TELÉFONO", "CORREO", "PREGUNTA", "ROL", "ESTATUS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUsuario.setColumnSelectionAllowed(true);
        tblUsuario.setGridColor(new java.awt.Color(241, 222, 201));
        tblUsuario.setSelectionForeground(new java.awt.Color(241, 222, 201));
        tblUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuario);
        tblUsuario.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 1010, 290));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(96, 54, 1));
        jLabel1.setText("CLIC EN UNA FILA PARA CAMBIAR EL ESTADO");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 510, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(96, 54, 1));
        jLabel2.setText("VERIFICAR USUARIOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, -1, -1));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 0, 50, 50));

        btnAyuda.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 0, 50, 50));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jblUsuario.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, 260, 30));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("USUARIO:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1050, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuarioMouseClicked
        int fila = tblUsuario.getSelectedRow();
        int id = Integer.parseInt(tblUsuario.getValueAt(fila, 0).toString());
        String correo = (tblUsuario.getValueAt(fila, 4).toString());
        String estatus = (tblUsuario.getValueAt(fila, 7).toString());

        int opcion = JOptionPane.showOptionDialog(this, "¿Desea cambiar el estado del usuario?", "Pregunta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (opcion == JOptionPane.YES_OPTION) {
            DefaultTableModel modelo = (DefaultTableModel) tblUsuario.getModel();

            if (estatus.equals("Activo")) {
                estatus = "Inactivo";
            } else if (estatus.equals("Inactivo")) {
                estatus = "Activo";
            }

            tblUsuario.setValueAt(estatus, fila, 7); // Modificar el estado en el modelo del JTable
            modelo.fireTableDataChanged(); // Actualizar la tabla para mostrar la modificación

            // Actualizar el estado en la base de datos
            try {
                ConectarBD(); // Método para conectarse a la base de datos (puedes adaptar tu método existente)
                String sentenciaSQL = "UPDATE usuarios SET estatus=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sentenciaSQL);
                ps.setString(1, estatus);
                ps.setInt(2, id); // Reemplaza "idUsuario" con el identificador del usuario seleccionado

                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(VerificarUsuario.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR EL ESTADO" + ex.getMessage());
            }
        } else {
            return;
        }

    }//GEN-LAST:event_tblUsuarioMouseClicked

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        obtenerUsuario(txtBuscar.getText());
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void txtBuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarKeyTyped

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame clientes = null;
        AyudaVerificarUsuarios dialog2 = new AyudaVerificarUsuarios(clientes, true);
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyudaActionPerformed

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
            java.util.logging.Logger.getLogger(VerificarUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VerificarUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VerificarUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VerificarUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerificarUsuario dialog = new VerificarUsuario(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JTable tblUsuario;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
