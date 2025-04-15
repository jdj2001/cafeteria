/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ayuda;

/**
 *
 * @author Juan Alvarado
 */
public class AyudaFacturacion extends javax.swing.JDialog {

    /**
     * Creates new form AyudaReportes
     */
    public AyudaFacturacion(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(96, 54, 1));

        jPanel2.setBackground(new java.awt.Color(255, 251, 233));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("INSTRUCCIONES");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, -1, -1));

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(255, 251, 233));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("SE LE PREGUNTARÁ SI LA FACTURA SE HARÁ CON O SIN RTN; SI \nES CON RTN, DEBE INGRESAR EL RTN DEL CLIENTE Y SI ESTE\n EXISTE EN NUETSRO SISTEMA PODRÁ ACCEDER A LA \nFACTURACIÓN, DE OTRO MODO, DEBERÁ SELECCIONAR QUE LA \nFACTURA NO SE HARÁ CON RTN, Y PASARÁ A LA FACTURACIÓN \nCOMO CONSUMIDOR FINAL.\n\nEN LA VENTANA PARA LA FACTURACIÓN SOLO DEBE \nSELECCIONAR PRIMERO LA CATEGORÍA DLE PRODUCTO Y SE \nMOSTRARÁN LOS PRODUCTOS DISPONIBLES EN INVENTARIO DE \nESA CATEGORÍA, TAMBIÉN PUEDE ESCRIBIR EL NOMBRE DE UN \nPRODUCTO ESPECÍFICO EN LA CAJA DE TEXTO DE BÚSQUEDA \nPARA ENCONTRALO; LUEGO DA CLIC EN UNA FILA (PRODUCTO) Y \nESTO MOSTRARÁ LOS DATOS DEL PRODUCTO EN LAS CAJAS DE \nTEXTO ARRIBA, DONDE DEBERÁ INCREMENTAR O NO LA \nCANTIDAD DEL PRODUCTO QUE EL CLIENTE DESEA COMPRAR, \nMOSTRÁNDOLE EL PRECIO DE VENTA, IMPUESTO, SUBTOTAL Y \nTOTAL DE LA COMPRA.\n\nUNA VEZ HAGA ESTO, DE CLIC EN EL BOTÓN \"AÑADIR ORDEN\" \nPARA AÑADIR EL LA COMPRA AL \"CARRITO\" Y PODRÁ SEGUIR \nAÑADIENDO PRODUCTOS SEGÚN DESEE EL CLIENTE. AL \nTEMINAR LAS COMPRAS DEBE DAR CLIC EN EL BOTÓN \n\"CERRAR VENTA\", DONDE SE LE PEDIRÁ CONFIRMACIÓN \nSOBRE SI LA COMPRA SE HARÁ CON O SIN TARJETA; SI ES \nCON TARJETA, SE AÑADIRÁ EL 5% DEL PRECIO DE VENTA AL \nPRECIO DEL PRODUCTO Y SE CALCULARÁ EL NUEVO IMPUESTO, \nSUBTOTAL Y TOTAL A PAGAR; DE SER SIN TARJETA, SE LE \nHABILITARÁ LA CAJA DE TEXTO PARA QUE INGRESE EL \nEFECTIVO DADO POR EL CLIENTE Y VERIFICAR SU CAMBIO \n(DE HABERLO). UNA VEZ INGRESADO EL EFECTIVO SE LE \nHABILITARÁ EL BOTÓN \"VERIFICAR EFECTIVO\" PARA \nVERIFICAR SI LA CANTIDAD ES MENOR, IGUAL O MAYOR.\n\n\nLUEGO SE LE HABILITARÁ EL BOTÓN \"CREAR FACTURA\" PARA \nCREAR LA FACTURA PARA EL CLIENTE; CON ESTO SE LE \nHABILITARÁ EL BOTÓN \"IMPRIMIR FACTURA\" PARA \nIMPRIMIR LA FACTURA EN PDF PARA EL CLIENTE.");
        jTextArea1.setAlignmentX(1.0F);
        jTextArea1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 500, 420));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 590, 550));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(AyudaFacturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AyudaFacturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AyudaFacturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AyudaFacturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AyudaFacturacion dialog = new AyudaFacturacion(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
