package Dominio;

import java.util.Collections;

public class Perfil{
    private String nombre;
    private String password;
    private int puntuacion;

    public Perfil(){

    }

    public Perfil(String nombre, String password){
        this.nombre = nombre;
        this.password = password;
        this.puntuacion = 0;
    }

    public Perfil(String nombre, String password, int puntuacion) {
        this.nombre = nombre;
        this.password = password;
        this.puntuacion = puntuacion;
    }

    public Perfil(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) {
        this.password = password; }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

}
