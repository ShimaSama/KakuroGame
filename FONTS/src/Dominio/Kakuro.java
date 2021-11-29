package Dominio;

import java.util.HashSet;
import java.util.BitSet;

public class Kakuro {

    private dificultad dif;
    // private static Dominio.Tablero t;
    // private int id;
    private String nombre;
    private final int altura;
    private final int anchura;
    private enum dificultad {FACIL, INTERMEDIO, DIFICIL}
    private Celda[][] matrix;

    // constructora
    public Kakuro(int altura, int anchura, String dif) {
        this.altura = altura;
        this.anchura = anchura;
        this.dif = dificultad.valueOf(dif);
        matrix = new Celda[altura][anchura];
    }

    public Kakuro(int altura, int anchura) {
        this.altura = altura;
        this.anchura = anchura;
        matrix = new Celda[altura][anchura];
    }

    // getters

    public int getAnchura() {
        return anchura;
    }

    public int getAltura() {
        return altura;
    }

    public dificultad getDificultad() {
        return dif;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDificultad(String d){
        this.dif = dificultad.valueOf(d);
    }

    public String getNombre() {
        return nombre;
    }

    public void createCell(int i, int j, String value) {
        if (hasWhiteValue(value)) matrix[i][j] = new Blanca(value);
        else matrix[i][j] = new Negra(value);
    }
    public void setCellValue(int i, int j, String value) {
        matrix[i][j].setValor(value);
    }

    public String getCellValue(int i, int j) {
        return matrix[i][j].getValor();
    }

    public boolean isSuma(int i, int j) {
        String str = getCellValue(i,j);
        if (!isBlanca(i,j) && !str.equals("*")) return true;
        return false;
    }

    public boolean isBlanca(int i, int j) {
        return matrix[i][j] instanceof Blanca;
    }

    public boolean isNegra(int i, int j) {
        return matrix[i][j] instanceof Negra;
    }

    public boolean hasWhiteValue(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return s.equals("?");
        }
    }
    public boolean isEmpty(int i, int j) {
        return getCellValue(i,j).equals("?");
    }

    public void setCellSumaC(int i, int j, IntWrapper SumaC){
        matrix[i][j].setSumaC(SumaC);
    }

    public IntWrapper getCellSumaC(int i, int j){ return matrix[i][j].getSumaC(); }

    public void setCellSumaF(int i, int j, IntWrapper SumaF){
        matrix[i][j].setSumaF(SumaF);
    }

    public IntWrapper getCellSumaF(int i, int j){ return matrix[i][j].getSumaF();}

    public void setCellCandidatesF(int i, int j, BitSet candidates){ matrix[i][j].candidatesF = candidates;}

    public void setCellCandidateFPos(int i, int j, int pos){ matrix[i][j].candidatesF.set(pos);}

    public void clearCellCandidateFPos(int i, int j, int pos){ matrix[i][j].candidatesF.clear(pos);}

    public BitSet getCellCandidatesF(int i, int j){ return matrix[i][j].candidatesF;}

    public void setCellCandidateCPos(int i, int j, int pos){ matrix[i][j].candidatesC.set(pos);}

    public void setCellCandidatesC(int i, int j, BitSet candidates){ matrix[i][j].candidatesC = candidates;}

    public void clearCellCandidateCPos(int i, int j, int pos){ matrix[i][j].candidatesC.clear(pos);}

    public BitSet getCellCandidatesC(int i, int j){ return matrix[i][j].candidatesC;}

    //implementacion de puntero para int

    private static class Celda {
        private String valor;

        private IntWrapper sumaF;

        private IntWrapper sumaC;

        /*vamos a representar cada valor posible de la interseccion {1,9}
            con un puntero a vector de bits [0,..,8] donde cada indice del vector
            representara el numero equivalente a indice+1, siendo 1 el bit si el
            numero se encuentra en la interseccion y 0 si no.
        */
        private BitSet candidatesF;

        private BitSet candidatesC;

        public Celda(String valor) {
            this.valor = valor;
            sumaF = new IntWrapper();
            sumaC = new IntWrapper();
            candidatesC = new BitSet(9);
            candidatesF = new BitSet(9);
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor){ this.valor = valor; }

        public void setCandidatesF(int pos){ candidatesF.set(pos); }

        public void clearCandidatesF(int pos){ candidatesF.clear(pos); }

        public void setCandidatesC(int pos){ candidatesC.set(pos); }

        public void clearCandidatesC(int pos){ candidatesC.clear(pos); }

        public BitSet getCandidatesF(){ return candidatesF;}

        public BitSet getCandidatesC(){ return candidatesC;}


        public IntWrapper getSumaF() { return sumaF; }

        public void setSumaF(IntWrapper sumaF) { this.sumaF = sumaF; }

        public IntWrapper getSumaC() { return sumaC; }

        public void setSumaC(IntWrapper sumaC) { this.sumaC = sumaC; }
    }

    private class Blanca extends Celda {
        public Blanca(String valor) { super(valor); }
    }

    private class Negra extends Celda {
        public Negra(String valor) { super(valor); }
    }
}
