package Presentacion.Vistas;

import Dominio.ControladorDeDominio;
import Dominio.Perfil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class VistaRanking {

    private JFrame frame;
    private JPanel basePanel;
    private Perfil p;
    private ControladorDeDominio cd;

    VistaRanking(Perfil p) throws IOException, FontFormatException {
        this.p = p;
        configureJFrame();
        configurebasePanel();

        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);

        setTitle(font);
        setButton();
        setList(font);
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

        Font sizedFont = font.deriveFont(70f);
        JLabel title = new JLabel("RANKING");
        title.setForeground(Color.white);
        title.setBounds(170, 10, 350, 70);
        title.setFont(sizedFont);
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
        List<Perfil> print = cd.getRanking();
        int length = print.size();
        //configurar los panels necessarios
        Font sizedFont = font.deriveFont(24f);

        JPanel panelwithList = new JPanel(new BorderLayout());
        panelwithList.setBackground(Color.decode("#1A0029"));
        panelwithList.setSize(685,595);
        panelwithList.setLocation(0,70);
        panelwithList.setVisible(true);
        panelwithList.setBackground(null);
        basePanel.add(panelwithList);
        JPanel aux = new JPanel();
        aux.setBackground(Color.decode("#1A0029"));
        aux.setLayout(new BoxLayout(aux, BoxLayout.PAGE_AXIS));
        for(int i=0; i<length ; i++){
            JLabel blanco = new JLabel( " ");
            aux.add(blanco);
            String user = i+1 + ". " + print.get(i).getNombre();
            String punt = String.valueOf(print.get(i).getPuntuacion());
            JLabel username = new JLabel(user + " - " +punt);
            username.setFont(sizedFont);
            username.setForeground(Color.white);
            username.setAlignmentX(Component.CENTER_ALIGNMENT);
            aux.add(username);

        }
        JScrollPane scrollPane = new JScrollPane(aux);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.decode("#1A0029"));
        scrollPane.setBackground(null);
        panelwithList.add(scrollPane);



    }
}
