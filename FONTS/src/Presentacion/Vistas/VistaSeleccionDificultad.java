package Presentacion.Vistas;

import Dominio.ControladorDeDominio;
import Dominio.Kakuro;
import Dominio.kakurogenerator;
import Dominio.Perfil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class VistaSeleccionDificultad extends JFrame {
    private static Perfil p;
    private JPanel basePanel;
    private JButton EASYButton;
    private JButton NORMALButton;
    private JButton HARDButton;
    private JButton BACKButton;
    private Kakuro k;
    private ControladorDeDominio cd;


    public VistaSeleccionDificultad(Perfil p) throws IOException, FontFormatException {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(basePanel);
        this.pack();
        this.p = p;

        setFont();
        listeners();

    }

    public void makePartida(String dif) throws IOException, BadLocationException {

        getKakuro(dif);
        cd = new ControladorDeDominio();
        if(cd.PartidaActiva(p)!=null) cd.eliminarPartida(cd.PartidaActiva(p));
        try {
            new VistaPartidaKakuro(p,k,0,0,0,false);
        } catch (BadLocationException | FontFormatException | IOException badLocationException) {
            badLocationException.printStackTrace();
        }
        dispose();

    }

    public void setFont() throws IOException, FontFormatException {
        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font sizedFont = font.deriveFont(20f);

        EASYButton.setFont(sizedFont);
        NORMALButton.setFont(sizedFont);
        HARDButton.setFont(sizedFont);
        BACKButton.setBorderPainted(false); //button
    }

    public void listeners(){

        BACKButton.addActionListener(e -> { //ir a menu
            VistaMenu itemloader;
            try {
                itemloader = new VistaMenu(p);
                itemloader.setSize(700,700);
                itemloader.setVisible(true);
                itemloader.setLocationRelativeTo(null); //centro pantalla
                dispose();
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
        });

        EASYButton.addActionListener(e -> {
            try {
                makePartida("FACIL");
            } catch (BadLocationException | IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        NORMALButton.addActionListener(e -> {
            try {
                makePartida("INTERMEDIO");
            } catch (BadLocationException | IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        HARDButton.addActionListener(e -> {
            try {
                makePartida("DIFICIL");
            } catch (BadLocationException | IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
    }

    public void getKakuro(String dif) throws FileNotFoundException, BadLocationException {
        int altura;
        int anchura;
        Random rand = new Random();
        //obtener altura y anchura random dependiendo de la dificultad
        //maximo 11 porque sino no cabe en la pantalla, si se hace muy pequeño no se ven los números
        if(dif.equals("FACIL")){
            altura = (rand.nextInt(7)+4);
            anchura = (rand.nextInt(7)+4);
        }
        else if(dif.equals("INTERMEDIO")){
            altura = (rand.nextInt(3)+8);
            anchura = (rand.nextInt(3)+8);
        }
        else{
            altura = (rand.nextInt(1)+10);
            anchura = (rand.nextInt(1)+10);
        }
        k = new Kakuro(altura, anchura, dif);
        kakurogenerator kg = new kakurogenerator(k);
        k = kg.getGeneratedKakuro();

    }

    public static void main (String[] args) throws IOException, FontFormatException {
        JFrame frame = new VistaSeleccionDificultad(p);
        frame.setSize(700,700);
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);

    }
}