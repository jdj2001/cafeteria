/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import java.awt.Color;
import java.awt.Image;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import accesoDatosObjetos.Conexion;
import ayuda.AyudaArqueoCaja;
import java.awt.Frame;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

/**
 *
 * @author Juan Alvarado
 */
public class arqueo extends javax.swing.JDialog {

    ImageIcon imagen;
    Icon icono;
    double acumuEfectivo = 0;
    boolean validar = true;
    double totalC, totalGeneralCaja, tarjeta, gastos, efectivo, diferencia;
    cafeObjetosSaldoInicial co;
    double saldoInicial;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;

    /**
     * Creates new form arqueo
     */
    public arqueo(java.awt.Frame parent, boolean modal, double saldo) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        this.saldoInicial = saldo;
        co = new cafeObjetosSaldoInicial(saldoInicial);
        cambioImagen("dibujos.png", fondo);
        cambioImagen("logoSinFondo.png", jblLogo);
        btnCaja.setEnabled(false);
        btnResumen.setEnabled(false);
        btnCierre.setEnabled(false);
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        //icono = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    public boolean validacion() {
        //EN FORMA DE PAGO. SI ESTÁ VACÍO LE MUETSRA QUE HAGA EL RECUENTO DE LAS CANTIDADES
        //
        if (txtTotalEfectivo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "DEBE EJECUTAR PRIMERO EL RECUENTO");
            txtTotalEfectivo.setBackground(Color.red);
            validar = false;//VER GRABACION 1 JUNIO 18:28
        }
        return validar;
    }

    //TARJETA DE PANEL FORMA DE AGO
    public void validacion2() {
        //VALIDAR SI LA TARJETA ESTÁ VACÍA, LOS COBROS POR TARJETA (PUEDE UE HAYA O NO)
        if (txtTarjeta.getText().isEmpty()) {
            txtTarjeta.setText(String.valueOf("0"));
            tarjeta = 0;
        } else {
            tarjeta = Double.parseDouble(txtTarjeta.getText());
        }
        //PARA GASTOS
        if (txtGastos.getText().isEmpty()) {
            txtGastos.setText(String.valueOf("0"));
            gastos = 0;
        } else {
            gastos = Double.parseDouble(txtGastos.getText());
        }
    }

    /*public void recuentoEfectivo4() {
        double[] cantidades = {500.00, 200.00, 100.00, 50.00, 20.00, 10.00, 5.00, 2.00, 1.00};
        acumuEfectivo = DoubleStream.range(0, cantidades.length)
                .mapToObj(i -> new Object[]{cero(((JTextField) Stream.of(txt500, txt200, txt100, txt50, txt20, txt10, txt5, txt2, txt1).toArray()[i]).getText()), i})
                .mapToDouble(obj -> (Double) obj[0] * cantidades[(Double) obj[1]]).sum();
        double[] cantidadesCentavos = {50, 20, 10, 5};
        acumuEfectivo = IntStream.range(0, cantidades.length)
                .mapToObj(i -> new Object[]{cero(((JTextField) Stream.of(txt50Cen, txt20Cen, txt10Cen, txt5Cen).toArray()[i]).getText()), i})
                .mapToInt(obj -> (Integer) obj[0] * cantidades[(Integer) obj[1]]).sum();
    }*/
    public void recuentoEfectivo4() {
        double[] cantidades = {500.00, 200.00, 100.00, 50.00, 20.00, 10.00, 5.00, 2.00, 1.00};
        double[] cantidadesCentavos = {0.50, 0.20, 0.10, 0.05};

        double sumaLempiras = IntStream.range(0, cantidades.length)
                .mapToObj(i -> new Object[]{cero(((JTextField) Stream.of(txt500, txt200, txt100, txt50, txt20, txt10, txt5, txt2, txt1).toArray()[i]).getText()), i})
                .mapToDouble(obj -> (Integer) obj[0] * cantidades[(int) obj[1]]).sum();

        double sumaCentavos = IntStream.range(0, cantidadesCentavos.length)
                .mapToObj(i -> new Object[]{cero(((JTextField) Stream.of(txt50Cen, txt20Cen, txt10Cen, txt5Cen).toArray()[i]).getText()), i})
                .mapToDouble(obj -> (Integer) obj[0] * cantidadesCentavos[(int) obj[1]]).sum();

        acumuEfectivo = Math.round((sumaLempiras + sumaCentavos) * 100.0) / 100.0;
        btnCaja.setEnabled(true);
        btnRecuento.setEnabled(false);
    }

    public static int cero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /*//PANEL "CAJA"
    public void caja() {
        validacion2();
        efectivo = Double.parseDouble(txtTotalEfectivo.getText()); //arriba derecha
        totalGeneralCaja = efectivo + tarjeta;
        //arriba derecha
        txtTotalCaja.setText("" + totalGeneralCaja);
    }
    public void diferencia() {
        //estan en los parametros, totalC y saldoI
        diferencia = totalGeneralCaja - totalC - saldoI + gastos;
        txtDiferencia.setText("" + diferencia);//PANEL ABAJO CON ETIQUETA DIFERENCIA
        if (diferencia < 0) {
            jlbInformacion.setText("PRESENTA UNA DIFERENCIA QUE TIENE QUE PAGAR");
            txtDiferencia.setForeground(Color.red);
        } else if (diferencia == 0) {
            jlbInformacion.setText("NO PRESENTA DIFERENCIA");
            txtDiferencia.setForeground(Color.black);
        } else {
            jlbInformacion.setText("PRESENTA UNA DIFERENCIA A SU FAVOR");
            txtDiferencia.setForeground(Color.black);
        }

    }*/
    public void calcularTotalCompras(LocalDate fecha) {
        try {
            String sql = "SELECT SUM(total) AS total FROM factura WHERE fecha = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totalC = rs.getDouble("total");
            } else {
                totalC = 0.0; // Si no hay compras para esa fecha, el total será cero
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al calcular el total de compras: " + ex.getMessage());
        }
    }

    public void caja() {
        validacion2();
        efectivo = Double.parseDouble(txtTotalEfectivo.getText()); //arriba derecha

        // Llama al método para calcular el total de compras de la fecha actual
        LocalDate fechaActual = LocalDate.now();
        calcularTotalCompras(fechaActual);

        totalGeneralCaja = efectivo + tarjeta;

        // Redondear las variables a dos decimales usando BigDecimal
        BigDecimal roundedTotalCaja = BigDecimal.valueOf(totalGeneralCaja).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedSaldoInicial = BigDecimal.valueOf(saldoInicial).setScale(2, RoundingMode.HALF_UP);

        //arriba derecha
        txtTotalCaja.setText(roundedTotalCaja.toString());
        txtSaldoInicial.setText(roundedSaldoInicial.toString());
        btnCaja.setEnabled(false);
        btnResumen.setEnabled(true);
    }

    public void InsertarArqueo() {
        double salInicial = Double.parseDouble(txtSaldoI.getText());
        double totCaja = Double.parseDouble(txtCajaT.getText());
        double IngEfectivo = Double.parseDouble(jlbEfectivo.getText());
        double IngTarjeta = Double.parseDouble(jlbTarjeta.getText());
        double gastosCaja = Double.parseDouble(jlbGastosCaja.getText());
        double totVenta = Double.parseDouble(txtTotalVenta.getText());
        double difer = Double.parseDouble(txtDiferencia.getText());
        // Redondear todas las variables a dos decimales usando BigDecimal
        BigDecimal roundedSalInicial = BigDecimal.valueOf(salInicial).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedTotCaja = BigDecimal.valueOf(totCaja).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedIngEfectivo = BigDecimal.valueOf(IngEfectivo).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedIngTarjeta = BigDecimal.valueOf(IngTarjeta).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedGastosCaja = BigDecimal.valueOf(gastosCaja).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedTotVenta = BigDecimal.valueOf(totVenta).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedDifer = BigDecimal.valueOf(difer).setScale(2, RoundingMode.HALF_UP);

        try {
            String sentenciaSQL = "INSERT INTO arqueocaja (id,fecha,saldoInicial,totalCaja,IngresosEfectivo,IngresosTarjeta,Gastos,"
                    + "TotalVenta,Diferencia)VALUES(?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            LocalDate fechaActual = LocalDate.now();
            Date fechaSQL = Date.valueOf(fechaActual);
            ps.setDate(2, fechaSQL);
            ps.setDouble(3, roundedSalInicial.doubleValue());
            ps.setDouble(4, roundedTotCaja.doubleValue());
            ps.setDouble(5, roundedIngEfectivo.doubleValue());
            ps.setDouble(6, roundedIngTarjeta.doubleValue());
            ps.setDouble(7, roundedGastosCaja.doubleValue());
            ps.setDouble(8, roundedTotVenta.doubleValue());
            ps.setDouble(9, roundedDifer.doubleValue());
            ps.execute();

            //salida PARA CONFIRMAR DATOS INGRESADOS CORRECTAMENTE
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORREXTAMENTE");
            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(clientes.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
        }
    }

    public void diferencia() {
        // Redondear la variable diferencia a dos decimales usando BigDecimal
        BigDecimal roundedDiferencia = BigDecimal.valueOf(diferencia).setScale(2, RoundingMode.HALF_UP);

        txtDiferencia.setText(roundedDiferencia.toString());

        if (roundedDiferencia.compareTo(BigDecimal.ZERO) < 0) {
            jlbInformacion.setText("PRESENTA UNA DIFERENCIA QUE TIENE QUE PAGAR");
            txtDiferencia.setForeground(Color.red);
        } else if (roundedDiferencia.compareTo(BigDecimal.ZERO) == 0) {
            jlbInformacion.setText("NO PRESENTA DIFERENCIA");
            txtDiferencia.setForeground(Color.black);
        } else {
            jlbInformacion.setText("PRESENTA UNA DIFERENCIA A SU FAVOR");
            txtDiferencia.setForeground(Color.green);
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

        jPanel2 = new javax.swing.JPanel();
        jlbInformacion = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txt5Cen = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txt10Cen = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txt20Cen = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txt50Cen = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt500 = new javax.swing.JTextField();
        txt10 = new javax.swing.JTextField();
        txt5 = new javax.swing.JTextField();
        txt2 = new javax.swing.JTextField();
        txt1 = new javax.swing.JTextField();
        txt20 = new javax.swing.JTextField();
        txt200 = new javax.swing.JTextField();
        txt50 = new javax.swing.JTextField();
        txt100 = new javax.swing.JTextField();
        btnRecuento = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtSaldoInicial = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCaja = new javax.swing.JTextField();
        btnResumen = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtTotalEfectivo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTarjeta = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtGastos = new javax.swing.JTextField();
        btnCaja = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtTotalVenta = new javax.swing.JTextField();
        jlbEfectivo = new javax.swing.JLabel();
        jlbTarjeta = new javax.swing.JLabel();
        jlbGastosCaja = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtCajaT = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtSaldoI = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtDiferencia = new javax.swing.JTextField();
        btnCierre = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnAyuda1 = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(241, 222, 201));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlbInformacion.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jlbInformacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbInformacion.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(jlbInformacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 530, 1020, 43));

        jPanel3.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "FORMA DE PAGO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(54, 21, 0))); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt5Cen.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt5Cen, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, 93, -1));

        jLabel24.setText("5 C.");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 220, 30, 20));

        txt10Cen.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt10Cen, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 190, 93, -1));

        jLabel28.setText("10 C.");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 190, -1, -1));

        txt20Cen.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt20Cen, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, 93, -1));

        jLabel29.setText("20 C.");
        jPanel3.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, -1, -1));

        jLabel30.setText("50 C.");
        jPanel3.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, -1, 20));

        txt50Cen.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt50Cen, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, 93, -1));

        jLabel3.setText("500 L.");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 35, -1, -1));

        jLabel4.setText("200 L.");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 70, -1, -1));

        jLabel5.setText("100 L.");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 105, -1, -1));

        jLabel6.setText("50 L.");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 140, -1, -1));

        jLabel7.setText("20 L.");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 175, -1, -1));

        jLabel8.setText("10 L.");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 210, -1, -1));

        jLabel9.setText("5 L.");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 245, -1, -1));

        jLabel10.setText("2 L.");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 280, -1, -1));

        jLabel11.setText("1 L.");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 315, -1, -1));

        txt500.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt500, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 32, 93, -1));

        txt10.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt10, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 207, 93, -1));

        txt5.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt5, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 242, 93, -1));

        txt2.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt2, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 277, 93, -1));

        txt1.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 312, 93, -1));

        txt20.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt20, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 172, 93, -1));

        txt200.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt200, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 67, 93, -1));

        txt50.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt50, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 137, 93, -1));

        txt100.setBackground(new java.awt.Color(255, 251, 233));
        jPanel3.add(txt100, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 102, 93, -1));

        btnRecuento.setBackground(new java.awt.Color(54, 21, 0));
        btnRecuento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnRecuento.setForeground(new java.awt.Color(227, 202, 165));
        btnRecuento.setText("RECUENTO");
        btnRecuento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRecuentoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRecuentoMouseExited(evt);
            }
        });
        btnRecuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecuentoActionPerformed(evt);
            }
        });
        jPanel3.add(btnRecuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 390, 170, -1));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 40, 380, 470));

        jPanel4.setBackground(new java.awt.Color(255, 251, 233));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "FORMA DE PAGO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(54, 21, 0))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setText("SALDO INICIAL L.");
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 105, -1));

        txtSaldoInicial.setBackground(new java.awt.Color(255, 251, 233));
        txtSaldoInicial.setEnabled(false);
        jPanel4.add(txtSaldoInicial, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, 120, -1));

        jLabel13.setText("TOTAL EN CAJA L.");
        jPanel4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, -1, -1));

        txtTotalCaja.setBackground(new java.awt.Color(255, 251, 233));
        txtTotalCaja.setEnabled(false);
        txtTotalCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalCajaActionPerformed(evt);
            }
        });
        jPanel4.add(txtTotalCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, 120, -1));

        btnResumen.setBackground(new java.awt.Color(54, 21, 0));
        btnResumen.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnResumen.setForeground(new java.awt.Color(227, 202, 165));
        btnResumen.setText("EJECUTAR RESUMEN CAJA");
        btnResumen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnResumenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnResumenMouseExited(evt);
            }
        });
        btnResumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResumenActionPerformed(evt);
            }
        });
        jPanel4.add(btnResumen, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 330, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 370, 190));

        jPanel5.setBackground(new java.awt.Color(255, 251, 233));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "FORMA DE PAGO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(54, 21, 0))); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setText("EFECTIVO   L.");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 35, -1, -1));

        txtTotalEfectivo.setBackground(new java.awt.Color(255, 251, 233));
        txtTotalEfectivo.setEnabled(false);
        jPanel5.add(txtTotalEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 32, 97, -1));

        jLabel15.setText("TARJETA    L.");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 70, 80, -1));

        txtTarjeta.setBackground(new java.awt.Color(255, 251, 233));
        jPanel5.add(txtTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 67, 97, -1));

        jLabel16.setText("GASTOS    L.");
        jPanel5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 105, 80, -1));

        txtGastos.setBackground(new java.awt.Color(255, 251, 233));
        jPanel5.add(txtGastos, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 102, 97, -1));

        btnCaja.setBackground(new java.awt.Color(54, 21, 0));
        btnCaja.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCaja.setForeground(new java.awt.Color(227, 202, 165));
        btnCaja.setText("INGRESOS Y GASTOS");
        btnCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCajaActionPerformed(evt);
            }
        });
        jPanel5.add(btnCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 230, 190));

        jPanel6.setBackground(new java.awt.Color(255, 251, 233));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("RESUMEN DE CAJA");
        jLabel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 587, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setText("TOTAL VENTA     L.");
        jPanel6.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(192, 60, -1, -1));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("TARJETA            L.");
        jPanel6.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(192, 111, 129, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setText("EFECTIVO           L.");
        jPanel6.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(192, 87, -1, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setText("GASTOS DE CAJA L.");
        jPanel6.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 135, -1, 20));

        txtTotalVenta.setBackground(new java.awt.Color(255, 251, 233));
        txtTotalVenta.setForeground(new java.awt.Color(102, 0, 204));
        txtTotalVenta.setEnabled(false);
        jPanel6.add(txtTotalVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 58, 70, -1));

        jlbEfectivo.setBackground(new java.awt.Color(255, 251, 233));
        jlbEfectivo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel6.add(jlbEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 88, 70, 20));

        jlbTarjeta.setBackground(new java.awt.Color(255, 251, 233));
        jlbTarjeta.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel6.add(jlbTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 111, 70, 20));

        jlbGastosCaja.setBackground(new java.awt.Color(255, 251, 233));
        jlbGastosCaja.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel6.add(jlbGastosCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 136, 70, 20));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setText("TOTAL EN CAJA L.");
        jPanel6.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 172, -1, 20));

        txtCajaT.setBackground(new java.awt.Color(255, 251, 233));
        txtCajaT.setForeground(new java.awt.Color(102, 0, 204));
        txtCajaT.setEnabled(false);
        jPanel6.add(txtCajaT, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 170, 136, 30));

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel26.setText("SALDO INICIAL L.");
        jPanel6.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 172, -1, 30));

        txtSaldoI.setBackground(new java.awt.Color(255, 251, 233));
        txtSaldoI.setForeground(new java.awt.Color(102, 0, 204));
        txtSaldoI.setEnabled(false);
        jPanel6.add(txtSaldoI, new org.netbeans.lib.awtextra.AbsoluteConstraints(432, 170, 137, 30));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel27.setText("DIFERENCIA L.");
        jPanel6.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 226, -1, 30));

        txtDiferencia.setEditable(false);
        txtDiferencia.setBackground(new java.awt.Color(255, 251, 233));
        txtDiferencia.setForeground(new java.awt.Color(255, 0, 51));
        jPanel6.add(txtDiferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 229, 86, 30));

        btnCierre.setBackground(new java.awt.Color(54, 21, 0));
        btnCierre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCierre.setForeground(new java.awt.Color(227, 202, 165));
        btnCierre.setText("CIERRE DE CAJA");
        btnCierre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCierreActionPerformed(evt);
            }
        });
        jPanel6.add(btnCierre, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 230, -1, 30));

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 240, 610, 270));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 280, 1040, 590));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel22.setText("USUARIO:");
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 230, 30));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel23.setText("ARQUEO DE CAJA");
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 130, -1, -1));

        btnAyuda1.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda1.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 0, 50, 50));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 0, 50, 50));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 890));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtTotalCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalCajaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalCajaActionPerformed

    private void btnRecuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecuentoActionPerformed
        recuentoEfectivo4();

        // Establecer el valor en el campo txtTotalEfectivo
        /*txtTotalEfectivo.setBackground(Color.white);*/
        txtTotalEfectivo.setText(String.valueOf(acumuEfectivo));

        // Habilitar el botón "Caja"
        btnCaja.setEnabled(true);
    }//GEN-LAST:event_btnRecuentoActionPerformed

    private void btnResumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumenActionPerformed
        if (validacion()) {
            txtTotalVenta.setText("" + BigDecimal.valueOf(totalC).setScale(2, RoundingMode.HALF_UP)); // totalC es el que recibe como parámetro
            jlbEfectivo.setText("" + BigDecimal.valueOf(efectivo).setScale(2, RoundingMode.HALF_UP));
            jlbTarjeta.setText("" + BigDecimal.valueOf(tarjeta).setScale(2, RoundingMode.HALF_UP));
            jlbGastosCaja.setText("" + BigDecimal.valueOf(gastos).setScale(2, RoundingMode.HALF_UP));
            txtCajaT.setText("" + BigDecimal.valueOf(totalGeneralCaja).setScale(2, RoundingMode.HALF_UP));
            txtSaldoI.setText("" + BigDecimal.valueOf(saldoInicial).setScale(2, RoundingMode.HALF_UP));
            diferencia();
            btnResumen.setEnabled(false);
            btnCierre.setEnabled(true);
        } else {
            validar = true; // Establece en true si efectivo está vacío, para un segundo intento
        }
    }//GEN-LAST:event_btnResumenActionPerformed

    private void btnCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajaActionPerformed
        caja();
    }//GEN-LAST:event_btnCajaActionPerformed

    private void btnRecuentoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRecuentoMouseEntered
        jlbInformacion.setText("PRESIONE PARA REALIZAR RECUENTO EFECTIVO");
    }//GEN-LAST:event_btnRecuentoMouseEntered

    private void btnRecuentoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRecuentoMouseExited
        jlbInformacion.setText(null);
    }//GEN-LAST:event_btnRecuentoMouseExited

    private void btnResumenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResumenMouseEntered
        jlbInformacion.setText("PRESIONE PARA REALIZAR RESUMEN CAJA");
    }//GEN-LAST:event_btnResumenMouseEntered

    private void btnResumenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResumenMouseExited
        jlbInformacion.setText(null);
    }//GEN-LAST:event_btnResumenMouseExited

    private void btnCierreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCierreActionPerformed
        InsertarArqueo();
    }//GEN-LAST:event_btnCierreActionPerformed

    private void btnAyuda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda1ActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame arqueo = null;
        AyudaArqueoCaja dialog2 = new AyudaArqueoCaja(arqueo, true);
        dialog2.setVisible(true);
    }//GEN-LAST:event_btnAyuda1ActionPerformed

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
            java.util.logging.Logger.getLogger(arqueo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(arqueo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(arqueo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(arqueo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                arqueo dialog = new arqueo(new javax.swing.JFrame(), true, 0);
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
    private javax.swing.JButton btnCaja;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCierre;
    private javax.swing.JButton btnRecuento;
    private javax.swing.JButton btnResumen;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JLabel jlbEfectivo;
    private javax.swing.JLabel jlbGastosCaja;
    private javax.swing.JLabel jlbInformacion;
    private javax.swing.JLabel jlbTarjeta;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt10;
    private javax.swing.JTextField txt100;
    private javax.swing.JTextField txt10Cen;
    private javax.swing.JTextField txt2;
    private javax.swing.JTextField txt20;
    private javax.swing.JTextField txt200;
    private javax.swing.JTextField txt20Cen;
    private javax.swing.JTextField txt5;
    private javax.swing.JTextField txt50;
    private javax.swing.JTextField txt500;
    private javax.swing.JTextField txt50Cen;
    private javax.swing.JTextField txt5Cen;
    private javax.swing.JTextField txtCajaT;
    private javax.swing.JTextField txtDiferencia;
    private javax.swing.JTextField txtGastos;
    private javax.swing.JTextField txtSaldoI;
    private javax.swing.JTextField txtSaldoInicial;
    private javax.swing.JTextField txtTarjeta;
    private javax.swing.JTextField txtTotalCaja;
    private javax.swing.JTextField txtTotalEfectivo;
    private javax.swing.JTextField txtTotalVenta;
    // End of variables declaration//GEN-END:variables
}
