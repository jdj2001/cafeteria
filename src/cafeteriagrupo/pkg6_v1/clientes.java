/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaClientes;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan Alvarado
 */
public class clientes extends javax.swing.JDialog {

    String usuario, sentenciaSQL, codCliente, nombreCliente;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon imagen;
    Icon icono;

    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosClientes[] = new Object[5];
    private static String correoUsu;
    String direccionFoto;
    FileInputStream fis; //PARA AGREGAR FOTOGRAFIA
    int longitudBytes;  //FOTOGRAFIA

    /**
     * Creates new form clientes
     */
    public clientes(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        this.setLocationRelativeTo(null);
        this.ConectarBD();
        btnGuardar.setEnabled(false);
        this.correoUsu = user;
        jblUsuario.setText(correoUsu);
        txtCodigo.setText("0");//ES AUTO INCREMENTABLE, SE PONE 0 Y SE MANDA PORQUE NO IMPORTA CUAL SE MANDE, PORQUE ES AUTO
        // txtNombre.requestFocus();//18:07 29/06
        //IMAGEN PERFIL
        //icono = new ImageIcon("src/imagenes/sinPerfil.jpg");
        //jblFoto.setIcon(icono);
        cambioImagen("dibujos.png", fondo);
        cambioImagen("sinPerfil.jpg", jblFoto);
        cambioImagen("logoSinFondo.png", jblLogo);
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        //icono = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
    }

    public void ConectarBD() {
        conecta = new accesoDatosObjetos.Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtRTN.setText("");
        txtNombre.requestFocus();//
        cambioImagen("sinPerfil.jpg", jblFoto);//aqui limpia la foto con sin perfil

        int fila = tblDatos.getRowCount();//TRAYENDO X CANTIDAD DE REGISTROS. CUANTAS FILAS TENEMOS EN JTABLE
        //CICLO INVERSO. EMPIEZA EN EL MAYOR HASTA MENOR. AGARRA DE ABAJO HASTA ARRIBA. VA IR REMOVIENDOLAS
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);//FILA X HASTA LLEGAR A CERO
        }
        btnGuardar.setVisible(true);
    }

    public void crearClientes() {
        if (txtNombre.getText().isEmpty() || txtRTN.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar.");
            return;
        }
        //SE DEBE METER EN UN BLOQUE TRY-CATCH PARA LLEGAR A ESTO:
        try {
            sentenciaSQL = "INSERT INTO clientes (id,nombreCliente,estado,foto,RTN)VALUES(?,?,?,?,?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setString(2, txtNombre.getText());
            ps.setString(3, "Activo");
            ps.setBinaryStream(4, fis, longitudBytes);//EN ESTE CASO SE ALMACENA LA DIRECCION DE LA FOTO
            ps.setString(5, txtRTN.getText());
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

    public void leerClientes() {
        //this.limpiar();
        sentenciaSQL = "SELECT id,nombreCliente,foto,RTN FROM clientes WHERE estado LIKE 'Activo'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblDatos.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                datosClientes[0] = (rs.getString(1));//POSICION CERO VA IR LA COLUMNA NUMERO 1. PRIMER DATO COMO ENTERO PERO COMO EL ARREGLO ES DE TIPO OBJETO, SE PUEDE UTILIZAR GET INT/STRING
                datosClientes[1] = (rs.getString(2));//TIPO DE DATO Y NUMERO DE COLUMNA

                byte[] imagenBytes = rs.getBytes(3);
                ImageIcon imagen = new ImageIcon(imagenBytes);
                Image img = imagen.getImage();
                Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
                ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);
                datosClientes[2] = imagenEscalada;

                datosClientes[3] = (rs.getString(4));
                modelo.addRow(datosClientes);//AGREGARLE LAS FILAS AL MODELO    

            }
            //AGREGAR MODELO A LA TABLA
            tblDatos.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    // NO SE MODIFICA EL CODIGO, SINO EL PARAMETRO. TAMBIÉN DEBE LLEVAR ID DE REGISTRO, NO SOLO IDENTIDAD
    public void actualizarClientes() {
        if (txtNombre.getText().isEmpty() || txtRTN.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar.");
            return;
        }
        try {
            //PARAMETROS QUE VAMOS A TRAER-->? DE LAS CAJAS DE TEXTO
            if (fis != null && longitudBytes > 0) {
                sentenciaSQL = "UPDATE clientes SET nombreCliente=?,RTN=?,foto=? WHERE id=?";//EL ESTADO AL MOMENTO DE HACER DELETE SE MODIFICA. SE PUEDE, PERO NO AHORA
                ps = con.prepareStatement(sentenciaSQL);
                //EL NUMERO SIGNIFICA EL PRIMER PARÁMETRO, NO DE COLUMNA. DE AQUI ES DONDE OBTIENE DE LA CAJA DE TEXTO Y LO MANDA AL PARAMETRO
                ps.setString(1, txtNombre.getText());
                ps.setString(2, txtRTN.getText());
                ps.setBinaryStream(3, fis, longitudBytes);
                ps.setString(4, txtCodigo.getText());//PARAMETRO 4 ES id=?

                ps.execute();
            } else {
                return;
                /*sentenciaSQL = "UPDATE clientes SET nombreCliente=?,RTN=? WHERE id=?";//EL ESTADO AL MOMENTO DE HACER DELETE SE MODIFICA. SE PUEDE, PERO NO AHORA
                ps = con.prepareStatement(sentenciaSQL);
                //EL NUMERO SIGNIFICA EL PRIMER PARÁMETRO, NO DE COLUMNA. DE AQUI ES DONDE OBTIENE DE LA CAJA DE TEXTO Y LO MANDA AL PARAMETRO
                ps.setString(1, txtNombre.getText());
                ps.setString(2, txtRTN.getText());
                ps.setString(3, txtCodigo.getText());//PARAMETRO 4 ES id=?*/
            }
            //salida PARA CONFIRMAR DATOS INGRESADOS CORRECTAMENTE
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(clientes.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR DATOS" + ex.getMessage());
        }
    }

    //METODO PARA ELIMINAR CLIENTES
    public void eliminarClientes() {
        if (txtNombre.getText().isEmpty() || txtRTN.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar.");
            return;
        }
        try {
            //ConectarBD();
            //sentenciaSQL="DELETE FROM clientes WHERE cod=" + jtfCodigo.getText().trim();//ELIMINAR COMPLETAMENTE SIN DEJAR RASTRO (NO RECOMENDABLE PARA REGSTROS DE TRANSACCIONES
            //TRAYENDO TEXTFIELD. QITA ESPACIOS INICIALES Y FINALES CON .TRIM. ESTO ACTUALIZA Y COLOCA EL ESTADO COMO INACTIVO DONDE EL ID SEA IGUAL AL TXT
            sentenciaSQL = "UPDATE clientes SET estado='Inactivo' WHERE id=" + txtCodigo.getText().trim();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }
    //METODO PARA ELIMINAR CLIENTES

    //LLAMAR EN EL BOTON FOTO
    public void photo() {
        jblFoto.setIcon(null);
        JFileChooser j = new JFileChooser();
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);//solo archivos y no carpetas
        int estado = j.showOpenDialog(null);
        if (estado == JFileChooser.APPROVE_OPTION) {
            try {
                fis = new FileInputStream(j.getSelectedFile());
                //necesitamos saber la cantidad de bytes
                this.longitudBytes = (int) j.getSelectedFile().length();
                try {
                    Image icono = ImageIO.read(j.getSelectedFile()).getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
                    jblFoto.setIcon(new ImageIcon(icono));
                    jblFoto.updateUI();
                    this.btnFoto.setEnabled(true);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(rootPane, "imagen: " + ex);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ImageIcon tamanioImagen(String dirImagen) {
        ImageIcon fotoCliente = new ImageIcon(dirImagen);//RECIBE LA DIRECCION RUTA IMAGEN
        Image img = fotoCliente.getImage();//OBTENER LA IMAGEN QUE TIENE FOTO CLIENTE, EN LA RUTA
        Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);//AJUSTANDO LA ESCALA, LA ESCALA ES POR DEFECTO. AGARRA EL ANCHO Y ALTO DE LA ETIQUETA
        ImageIcon imagen = new ImageIcon(nuevaImagen); //TOMANDO LA IMAGEN CON EL TAMAÑO NUEVO
        return imagen;
    }

    public void validarCampos() {
        String nombre = txtNombre.getText();
        String rtn = txtRTN.getText();

        if (!nombre.equals("") && !rtn.equals("") || (!nombre.equals("") && rtn.equals(""))) {
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
        } else if (!txtRTN.getText().isEmpty()) {
            criterio = txtRTN.getText();
            buscarPorNombre = true;
        }

        if (buscarPorCodigo || buscarPorNombre) {
            try {
                String query = "SELECT id,nombreCliente,foto,RTN FROM clientes WHERE ";
                if (buscarPorCodigo) {
                    query += "id = ?";
                } else if (buscarPorNombre) {
                    query += "nombreCliente = ?";
                }
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, criterio);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Si se encuentra el cliente, cargar sus datos en los campos correspondientes

                    txtCodigo.setText(rs.getString("id"));
                    txtNombre.setText(rs.getString("nombreCliente"));
                    txtRTN.setText(rs.getString("RTN"));

                    modelo = (DefaultTableModel) tblDatos.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
                    modelo.setRowCount(0);
                    datosClientes[0] = rs.getString(1);
                    datosClientes[1] = rs.getString(2);
                    byte[] imagenBytes = rs.getBytes(3);
                    ImageIcon imagen = new ImageIcon(imagenBytes);
                    Image img = imagen.getImage();
                    Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
                    jblFoto.setIcon(new ImageIcon(nuevaImagen));
                    jblFoto.updateUI();
                    ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);
                    datosClientes[2] = imagenEscalada;
                    datosClientes[3] = rs.getString(4);

                    modelo.addRow(datosClientes);

                    //tblDatos.setModel(modelo);
                    //this.leerClientes();
                } else {
                    JOptionPane.showMessageDialog(null, "NO EXISTE TAL SERVICIO");
                }
                tblDatos.setModel(modelo);
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

        btnCerrar = new javax.swing.JButton();
        txtCodigo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        btnLeer = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtRTN = new javax.swing.JFormattedTextField();
        jblLogo = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jblFoto = new javax.swing.JLabel();
        btnFoto = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDatos = new javax.swing.JTable();
        btnAyuda = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnBuscar1 = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 0, 50, 50));

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
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 770, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 770, 270, 30));

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
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 370, 150, 40));

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
        getContentPane().add(btnLeer, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 290, 160, 40));

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
        getContentPane().add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 370, 160, 40));

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
        getContentPane().add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 290, 150, 40));

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
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 440, 320, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Nombre");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 100, 30));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("RTN");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, 80, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("CLIENTES");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 120, -1, -1));

        txtRTN.setBackground(new java.awt.Color(241, 222, 201));
        try {
            txtRTN.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##############")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtRTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRTNActionPerformed(evt);
            }
        });
        getContentPane().add(txtRTN, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 432, 250, 30));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));

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

        jblFoto.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jblFoto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jblFotoKeyReleased(evt);
            }
        });
        getContentPane().add(jblFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 290, 190, 190));

        btnFoto.setBackground(new java.awt.Color(54, 21, 0));
        btnFoto.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnFoto.setForeground(new java.awt.Color(227, 202, 165));
        btnFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/foto.png"))); // NOI18N
        btnFoto.setText("Foto");
        btnFoto.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFotoActionPerformed(evt);
            }
        });
        getContentPane().add(btnFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 490, 120, 40));

        tblDatos.setBackground(new java.awt.Color(227, 202, 165));
        tblDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "NOMBRE", "FOTO", "RTN"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDatos.setColumnSelectionAllowed(true);
        tblDatos.getTableHeader().setReorderingAllowed(false);
        tblDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDatosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDatos);
        tblDatos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 550, 1110, 200));

        btnAyuda.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 0, 50, 50));

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
        getContentPane().add(btnBuscar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 370, 40, 40));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 810));

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        eliminarClientes();
        limpiar();
        leerClientes();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        limpiar();
        leerClientes();
    }//GEN-LAST:event_btnLeerActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarClientes();
        limpiar();
        leerClientes();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.crearClientes();
        limpiar();
        this.leerClientes();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        this.limpiar();
        this.leerClientes();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtNombreKeyReleased

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreKeyTyped

    private void jblFotoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jblFotoKeyReleased
        validarCampos();
    }//GEN-LAST:event_jblFotoKeyReleased

    private void btnFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFotoActionPerformed
        photo();
    }//GEN-LAST:event_btnFotoActionPerformed

    private void tblDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDatosMouseClicked
        //CLIC EN LAS FILAS DE LA TABLA
        int fila = tblDatos.getSelectedRow();//QUÉ FILA SELECCIONO, DIO CLIC
        txtCodigo.setText(tblDatos.getValueAt(fila, 0).toString());//CERO ES DONDE ESTARA EL CODIGO.
        txtNombre.setText(tblDatos.getValueAt(fila, 1).toString());
        ImageIcon imagen = (ImageIcon) tblDatos.getValueAt(fila, 2);
        Image img = imagen.getImage();
        Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
        ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);
        jblFoto.setIcon(imagenEscalada);
        txtRTN.setText(tblDatos.getValueAt(fila, 3).toString());
        btnGuardar.setVisible(false);
    }//GEN-LAST:event_tblDatosMouseClicked

    private void txtRTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRTNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRTNActionPerformed

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame clientes = null;
        AyudaClientes dialog2 = new AyudaClientes(clientes, true);
        dialog2.setVisible(true);

    }//GEN-LAST:event_btnAyudaActionPerformed

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
            java.util.logging.Logger.getLogger(clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                clientes dialog = new clientes(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscar1;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnFoto;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLeer;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblFoto;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JTable tblDatos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JFormattedTextField txtRTN;
    // End of variables declaration//GEN-END:variables
}
