package Persistencia;

import Dominio.Kakuro;
import Dominio.Partida;
import Dominio.Perfil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class GestionRecord {
    private List<Perfil> Record = new ArrayList<Perfil>();


    public List<Perfil> getRecord(Partida p){
        List<Integer> names= new ArrayList<>();
        int top_punt;
        boolean first = true;
        boolean changed = false;
        int firstScore = 0;
        try {
            File file = new File("Partida/Partidas.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                if (scanner.next().equals(p.getK().getNombre())) {

                    String user = scanner.next();

                    Integer score = Integer.parseInt(scanner.next());

                    if (first){
                        firstScore = score;
                        names.add(score);
                        first = false;
                    }
                    top_punt = score;
                    if (score > top_punt){
                        names.add(score);
                        changed = true;
                    }

                }
            }
            if (changed) names.remove(firstScore);

            Collections.reverse(names);


            Integer i = 0;
            for(Integer str : names)
            {
                boolean done = false;
                file = new File("Partida/Partidas.txt");
                scanner = new Scanner(file);
                while (scanner.hasNext() && !done) {
                    String user = null;
                    if (scanner.next().equals(p.getK().getNombre())) {
                        user = scanner.next();
                        if (scanner.next().equals(String.valueOf(str))) {
                            int score = str;
                            done = true;
                            Perfil per = new Perfil(user, score);
                            Record.add(i, per);
                            ++i;
                        }
                    }

                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return Record;
    }
}
