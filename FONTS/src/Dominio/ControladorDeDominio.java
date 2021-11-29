package Dominio;
import Persistencia.ControladorDePersistencia;

import java.io.IOException;
import java.util.List;

public class ControladorDeDominio {
    private ControladorDePersistencia CtrlPer;

    public ControladorDeDominio() {
        this.CtrlPer = new ControladorDePersistencia();
    }

    /*-------------------------Métodos para connectar con capa de Persistencia-----------------------------------*/

    public String loginPerfil(String filename, Perfil p) {
        //Método que carga el fichero introducido a Tabla

        boolean perfilExists = CtrlPer.PerfilAlreadyExists(filename, p);
        if(!perfilExists) return "username doesn't exist";
        boolean pwdiscorrect = CtrlPer.pwdIsCorrect(filename, p);
        if(!pwdiscorrect) return "Password is wrong, try again";
        return "";
    }

    public String registerPerfil(String filename, String foldername, Perfil p){
        CtrlPer.controlFolderAndFilePerfils(filename, foldername);
        boolean perfilExists = CtrlPer.PerfilAlreadyExists(filename, p);
        if(perfilExists) return "Username already exists";
        else{
            CtrlPer.addUserToFilePerfil(filename, p);
        }
        return "";
    }

    public List<String> KakurosPublicos(){
        return CtrlPer.KakurosPublicos();
    }

    public List<String> KakurosPrivados(String username){
        return CtrlPer.KakurosPrivados(username);
    }

    public Kakuro DevolverKakuroPublico(String nombre){
        return CtrlPer.DevolverKakuroPublico(nombre);
    }

    public void GuardarKakuroPartida(Partida p){

        CtrlPer.GuardarKakuroPartida(p);
    }

    public List<Perfil> getRanking(){
        return CtrlPer.getRanking();
    }

    public List<Perfil> getRecord(Partida p){
        return CtrlPer.getRecord(p);
    }

    public void eliminarPartida(Partida p) throws IOException {
        CtrlPer.eliminarPartida(p);
    }

    public Partida PartidaActiva(Perfil p){
        return CtrlPer.PartidaActiva(p);
    }

    public void guardarKakuro(Perfil p, Kakuro k, String dificultad){
        CtrlPer.guardarKakuro(p,k,dificultad);
    }

    /*

    public void createPartida(String filename, String foldername, Integer time){
        CtrlPer.controlFolderAndFilePartida(filename, foldername);
        CtrlPer.addInfoToFilePartida(filename, username, values, time, 1);
    }*/

    //FIXME: FALTAN FUNCIONES PARA GALERIA, PARTIDA

    //FIXME: DEBE SER RECURSIVO
    /*
    public void addEntriesRanking(String filename, String username){
        //LEER TODAS LAS PUNTUACIONES GLOBALES DEL FICHERO DE PERFIL
        //LLAMARTODO EL RATO A ACTUALIZAR RANKING
        List<String> score = CtrlPer.getscore(filename, username);
        CtrlPer.addKakuroToFileRanking(filename, username, score);

    }*/
}
