package Persistencia;

import Dominio.Perfil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GestionRanking{

    private List<Perfil> Ranking = new ArrayList<Perfil>();

    public List<Perfil> getRanking(){
        List<Integer> names= new ArrayList<Integer>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get("Perfiles/Perfiles.txt"));

            for (String line : allLines) {

                names.add(Integer.parseInt(line.split(" ")[2]));
            }

            Collections.reverse(names);

            allLines = Files.readAllLines(Paths.get("Perfiles/Perfiles.txt"));
            Integer i = 0;
            for(Integer score : names) {
                for (String line : allLines) {
                    if (score == Integer.parseInt(line.split(" ")[2])) {
                        Perfil p = new Perfil(line.split(" ")[0], line.split(" ")[1], Integer.parseInt(line.split(" ")[2]));
                        Ranking.add(i, p);
                        ++i;
                    }
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Ranking;
    }
}
