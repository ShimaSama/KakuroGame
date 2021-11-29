package Presentacion.Vistas;

import Dominio.Perfil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

public class VistaSeleccionDimensiones {

    private JTextField width; //no pueden ser locales
    private JTextField height;
    private int m; //si fuesen locales problems tendria q devolver pair <int,int>
    private int n;
    private JFrame frame;
    private JPanel panel;
    private Perfil p;

    public VistaSeleccionDimensiones(Perfil p) throws IOException, FontFormatException {
        this.p=p;
        configureJFrame();
        configureJPanel();

        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);

        setJLabels(font);
        setJTextFields(font);
        setExitButton();

    }

    public void configureJFrame(){

        frame = new JFrame();
        frame.setSize(700,700);
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void configureJPanel(){
        panel = new JPanel();
        panel.setSize(700,700);
        panel.setVisible(true);
        panel.setBackground(new Color(10, 0, 30, 240));
        panel.setLayout(null);
        frame.add(panel);
    }

    public void setJLabels(Font font){

        Font sizedFont = font.deriveFont(25f);

        // title (◕‿◕✿)
        JLabel size = new JLabel("CHOOSE THE SIZE OF YOUR KAKURO");
        size.setFont(sizedFont);
        size.setForeground(Color.white);
        panel.add(size);
        size.setBounds(100, 100, 700, 200);

        //get height (◕‿◕✿)
        JLabel altura = new JLabel("HEIGHT:");
        altura.setFont(sizedFont);
        altura.setForeground(Color.white);
        panel.add(altura);
        altura.setBounds(250, 150, 700, 200);

        //get width (◕‿◕✿)
        JLabel anchura = new JLabel("WIDTH:");
        anchura.setFont(sizedFont);
        anchura.setForeground(Color.white);
        panel.add(anchura);
        anchura.setBounds(250, 200, 700, 200);

    }

    public void setJTextFields(Font font){

        Font sizedFont2 = font.deriveFont(35f);

        //introducir altura ʕ•ᴥ•ʔ
        height = new JTextField();
        height.setBounds(390, 230, 35, 30);
        height.setFont(new Font("Arial", Font.PLAIN, 22));
        height.setHorizontalAlignment(JTextField.CENTER);
        panel.add(height);

        //introducir anchura ʕ•ᴥ•ʔ
        width = new JTextField();
        width.setBounds(390, 285, 35, 30);
        width.setFont(new Font("Arial", Font.PLAIN, 22));
        width.setHorizontalAlignment(JTextField.CENTER);
        panel.add(width);

        //enter ʕ•ᴥ•ʔ
        JLabel cont = new JLabel("PRESS ENTER TO CONTINUE");
        cont.setFont(sizedFont2);
        cont.setForeground(Color.white);
        panel.add(cont);
        cont.setBounds(80, 300, 700, 350);

        //mirar si se hace enter
        width.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    checkEnter();
                }
            }
        });
        height.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    checkEnter();
                }
            }
        });
    }

    public void checkEnter(){
        if(!problems()){
            frame.dispose();
            try {
                new VistaCreacionKakuro(n,m,p);
            } catch (IOException | FontFormatException badLocationException) {
                badLocationException.printStackTrace();
            }
        }
    }
    //mirar si hay problemas con los valores añadidos
    public boolean problems() {
        String aux = width.getText();
        String aux2 = height.getText();
        boolean problem = false;
        String problems= "Oops, there are some problems:";
        problems = problems + "\r\n";

        if(aux.equals("")){  //no han puesto nada
            problem = true;
            problems = problems + "- Width can't be empty";
            problems = problems + "\r\n";
        }
        else{
            try {
                m = Integer.parseInt(aux);
                if(m<3) { //mirar si es el valor minimo almenos
                    problem=true;
                    problems = problems + "- Width has to be at least 3";
                    problems = problems + "\r\n";
                }
            } catch (NumberFormatException nfe) { //no es un numero
                problems = problems + "- Width has to be a number";
                problems = problems + "\r\n";
                problem = true;
            }
        }

        if(aux2.equals("")){ //no han puesto nada
            problem = true;
            problems = problems + "- Height can't be empty";
            problems = problems + "\r\n";
        }
        else {
            try {
                n = Integer.parseInt(aux2);
                if(n<3) { //mirar si tenga valor minimo
                    problem=true;
                    problems = problems + "- Height has to be at least 3";
                }
            } catch (NumberFormatException nfe) { //no es un numero
                problem = true;
                problems = problems + "- Height has to be a number";

            }
        }
        if (problem) JOptionPane.showMessageDialog(null,
                problems); //avisar que hay problemas
        return problem;

    }

    public void setExitButton() throws IOException {

        Image aux = ImageIO.read((ExtensionJPanel.class.getResource("/exit.png")));
        Icon icon = new ImageIcon(aux);
        JButton exit = new JButton(icon);
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        exit.setBounds(640, 20, 30, 30);
        panel.add(exit);
        exit.addActionListener(e -> {
            frame.dispose();
            try {
                new VistaGaleria(p);
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
        });
    }


}
