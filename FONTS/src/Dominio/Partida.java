package Dominio;

public class Partida {

    private Kakuro k;
    private Perfil p;
    private int segundos;
    private int minutos;
    private int horas;

    public Partida() {
    }

    public Partida(Kakuro k, Perfil p, int segundos, int minutos, int horas) {
        this.k = k;
        this.p = p;
        this.segundos = segundos;
        this.minutos = minutos;
        this.horas = horas;
    }

    public Kakuro getK() {
        return k;
    }

    public Perfil getP() {
        return p;
    }

    public int getSegundos() {
        return segundos;
    }

    public int getMinutos() {
        return minutos;
    }

    public int getHoras() {
        return horas;
    }

    public void setK(Kakuro k) {
        this.k = k;
    }

    public void setP(Perfil p) {
        this.p = p;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }
}
