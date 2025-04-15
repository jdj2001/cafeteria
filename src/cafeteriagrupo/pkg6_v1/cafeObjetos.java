public class cafeObjetos {
    
    /*todas las clases conectadas a este objeto PARA CUADRAR SI CON LO QUE SE TIENE EN EFECTIVO CUADRA
    CON LO QUE VENDIÓ, CON LO QUE SE GASTÓ, CON LO QUE SE COBRÓ CON TARJETA, Y VER I HAY ALGUÚN FALTANTE*/
    int saldoIni,totalV; //ATRIBUTOS
    //EN EL CONSTRUCTOR SE INICIALIZAN LAS VARIABLES
    public cafeObjetos(int saldoIni, int totalV) {
        /*SALDOINI QUE PERTENECE AL ATRIBUTO*/this.saldoIni = saldoIni;//SALDOINI QUE VIENE DE AFUERA
        this.totalV = totalV;
    }
    //ESTABLECER INFO EN LOS OBJETOS ES A TRAVES DE LOS SETTER Y GETTER
    public void setSaldoIni(int saldoIni) {
        this.saldoIni = saldoIni;
    }

    public void setTotalV(int totalV) {
        this.totalV = totalV;
    }
    
    public int getSaldoIni() {
        return saldoIni;
    }

    public int getTotalV() {
        return totalV;
    } 
}
