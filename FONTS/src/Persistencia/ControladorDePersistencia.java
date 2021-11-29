package Persistencia;

import Dominio.Kakuro;
import Dominio.Partida;
import Dominio.Perfil;

import java.io.IOException;
import java.util.List;

public class ControladorDePersistencia {
    private GestionPerfil gp;
    private GestionGaleria gg;
    private GestionPartida gpart;
    private GestionRanking gr;
    private GestionRecord grec;

    public ControladorDePersistencia() {
        this.gp = new GestionPerfil();
        this.gg = new GestionGaleria();
        this.gpart = new GestionPartida();
        this.gr = new GestionRanking();
        this.grec = new GestionRecord();

    }

    public void addUserToFilePerfil(String file, Perfil p){
        gp.addUserToFilePerfil(file,p);
    }

    public boolean PerfilAlreadyExists(String filename, Perfil p){
        return gp.PerfilAlreadyExists(filename,p);
    }

    public boolean pwdIsCorrect(String filename, Perfil p){
        return gp.pwdIsCorrect(filename, p);
    }

    public void controlFolderAndFilePerfils(String filename, String foldername){
        gp.controlFolderAndFilePerfils(filename,foldername);
    }

     public List<String> KakurosPublicos(){
        return gg.KakurosPublicos();
    }

    public Kakuro DevolverKakuroPublico(String nombre){
        return gg.DevolverKakuroPublico(nombre);
    }

    public List<String> KakurosPrivados(String username){
        return gg.KakurosPrivados(username);
    }

    public void GuardarKakuroPartida( Partida p){

        gpart.GuardarKakuroPartida(p);
    }

    public List<Perfil> getRanking(){
        return gr.getRanking();
    }

    public List<Perfil> getRecord(Partida p){
        return grec.getRecord(p);
    }

    public void eliminarPartida(Partida p) throws IOException {
        gp.updateFile(p);
        gpart.eliminarPartida(p);
    }

    public Partida PartidaActiva(Perfil p){
        return gpart.PartidaActiva(p);
    }

    public void guardarKakuro(Perfil p, Kakuro k, String dificultad){

        gg.guardarKakuro(p,k,dificultad);
    }


}
