/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accesoDatosObjetos;
import java.sql.*;

public class Conexion {
    Connection c=null;
    //constructor con parametro: el nombre de nuestra base de datos
    public Conexion(String bd){
        // con esto pueda mostrar el error que ocurrio: el nombre de bd en tiempo y más ejecución o algo más
        try {
            //DRIVER
            Class.forName("com.mysql.jdbc.Driver");
            //DEFINIMOS LA BD. CONCATENANDO EL NOMBRE DE LA BD. ES DONDE VA IR A BUSCAR 
            String url="jdbc:mysql://localhost/"+bd;
            //frame donde se haga la conf inicial, si lleva usuario, pass, ect. se puede hacer
            String user="root",pass="";// configurar en el XAMPP para que la BD tenga password
            //CONECTAMOS LA BD
            c=DriverManager.getConnection(url,user,pass);
            System.out.print("CONEXION REALIZADA EXITOSAMENTE! ");
        } catch (ClassNotFoundException ex) {
            System.out.print("ERROR, NO SE PUDO REALIZAR LA CONEXIÓN "+ex.getMessage());//el catch captura el error en ex Y ASÍ NOS TIRE QUÉ CAPTURO
            //ESTA ES LA EXCEPCION DEL SQL
        } catch (SQLException ex) {
            System.out.print("ERROR, NO SE PUDO REALIZAR LA CONEXIÓN "+ex.getMessage());
        }
    }
    //17:58
    public Connection getConexion(){
        return c;// c de la conexion
    }
    //PROBANDO/OPCIONAL
    public static void main(String args[]){
        //LLAMANDO AL CONSTRUCTOR DE LA CLASE
        new Conexion("cafeteria");
    }   
}
