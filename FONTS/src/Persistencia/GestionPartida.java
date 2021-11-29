package Persistencia;
import Dominio.Kakuro;
import Dominio.Partida;
import Dominio.Perfil;

import java.io.*;
import java.util.Scanner;

import static java.lang.Integer.max;

public class GestionPartida {
    private Kakuro k;
    private Partida part;

    public void controlFolderAndFilePartida(String filename, String foldername){
        try{
            //Miramos si el file GaleriaPublica o la galeria privada de ese usuario existe
            File file = new File(filename);
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

    //NO SE ACTUALIZAN LOS CAMBIOS EN EL KAKURO SI SE INTRODUCE ALGO EN LA INTERFAZ
    public void GuardarKakuroPartida(Partida p){
        try {
            if(!partidaAlreadyExists(p)) {
                BufferedWriter output = null;
                String d = String.valueOf(p.getK().getDificultad());
                int s;
                if (d == "FACIL") s = 1000;
                else if (d == "INTERMEDIO") s = 2000;
                else s = 3000;
                Integer score = max(0, s - 3600*p.getHoras() + 60*p.getMinutos() + p.getSegundos());
                p.getP().setPuntuacion(score + p.getP().getPuntuacion());
                output = new BufferedWriter(new FileWriter("Partida/Partidas.txt", true));
                output.write(p.getK().getNombre());
                output.newLine();
                output.write(p.getP().getNombre());
                output.newLine();
                output.write(score.toString());
                output.newLine();
                output.write("activa");
                output.newLine();
                output.write(p.getK().getAltura() + "," + p.getK().getAnchura());
                output.newLine();
                for (int i = 0; i < p.getK().getAltura(); i++) {
                    for (int j = 0; j < p.getK().getAnchura(); j++) {
                        if (j == 0) output.write(p.getK().getCellValue(i, j));
                        else output.write("," + p.getK().getCellValue(i, j));
                    }
                    output.newLine();
                }
                //score = dificultad/time*100;
                output.write(p.getHoras() + ":" + p.getMinutos() + ":" + p.getSegundos());
                output.newLine();
                output.close();
            }
            else{

                BufferedReader file = new BufferedReader(new FileReader("Partida/Partidas.txt"));
                String line;

                String input = "";
                boolean nameCorrect = false;
                boolean userCorrect = false;
                boolean firstTime = true;
                String user = "";


                while((line = file.readLine()) != null){

                    if (line.equals(p.getK().getNombre())) {
                        nameCorrect = true;
                    }

                    if(userCorrect){
                        String score = String.valueOf(3600*p.getHoras() + 60*p.getMinutos() + p.getSegundos());
                        line = score;
                        userCorrect = false;
                    }

                    if (nameCorrect && line.equals(p.getP().getNombre())) {
                        userCorrect = true;
                        user = line;
                    }

                    if(line.contains(":") && nameCorrect && user.equals(p.getP().getNombre())){
                        line = p.getHoras() + ":" + p.getMinutos() + ":" + p.getSegundos();
                        nameCorrect = false;
                        userCorrect = false;
                        firstTime = false;
                    }


                    String kakuro = "";
                    if(line.startsWith("*,") && firstTime && nameCorrect && user.equals(p.getP().getNombre())){
                        for(int y = 0; y < p.getK().getAltura()-1; ++y){
                            file.readLine();
                        }
                        for (int i = 0; i < p.getK().getAltura(); i++) {
                            for (int j = 0; j < p.getK().getAnchura(); j++) {
                                if (j == 0) kakuro += p.getK().getCellValue(i,j);
                                else kakuro += "," + p.getK().getCellValue(i,j);
                            }

                            input += kakuro+"\r\n";
                            kakuro = "";
                        }
                    }
                    else{

                        input += line+"\r\n";
                    }
                }

                FileOutputStream fileOut = new FileOutputStream("Partida/Partidas.txt");
                fileOut.write(input.getBytes());
                fileOut.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Partida PartidaActiva(Perfil p){
        try {
            File file = new File("Partida/Partidas.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()) {
                String nombre = scanner.next();
                if(scanner.hasNext() && scanner.next().equals(p.getNombre())) {
                    // nos saltamos la puntuación
                    scanner.next();

                    if (scanner.next().equals("activa")) {
                        String[] dimensiones = scanner.next().split(",");
                        int altura = Integer.parseInt(dimensiones[0]);
                        int anchura = Integer.parseInt(dimensiones[1]);
                        k = new Kakuro(altura, anchura);
                        k.setNombre(nombre);
                        scanner.useDelimiter(",|\\n");
                        scanner.next();
                        for (int i = 0; i < altura; i++) {
                            for (int j = 0; j < anchura; j++) {
                                k.createCell(i, j, scanner.next().trim());

                            }
                        }
                       // String[] tiempo = scanner.next().split(":");
                        String[] tiempo = scanner.next().trim().split(":");
                        int horas = Integer.parseInt(tiempo[0]);
                        int minuts = Integer.parseInt(tiempo[1]);
                       // tiempo[2] = String.Replace("\\n", String.Empty);
                        int segundos = Integer.parseInt(tiempo[2]);
                        part = new Partida(k,p,segundos,minuts, horas);

                        return part;
                    }
                    else{
                        String tam = scanner.next();
                        System.out.println(tam);
                        //ens saltem el tamany i el kakuro i el temps
                        String[] split = tam.split(",");
                        int x = Integer.parseInt(split[0]);
                        for (int i = 0; i < x+1; i++) scanner.next();

                    }
                }
                else{
                    scanner.next();
                    scanner.next();
                    String tam = scanner.next();
                    System.out.println(tam);
                    //ens saltem el tamany i el kakuro i el temps
                    String[] split = tam.split(",");
                    int x = Integer.parseInt(split[0]);
                    for (int i = 0; i < x+1; i++) scanner.next();

                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void eliminarPartida(Partida p) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("Partida/Partidas.txt"));
        String line;
        String input = "";
        boolean nameCorrect = false;
        boolean userCorrect = false;

        while((line = file.readLine()) != null){
        /* Podemos verificar si es Usuario_1 y \r\n es para hacer el
          Salto de Línea y tener el formato original */
            if (line.equals(p.getK().getNombre())) {
                nameCorrect = true;
            }

            if (nameCorrect && line.equals(p.getP().getNombre())) {
                nameCorrect = false;
                userCorrect = true;
            }

            if(userCorrect && line.contains("activa")){
                input += line.replaceAll("activa", "finalizada\r\n");
                userCorrect = false;
            }

            else
                input += line+"\r\n";
        }
        FileOutputStream fileOut = new FileOutputStream("Partida/Partidas.txt");
        fileOut.write(input.getBytes());
        fileOut.close();
    }

    public Boolean partidaAlreadyExists(Partida p) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("Partida/Partidas.txt"));
        String line;
        while((line = file.readLine()) != null) {
        /* Podemos verificar si es Usuario_1 y \r\n es para hacer el
          Salto de Línea y tener el formato original */
            if (line.equals(p.getK().getNombre())) {
                if(file.readLine().equals(p.getP().getNombre())){
                    file.readLine();
                    if(file.readLine().equals("activa")) return true;
                }
            }
        }
        return false;
    }
}
