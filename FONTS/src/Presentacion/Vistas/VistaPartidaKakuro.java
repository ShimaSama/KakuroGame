package Presentacion.Vistas;

import Dominio.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.sql.Timestamp;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class VistaPartidaKakuro extends JFrame{

    private JButton exit;
    private JButton pause;
    private JButton done;
    private JButton help;
    private int sec;
    private int min;
    private int h;
    private int altura;
    private int anchura;
    private JFrame frame;
    private static Kakuro k;
    private Perfil p;
    private boolean galeria;
    private JPanel[][] tableroCeldas;
    private JPanel tablero;
    private JTextField lastCelda;
    private JLabel saved;
    private JTextField[] Celdas; //para poder acceder a una celda concreta
    private ControladorDeDominio cd;
    Dominio.Partida parti;


    public VistaPartidaKakuro(Perfil p, Kakuro k, int sec, int min, int h, boolean galeria) throws BadLocationException, IOException, FontFormatException {
        this.p = p;
        this.k = k;
        this.sec=sec;
        this.min=min;
        this.h=h;
        this.galeria = galeria;
        altura = k.getAltura();
        anchura = k.getAnchura();
        cd = new ControladorDeDominio();

        configureJFrame();
        setTablero();

        setGUI(); //el GUI tiene all
    }

    public void configureJFrame () throws IOException, FontFormatException {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(anchura*70,altura*70+80);
        frame.setResizable(false);
        frame.setBackground(Color.decode("#1A0029"));
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);
        GlassPane blur = new GlassPane(anchura*70,(altura*70+80)); //la pantalla de pausa
        frame.setGlassPane(blur);
    }

    public void setGUI() throws IOException {

        JPanel gui = new JPanel(new BorderLayout(3, 3));
        gui.add(tablero);
        gui.setBorder(new EmptyBorder(5, 15, 15, 15));
        JToolBar tools = setToolBar();
        gui.add(tools, BorderLayout.PAGE_START);
        gui.setBackground(Color.decode("#1A0029"));
        JPanel bot = new JPanel();
        bot.setBackground(null);
        gui.add(bot,BorderLayout.PAGE_END);
        bot.setLayout(new BoxLayout(bot, BoxLayout.LINE_AXIS));
        SetTimer(bot);
        bot.add(Box.createHorizontalGlue());
        saved = new JLabel("");
        saved.setForeground(Color.white);
        saved.setFont(new Font("Arial", Font.PLAIN, 18));
        bot.add(saved);
        frame.add(gui);

    }

    public void SetTimer(JPanel panel){

        //FORMATO BONITO DE TIMER ༼☯﹏☯༽

        JLabel time = new JLabel();
        time.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(time);
        time.setForeground(Color.white);

        //---------timer--------
        Timer timer = new Timer(1000, e -> {

            String sec2=String.valueOf(sec);
            if(sec<10) sec2="0"+sec2;

            String min2=String.valueOf(min);
            if(min<10) min2="0"+min2;

            String h2=String.valueOf(h);
            if(h<10) h2="0"+h2;

            time.setText(h2+":"+min2+":"+sec2);
            sec++;

            if(sec==60){
                min++;
                sec=0;
            }
            if(min==60){
                h++;
                min=0;
            }
        });

        timer.start();

        pause.addActionListener(e -> { //pantalla de pausa
            timer.stop();
            frame.getGlassPane().setVisible(true);
        });
        pause.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { //quita pantalla de pausa
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    frame.getGlassPane().setVisible(false);
                    timer.start();
                }
            }

        });
    }

    public JToolBar setToolBar() throws IOException {

        JToolBar tools = new JToolBar();
        tools.setBorder(new EmptyBorder(5, 0, 5, 0));
        tools.setFloatable(false);
        tools.setBackground(Color.decode("#1A0029"));
        //---------------PAUSE--------------- su listener esta en setTimer
        Image aux = ImageIO.read((ExtensionJPanel.class.getResource("/pause.png")));
        Icon pauseicon = new ImageIcon(aux);
        pause = new JButton(pauseicon);
        pause.setContentAreaFilled(false);
        pause.setBorderPainted(false);
        pause.setBorder(null);
        tools.add(pause);
        tools.addSeparator();
        //---------------HELP---------------
        Image aux2 = ImageIO.read((ExtensionJPanel.class.getResource("/help.png")));
        Icon helpicon = new ImageIcon(aux2);
        help=new JButton(helpicon);
        help.setContentAreaFilled(false);
        help.setBorderPainted(false);
        help.setBorder(null);
        tools.add(help);
        tools.addSeparator();
        helpListeners();
        //---------------DONE---------------
        Image aux3 = ImageIO.read((ExtensionJPanel.class.getResource("/done.png")));
        Icon doneicon = new ImageIcon(aux3);
        done = new JButton(doneicon);
        done.setContentAreaFilled(false);
        done.setBorderPainted(false);
        done.setBorder(null);
        tools.add(done);
        tools.addSeparator();
        doneListeners();

        //---------------GUARDAR--------------- estaria guay poner un texto abajo q ponga saved
        Image aux4 = ImageIO.read((ExtensionJPanel.class.getResource("/save.png")));
        Icon icon2 = new ImageIcon(aux4);
        JButton guardar = new JButton(icon2);
        guardar.setContentAreaFilled(false);
        guardar.setBorderPainted(false);
        guardar.setBorder(null);
        tools.add(guardar);

        guardar.addActionListener(e -> {
            try {
                guardarTablero(true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        });
        //---------------EXIT---------------
        tools.add(Box.createHorizontalGlue());
        Image aux5 = ImageIO.read((ExtensionJPanel.class.getResource("/exit.png")));
        Icon icon = new ImageIcon(aux5);
        exit = new JButton(icon);
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        exit.setBorder(null);
        tools.add(exit);
        exitListeners();

        return tools;
    }

    public void doneListeners(){
        done.addActionListener(e -> {
            try {
                guardarTablero(false);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            KakuroValidator kv = new KakuroValidator(k);
            if(kv.validate()==1){ //kakuro correcto
                try {
                    guardarTablero(true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                cd.GuardarKakuroPartida(parti);
                try {
                    cd.eliminarPartida(parti);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                if(galeria) { //partida desde galeria
                    try {
                        frame.dispose();
                        new VistaRecord(p,parti);
                    } catch (IOException | FontFormatException ioException) {
                        ioException.printStackTrace();
                    }
                }
                else{ //partida rapida
                    JOptionPane.showMessageDialog(null,
                            "Kakuro Completed");
                    VistaMenu itemloader;
                    try { //ir a menu
                        frame.dispose();
                        itemloader = new VistaMenu(p);
                        itemloader.setSize(700,700);
                        itemloader.setLocationRelativeTo(null); //centro pantalla
                        itemloader.setVisible(true);
                    } catch (IOException | FontFormatException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
            //errores
            else if(kv.validate()==2)JOptionPane.showMessageDialog(null,
                    "Some values are missing ");
            else JOptionPane.showMessageDialog(null,
                        "Wrong Values");
        });

    }

    public void exitListeners(){
        exit.addActionListener(e -> { //quieres guardar antes de irte?
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int response =  JOptionPane.showConfirmDialog (null, "Would you like to save?","Warning",dialogButton);
            if(response == JOptionPane.YES_OPTION){
                try {
                    guardarTablero(true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            VistaMenu itemloader;
            try { //ir a menu
                frame.dispose();
                itemloader = new VistaMenu(p);
                itemloader.setSize(700,700);
                itemloader.setLocationRelativeTo(null); //centro pantalla
                itemloader.setVisible(true);
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void helpListeners(){
        help.addActionListener(e -> {
            try {
                guardarTablero(false);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            CtrlHelpValidator hv = new CtrlHelpValidator(k);
            if(hv.validate()) {
                try {
                    kakurosolver ks = new kakurosolver(k);
                    String nombre = k.getNombre();
                    k.setDificultad("FACIL");
                    k = ks.help(k);
                    k.setNombre(nombre);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null,
                            "No solution for the combination of values you have chosen");
                }
                saved.setText("+2 min");
                min = min + 2;
                final Timer timer = new Timer(1000, null);
                timer.addActionListener((ke) -> {
                    saved.setText("");
                    timer.stop();
                });
                timer.start();
                updateKakuro();
            } else {
                JOptionPane.showMessageDialog(null,
                        "It looks like you have a repeated number or one of the sums is higher than it should be");
            }
        });
    }
    //funciones para general el tablero
    public void setTablero() throws BadLocationException {

        tableroCeldas = new JPanel[altura][anchura];
        initializeTablero();
        lastCelda = new JTextField();
    }

    public void guardarTablero(Boolean guardarFixero) throws IOException {

        //update kakuro antes de guardar
        for(int i=0; i<altura; i++) {
            for (int j = 0; j < anchura; j++) {
                if (j == 0) k.setCellValue(i,j,k.getCellValue(i,j));
                else {
                    String aux = k.getCellValue(i,j);
                    if (aux.equals("?")) {
                        String aux2 = Celdas[i * anchura + j].getText();
                        if(!aux2.equals("")) k.setCellValue(i,j,aux2); //si se ha canviado
                    }
                    try{
                        Integer.valueOf(aux); //si era un numero
                        String aux2 = Celdas[i * anchura + j].getText();
                        k.setCellValue(i,j,aux2);
                    } catch (NumberFormatException ignored) {

                    }
                }
            }
        }
        if(guardarFixero) {
            //para que el usuario vea que se a
            saved.setText("saved");
            final Timer timer = new Timer(1000, null);
            timer.addActionListener((e) -> {
                saved.setText("");
                timer.stop();
            });
            timer.start();

            //finalizamos partida activa previa

            if(cd.PartidaActiva(p)!=null && !cd.PartidaActiva(p).getK().getNombre().equals(k.getNombre())){
                cd.eliminarPartida(cd.PartidaActiva(p));
            }


            //Se asigna un nombre al Kakuro
            if (k.getNombre() == null) {
                Timestamp t = new Timestamp(System.currentTimeMillis());
              //  int time = (int) (Math.random() * (300 - 1 + 1) + 1);
                k.setNombre("KakuroRandom" + t.getTime());
            }
            //Creamos la partida
            parti = new Dominio.Partida(k, p, sec, min, h);
            //Guardamos la Partida
            cd.GuardarKakuroPartida(parti);
        }
    }

    public JPanel setSumCell(JLabel sumaFila, JLabel sumaColumna, int i, int j){
        int top=1; int left=1; int bot=1; int right=1;
        if(i>0 && k.isBlanca(i-1,j)) top=0;
        if(j>0 && k.isBlanca(i,j-1)) left=0;
        if(j<(anchura-1) && k.isBlanca(i,j+1)) right=0;
        if(i<(altura-1) && k.isBlanca(i+1,j)) bot=0;
        JPanel sum = new ExtensionJPanel(1);
        sum.setPreferredSize( new Dimension( 70, 70 ) );
        sum.setLayout(null);
        sum.setBorder(BorderFactory.createMatteBorder(
                top, left, bot, right, Color.white));
       // sum.setBorder(new EmptyBorder(5, 7, 5, 7) );
      //  sum.setLayout(new GridLayout(2, 1, 8, 8));
        sumaFila.setForeground(Color.WHITE);
        sumaFila.setFont(new Font("Arial", Font.PLAIN, 22));
        sumaFila.setBounds(30, 5, 30, 30);
        sumaColumna.setFont(new Font("Arial", Font.PLAIN, 22));
        sumaColumna.setBounds(5,30,30,30);
        sumaColumna.setForeground(Color.WHITE);
        sum.add(sumaFila);
        sum.add(sumaColumna);

        return sum;
    }

    public JPanel setWhiteCell(int i, int j){
        int top=1; int left=1; int bot=1; int right=1;
        if(i==0) top=0;
        if(j==0) left=0;
        if(i==altura-1) bot=0;
        if(j==anchura-1) right=0;
        JPanel white = new JPanel();
        white.setPreferredSize( new Dimension( 70, 70) );
        white.setBackground(Color.WHITE);
        white.setBorder(BorderFactory.createMatteBorder(
                top, left, bot, right, Color.black));
        white.setLayout(new BoxLayout(white, BoxLayout.PAGE_AXIS));
        return white;
    }

    public JPanel setBlackCell(int i, int j){
        int top=1; int left=1; int bot=1; int right=1;
        if(i>0 && k.isBlanca(i-1,j)) top=0;
        if(j>0 && k.isBlanca(i,j-1)) left=0;
        if(j<(anchura-1) && k.isBlanca(i,j+1)) right=0;
        if(i<(altura-1) && k.isBlanca(i+1,j)) bot=0;
        JPanel black = new JPanel();
        black.setBackground(Color.BLACK);
        black.setPreferredSize( new Dimension( 70, 70 ) );
        black.setBorder(BorderFactory.createMatteBorder(
                top, left, bot, right, Color.white));
        return black;
    }

    public JTextField setTextField(){
        JTextField text = new JTextField(1);
        text.setCaretColor(Color.decode("#C6B7E3"));
        text.setBorder(null);
        text.setEditable(true);
        text.setFont(new Font("Arial", Font.PLAIN, 22));
        text.setHorizontalAlignment(JTextField.CENTER);

        return text;
    }

    public void updateKakuro(){

        for(int i=0; i<altura; i++){
            for (int j = 0; j < anchura; j++) {
                if(Celdas[i*anchura+j]!=null && !k.getCellValue(i,j).equals("?") &&k.isBlanca(i,j)) {
                    if (!Celdas[i * anchura + j].getText().equals(k.getCellValue(i, j))) {
                        //para q el usuario vea q se a añadido un valor
                        Celdas[i * anchura + j].setText(k.getCellValue(i, j));
                        JTextField aux = Celdas[i * anchura + j];
                        final Timer timer = new Timer(1000, null);
                        aux.setCaretColor(Color.decode("#F090F3"));
                        aux.setBackground(Color.decode("#F090F3"));
                        timer.addActionListener((ke) -> {
                            aux.setCaretColor(Color.decode("#C6B7E3"));
                            aux.setBackground(Color.white);
                            timer.stop();

                        });
                        timer.start();
                    }
                }
            }
        }

    }

    public void textListeners(JTextField text){
        //este pone celda de otro color al clicar i texto bold
        text.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if (lastCelda!=null){
                    lastCelda.setFont(new Font("Arial", Font.PLAIN, 22));
                    lastCelda.setBackground(Color.white);
                }
                text.setBackground(Color.decode("#C6B7E3"));
                text.setFont(new Font("Arial", Font.BOLD, 24));
                lastCelda=text;

            }
        });
        //este pone rojo si intentas escribir una letras i hace que solo puedas escribir un numero
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                String value = text.getText();
                int l = value.length();
                if (ke.getKeyChar() >= '1' && ke.getKeyChar() <= '9' ) {
                    text.setCaretColor(Color.decode("#C6B7E3"));
                    text.setBackground(Color.decode("#C6B7E3"));
                    if(l==1) text.setText("");
                    text.setEditable(true);
                } else {
                    text.setEditable(false);
                    text.setBackground(Color.decode("#CB94446"));
                    text.setCaretColor(Color.decode("#CB94446"));
                    //timer para el color rojo
                    final Timer timer = new Timer(100, null);
                    timer.addActionListener((e) -> {
                        text.setCaretColor(Color.decode("#C6B7E3"));
                        text.setBackground(Color.decode("#C6B7E3"));
                        timer.stop();

                    });
                    timer.start();

                }
            }
        });
    }

    public void initializeTablero() throws BadLocationException {

        Celdas = new JTextField[(altura*anchura)];
        tablero = new JPanel(new GridLayout(altura, anchura));

        SimpleAttributeSet cell = new SimpleAttributeSet();
        StyleConstants.setAlignment(cell, 1);

        for(int i = 0; i < tableroCeldas.length; ++i) {
            for(int j = 0; j < tableroCeldas[i].length; ++j) {

                //-------sum cell-----------
                JLabel sumaFila = new JLabel("",SwingConstants.RIGHT);
                JLabel sumaColumna = new JLabel("");
                JPanel sum = setSumCell(sumaFila,sumaColumna,i,j);

                //-----white cell----
                JPanel white = setWhiteCell(i,j);

                //------black cell-----
                JPanel black = setBlackCell(i,j);
                //-----para que el usuario escriba
                JTextField text = setTextField();
                Celdas[i*anchura+j]=text;

                textListeners(text);

                white.add(text);
                int num =0;
                boolean isNumber = true;
                try {
                    num = Integer.parseInt(k.getCellValue(i,j));
                } catch (NumberFormatException e) {
                    isNumber=false;
                }
                if (k.isBlanca(i,j)) {
                    this.tableroCeldas[i][j] = white;
                    if(isNumber) text.setText(Integer.toString(num));
                } else {
                    if (k.isSuma(i,j)) {
                        String[] aux2 = Sum(i,j);
                        if(aux2[0]!=null) sumaFila.setText(aux2[0]);
                        else sumaFila.setText("");
                        if (aux2[1]!=null) sumaColumna.setText(aux2[1]);
                        else sumaColumna.setText("");
                        this.tableroCeldas[i][j] = sum;
                    } else {
                        this.tableroCeldas[i][j] = black;
                    }
                }
                this.tablero.add(this.tableroCeldas[i][j]);

            }
        }
    }

    //para mirar suma columna i suma fila
    public String[] Sum(int a, int b) {
        String[] res = {null, null};
        if (k.getCellValue(a,b).contains("C")) {
            res[1] = k.getCellValue(a,b).split("C")[1];
            if(res[1].contains("F")) {
                res = res[1].split("F");
                String aux2 = res[1];
                res[1] = res[0];
                res[0] = aux2;
            }
        }
        else if (k.getCellValue(a,b).contains("F")){
            res[0] = k.getCellValue(a,b).split("F")[1];
        }
        return res;
    }
}
