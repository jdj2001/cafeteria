/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import REPORTES.reportePrincipal;
import accesoDatosObjetos.Conexion;
import ayuda.AyudaReportes;
import java.awt.Desktop;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Juan Alvarado
 */
public class reportes extends javax.swing.JFrame {

    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon icono;
    ImageIcon imagen;
    private static String correoUsu;
    String PARAMETRO;
    // Crear un diccionario con los parámetros
    //Map<String, Object> parametros = new HashMap<>();

    /**
     * Creates new form reportes
     */
    public reportes(String user) {
        initComponents();
        ConectarBD();
        this.correoUsu = user;
        jblUsuario.setText(correoUsu);
        cambioImagen("dibujos.png", jblFondo);
        cambioImagen("logoSinFondo.png", jblLogo);
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        //icono = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    private void mostrarDocumentoDeAyuda() {
        // Ruta del archivo de ayuda
        String rutaDocumentoAyuda = "D:/clases/Program_Avanzada II/CafeteriaGrupo#6_V1/src/ayuda/RegistrarProveedor.docx";

        // Abrir el archivo de ayuda en el visor de PDF predeterminado
        try {
            Desktop.getDesktop().open(new java.io.File(rutaDocumentoAyuda));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir el documento de ayuda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void llamarReportesPredeterminados(String nombre1) {
        try {
            //ConectarBD();
            //RUTA DEL ARCHIVO
            URL urlMaestro = getClass().getResource(nombre1);
            //LLAMADO DEL ARCHIVO
            JasperReport reporte = (JasperReport) JRLoader.loadObject(urlMaestro);
            //LLENAMOS EL REPORTE
            JasperPrint jprint = JasperFillManager.fillReport(reporte, null, con);//NULL PORQUE NO ESTAMOS MANDADO NINGUN PARAMETRO
            //VISTA DLE REPORTE
            JasperViewer view = new JasperViewer(jprint, false);//
            //TÍTULO OPCIONAL VENTANA
            view.setTitle("REPORTE CAFETERÍA GRUPO #6");
            //CIERRE REPORTE
            view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            view.setVisible(true);
            /*try {
                con.close();
            } catch (SQLException ex) {
                System.out.print(ex.getMessage());
            }*/
        } catch (JRException ex) {
            System.out.print(ex.getMessage());
        }
    }

    public void llamarReportePorParametro() {
        PARAMETRO = JOptionPane.showInputDialog(null, "INGRESE UNO DE LOS SIGUIENTES DATOS PARA LA BÚSQUEDA:\n 1) EL NOMBRE DEL CLIENTE\n 2) RTN DEL CLIENTE\n 3) ID DEL CLIENTE");
        try {
            //18.22 01/08
            Map parametro = new HashMap();
            parametro.put("PARAMETRO", PARAMETRO);//NOMBRE PARAMETRO QUE VA RECIBIR DEL REPORTE "nombreC" y ENVIAMOS EL PARAMETRO ESCRITO DESDE EL FORMULARIO
            //ConectarBD();
            //RUTA DEL ARCHIVO
            URL urlMaestro = getClass().getResource("/REPORTES/busq_reporteComprasClientes.jasper");
            //LLAMADO DEL ARCHIVO
            JasperReport reporte = (JasperReport) JRLoader.loadObject(urlMaestro);
            //LLENAMOS EL REPORTE
            //AHORA SÍ LLEVA PARÁMETRO= parametro
            JasperPrint jprint = JasperFillManager.fillReport(reporte, parametro, con);//NULL PORQUE NO ESTAMOS MANDADO NINGUN PARAMETRO
            //VISTA DLE REPORTE
            JasperViewer view = new JasperViewer(jprint, false);//
            //TÍTULO OPCIONAL VENTANA
            view.setTitle("REPORTE CAFETERÍA GRUPO #6");
            //CIERRE REPORTE
            view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            view.setVisible(true);
            /*try {
                con.close();
            } catch (SQLException ex) {
                System.out.print(ex.getMessage());
            }*/
        } catch (JRException ex) {
            System.out.print(ex.getMessage());
        }
    }

    public void llamarReportePorParametroInventario() {
        PARAMETRO = JOptionPane.showInputDialog(null, "INGRESE UNO DE LOS SIGUIENTES DATOS PARA LA BÚSQUEDA:\n 1) EL CÓDIGO DEL PRODUCTO\n 2) NOMBRE DEL PRODUCTO\n 3) CANTIDAD");
        try {
            //18.22 01/08
            Map parametro = new HashMap();
            parametro.put("PARAMETRO", PARAMETRO);//NOMBRE PARAMETRO QUE VA RECIBIR DEL REPORTE "nombreC" y ENVIAMOS EL PARAMETRO ESCRITO DESDE EL FORMULARIO
            //ConectarBD();
            //RUTA DEL ARCHIVO
            URL urlMaestro = getClass().getResource("/REPORTES/busq_reporteMovimientoInventario.jasper");
            //LLAMADO DEL ARCHIVO
            JasperReport reporte = (JasperReport) JRLoader.loadObject(urlMaestro);
            //LLENAMOS EL REPORTE
            //AHORA SÍ LLEVA PARÁMETRO= parametro
            JasperPrint jprint = JasperFillManager.fillReport(reporte, parametro, con);//NULL PORQUE NO ESTAMOS MANDADO NINGUN PARAMETRO
            //VISTA DLE REPORTE
            JasperViewer view = new JasperViewer(jprint, false);//
            //TÍTULO OPCIONAL VENTANA
            view.setTitle("REPORTE CAFETERÍA GRUPO #6");
            //CIERRE REPORTE
            view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            view.setVisible(true);
            /*try {
                con.close();
            } catch (SQLException ex) {
                System.out.print(ex.getMessage());
            }*/
        } catch (JRException ex) {
            System.out.print(ex.getMessage());
        }
    }
    
    public void llamarReportePorParametroAccesosSistema() {
        PARAMETRO = JOptionPane.showInputDialog(null, "INGRESE UNO DE LOS SIGUIENTES DATOS PARA LA BÚSQUEDA:\n 1) EL CÓDIGO DEL USUARIO\n 2) NOMBRE DEL USUARIO\n 3) CORREO DEL USUARIO");
        try {
            //18.22 01/08
            Map parametro = new HashMap();
            parametro.put("PARAMETRO", PARAMETRO);//NOMBRE PARAMETRO QUE VA RECIBIR DEL REPORTE "nombreC" y ENVIAMOS EL PARAMETRO ESCRITO DESDE EL FORMULARIO
            //ConectarBD();
            //RUTA DEL ARCHIVO
            URL urlMaestro = getClass().getResource("/REPORTES/busq_reporteAccesosUsuarios.jasper");
            //LLAMADO DEL ARCHIVO
            JasperReport reporte = (JasperReport) JRLoader.loadObject(urlMaestro);
            //LLENAMOS EL REPORTE
            //AHORA SÍ LLEVA PARÁMETRO= parametro
            JasperPrint jprint = JasperFillManager.fillReport(reporte, parametro, con);//NULL PORQUE NO ESTAMOS MANDADO NINGUN PARAMETRO
            //VISTA DLE REPORTE
            JasperViewer view = new JasperViewer(jprint, false);//
            //TÍTULO OPCIONAL VENTANA
            view.setTitle("REPORTE CAFETERÍA GRUPO #6");
            //CIERRE REPORTE
            view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            view.setVisible(true);
            /*try {
                con.close();
            } catch (SQLException ex) {
                System.out.print(ex.getMessage());
            }*/
        } catch (JRException ex) {
            System.out.print(ex.getMessage());
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

        btnAyuda = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnFeIngresos1 = new javax.swing.JButton();
        btnPrProductos3 = new javax.swing.JButton();
        btnPrProductos4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnBuComprasCliente = new javax.swing.JButton();
        btnBuSalidaInventario = new javax.swing.JButton();
        btnBuAccesoSistema = new javax.swing.JButton();
        btnPrProductos1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnPrProductos = new javax.swing.JButton();
        btnPrEmpleados = new javax.swing.JButton();
        btnPrProveedores = new javax.swing.JButton();
        btnPrProductos2 = new javax.swing.JButton();
        jblLogo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        jblFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAyuda.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(1440, 0, 50, 50));

        jPanel4.setBackground(new java.awt.Color(241, 222, 201));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Reportes y Gráficos por Fecha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnFeIngresos1.setBackground(new java.awt.Color(54, 21, 0));
        btnFeIngresos1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnFeIngresos1.setForeground(new java.awt.Color(227, 202, 165));
        btnFeIngresos1.setText("Ingresos y Ventas");
        btnFeIngresos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFeIngresos1ActionPerformed(evt);
            }
        });
        jPanel4.add(btnFeIngresos1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 340, 40));

        btnPrProductos3.setBackground(new java.awt.Color(54, 21, 0));
        btnPrProductos3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrProductos3.setForeground(new java.awt.Color(227, 202, 165));
        btnPrProductos3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/reporteFecha.png"))); // NOI18N
        btnPrProductos3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrProductos3ActionPerformed(evt);
            }
        });
        jPanel4.add(btnPrProductos3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 170, 40));

        btnPrProductos4.setBackground(new java.awt.Color(54, 21, 0));
        btnPrProductos4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrProductos4.setForeground(new java.awt.Color(227, 202, 165));
        btnPrProductos4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/reporteGrafico.png"))); // NOI18N
        btnPrProductos4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrProductos4ActionPerformed(evt);
            }
        });
        jPanel4.add(btnPrProductos4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, 170, 40));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 570, 490, 250));

        jPanel2.setBackground(new java.awt.Color(241, 222, 201));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Reportes Por Búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnBuComprasCliente.setBackground(new java.awt.Color(54, 21, 0));
        btnBuComprasCliente.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnBuComprasCliente.setForeground(new java.awt.Color(227, 202, 165));
        btnBuComprasCliente.setText("Reporte Compras Clientes");
        btnBuComprasCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuComprasClienteActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuComprasCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 340, 40));

        btnBuSalidaInventario.setBackground(new java.awt.Color(54, 21, 0));
        btnBuSalidaInventario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnBuSalidaInventario.setForeground(new java.awt.Color(227, 202, 165));
        btnBuSalidaInventario.setText("Reporte Salidas Inventario");
        btnBuSalidaInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuSalidaInventarioActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuSalidaInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 340, 40));

        btnBuAccesoSistema.setBackground(new java.awt.Color(54, 21, 0));
        btnBuAccesoSistema.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnBuAccesoSistema.setForeground(new java.awt.Color(227, 202, 165));
        btnBuAccesoSistema.setText("Reporte Accesos Sistema");
        btnBuAccesoSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuAccesoSistemaActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuAccesoSistema, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 220, 340, 40));

        btnPrProductos1.setBackground(new java.awt.Color(54, 21, 0));
        btnPrProductos1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrProductos1.setForeground(new java.awt.Color(227, 202, 165));
        btnPrProductos1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnPrProductos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrProductos1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnPrProductos1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 340, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 250, 490, 300));

        jPanel1.setBackground(new java.awt.Color(241, 222, 201));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Reportes Predefinidos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPrProductos.setBackground(new java.awt.Color(54, 21, 0));
        btnPrProductos.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrProductos.setForeground(new java.awt.Color(227, 202, 165));
        btnPrProductos.setText("Reporte Productos");
        btnPrProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrProductosActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 340, 40));

        btnPrEmpleados.setBackground(new java.awt.Color(54, 21, 0));
        btnPrEmpleados.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrEmpleados.setForeground(new java.awt.Color(227, 202, 165));
        btnPrEmpleados.setText("Reporte Empleados");
        btnPrEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrEmpleadosActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrEmpleados, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 340, 40));

        btnPrProveedores.setBackground(new java.awt.Color(54, 21, 0));
        btnPrProveedores.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrProveedores.setForeground(new java.awt.Color(227, 202, 165));
        btnPrProveedores.setText("Reporte Proveedores");
        btnPrProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrProveedoresActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrProveedores, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, 340, 40));

        btnPrProductos2.setBackground(new java.awt.Color(54, 21, 0));
        btnPrProductos2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPrProductos2.setForeground(new java.awt.Color(227, 202, 165));
        btnPrProductos2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/verFactura.png"))); // NOI18N
        btnPrProductos2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrProductos2ActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrProductos2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 50, 340, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 250, 490, 300));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 230));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setText("REPORTES");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 110, -1, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("USUARIO:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 890, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 890, 290, 30));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1510, 0, 50, 50));
        getContentPane().add(jblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1560, 940));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrProveedoresActionPerformed
        llamarReportesPredeterminados("/REPORTES/pre_reporteProveedores.jasper");
    }//GEN-LAST:event_btnPrProveedoresActionPerformed

    private void btnPrProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrProductosActionPerformed
        llamarReportesPredeterminados("/REPORTES/pre_reporteProductos.jasper");
    }//GEN-LAST:event_btnPrProductosActionPerformed

    private void btnPrEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrEmpleadosActionPerformed
        llamarReportesPredeterminados("/REPORTES/pre_reporteEmpleados.jasper");
    }//GEN-LAST:event_btnPrEmpleadosActionPerformed

    private void btnBuComprasClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuComprasClienteActionPerformed
        llamarReportePorParametro();
    }//GEN-LAST:event_btnBuComprasClienteActionPerformed

    private void btnBuSalidaInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuSalidaInventarioActionPerformed
        llamarReportePorParametroInventario();
    }//GEN-LAST:event_btnBuSalidaInventarioActionPerformed

    private void btnBuAccesoSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuAccesoSistemaActionPerformed
        llamarReportePorParametroAccesosSistema();
    }//GEN-LAST:event_btnBuAccesoSistemaActionPerformed

    private void btnFeIngresos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFeIngresos1ActionPerformed
        new reportePrincipal().setVisible(true);
    }//GEN-LAST:event_btnFeIngresos1ActionPerformed

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        AyudaReportes dialog2 = new AyudaReportes(this, true); // El parámetro 'this' hace que Dialog1 sea el dialogo padre de Dialog2
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyudaActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnPrProductos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrProductos1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrProductos1ActionPerformed

    private void btnPrProductos2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrProductos2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrProductos2ActionPerformed

    private void btnPrProductos3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrProductos3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrProductos3ActionPerformed

    private void btnPrProductos4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrProductos4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrProductos4ActionPerformed

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
            java.util.logging.Logger.getLogger(reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(reportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new reportes(correoUsu).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnBuAccesoSistema;
    private javax.swing.JButton btnBuComprasCliente;
    private javax.swing.JButton btnBuSalidaInventario;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnFeIngresos1;
    private javax.swing.JButton btnPrEmpleados;
    private javax.swing.JButton btnPrProductos;
    private javax.swing.JButton btnPrProductos1;
    private javax.swing.JButton btnPrProductos2;
    private javax.swing.JButton btnPrProductos3;
    private javax.swing.JButton btnPrProductos4;
    private javax.swing.JButton btnPrProveedores;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel jblFondo;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    // End of variables declaration//GEN-END:variables
}
