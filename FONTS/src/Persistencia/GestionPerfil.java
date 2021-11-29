package Persistencia;

import Dominio.Partida;
import Dominio.Perfil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class GestionPerfil {

    //HAY QUE CREAR UN SOLO FICHERO Y ANIADIR CADA USER CON SU CONTRA

    public GestionPerfil(){

    }

    public void addUserToFilePerfil(String file, Perfil p){
        try {
            BufferedWriter output = null;
            output = new BufferedWriter(new FileWriter(file, true));
            //FIXME: SE DEBE ANIADIR LA PUNTUACIÓN GLOBAL
            output.write(p.getNombre() + " " + SHA256(p.getPassword()) + " " +  p.getPuntuacion());
            output.newLine();
            output.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //FIXME: SE DEBE ANIADIR UNA FUNCION PARA ACTUALIZAR EL VALOR DEL SCORE

    public boolean PerfilAlreadyExists(String filename, Perfil p){
        try {
            List<String> allLines = Files.readAllLines(Paths.get(filename));
            for (String line : allLines) {
                if(line.split(" ")[0].equals(p.getNombre())){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean pwdIsCorrect(String filename, Perfil p){
        try {
            List<String> allLines = Files.readAllLines(Paths.get(filename));
            for (String line : allLines) {
                if(line.split(" ")[0].equals(p.getNombre())){
                    return line.split(" ")[1].equals(SHA256(p.getPassword()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void controlFolderAndFilePerfils(String filename, String foldername){
        try{
            File file = new File(filename);
            //System.out.println(filename);
            if(!file.exists()){
                BufferedWriter output;
                File folder = new File(foldername);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                output = null;
                file = new File(filename);
                file.createNewFile();
                output = new BufferedWriter(new FileWriter(filename, true));
                output.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getscore(String filename, String username){
        try {
            List<String> scores = new ArrayList<String>();
            List<String> allLines = Files.readAllLines(Paths.get(filename));

            for (String line : allLines) {
                scores.add(line.split(" ")[2]);

            }
            return scores;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateFile(Partida p){


        //System.out.println(filename);

        try {
            BufferedReader file = new BufferedReader(new FileReader("Perfiles/Perfiles.txt"));
            String line;
            String input = "";
            while ((line = file.readLine()) != null)
            {
                String[] split = line.split(" ");
                if(split[0].equals(p.getP().getNombre())){
                    String puntuacion = p.getP().getPuntuacion() + "\r\n";
                    input += p.getP().getNombre() + " " + SHA256(p.getP().getPassword()) + " " + String.format(puntuacion);
                }
                else input += line+"\r\n";
            }
            FileOutputStream fileOut = new FileOutputStream("Perfiles/Perfiles.txt");
            fileOut.write(input.getBytes());
            fileOut.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    public static String SHA256(String value) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}



