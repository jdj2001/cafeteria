/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeteriagrupo.pkg6_v1;

import accesoDatosObjetos.Conexion;
import ayuda.AyudaProveedores;

import java.awt.Color;
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
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.util.regex.Pattern;

import java.sql.Date;
import java.time.LocalDate;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 *
 * @author Juan Alvarado
 */
public class Proveedores extends javax.swing.JDialog {

    FileInputStream fis; //PARA AGREGAR FOTOGRAFIA
    int longitudBytes;  //FOTOGRAFIA
    String codCategoria, codProveedor, usuario, sentenciaSQL, codCliente, nombreCliente;
    Double costo;
    Double utilidad;
    Double precio;
    int stockMin = 0, stockMax = 0;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon icono;
    ImageIcon imagen;
    private static String correoUsu;
    ResultSet rs = null; //PERMITE TENER METODOS PARA OBTENER INFO DE LAS DIFERENTES COLUMNAS DE UNA FILA=TIPO RESULT SET VARIABE
    DefaultTableModel modelo; //PARA EL MÓDELO DE NUESTRA TABLA
    Object datosProveedor[] = new Object[5];
    String direccionFoto;

    /**
     * Creates new form Proveedores
     */
    public Proveedores(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        ConectarBD();
        this.setLocationRelativeTo(null);
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

    public void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        jftTelefono.setText("");
        cambioImagen("sinPerfil.jpg", jblFoto);
        txtCiudad.setText("");
        txtNombre.requestFocus();//

        int fila = tblProveedores.getRowCount();//TRAYENDO X CANTIDAD DE REGISTROS. CUANTAS FILAS TENEMOS EN JTABLE
        //CICLO INVERSO. EMPIEZA EN EL MAYOR HASTA MENOR. AGARRA DE ABAJO HASTA ARRIBA. VA IR REMOVIENDOLAS
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);//FILA X HASTA LLEGAR A CERO
        }
        btnGuardar.setVisible(true);
    }

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

    public void crearProveedor() {
        if (txtNombre.getText().isEmpty() || txtCiudad.getText().isEmpty() || jblFoto.getIcon().toString().equals("sinPerfil.jpg")
                ||  jftTelefono.getText().length() < 8) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de crear el proveedor");
            return;
        }
        try {
            //ConectarBD();//METODO ANTERIOR
            sentenciaSQL = "INSERT INTO proveedor (id, descripcion,ciudad,telefono, foto, estado) VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setString(2, txtNombre.getText());
            ps.setString(3, txtCiudad.getText());
            ps.setString(4, jftTelefono.getText());
            ps.setBinaryStream(5, fis, longitudBytes);
            ps.setString(6, "Activo");
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORREXTAMENTE");
            // ... Rest of the code remains unchanged ...
        } catch (SQLException ex) {
            Logger.getLogger(NuevoProducto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
        }
    }

    public void leerProveedor() {
        //this.limpiar();
        sentenciaSQL = "SELECT id,descripcion,ciudad,telefono,foto FROM proveedor WHERE estado LIKE 'Activo'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblProveedores.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                datosProveedor[0] = (rs.getString(1));
                datosProveedor[1] = (rs.getString(2));
                datosProveedor[2] = (rs.getString(3));
                datosProveedor[3] = (rs.getString(4));
                byte[] imagenBytes = rs.getBytes(5);
                ImageIcon imagen = new ImageIcon(imagenBytes);
                Image img = imagen.getImage();
                Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
                ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);
                datosProveedor[4] = imagenEscalada;
                modelo.addRow(datosProveedor);//AGREGARLE LAS FILAS AL MODELO    
            }
            tblProveedores.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }

    // NO SE MODIFICA EL CODIGO, SINO EL PARAMETRO. TAMBIÉN DEBE LLEVAR ID DE REGISTRO, NO SOLO IDENTIDAD
    public void actualizarProveedor() {
        if (txtNombre.getText().isEmpty() || txtCiudad.getText().isEmpty() || jblFoto.getIcon().toString().equals("sinPerfil.jpg")
                ||  jftTelefono.getText().length() < 8) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de actualizar el proveedor");
            return;
        }
        try {
            sentenciaSQL = "UPDATE proveedor SET descripcion=?,ciudad=?,telefono=?,foto=? WHERE id=?";
            ps = con.prepareStatement(sentenciaSQL);
            //EL NUMERO SIGNIFICA EL PRIMER PARÁMETRO, NO DE COLUMNA. DE AQUI ES DONDE OBTIENE DE LA CAJA DE TEXTO Y LO MANDA AL PARAMETRO
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtCiudad.getText());
            ps.setString(3, jftTelefono.getText());
            ps.setBinaryStream(4, fis, longitudBytes);
            ps.setString(5, txtCodigo.getText());
            // Obtener la fecha actual
            /*LocalDate fechaActual = LocalDate.now();
            Date fechaSQL = Date.valueOf(fechaActual);
            ps.setDate(10, fechaSQL);*/
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Proveedores.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR DATOS" + ex.getMessage());
        }
    }

    //METODO PARA ELIMINAR CLIENTES
    public void eliminarProveedor() {
        if (txtNombre.getText().isEmpty() || txtCiudad.getText().isEmpty() || jblFoto.getIcon().toString().equals("sinPerfil.jpg")
                ||  jftTelefono.getText().length() < 8) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de eliminar el proveedor");
            return;
        }
        try {
            //ConectarBD();
            //sentenciaSQL="DELETE FROM clientes WHERE cod=" + jtfCodigo.getText().trim();//ELIMINAR COMPLETAMENTE SIN DEJAR RASTRO (NO RECOMENDABLE PARA REGSTROS DE TRANSACCIONES
            //TRAYENDO TEXTFIELD. QITA ESPACIOS INICIALES Y FINALES CON .TRIM. ESTO ACTUALIZA Y COLOCA EL ESTADO COMO INACTIVO DONDE EL ID SEA IGUAL AL TXT
            sentenciaSQL = "UPDATE proveedor SET estado='Inactivo' WHERE id=" + txtCodigo.getText().trim();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void validarCampos() {
        String nombre = txtNombre.getText();
        String ciudad = txtCiudad.getText();
        String telefono = jftTelefono.getText();
        Icon foto = jblFoto.getIcon(); // Get the icon from the JLabel
        String telefonoValido = telefono.replaceAll("[^0-9]", "");

        if (!nombre.equals("") && !ciudad.equals("") && telefono.length() == 14) {
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
        } else if (!txtNombre.getText().isEmpty()) {
            criterio = txtNombre.getText();
            buscarPorNombre = true;
        }

        if (buscarPorCodigo || buscarPorNombre) {
            try {
                String query = "SELECT * FROM proveedor WHERE ";
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
                    txtCiudad.setText(rs.getString("ciudad"));
                    jftTelefono.setText(rs.getString("telefono"));
                    txtNombre.setText(rs.getString("descripcion"));

                    modelo = (DefaultTableModel) tblProveedores.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
                    modelo.setRowCount(0);
                    //Object[] datosClientes = new Object[4];
                    datosProveedor[0] = rs.getString(1);
                    datosProveedor[1] = rs.getString(2);
                    datosProveedor[2] = rs.getString(3);
                    datosProveedor[3] = rs.getString(4);
                    byte[] imagenBytes = rs.getBytes(5);
                    ImageIcon imagen = new ImageIcon(imagenBytes);
                    Image img = imagen.getImage();
                    Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
                    jblFoto.setIcon(new ImageIcon(nuevaImagen));
                    jblFoto.updateUI();
                    ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);

                    modelo.addRow(datosProveedor);
                    btnGuardar.setVisible(false);
                    //tblProveedores.setModel(modelo);
                    //this.leerClientes();
                } else {
                    JOptionPane.showMessageDialog(null, "NO EXISTE ESTE PROVEEDOR");
                }
                tblProveedores.setModel(modelo);
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

        txtCodigo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jblUsuario = new javax.swing.JLabel();
        btnFoto = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProveedores = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        btnLeer = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtCiudad = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jftTelefono = new javax.swing.JFormattedTextField();
        jblFoto = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        jblLogo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnAyuda1 = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnBuscar1 = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        getContentPane().add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 210, 250, 30));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setText("Código");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 210, 80, 30));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setText("USUARIO:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 880, 90, 30));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jblUsuario.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 880, 240, 30));

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
        getContentPane().add(btnFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 460, 120, 40));

        tblProveedores.setBackground(new java.awt.Color(227, 202, 165));
        tblProveedores.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(54, 21, 0), 2, true));
        tblProveedores.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tblProveedores.setForeground(new java.awt.Color(54, 21, 0));
        tblProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CÓDIGO", "DESCRIPCION", "CIUDAD", "TELÉFONO", "FOTO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProveedores.setColumnSelectionAllowed(true);
        tblProveedores.setGridColor(new java.awt.Color(241, 222, 201));
        tblProveedores.setSelectionForeground(new java.awt.Color(241, 222, 201));
        tblProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProveedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProveedores);
        tblProveedores.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tblProveedores.getColumnModel().getColumnCount() > 0) {
            tblProveedores.getColumnModel().getColumn(1).setResizable(false);
            tblProveedores.getColumnModel().getColumn(2).setResizable(false);
            tblProveedores.getColumnModel().getColumn(3).setResizable(false);
            tblProveedores.getColumnModel().getColumn(4).setResizable(false);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 600, 760, 290));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel13.setText("Teléfono");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 390, 80, 30));

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
        getContentPane().add(btnLeer, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 530, 160, 40));

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
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 530, 150, 40));

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
        getContentPane().add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 480, 150, 40));

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
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 480, 140, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Descripción");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 100, 30));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("Ciudad");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 330, 80, 30));

        txtCiudad.setBackground(new java.awt.Color(241, 222, 201));
        txtCiudad.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtCiudad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCiudadActionPerformed(evt);
            }
        });
        txtCiudad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCiudadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCiudadKeyTyped(evt);
            }
        });
        getContentPane().add(txtCiudad, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 330, 250, 30));

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
        getContentPane().add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 270, 250, 30));

        jftTelefono.setBackground(new java.awt.Color(241, 222, 201));
        try {
            jftTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(504) ########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jftTelefono.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jftTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jftTelefonoActionPerformed(evt);
            }
        });
        jftTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jftTelefonoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jftTelefonoKeyTyped(evt);
            }
        });
        getContentPane().add(jftTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 390, 250, 30));

        jblFoto.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jblFoto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jblFotoKeyReleased(evt);
            }
        });
        getContentPane().add(jblFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 240, 190, 190));

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
        getContentPane().add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 480, 160, 40));
        getContentPane().add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 240));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("PROVEEDORES");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 90, -1, -1));

        btnAyuda1.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda1.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyuda1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnAyuda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 0, 50, 50));

        btnCerrar.setBackground(new java.awt.Color(54, 21, 0));
        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(227, 202, 165));
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cerrar.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1360, 0, 50, 50));

        btnBuscar.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 200, 40, 40));

        btnBuscar1.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar1.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnBuscar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 260, 40, 40));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1410, 920));

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

    private void btnFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFotoActionPerformed
        photo();
    }//GEN-LAST:event_btnFotoActionPerformed

    private void tblProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProveedoresMouseClicked
        int fila = tblProveedores.getSelectedRow();//QUÉ FILA SELECCIONO, DIO CLIC
        txtCodigo.setText(tblProveedores.getValueAt(fila, 0).toString());//CERO ES DONDE ESTARA EL CODIGO.
        txtNombre.setText(tblProveedores.getValueAt(fila, 1).toString());
        txtCiudad.setText(tblProveedores.getValueAt(fila, 2).toString());
        jftTelefono.setText(tblProveedores.getValueAt(fila, 3).toString());
        ImageIcon imagen = (ImageIcon) tblProveedores.getValueAt(fila, 4);
        Image img = imagen.getImage();
        Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
        ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);
        jblFoto.setIcon(imagenEscalada);
        btnGuardar.setVisible(false);
    }//GEN-LAST:event_tblProveedoresMouseClicked

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.crearProveedor();
        this.limpiar();
        this.leerProveedor();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        this.limpiar();
        this.leerProveedor();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtCiudadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCiudadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCiudadActionPerformed

    private void txtCiudadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCiudadKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtCiudadKeyReleased

    private void txtCiudadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCiudadKeyTyped
        char c = evt.getKeyChar();

        // Validar la longitud del texto actual en la caja de texto
        if (txtCiudad.getText().length() == 50) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE)
                || !String.valueOf(c).matches("[\\p{L}\\s]+")) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }
    }//GEN-LAST:event_txtCiudadKeyTyped

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        validarCampos();
    }//GEN-LAST:event_txtNombreKeyReleased

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        char c = evt.getKeyChar();

        // Validar la longitud del texto actual en la caja de texto
        if (txtNombre.getText().length() == 50) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE)
                || !String.valueOf(c).matches("[\\p{L}\\s]+")) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }
    }//GEN-LAST:event_txtNombreKeyTyped

    private void jftTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jftTelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jftTelefonoActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarProveedor();
        limpiar();
        leerProveedor();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        this.leerProveedor();
    }//GEN-LAST:event_formComponentShown

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarProveedor();
        limpiar();
        leerProveedor();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        limpiar();
        leerProveedor();
    }//GEN-LAST:event_btnLeerActionPerformed

    private void jftTelefonoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jftTelefonoKeyReleased
        validarCampos();
    }//GEN-LAST:event_jftTelefonoKeyReleased

    private void jblFotoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jblFotoKeyReleased
        validarCampos();
    }//GEN-LAST:event_jblFotoKeyReleased

    private void jftTelefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jftTelefonoKeyTyped
        /*char c = evt.getKeyChar();

        // Definir la expresión regular para validar el carácter ingresado
        String regex = "[0-9-]+";

        // Validar la longitud del texto actual en la caja de texto
        if (jftTelefono.getText().length() == 13) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado con la expresión regular
        else if (!String.valueOf(c).matches(regex)) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }*/
    }//GEN-LAST:event_jftTelefonoKeyTyped

    private void btnAyuda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyuda1ActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame Proveedores = null;
        AyudaProveedores dialog2 = new AyudaProveedores(Proveedores, true);
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
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Proveedores dialog = new Proveedores(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnAyuda1;
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
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jblFoto;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JFormattedTextField jftTelefono;
    private javax.swing.JTable tblProveedores;
    private javax.swing.JTextField txtCiudad;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
