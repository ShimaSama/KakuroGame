package Persistencia;

import Dominio.Kakuro;
import Dominio.Perfil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestionGaleria {

    private Kakuro k;

    public void controlFolderAndFileGaleria(String filename, String foldername){
        try{
            //Miramos si el file GaleriaPublica o la galeria privada de ese usuario existe
            File file = new File(filename);
            //System.out.println(filename);
            if(!file.exists()){
                BufferedWriter output;
                File folder = new File(foldername);
                //Se mira si la carpeta de Galeria existe
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                file = new File(filename);
                file.createNewFile();

            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<String> KakurosPublicos(){
        List<String> names= new ArrayList<String>();
        boolean found = false;
        try {
            List<String> allLines = Files.readAllLines(Paths.get("Galeria/Galeria.txt"));
            for (String line : allLines) {
                if(found){
                    found = false;
                    names.add(line.split(" ")[0]);
                }
                if(line.split(" ")[0].equals("publico")){
                    found = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    public List<String> KakurosPrivados(String username){
        List<String> names= new ArrayList<String>();
        boolean found = false;
        boolean ismine = false;
        try {
            List<String> allLines = Files.readAllLines(Paths.get("Galeria/Galeria.txt"));
            for (String line : allLines) {
                if(found){
                    found = false;

                    if(line.split(" ")[0].equals(username)){
                        ismine = true;

                    }
                }
                else if(ismine){
                    ismine = false;
                    names.add(line.split(" ")[0]);
                }
                if(line.split(" ")[0].equals("privado")){
                    found = true;


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    //REALMENTE DEVUELVE CUALQUIER TIPO DE KAKURO
    public Kakuro DevolverKakuroPublico(String nombre) {
        try {
            File file = new File("Galeria/Galeria.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()) {
                if(scanner.next().equals(nombre)) {

                    String dif = scanner.next();
                    //System.out.println(dif);
                    //System.out.println(scanner.next());
                    //System.out.println(scanner.next());
                    String[] dimensiones = scanner.next().split(",");
                    //System.out.println(dimensiones[0] + dimensiones[1]);
                    int altura = Integer.parseInt(dimensiones[0]);
                    int anchura = Integer.parseInt(dimensiones[1]);
                    k = new Kakuro(altura, anchura, dif);
                    k.setNombre(nombre);
                    scanner.useDelimiter(",|\\n");
                    scanner.next();
                    for (int i = 0; i < altura; i++) {
                        for (int j = 0; j < anchura; j++) {
                            k.createCell(i, j, scanner.next().trim());
                        }
                    }
                    return k;
                }

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    //EL USUARIO LE DEBE DAR UN NOMBRE
    //EL USUARIO DEBE DECIR QUE DIFICULTAD TIENE 
    public void guardarKakuro(Perfil p, Kakuro k, String dificultad){
        try {
            BufferedWriter output = null;

            output = new BufferedWriter(new FileWriter("Galeria/Galeria.txt", true));
            output.write("privado");
            output.newLine();
            output.write(p.getNombre());
            output.newLine();
            output.write(k.getNombre());
            output.newLine();
            output.write(dificultad);
            output.newLine();
            output.write(k.getAltura() + "," +k.getAnchura());
            output.newLine();
            for (int i = 0; i < k.getAltura(); i++) {
                for (int j = 0; j < k.getAnchura(); j++) {
                    if (j == 0) output.write(k.getCellValue(i,j));
                    else output.write("," + k.getCellValue(i,j));
                }
                output.newLine();
            }
            output.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }

}
