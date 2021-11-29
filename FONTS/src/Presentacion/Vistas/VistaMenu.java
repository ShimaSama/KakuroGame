package Presentacion.Vistas;

import Dominio.ControladorDeDominio;
import Dominio.Partida;
import Dominio.Perfil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class VistaMenu extends JFrame {

    private JPanel basePanel;
    private JButton PLAYButton;
    private JButton GALLERYButton;
    private JButton RANKINGButton;
    private JButton LOGOUTButton;
    private Perfil p;
    private ControladorDeDominio cd;

    public VistaMenu(Perfil p) throws IOException, FontFormatException {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(basePanel);
        this.pack();
        this.p=p;
        setFont();

        LOGOUTButton.setBorderPainted(false);

        listeners();
    }
    public void setFont() throws IOException, FontFormatException {
        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        String path = getClass(). getResource(""). getPath();
        System.out.println(path);
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font sizedFont = font.deriveFont(20f);

        PLAYButton.setFont(sizedFont);
        GALLERYButton.setFont(sizedFont);
        RANKINGButton.setFont(sizedFont);
        LOGOUTButton.setFont(sizedFont);
    }

    public void listeners(){

        LOGOUTButton.addActionListener(e -> { //cargar login
            VistaLogIn itemloader=new VistaLogIn();
            itemloader.setSize(700,700);
            itemloader.setVisible(true);
            itemloader.setLocationRelativeTo(null); //centro pantalla
            dispose();
        });
        PLAYButton.addActionListener(e -> {

            //cargar partida anterior
            cd = new ControladorDeDominio();
            boolean cargar = false;
            if(cd.PartidaActiva(p)!=null){
                Partida part = cd.PartidaActiva(p);
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int response =  JOptionPane.showConfirmDialog (null, "Would You Like to continue the last game?","Warning",dialogButton);
                if(response == JOptionPane.YES_OPTION) {
                    cargar = true;
                    try {
                        new VistaPartidaKakuro(p,part.getK(), part.getSegundos(),part.getMinutos(),part.getHoras(),true);
                        dispose();
                    } catch (BadLocationException | FontFormatException | IOException badLocationException) {
                        badLocationException.printStackTrace();
                    }
                }
            }
            if(!cargar) {
                VistaSeleccionDificultad itemloader; //cargar dificultad
                try {
                    itemloader = new VistaSeleccionDificultad(p);
                    itemloader.setSize(700, 700);
                    itemloader.setVisible(true);
                    itemloader.setLocationRelativeTo(null); //centro pantalla
                    dispose();


                } catch (IOException | FontFormatException ioException) {
                    ioException.printStackTrace();
                }
            }

        });
        //ir a galeria
        GALLERYButton.addActionListener(e -> {
            try {
                new VistaGaleria(p);
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
            dispose();
        });
        //ir al ranking
        RANKINGButton.addActionListener(e -> {
            try {
                new VistaRanking(p);
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
            dispose();
        });
    }

}


