package Presentacion.Vistas;

import Dominio.ControladorDeDominio;
import Dominio.Kakuro;
import Dominio.Perfil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VistaGaleria {

    private JPanel generalPanel;
    private JLayeredPane panels;
    private JPanel localPanel;
    private  JFrame frame;
    private Perfil p;
    private ControladorDeDominio cd;
    private Kakuro k;

    public VistaGaleria(Perfil p) throws IOException, FontFormatException {

        configureJFrame();
        configureJPanels();
        this.p=p;

        frame.setLayout(null); //para q no se canvie la mida del button
        setButtons(); //botones para hacer switch ( ͡°_ʖｰ)～☆
        setListGeneral();
        setListLocal();

    }

    public void setButtons(){

        //----------------------GENERAL--------------------
        JButton general = new JButton();
        general.setBounds(55, 30, 100, 40);
        //para que sea invisible ヽ(͡◕ ͜ʖ ͡◕)ﾉ
        general.setOpaque(false);
        general.setContentAreaFilled(false);
        general.setBorderPainted(false);
        frame.add(general);

        general.addActionListener(e -> SwitchPanels(generalPanel));

        //----------------------LOCAL--------------------
        JButton local = new JButton();
        local.setBounds(180, 30, 100, 40);
        // para que sea invisible ヽ(͡◕ ͜ʖ ͡◕)ﾉ
        local.setOpaque(false);
        local.setContentAreaFilled(false);
        local.setBorderPainted(false);
        frame.add(local);

        local.addActionListener(e -> SwitchPanels(localPanel));

        //----------------------EXIT--------------------
        JButton exit = new JButton("EXIT");
        exit.setFont(new Font("Arial", Font.BOLD, 20));
        exit.setBounds(640, 20, 30, 30);
        exit.setOpaque(false);
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        frame.add(exit);
        exit.addActionListener(e -> {
            frame.dispose();
            VistaMenu itemloader;
            try { //cargar menu
                itemloader = new VistaMenu(p);
                itemloader.setSize(700,700);
                itemloader.setVisible(true);
                itemloader.setLocationRelativeTo(null); //centro pantalla
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void configureJFrame(){
        //set frame
        panels= new JLayeredPane();
        frame = new JFrame();
        frame.add(panels); //ha de ir en este orden, sino no va bien
        frame.setSize(700,700);
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void configureJPanels(){
        //panels de local i general
        generalPanel = new ExtensionJPanel(2);
        localPanel = new ExtensionJPanel(3);
        generalPanel.setSize(700,700);
        localPanel.setSize(700,700);
        generalPanel.setVisible(true);
        localPanel.setVisible(true);
        panels.add(generalPanel); //este es el primero
        frame.add(panels);
    }
    //para hacer lo de los tabs (ㆁᴗㆁ✿)
    public void SwitchPanels(JPanel Panel){
        panels.removeAll();
        panels.add(Panel);
    }

    public void newKakuro(JPanel panel) throws IOException, FontFormatException {

        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font sizedFont = font.deriveFont(18f);
        JTextField nuevoKakuro = new JTextField("NEW KAKURO");
        nuevoKakuro.setEditable(false);
        nuevoKakuro.setHorizontalAlignment(JTextField.CENTER);
        nuevoKakuro.setBackground(null);
        nuevoKakuro.setFont(sizedFont);
        panel.add(nuevoKakuro);
        nuevoKakuro.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){

            //comenzar a crear kakuro
            try {
                new VistaSeleccionDimensiones(p);
                frame.dispose();
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
            }
        });

    }

    public JTextField setJText(String str) throws IOException, FontFormatException {

        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font sizedFont = font.deriveFont(18f);

        JTextField kakuros = new JTextField();
        kakuros.setText(str);
        kakuros.setHorizontalAlignment(JTextField.CENTER);
        kakuros.setEditable(false);
        kakuros.setBackground(null);
        kakuros.setFont(sizedFont);
        kakuros.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                cd = new ControladorDeDominio();
                if(cd.PartidaActiva(p)!=null) {
                    try {
                        cd.eliminarPartida(cd.PartidaActiva(p));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                k = cd.DevolverKakuroPublico(kakuros.getText());
                System.out.println(kakuros.getText());
                try {
                    new VistaPartidaKakuro(p,k,0,0,0,true);
                } catch (BadLocationException | FontFormatException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }
                frame.dispose();
            }
        });
        return kakuros;
    }

    public void setListGeneral() throws IOException, FontFormatException {

        cd =  new ControladorDeDominio();
        //configurar los panels necessarios
        generalPanel.setLayout(null);
        JPanel panelwithList = new JPanel(new BorderLayout());
        panelwithList.setSize(700,610);
        panelwithList.setLocation(0,70);
        panelwithList.setVisible(true);
        panelwithList.setBackground(null);
        generalPanel.add(panelwithList);
        JPanel aux = new JPanel();
        aux.setLayout(new BoxLayout(aux, BoxLayout.PAGE_AXIS));
        aux.setBackground(null);
        List<String> print = cd.KakurosPublicos();
        for(String str : print) //ir añadiendo todos los kakuros
        {
            JTextField kakuros = setJText(str);
            aux.add(kakuros);
        }

        JScrollPane scrollPane = new JScrollPane(aux);
        scrollPane.setBackground(null);
        panelwithList.add(scrollPane);

    }

    public void setListLocal() throws IOException, FontFormatException {

        cd =  new ControladorDeDominio();
        //configurar los panels necessarios
        localPanel.setLayout(null);
        JPanel panelwithList = new JPanel(new BorderLayout());
        panelwithList.setSize(700,610);
        panelwithList.setLocation(0,70);
        panelwithList.setVisible(true);
        panelwithList.setBackground(null);
        localPanel.add(panelwithList);
        JPanel aux = new JPanel();
        aux.setLayout(new BoxLayout(aux, BoxLayout.PAGE_AXIS));
        aux.setBackground(null);
        newKakuro(aux);
        List<String> print = cd.KakurosPrivados(p.getNombre());
        for(String str : print) //ir añadiendo todos los kakuros
        {
            JTextField kakuros = setJText(str);
            aux.add(kakuros);
            System.out.println(str);
        }

        JScrollPane scrollPane = new JScrollPane(aux);
        scrollPane.setBackground(null);
        panelwithList.add(scrollPane);

    }

}
