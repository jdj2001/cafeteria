package cafeteriagrupo.pkg6_v1;

public class cafeObjetosSaldoInicial {
    
    /*todas las clases conectadas a este objeto PARA CUADRAR SI CON LO QUE SE TIENE EN EFECTIVO CUADRA
    CON LO QUE VENDIÓ, CON LO QUE SE GASTÓ, CON LO QUE SE COBRÓ CON TARJETA, Y VER I HAY ALGUÚN FALTANTE*/
    double saldoIni,totalV; //ATRIBUTOS
    //EN EL CONSTRUCTOR SE INICIALIZAN LAS VARIABLES
    public cafeObjetosSaldoInicial(double saldoIni) {
        /*SALDOINI QUE PERTENECE AL ATRIBUTO*/this.saldoIni = saldoIni;//SALDOINI QUE VIENE DE AFUERA
        //this.totalV = totalV;
    }
    //ESTABLECER INFO EN LOS OBJETOS ES A TRAVES DE LOS SETTER Y GETTER
    public void setSaldoIni(double saldoIni) {
        this.saldoIni = saldoIni;
    }

    /*public void setTotalV(int totalV) {
        this.totalV = totalV;
    }*/
    
    public double getSaldoIni() {
        return saldoIni;
    }

    /*public int getTotalV() {
        return totalV;
    } */
}
