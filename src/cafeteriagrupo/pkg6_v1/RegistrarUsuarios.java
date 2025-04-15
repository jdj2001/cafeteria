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
import ayuda.AyudaRegistrarUsuarios;
import java.awt.Frame;
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
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan Alvarado
 */
public class RegistrarUsuarios extends javax.swing.JDialog {

    private static String correoUsu;
    String imagenActual;
    ImageIcon imagen;
    String rol, direccionFoto, sentenciaSQL, codRol, estatus, estatusValidar, rolValidar;
    Connection con = null;
    Conexion conecta;
    PreparedStatement ps = null;
    ImageIcon icono;
    FileInputStream fis; //PARA AGREGAR FOTOGRAFIA
    int longitudBytes;  //FOTOGRAFIA

    public String modeloCorreo = "^[a-zA-Z0-9]+([._]?[a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.-]?[a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";
    public String modeloTelefono = "^[0-9]*$";

    /**
     * Creates new form RegistrarUsuarios
     */
    public RegistrarUsuarios(java.awt.Frame parent, boolean modal, String user) {
        super(parent, modal);
        initComponents();
        /*Principal jFrame;
        jFrame = new Principal(user,);*/
        this.ConectarBD();
        this.CargarRol();

        //this.desactivar();
        correoUsu = user;
        validar();
        jblUsuario.setText(correoUsu);
        btnGuardar.setEnabled(false);
        txtEstatus.setText("0");
        cambioImagen("sinPerfil.jpg", jblFoto);
        cambioImagen("iconos.png", jblFondo);
        cambioImagen("logo.png", jblLogo);
    }

    public void ConectarBD() {
        conecta = new Conexion("cafeteria");
        con = conecta.getConexion();//LLAMANDO CLASE CONEXION
    }

    public void CargarRol() {
        cboRol.removeAllItems();
        String Csql = "SELECT * FROM rol";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(Csql); //EJECUTA UNA CONSULTA

            this.cboRol.addItem("ROL:");

            while (rs.next()) {
                this.cboRol.addItem(rs.getString("id") + " | " + rs.getString("descripcion"));
            }//FIN DEL WHILE
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
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
                btnBuscar.setVisible(true);
                txtCodigo.setVisible(true);
                btnActualizar.setVisible(true);
            } else if (rol2.equals("Ventas")) {
                btnBuscar.setVisible(false);
                txtCodigo.setVisible(false);
                btnActualizar.setVisible(false);
            } else if (rol2.equals("Inventario")) {
                btnBuscar.setVisible(false);
                txtCodigo.setVisible(false);
                btnActualizar.setVisible(false);
            }
        }
    }

    public void crearUsuario() {
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty() || !txtCorreo.getText().matches(modeloCorreo) || txtContraseña.getText().isEmpty() || jblFoto.getIcon().toString().equals("sinPerfil.jpg")
                || txtPregunta.getText().isEmpty() || txtRespuesta.getText().isEmpty() || cboRol.getSelectedIndex() == 0 || jftTelefono.getText().length() < 8 || txtIdentidad.getText().length() < 13) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de crear el usuario.");
            return;
        }
        //SE DEBE METER EN UN BLOQUE TRY-CATCH PARA LLEGAR A ESTO:
        codRol = (String) this.cboRol.getSelectedItem();
        String rol = this.codRol.substring(0, 2);
        estatus = this.txtEstatus.getText();
        String estatus2 = this.estatus.substring(0, 1);
        try {
            //ConectarBD();//METODO ANTERIOR
            sentenciaSQL = "INSERT INTO usuarios (nombre,identidad,telefono,correo,estatus,preguntaSeguridad,respuesta,rol,contraseña,foto)VALUES(?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtIdentidad.getText());
            ps.setString(3, jftTelefono.getText());
            ps.setString(4, txtCorreo.getText());
            ps.setString(5, "Inactivo");
            ps.setString(6, txtPregunta.getText());
            ps.setString(7, txtRespuesta.getText());
            ps.setString(8, rol);
            ps.setString(9, txtContraseña.getText());
            ps.setBinaryStream(4, fis, longitudBytes);

            ps.execute();
            //salida PARA CONFIRMAR DATOS INGRESADOS CORRECTAMENTE
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORREXTAMENTE\nESPERE POR LA APROBACIÓN DEL ADMINISTRADOR");

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(RegistrarUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO INGRESAR DATOS" + ex.getMessage());
        }
    }

    public void desactivar() {
        btnGuardar.setEnabled(false);
    }

    public void limpiar() {
        //cboRol.removeAllItems();
        txtNombre.setText("");
        txtCorreo.setText("");
        txtCodigo.setText("");
        txtIdentidad.setText("");
        txtPregunta.setText("");
        jftTelefono.setText("");
        cboRol.setSelectedIndex(0);
        //jftTelefono.setText("");
        txtRespuesta.setText("");
        txtContraseña.setText("");
        cambioImagen("sinPerfil.jpg", jblFoto);
        btnGuardar.setVisible(true);
        txtPregunta.setEditable(true);
        txtRespuesta.setEditable(true);
        txtContraseña.setEditable(true);
        txtCodigo.setEditable(true);
    }

    public void cambioImagen(String nombreImagen, JLabel img) {
        imagen = new ImageIcon("src/imagenes/" + nombreImagen);
        icono = new ImageIcon(imagen.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT));
        //jlbImagen.setIcon(icono); SE OMITE PORQUE SE TIENEN DOS ETIQUETAS
        img.setIcon(icono);
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

    public void validarCampos() {
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String telefono = jftTelefono.getText();
        String identi = txtIdentidad.getText();
        String contrasenia = txtContraseña.getText();
        String pregunta = txtPregunta.getText();
        String respuesta = txtRespuesta.getText();
        //String imagenactual = jblFoto.getIcon().toString();

        String rol = (String) this.cboRol.getSelectedItem();
        //estatusValidar = (String) this.cboEstatus.getSelectedItem();

        if (!nombre.equals("")
                && !correo.equals("") && correo.matches(modeloCorreo) && !contrasenia.equals("") && !jblFoto.getIcon().toString().equals("sinPerfil.jpg") /*&& !imagenActual.equals("")*/
                && !pregunta.equals("") && !respuesta.equals("") && !rol.equals("ROL:") && telefono.length() >= 8 && identi.length() >= 13) {
            btnGuardar.setEnabled(true);
        } else {
            btnGuardar.setEnabled(false);
        }
//&& isValidEmail(correo)
    }
    // Método para validar el formato del correo electrónico

    // Método para validar el formato del correo electrónico
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@(gmail|hotmail|yahoo)\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /*public void leerUsuario() {
        //this.limpiar();
        sentenciaSQL = "SELECT id,nombre,identidad FROM usuarios WHERE estado LIKE 'Activo'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) tblProveedores.getModel();//HACIENDO CASTEO. NOMBRE DEL JTABLE
            while (rs.next()) {
                datosProveedor[0] = (rs.getString(1));
                datosProveedor[1] = (rs.getString(2));
                datosProveedor[2] = (rs.getString(3));
                datosProveedor[3] = (rs.getString(4));
                datosProveedor[4] = (rs.getString(5));
                modelo.addRow(datosProveedor);//AGREGARLE LAS FILAS AL MODELO    
            }
            tblProveedores.setModel(modelo);
            //con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER DATOS" + ex.getMessage());
        }
    }*/
    public void actualizarUsuario() {
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty() || !txtCorreo.getText().matches(modeloCorreo) || txtContraseña.getText().isEmpty() || jblFoto.getIcon().toString().equals("sinPerfil.jpg")
                || txtPregunta.getText().isEmpty() || txtRespuesta.getText().isEmpty() || cboRol.getSelectedIndex() == 0 || jftTelefono.getText().length() < 8 || txtIdentidad.getText().length() < 13) {
            JOptionPane.showMessageDialog(null, "Debe llenar todos los campos antes de actualizar el usuario.");
            return;
        }
        codRol = (String) this.cboRol.getSelectedItem();
        String rol = this.codRol.substring(0, 2);
        //estatus = this.txtEstatus.getText();

        try {
            sentenciaSQL = "UPDATE usuarios SET telefono=?,correo=?,contraseña=?,preguntaSeguridad=?,respuesta=?,rol=?,foto=? WHERE id=?";//EL ESTADO AL MOMENTO DE HACER DELETE SE MODIFICA. SE PUEDE, PERO NO AHORA
            ps = con.prepareStatement(sentenciaSQL);
            //EL NUMERO SIGNIFICA EL PRIMER PARÁMETRO, NO DE COLUMNA. DE AQUI ES DONDE OBTIENE DE LA CAJA DE TEXTO Y LO MANDA AL PARAMETRO
            ps.setString(1, jftTelefono.getText());
            ps.setString(2, txtCorreo.getText());
            ps.setString(3, txtContraseña.getText());
            ps.setString(4, txtPregunta.getText());
            ps.setString(5, txtRespuesta.getText());
            ps.setString(6, rol);
            ps.setBinaryStream(7, fis, longitudBytes);
            ps.setString(8, txtCodigo.getText());
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORREXTAMENTE");

            //con.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistrarUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            //SI HAY ALGUN ERROR NOS MUESTRA EL MENSAJE QUE SE CAPTURÓ EN LA EXEPCION CON: ex.getMessage() -->ex es ejecutar
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR DATOS" + ex.getMessage());
        }
    }

    public void buscar() {
        String criterio = "";
        boolean buscarPorCodigo = false;
        boolean buscarPorNombre = false;
        boolean buscarPorIdentidad = false;

        if (!txtCodigo.getText().isEmpty()) {
            criterio = txtCodigo.getText();
            buscarPorCodigo = true;
        } else if (!txtNombre.getText().isEmpty()) {
            criterio = txtNombre.getText();
            buscarPorNombre = true;
        } else if (!txtIdentidad.getText().isEmpty()) {
            criterio = txtIdentidad.getText();
            buscarPorIdentidad = true;
        }

        if (buscarPorCodigo || buscarPorNombre || buscarPorIdentidad) {
            try {
                String query = "SELECT * FROM usuarios WHERE ";
                if (buscarPorCodigo) {
                    query += "id = ?";
                } else if (buscarPorNombre) {
                    query += "nombre = ?";
                } else if (buscarPorIdentidad) {
                    query += "identidad = ?";
                }
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, criterio);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Si se encuentra el cliente, cargar sus datos en los campos correspondientes

                    txtCodigo.setText(rs.getString("id"));
                    txtNombre.setText(rs.getString("nombre"));
                    txtIdentidad.setText(rs.getString("identidad"));
                    String rolDB = rs.getString("rol"); // Obtener el valor del rol desde la base de datos (por ejemplo, "1")
                    String rolBuscado = rolDB + " |"; // Construir el valor buscado en el JComboBox (por ejemplo, "1 |")

                    for (int i = 0; i < cboRol.getItemCount(); i++) {
                        String item = (String) cboRol.getItemAt(i);
                        if (item.startsWith(rolBuscado)) {
                            cboRol.setSelectedIndex(i);
                            break;
                        }
                    }
                    jftTelefono.setText(rs.getString("telefono"));
                    txtCorreo.setText(rs.getString("correo"));
                    txtContraseña.setText(rs.getString("contraseña"));
                    txtPregunta.setText(rs.getString("preguntaSeguridad"));
                    txtRespuesta.setText(rs.getString("respuesta"));
                    // Supongamos que la columna "foto" contiene la ruta de la imagen en la base de datos
                    byte[] imagenBytes = rs.getBytes(11);
                    ImageIcon imagen = new ImageIcon(imagenBytes);
                    Image img = imagen.getImage();
                    Image nuevaImagen = img.getScaledInstance(jblFoto.getWidth(), jblFoto.getHeight(), Image.SCALE_DEFAULT);
                    jblFoto.setIcon(new ImageIcon(nuevaImagen));
                    jblFoto.updateUI();
                    ImageIcon imagenEscalada = new ImageIcon(nuevaImagen);

                    //tblDatos.setModel(modelo);
                    //this.leerClientes();
                    btnGuardar.setVisible(false);
                    txtPregunta.setEditable(false);
                    txtRespuesta.setEditable(false);
                    txtContraseña.setEditable(false);
                    txtCodigo.setEditable(false);
                } else {
                    JOptionPane.showMessageDialog(null, "EL USUARIO NO EXISTE");
                }

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
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtPregunta = new javax.swing.JTextField();
        txtContraseña = new javax.swing.JPasswordField();
        txtNombre = new javax.swing.JTextField();
        txtRespuesta = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnOlvidoContra = new javax.swing.JButton();
        btnIngresar = new javax.swing.JButton();
        jblFondo = new javax.swing.JLabel();
        jblLogo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cboRol = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txtEstatus = new javax.swing.JTextField();
        jblFoto = new javax.swing.JLabel();
        btnFoto = new javax.swing.JButton();
        jblUsuario = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jftTelefono = new javax.swing.JFormattedTextField();
        btnBuscar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtIdentidad = new javax.swing.JFormattedTextField();
        btnAyuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(141, 123, 104));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(241, 222, 201));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(96, 54, 1));
        jLabel1.setText("Registrar Usuario");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Nombre");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 90, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Teléfono");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 90, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Identidad");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 90, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Contraseña");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 320, 157, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("Pregunta de seguridad");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 200, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Respuesta:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 240, 157, 20));

        txtPregunta.setBackground(new java.awt.Color(241, 222, 201));
        txtPregunta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtPregunta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPreguntaKeyReleased(evt);
            }
        });
        jPanel2.add(txtPregunta, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 200, 230, -1));

        txtContraseña.setBackground(new java.awt.Color(241, 222, 201));
        txtContraseña.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtContraseñaKeyReleased(evt);
            }
        });
        jPanel2.add(txtContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 320, 230, -1));

        txtNombre.setBackground(new java.awt.Color(241, 222, 201));
        txtNombre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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
        jPanel2.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, 230, -1));

        txtRespuesta.setBackground(new java.awt.Color(241, 222, 201));
        txtRespuesta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtRespuesta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRespuestaActionPerformed(evt);
            }
        });
        txtRespuesta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRespuestaKeyReleased(evt);
            }
        });
        jPanel2.add(txtRespuesta, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 240, 230, -1));

        btnGuardar.setBackground(new java.awt.Color(54, 21, 0));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(227, 202, 165));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jPanel2.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 400, -1, -1));

        btnLimpiar.setBackground(new java.awt.Color(54, 21, 0));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(227, 202, 165));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        jPanel2.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 400, -1, -1));

        btnSalir.setBackground(new java.awt.Color(54, 21, 0));
        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(227, 202, 165));
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        jPanel2.add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 400, 100, -1));

        btnOlvidoContra.setBackground(new java.awt.Color(54, 21, 0));
        btnOlvidoContra.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnOlvidoContra.setForeground(new java.awt.Color(227, 202, 165));
        btnOlvidoContra.setText("¿Olvidó su contraseña?");
        btnOlvidoContra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOlvidoContraActionPerformed(evt);
            }
        });
        jPanel2.add(btnOlvidoContra, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 440, 210, -1));

        btnIngresar.setBackground(new java.awt.Color(54, 21, 0));
        btnIngresar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnIngresar.setForeground(new java.awt.Color(227, 202, 165));
        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });
        jPanel2.add(btnIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 440, 100, -1));

        jblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconos.png"))); // NOI18N
        jPanel2.add(jblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 540, 1180, 200));

        jblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo.png"))); // NOI18N
        jPanel2.add(jblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 170));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Correo");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 90, -1));

        txtCorreo.setBackground(new java.awt.Color(241, 222, 201));
        txtCorreo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtCorreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorreoActionPerformed(evt);
            }
        });
        txtCorreo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCorreoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCorreoKeyTyped(evt);
            }
        });
        jPanel2.add(txtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 320, 230, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Rol");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 280, 157, 20));

        cboRol.setBackground(new java.awt.Color(227, 202, 165));
        cboRol.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cboRol.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboRolItemStateChanged(evt);
            }
        });
        cboRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboRolActionPerformed(evt);
            }
        });
        cboRol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboRolKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cboRolKeyReleased(evt);
            }
        });
        jPanel2.add(cboRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 280, 230, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Estatus:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 90, -1));

        txtEstatus.setEditable(false);
        txtEstatus.setBackground(new java.awt.Color(241, 222, 201));
        txtEstatus.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtEstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstatusActionPerformed(evt);
            }
        });
        txtEstatus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEstatusKeyReleased(evt);
            }
        });
        jPanel2.add(txtEstatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 360, 230, -1));

        jblFoto.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jblFoto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jblFotoKeyReleased(evt);
            }
        });
        jPanel2.add(jblFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 180, 200, 210));

        btnFoto.setBackground(new java.awt.Color(54, 21, 0));
        btnFoto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnFoto.setForeground(new java.awt.Color(227, 202, 165));
        btnFoto.setText("Foto");
        btnFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFotoActionPerformed(evt);
            }
        });
        jPanel2.add(btnFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 410, -1, -1));

        jblUsuario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jblUsuario.setForeground(new java.awt.Color(96, 54, 1));
        jPanel2.add(jblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 510, 240, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(96, 54, 1));
        jLabel11.setText("USUARIO:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, 80, 30));

        jftTelefono.setBackground(new java.awt.Color(241, 222, 201));
        try {
            jftTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jPanel2.add(jftTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 280, 230, -1));

        btnBuscar.setBackground(new java.awt.Color(54, 21, 0));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(227, 202, 165));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        jPanel2.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 230, 40, 40));

        btnActualizar.setBackground(new java.awt.Color(54, 21, 0));
        btnActualizar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(227, 202, 165));
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        jPanel2.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 480, 360, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("Código:");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 90, 20));

        txtCodigo.setBackground(new java.awt.Color(241, 222, 201));
        txtCodigo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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
        jPanel2.add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, 230, -1));

        txtIdentidad.setBackground(new java.awt.Color(241, 222, 201));
        try {
            txtIdentidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#############")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jPanel2.add(txtIdentidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 240, 230, -1));

        btnAyuda.setBackground(new java.awt.Color(54, 21, 0));
        btnAyuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAyuda.setForeground(new java.awt.Color(227, 202, 165));
        btnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cambiarPre.png"))); // NOI18N
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        jPanel2.add(btnAyuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 0, 50, 50));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 1180, 740));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1244, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1244, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 802, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 802, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPreguntaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPreguntaKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtPreguntaKeyReleased

    private void txtContraseñaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContraseñaKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtContraseñaKeyReleased

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtNombreKeyReleased

    private void txtRespuestaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRespuestaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRespuestaActionPerformed

    private void txtRespuestaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRespuestaKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtRespuestaKeyReleased

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        int a = JOptionPane.showConfirmDialog(null, "¿DESEA SALIR DE LA APLICACIÓN?", "SELECCIONE", JOptionPane.YES_NO_OPTION);
        if (a == 0) {
            this.dispose();
            //System.exit(0);
        }
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnOlvidoContraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOlvidoContraActionPerformed
        setVisible(false);
        new ContraOlvidada("").setVisible(true);
    }//GEN-LAST:event_btnOlvidoContraActionPerformed

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        setVisible(false);
        new Login().setVisible(true);
    }//GEN-LAST:event_btnIngresarActionPerformed

    private void txtCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoActionPerformed

    private void txtCorreoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCorreoKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtCorreoKeyReleased

    private void cboRolKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboRolKeyReleased
        //this.validarCampos();
    }//GEN-LAST:event_cboRolKeyReleased

    private void cboRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboRolActionPerformed
        this.validarCampos();
    }//GEN-LAST:event_cboRolActionPerformed

    private void txtEstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEstatusActionPerformed

    private void txtEstatusKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEstatusKeyReleased
        this.validarCampos();
    }//GEN-LAST:event_txtEstatusKeyReleased

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        char c = evt.getKeyChar();

        // Validar la longitud del texto actual en la caja de texto
        if (txtNombre.getText().length() == 70) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE)
                || !String.valueOf(c).matches("[\\p{L}\\s]+")) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        }
    }//GEN-LAST:event_txtNombreKeyTyped

    private void txtCorreoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCorreoKeyTyped
        char c = evt.getKeyChar();

// Validar la longitud del texto actual en la caja de texto
        if (txtCorreo.getText().length() == 50) {
            evt.consume(); // Si se ha alcanzado el límite de caracteres, ignorar el carácter ingresado
        } // Validar el carácter ingresado
        else if (!(Character.isLetter(c) || c == KeyEvent.VK_SPACE || c == KeyEvent.VK_BACK_SPACE
                || c == '@' || c == '.' || c == '_' || c == '-' || Character.isDigit(c))) {
            evt.consume(); // Si el carácter no es válido, ignorarlo
        } // Validar el formato del correo electrónico
        else if (c == '@' && txtCorreo.getText().indexOf('@') != -1) {
            evt.consume(); // Si ya hay un '@' presente, ignorar el carácter ingresado
        } // Validar el dominio del correo electrónico
        else if (c == '.' && txtCorreo.getText().endsWith(".")) {
            evt.consume(); // Si el último carácter es un '.', ignorar el carácter ingresado
        }

    }//GEN-LAST:event_txtCorreoKeyTyped

    private void cboRolItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboRolItemStateChanged
        this.validarCampos();
    }//GEN-LAST:event_cboRolItemStateChanged

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.crearUsuario();
        this.limpiar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void cboRolKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboRolKeyPressed
        this.validarCampos();
    }//GEN-LAST:event_cboRolKeyPressed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        this.limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFotoActionPerformed
        photo();
    }//GEN-LAST:event_btnFotoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscar();

    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarUsuario();
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

    private void jblFotoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jblFotoKeyReleased
        validarCampos();
    }//GEN-LAST:event_jblFotoKeyReleased

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // Suponiendo que estás llamando al segundo diálogo desde el primer diálogo
        // Suponiendo que estás llamando al segundo diálogo desde la clase inventario
        /*AyudaInventario dialog2 = new AyudaInventario((inventario) getOwner(), true);
        dialog2.setVisible(true);*/
        Frame clientes = null;
        AyudaRegistrarUsuarios dialog2 = new AyudaRegistrarUsuarios(clientes, true);
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
            java.util.logging.Logger.getLogger(RegistrarUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrarUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrarUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrarUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegistrarUsuarios dialog = new RegistrarUsuarios(new javax.swing.JFrame(), true, correoUsu);
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
    private javax.swing.JButton btnFoto;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnIngresar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnOlvidoContra;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboRol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jblFondo;
    private javax.swing.JLabel jblFoto;
    private javax.swing.JLabel jblLogo;
    private javax.swing.JLabel jblUsuario;
    private javax.swing.JFormattedTextField jftTelefono;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JPasswordField txtContraseña;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtEstatus;
    private javax.swing.JFormattedTextField txtIdentidad;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPregunta;
    private javax.swing.JTextField txtRespuesta;
    // End of variables declaration//GEN-END:variables
}
