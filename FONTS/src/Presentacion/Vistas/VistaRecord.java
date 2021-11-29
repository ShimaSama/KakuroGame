package Presentacion.Vistas;

import Dominio.ControladorDeDominio;
import Dominio.Perfil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class VistaRecord {
    private JFrame frame;
    private JPanel basePanel;
    private Perfil p;
    private ControladorDeDominio cd;
    Dominio.Partida parti;

    VistaRecord(Perfil p, Dominio.Partida parti) throws IOException, FontFormatException {
        this.p=p;
        this.parti=parti;
        configureJFrame();
        configurebasePanel();

        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font sizedFont = font.deriveFont(70f);

        setTitle(sizedFont);
        setList(font);
        setButton();
    }

    public void configureJFrame (){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,700);
        frame.setResizable(false);
        frame.setBackground(Color.decode("#1A0029"));
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);

    }

    public void configurebasePanel(){

        basePanel = new JPanel();
        basePanel.setBackground(Color.decode("#1A0029"));
        basePanel.setVisible(true);
        basePanel.setLayout(null);
        frame.add(basePanel);

    }

    public void setTitle(Font font){

        JLabel title = new JLabel("RECORDS");
        title.setForeground(Color.white);
        title.setBounds(170, 10, 350, 70);
        title.setFont(font);
        basePanel.add(title);
    }

    public void setButton() throws IOException {

        //poner icono a exit (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧
        Image aux = ImageIO.read((ExtensionJPanel.class.getResource("/exit.png")));
        Icon icon = new ImageIcon(aux);
        JButton exit = new JButton(icon);
        exit.setFont(new Font("Arial", Font.BOLD, 20));
        exit.setBounds(640, 20, 30, 30);
        exit.setBorderPainted(false);
        basePanel.add(exit);
        exit.addActionListener(e -> {
            frame.dispose();
            VistaMenu itemloader;
            try {
                itemloader = new VistaMenu(p);
                itemloader.setSize(700,700);
                itemloader.setVisible(true);
                itemloader.setLocationRelativeTo(null); //centro pantalla
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }

        });

    }

    public void setList(Font font) throws IOException, FontFormatException {

        cd =  new ControladorDeDominio();
        List<Perfil> print = cd.getRecord(parti);
        int length = print.size();
        //configurar los panels necessarios
        Font sizedFont = font.deriveFont(24f);
        JPanel aux = new JPanel();
        aux.setLayout(new GridLayout(length, 2));
        aux.setSize(330,595);
        aux.setLocation(170,70);
        aux.setBackground(null);
        basePanel.add(aux);

        for(int i=0; i<10 && i<length ; i++){
            String user = i+1 + ". " + print.get(i).getNombre();
            JLabel username = new JLabel(user);
            username.setFont(sizedFont);
            username.setPreferredSize(new Dimension(200, 100));
            username.setForeground(Color.white);
            username.setAlignmentX(Component.CENTER_ALIGNMENT);
            aux.add(username);
            String punt = String.valueOf(print.get(i).getPuntuacion());
            JLabel pwd = new JLabel(punt,SwingConstants.RIGHT);
            pwd.setFont(sizedFont);
            pwd.setPreferredSize(new Dimension(200, 100));
            pwd.setForeground(Color.white);
            pwd.setAlignmentX(Component.CENTER_ALIGNMENT);
            aux.add(pwd);
        }


    }

}

