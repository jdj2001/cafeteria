/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import accesoDatosObjetos.Conexion;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import accesoDatosObjetos.Conexion;
import ayuda.AyudaPrincipal;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
//import cafeteriagrupo.pkg6_v1.Principal.jblUsuario;

/**
 *
 * @author Juan Alvarado
 */
public class Principal extends javax.swing.JFrame {

    String rol;
    public String correo;
    private static String correoUsu;
    ImageIcon imagen;
    Icon icono;
    Connection con = null;
    //private static String correoUsu;
    Conexion conecta;
    PreparedStatement ps = null;
    cafeObjetosSaldoInicial co;
    double saldoInicial;

    /**
     * Creates new form Principal
     *
     * @param user
     * @param saldo
     */
    public Principal(String user, double saldo) {
        initComponents();
        ConectarBD();
        this.saldoInicial = saldo;
        co = new cafeObjetosSaldoInicial(saldoInicial);
        //Login jFrame;
        //jFrame = new Login();
        //Login jFrame = new Login();
        jblUsuario.enable(false);
        correoUsu = user;
        validar();
        jblUsuario.setText(correoUsu);
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();
    }

    public String obtenerRolUsuario(String correo) {
        try {
            String sentenciaSQL = "SELECT r.descripcion FROM rol r INNER JOIN usuarios u ON r.id=u.rol WHERE u.correo = ?";
            PreparedStatement ps = con.prepareStatement(sentenciaSQL);
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rol = rs.getString("r.descripcion");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rol;
    }

    public void validar() {
        String rol2 = obtenerRolUsuario(correoUsu);
        if (rol2 != null) {
            if (rol2.equals("Administrador")) {
                btnCategoria.setVisible(true);
                btnProductos.setVisible(true);
                btnModificarUsuario.setVisible(true);
                btnVerificarUsuarios.setVisible(true);
                btnGestionarRol.setVisible(true);
                btnReportes.setVisible(true);
            } else if (rol2.equals("Ventas")) {
                btnCrearOrden.setVisible(true);
                btnClientes.setVisible(true);
                btnCategoria.setVisible(false);
                btnProveedores.setVisible(false);
                btnInventario.setVisible(false);
                btnProductos.setVisible(false);
                btnModificarUsuario.setVisible(false);
                btnVerificarUsuarios.setVisible(false);
                btnGestionarRol.setVisible(false);
                btnReportes.setVisible(false);
            } else if (rol2.equals("Inventario")) {
                btnCategoria.setVisible(false);
                btnProductos.setVisible(false);
                btnModificarUsuario.setVisible(false);
                btnVerificarUsuarios.setVisible(false);
                btnGestionarRol.setVisible(false);
                btnReportes.setVisible(false);
                btnCrearOrden.setVisible(false);
                btnClientes.setVisible(false);
                btnCategoria.setVisible(false);
                btnProveedores.setVisible(false);
                btnInventario.setVisible(true);
                btnProductos.setVisible(true);
                btnModificarUsuario.setVisible(false);
                btnVerificarUsuarios.setVisible(false);
                btnGestionarRol.setVisible(false);
                btnReportes.setVisible(false);
            }
        }
    }

    /*public Principal(String usuCorreo) {
        initComponents();
        correo=usuCorreo;
        if(!correo.equals("admin@gmail.com")){
            btnCategoria.setVisible(false);
            btnNuevoProducto.setVisible(false);
            btnRUDproducto.setVisible(false);
            btnVerificarUsuarios.setVisible(false);
        }
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnAyuda2 = new javax.swing.JButton();
        btnAyuda1 = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnArqueo = new javax.swing.JButton();
        btnInventario = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnVerificarUsuarios = new javax.swing.JButton();
        btnProductos = new javax.swing.JButton();
        btnCategoria = new javax.swing.JButton();
        btnCrearOrden = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        btnReportes = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnCambiarContra = new javax.swing.JButton();
        btnGestionarRol = new javax.swing.JButton();
        btnModificarUsuario = new javax.swing.JButton();
        Fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAyuda2.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda2.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/informacion.png"))); // NOI18N
        btnAyuda2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda2ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1370, 0, 50, 50));

        btnAyuda1.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda1.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1450, 0, 50, 50));

        btnProveedores.setBackground(new java.awt.Color(54, 21, 0));
        btnProveedores.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnProveedores.setForeground(new java.awt.Color(227, 202, 165));
        btnProveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/proveedor.png"))); // NOI18N
        btnProveedores.setText("Proveedores");
        btnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedoresActionPerformed(evt);
            }
        });
        getContentPane().add(btnProveedores, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 470, 310, -1));

        btnClientes.setBackground(new java.awt.Color(54, 21, 0));
        btnClientes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnClientes.setForeground(new java.awt.Color(227, 202, 165));
        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cliente.png"))); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        getContentPane().add(btnClientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 350, 310, -1));

        btnArqueo.setBackground(new java.awt.Color(54, 21, 0));
        btnArqueo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnArqueo.setForeground(new java.awt.Color(227, 202, 165));
        btnArqueo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/verFactura.png"))); // NOI18N
        btnArqueo.setText("Arqueo Caja");
        btnArqueo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArqueoActionPerformed(evt);
            }
        });
        getContentPane().add(btnArqueo, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 650, 650, 50));

        btnInventario.setBackground(new java.awt.Color(54, 21, 0));
        btnInventario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnInventario.setForeground(new java.awt.Color(227, 202, 165));
        btnInventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/inventario.png"))); // NOI18N
        btnInventario.setText("Inventario");
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });
        getContentPane().add(btnInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 410, 310, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("USUARIO:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 80, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jblUsuario.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 260, 30));

        btnVerificarUsuarios.setBackground(new java.awt.Color(54, 21, 0));
        btnVerificarUsuarios.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnVerificarUsuarios.setForeground(new java.awt.Color(227, 202, 165));
        btnVerificarUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/verificarUsuarios.png"))); // NOI18N
        btnVerificarUsuarios.setText("Verificar Usuarios");
        btnVerificarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarUsuariosActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerificarUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 710, -1, -1));

        btnProductos.setBackground(new java.awt.Color(54, 21, 0));
        btnProductos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnProductos.setForeground(new java.awt.Color(227, 202, 165));
        btnProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/producto.png"))); // NOI18N
        btnProductos.setText("Productos");
        btnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductosActionPerformed(evt);
            }
        });
        getContentPane().add(btnProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 410, 310, -1));

        btnCategoria.setBackground(new java.awt.Color(54, 21, 0));
        btnCategoria.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCategoria.setForeground(new java.awt.Color(227, 202, 165));
        btnCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/categoria.png"))); // NOI18N
        btnCategoria.setText("Gestionar Categoría");
        btnCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriaActionPerformed(evt);
            }
        });
        getContentPane().add(btnCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 470, 310, -1));

        btnCrearOrden.setBackground(new java.awt.Color(54, 21, 0));
        btnCrearOrden.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCrearOrden.setForeground(new java.awt.Color(227, 202, 165));
        btnCrearOrden.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/crearOrden.png"))); // NOI18N
        btnCrearOrden.setText("Crear Orden");
        btnCrearOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearOrdenActionPerformed(evt);
            }
        });
        getContentPane().add(btnCrearOrden, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 350, 310, -1));

        btnCerrarSesion.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrarSesion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrarSesion.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/logout.png"))); // NOI18N
        btnCerrarSesion.setText("Cerrar Sesión");
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrarSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 530, 310, -1));

        btnReportes.setBackground(new java.awt.Color(54, 21, 0));
        btnReportes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnReportes.setForeground(new java.awt.Color(227, 202, 165));
        btnReportes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/verFactura.png"))); // NOI18N
        btnReportes.setText("Reportes");
        btnReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportesActionPerformed(evt);
            }
        });
        getContentPane().add(btnReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 590, 310, 50));

        btnSalir.setBackground(new java.awt.Color(54, 21, 0));
        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(227, 202, 165));
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/salir.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        getContentPane().add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 530, 310, 50));

        btnCambiarContra.setBackground(new java.awt.Color(54, 21, 0));
        btnCambiarContra.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCambiarContra.setForeground(new java.awt.Color(227, 202, 165));
        btnCambiarContra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarContra.png"))); // NOI18N
        btnCambiarContra.setText("Cambiar Contraseña");
        btnCambiarContra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarContraActionPerformed(evt);
            }
        });
        getContentPane().add(btnCambiarContra, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 590, 310, -1));

        btnGestionarRol.setBackground(new java.awt.Color(54, 21, 0));
        btnGestionarRol.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnGestionarRol.setForeground(new java.awt.Color(227, 202, 165));
        btnGestionarRol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rol.png"))); // NOI18N
        btnGestionarRol.setText("Gestionar Roles");
        btnGestionarRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGestionarRolActionPerformed(evt);
            }
        });
        getContentPane().add(btnGestionarRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 710, -1, -1));

        btnModificarUsuario.setBackground(new java.awt.Color(54, 21, 0));
        btnModificarUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnModificarUsuario.setForeground(new java.awt.Color(227, 202, 165));
        btnModificarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/usuarios.png"))); // NOI18N
        btnModificarUsuario.setText("Modificar/Registrar Usuario");
        btnModificarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarUsuarioActionPerformed(evt);
            }
        });
        getContentPane().add(btnModificarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 710, -1, -1));

        Fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/coffee-cup-coffee-beans-wooden-tray.jpg"))); // NOI18N
        getContentPane().add(Fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        int a = JOptionPane.showConfirmDialog(null, "¿QUIERE CERRAR LA SESIÓN", "Seleccione", JOptionPane.YES_NO_OPTION);
        if (a == 0) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        new reportes(correoUsu).setVisible(true);
    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        int a = JOptionPane.showConfirmDialog(null, "¿DESEA CERRA LA APLICACIÓN?", "SELECCIONE", JOptionPane.YES_NO_OPTION);
        if (a == 0) {
            //this.dispose();
            System.exit(0);
        }
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnCambiarContraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarContraActionPerformed
        //setVisible(false);
        new ContraOlvidada(correoUsu).setVisible(true);
    }//GEN-LAST:event_btnCambiarContraActionPerformed

    private void btnCrearOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearOrdenActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        Facturacion dialog = new Facturacion(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnCrearOrdenActionPerformed

    private void btnCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriaActionPerformed
        //setVisible(false);
        new GestionarCategoria(correoUsu).setVisible(true);
    }//GEN-LAST:event_btnCategoriaActionPerformed

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        NuevoProducto dialog = new NuevoProducto(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnProductosActionPerformed

    private void btnVerificarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerificarUsuariosActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        VerificarUsuario dialog = new VerificarUsuario(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnVerificarUsuariosActionPerformed

    private void btnModificarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarUsuarioActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        RegistrarUsuarios dialog = new RegistrarUsuarios(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnModificarUsuarioActionPerformed

    private void btnGestionarRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGestionarRolActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        RegistrarRolU dialog = new RegistrarRolU(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnGestionarRolActionPerformed

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        inventario dialog = new inventario(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        clientes dialog = new clientes(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        Proveedores dialog = new Proveedores(owner, true, correoUsu);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnProveedoresActionPerformed

    private void btnAyuda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda1ActionPerformed
        // Verificar si Desktop es compatible en este entorno
        if (Desktop.isDesktopSupported()) {
            try {
                String pdfPath = "src/InformacionSistema/manualUsuario.pdf";
                File pdfFile = new File(pdfPath);

                // Verificar si el archivo existe
                if (pdfFile.exists()) {
                    // Abrir el archivo PDF con la aplicación predeterminada
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("El archivo PDF no existe.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Desktop no es compatible en este sistema.");
        }

    }//GEN-LAST:event_btnAyuda1ActionPerformed

    private void btnArqueoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArqueoActionPerformed
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this); // Obtiene el JFrame actual
        //setVisible(false);

        arqueo dialog = new arqueo(owner, true, saldoInicial);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnArqueoActionPerformed

    private void btnAyuda2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda2ActionPerformed
        if (Desktop.isDesktopSupported()) {
            try {
                String pdfPath = "src/InformacionSistema/informacionSistema.pdf";
                File pdfFile = new File(pdfPath);

                // Verificar si el archivo existe
                if (pdfFile.exists()) {
                    // Abrir el archivo PDF con la aplicación predeterminada
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("El archivo PDF no existe.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Desktop no es compatible en este sistema.");
        }
    }//GEN-LAST:event_btnAyuda2ActionPerformed

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
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal(correoUsu, 0).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Fondo;
    private javax.swing.JButton btnArqueo;
    private javax.swing.JButton btnAyuda1;
    private javax.swing.JButton btnAyuda2;
    private javax.swing.JButton btnCambiarContra;
    private javax.swing.JButton btnCategoria;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnCrearOrden;
    private javax.swing.JButton btnGestionarRol;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnModificarUsuario;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVerificarUsuarios;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jblUsuario;
    // End of variables declaration//GEN-END:variables
}
