/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REPORTES;

import accesoDatosObjetos.Conexion;
import cafeteriagrupo.pkg6_v1.reportes;
//import cafeteriagrupo.pkg6_v1.reportes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
//


/**
 *
 * @author Juan Alvarado
 */
public class graFecha_reporteVentas extends javax.swing.JPanel {

    Connection con = null;
    Conexion conecta;
    String sentenciaSQL;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String encontrado;
    Date fechasSQL;
    Date fechaI, fechaF;
    long fe1, fe2;
    java.sql.Date f1, f2;
    PdfPTable tabla = new PdfPTable(3);
    Document reporte = new Document();
    ImageIcon icono;
    ImageIcon imagen;
    DefaultTableModel modelo;
    Object datosFecha[] = new Object[3];
    private reportePrincipal frame;
    private static String correoUsu;

    /**
     * Creates new form graFecha_reporteVentas
     * @param principal
     */
    public graFecha_reporteVentas(JFrame principal) {
        initComponents();
        conectarBD();
        cambioImagen("dibujos.png", jblFondo);
        cambioImagen("logoSinFondo.png", jblLogo);
        this.frame = (reportePrincipal) principal;
    }
    public void fechaSQL() {
        fechaI = jdcFecha1.getDate();//OBTENEMOS LA FECHA DEL JDATECHOOSER
        fechaF = jdcFecha2.getDate();
        fe1 = fechaI.getTime();//getTime() AYUDA A ASIGNAR  UNA FECHA Y HORA A OTRO OBJETO DATE        
        fe2 = fechaF.getTime();
        f1 = new java.sql.Date(fe1);//CONVERTIMOS A FECHA SQL
        f2 = new java.sql.Date(fe2);
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        //icono = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), java.awt.Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    public void conectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();
    }

    //MOSTRAR EN EL JTABLE
    /*public void reportePorFechas() {
        encontrado = "NO";
        try {
            //sentenciaSQL = "SELECT P.id,F.producto,F.fecha,P.utilidad FROM factura F INNER JOIN productos P ON F.codigoProducto=P.id WHERE F.fecha BETWEEN ' " + f1 + " 'and ' " + f2 + " ' ";
            sentenciaSQL = "SELECT codigoProducto,producto,fecha,total FROM factura WHERE fecha BETWEEN ' " + f1 + " 'and ' " + f2 + " ' ";
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblDatos.getModel();
            while (rs.next()) {
                datosFecha[0] = (rs.getInt("codigoProducto"));
                datosFecha[1] = (rs.getDate("producto"));
                datosFecha[2] = (rs.getInt("fecha"));
                datosFecha[3] = (rs.getInt("total"));
                modelo.addRow(datosFecha);
                encontrado = "SI";
            }
            if (encontrado.equals("NO")) {
                JOptionPane.showMessageDialog(null, "PAGO NO ENCONTRADO", "ATENCION!", JOptionPane.ERROR_MESSAGE);
            }
            tblDatos.setModel(modelo);
        } catch (HeadlessException | SQLException y) {
        }
    }*/
    public void reportePorFechas() {
        encontrado = "NO";
        try {
            sentenciaSQL = "SELECT producto, cantidad, fecha FROM factura WHERE fecha BETWEEN ? AND ?";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setDate(1, f1);
            ps.setDate(2, f2);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblDatos.getModel();
            while (rs.next()) {
                datosFecha[0] = rs.getString("producto");
                datosFecha[1] = rs.getString("cantidad");
                datosFecha[2] = rs.getDate("fecha");
                modelo.addRow(datosFecha);
                encontrado = "SI";
            }
            if (encontrado.equals("NO")) {
                JOptionPane.showMessageDialog(null, "REGISTRO NO ENCONTRADO", "ATENCION!", JOptionPane.ERROR_MESSAGE);
            }
            tblDatos.setModel(modelo);
        } catch (SQLException y) {
            y.printStackTrace();
        }
    }

    //LIMPIA LA TABLA
    public void limpiarTabla() {
        int fila = tblDatos.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);
        }
    }

    //MISMO QUE EL HECHO COMO CLASE DE JAVA. PARA QUE LO
    public void crearReportePDF() {

        try {
            //RUTA DE LA CARPETA DE USUARIO
            String ruta = System.getProperty("user.home");
            PdfWriter.getInstance(reporte, new FileOutputStream(ruta + "/Desktop/REPORTES/ReporteGrafico_VentasPorFechas.pdf"));
            Image logo = Image.getInstance("src/imagenes/logoSinFondo.png");
            logo.scaleToFit(400, 400);
            logo.setAlignment(Chunk.ALIGN_CENTER);
            Paragraph encabezado = new Paragraph();
            encabezado.setAlignment(Element.ALIGN_CENTER);//ALINEACION
            encabezado.setFont(FontFactory.getFont("Arial", 20, Font.BOLD, BaseColor.BLUE));//TIPO DE FUENTE, FORMATO Y COLOR
            encabezado.add("INGRESOS\n\n");//TEXTO QUE APARECERÁ
            encabezado.setFont(FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.DARK_GRAY));
            encabezado.add("REPORTE DE INGRESOS POR FECHAS\n\n");

            //AGREGAMOS LAS CELDAS A LA TABLA
            tabla.addCell("PRODUCTO");
            tabla.addCell("CANTIDAD");
            tabla.addCell("FECHA");

            //ABRIMOS EL DOCUMENTO
            reporte.open();
            //AGREGAMOS AL DOCUMENTO, EL LOGO (IMAGEN) Y ENCABEZADO
            reporte.add(logo);
            reporte.add(encabezado);
            //LLAMAMOS AL METODO BUSCAR EN LA BASE DE DATOS LOS REGISTROS QUE TENEMOS
            BuscarEnBD(f1, f2);
            //ABRIMOS EL PDF UNA VEZ QUE HA SIDO CREADO
            abrirReporte();

        } catch (DocumentException | FileNotFoundException e) {
            System.out.print("ERROR " + e);
        } catch (IOException e) {
            System.out.print("ERROR " + e);
        }
    }
    //PARA QUE LO PONGA EN EL PDF

    public void BuscarEnBD(java.sql.Date fechaInicio, java.sql.Date fechaFin) {
        try {
            //sentenciaSQL = "SELECT P.id,F.producto,F.fecha,P.utilidad FROM factura F INNER JOIN productos P ON F.codigoProducto=P.id WHERE F.fecha WHERE fecha BETWEEN ' " + fechaInicio + " 'and ' " + fechaFin + " ' ";
            sentenciaSQL = "SELECT producto, cantidad, fecha FROM factura WHERE fecha BETWEEN ? AND ?";

            ps = con.prepareStatement(sentenciaSQL);
            ps.setDate(1, f1);
            ps.setDate(2, f2);
            rs = ps.executeQuery();
            //HACE EL RECORRIDO EN LA TABLA DE LA BD Y LOS VA AGREGANDO A LA TABLA DEL PDF
            while (rs.next()) {
                tabla.addCell(rs.getString(1));//AQUI IRÁ EL NUMERO SEGUN LA POSICION DEL CAMPO EN SU TABLA DE LA BD
                tabla.addCell(rs.getString(2));
                tabla.addCell(rs.getString(3));

            }
            reporte.add(tabla);//AGREGAMOS AL DOCUMENTO LA TABLA QUE YA ESTÁ LLENA CON LOS DATOS OBTENIDOS DE LA BD                                 
            encontrado = "SI";
            if (encontrado.equals("NO")) {
                JOptionPane.showMessageDialog(null, "REGISTRO NO ENCONTRADO", "ATENCION!", JOptionPane.ERROR_MESSAGE);
            }
            reporte.close();//CERRAMOS EL DOCUMENTO
            JOptionPane.showMessageDialog(null, "REPORTE HA SIDO CREADO!", "ATENCION", 1);
        } catch (DocumentException | HeadlessException | SQLException x) {
            System.out.print("ERROR " + x);
        }
    }

    public void abrirReporte() {
        try {
            //RUTA DEL DIRECTORIO
            String ruta = System.getProperty("user.home");
            //AGREGAMOS LA RUTA DONDE DEBE IR A BUSCAR EL PDF PARA ABRIRLO
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + ruta + "/Desktop/REPORTES/ReporteGrafico_VentasPorFechas.pdf");
            System.out.println("REPORTE VISUALIZADO");
        } catch (IOException e) {
        }
    }

    //PARA QUE LO PONGA EN EL IREPORT
    public void reportePorFechasiReport() {
        try {
            Map parametro = new HashMap();
            //SI EL REPORTE DEPENDE DE VARIOS PARÁMETROS SE PONE TODO EN UNO SOLO, SE ENVÍA UNA SOLA VARIABLE
            parametro.put("fechaIni", f1);//EL QUE RECIBE
            parametro.put("fechaFin", f2);//EL QUE SE ENVÍA

            JasperReport reporte = null;
            //RUTA DEL ARCHIVO
            URL urlMaestro = getClass().getResource("/REPORTES/graf_reporteProductosMasVendidos.jasper");
            //LLAMADO DEL ARCHIVO
            reporte = (JasperReport) JRLoader.loadObject(urlMaestro);
            //LLENADO DEL REPORTE
            JasperPrint jprint = JasperFillManager.fillReport(reporte, parametro, con);
            //VISTA DEL REPORTE
            JasperViewer view = new JasperViewer(jprint, false);
            //TITULO (OPCIONAL)
            view.setTitle("REPORTE DE INGRESOS POR FECHAS");
            //CIERRE DEL REPORTE
            view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            view.setVisible(true);
        } catch (JRException e) {
            System.out.print(e);
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
        btnObtenerFecha = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDatos = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jdcFecha2 = new com.toedter.calendar.JDateChooser();
        jdcFecha1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        btnPDF = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        botSalir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        jblLogo = new javax.swing.JLabel();
        jblFondo = new javax.swing.JLabel();

        setLayout(null);

        jPanel1.setBackground(new java.awt.Color(241, 222, 201));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Reportes por Rango de Fechas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnObtenerFecha.setBackground(new java.awt.Color(54, 21, 0));
        btnObtenerFecha.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnObtenerFecha.setForeground(new java.awt.Color(227, 202, 165));
        btnObtenerFecha.setText("GENERAR");
        btnObtenerFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObtenerFechaActionPerformed(evt);
            }
        });
        jPanel1.add(btnObtenerFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 90, 110, 30));

        tblDatos.setBackground(new java.awt.Color(241, 222, 201));
        tblDatos.setForeground(new java.awt.Color(54, 21, 0));
        tblDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOMBRE PRODUCTO", "FECHA", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDatos.getTableHeader().setReorderingAllowed(false);
        tblDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDatosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDatos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 760, 240));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("FECHA FINAL");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, -1, 30));
        jPanel1.add(jdcFecha2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 140, 30));

        jdcFecha1.setDateFormatString("dd-MM-yyyy");
        jPanel1.add(jdcFecha1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, 150, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("FECHA INICIAL");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, 30));

        btnLimpiar.setBackground(new java.awt.Color(54, 21, 0));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(227, 202, 165));
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        jPanel1.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 220, 110, 30));

        btnPDF.setBackground(new java.awt.Color(54, 21, 0));
        btnPDF.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnPDF.setForeground(new java.awt.Color(227, 202, 165));
        btnPDF.setText("GENERAR PDF");
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });
        jPanel1.add(btnPDF, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 220, 150, 30));

        btnReport.setBackground(new java.awt.Color(54, 21, 0));
        btnReport.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnReport.setForeground(new java.awt.Color(227, 202, 165));
        btnReport.setText("REPORTE EN iREPORT");
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        jPanel1.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 220, 190, 30));

        botSalir.setText("SALIR");
        botSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botSalirActionPerformed(evt);
            }
        });
        jPanel1.add(botSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, 150, -1));

        add(jPanel1);
        jPanel1.setBounds(120, 260, 860, 540);

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        add(btnCerrar);
        btnCerrar.setBounds(990, 0, 60, 49);
        add(jblLogo);
        jblLogo.setBounds(0, 0, 260, 230);
        add(jblFondo);
        jblFondo.setBounds(0, 0, 1050, 840);
    }// </editor-fold>//GEN-END:initComponents

    private void btnObtenerFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObtenerFechaActionPerformed
        fechaSQL();
        reportePorFechas();
    }//GEN-LAST:event_btnObtenerFechaActionPerformed

    private void tblDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDatosMouseClicked

    }//GEN-LAST:event_tblDatosMouseClicked

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarTabla();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        crearReportePDF();
    }//GEN-LAST:event_btnPDFActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        reportePorFechasiReport();
    }//GEN-LAST:event_btnReportActionPerformed

    private void botSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botSalirActionPerformed
        /*reportes miReporte = new reportes();

        // Llamar al método dispose() en la instancia de reportes
        miReporte.dispose();*/
        // Crear una instancia de la clase reportes
        /*String argumento = "graFecha_reporteVentas"; // Reemplaza esto con el argumento adecuado
        reportes miReporte = new reportes(argumento);

        // Llamar al método dispose() en la instancia de reportes
        miReporte.dispose();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);*/
    }//GEN-LAST:event_botSalirActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        frame.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botSalir;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnObtenerFecha;
    private javax.swing.JButton btnPDF;
    private javax.swing.JButton btnReport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblFondo;
    private javax.swing.JLabel jblLogo;
    private com.toedter.calendar.JDateChooser jdcFecha1;
    private com.toedter.calendar.JDateChooser jdcFecha2;
    private javax.swing.JTable tblDatos;
    // End of variables declaration//GEN-END:variables
}
