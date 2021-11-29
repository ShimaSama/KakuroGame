package Presentacion.Vistas;

import Dominio.CtrlCreateValidator;
import Dominio.Kakuro;
import Dominio.Perfil;
import Dominio.kakurosolver;
import Persistencia.GestionGaleria;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

public class VistaCreacionKakuro {

    private static Kakuro k;
    private JFrame frame;
    private int n;
    private int m;
    private JPanel gui;
    private JButton done;
    private CardLayout panels;
    private JPanel cardPanel;
    private JTextField[] Celdas;
    private String help;
    private JPanel[][] tableroCeldas;
    private JPanel tablero;
    private JTextField lastCell;
    private Perfil p;


    public VistaCreacionKakuro(int n, int m, Perfil p) throws IOException, FontFormatException {

        this.n=n;
        this.m=m;
        this.p=p;
        configureJFrame();

        createKakuro();

        //crear tablero
        setTablero();

        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);

        configureCardPanel();
        configureGUI(font);

        help(font);

    }

    public void help(Font font){

        Font sizedFont = font.deriveFont(15f);
        JLabel help = new JLabel("Select a cell and press h for help");
        help.setFont(sizedFont);
        help.setFont(new Font("Arial", Font.PLAIN, 15)); //de momento esta queda mejor
        gui.add(help,BorderLayout.PAGE_END);
        help.setForeground(Color.white);
    }

    public void configureCardPanel(){
        panels= new CardLayout();  //card layout para las actualizacions
        cardPanel = new JPanel();
        cardPanel.setLayout(panels);
        cardPanel.add(tablero);
        cardPanel.setVisible(true);
    }

    public void configureJFrame (){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(70*m+40,70*n+80);
        frame.setResizable(false);
        frame.setBackground(Color.decode("#1A0029"));
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);
    }

    public void createKakuro(){
        k = new Kakuro(n,m,"FACIL");
        for(int i=0; i<n; i++) {
            for(int j=0; j<m; j++) {
                if(i==0 ||j==0)  k.createCell(i,j,"*");
                else k.createCell(i,j,"?");
            }
        }
    }

    public void configureGUI(Font font) throws IOException {
        gui = new JPanel(new BorderLayout(3, 3));
        gui.add(cardPanel);
        gui.setBorder(new EmptyBorder(5, 15, 15, 15));
        JToolBar tools = setToolBar(font);
        gui.add(tools, BorderLayout.PAGE_START);
        gui.setBackground(Color.decode("#1A0029"));
        frame.add(gui);
    }

    public void getRowColumn(){
        int row=0;
        int column=0;
        Boolean errors = false;
        //esto es para poder recoger datos con el popup ヽ(͡◕ ͜ʖ ͡◕)ﾉ
        JTextArea textArea = new JTextArea();
        textArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.requestFocus();
        textArea.requestFocusInWindow();
        scrollPane.setPreferredSize(new Dimension(30, 30));
        JOptionPane.showMessageDialog(
                null, scrollPane,
                "Row number", JOptionPane.PLAIN_MESSAGE);
        String info = textArea.getText(); //cojemos datos del usuario

        if(!info.equals("") ) {
            try {
                row = Integer.parseInt(info); //mirar si es una letra

            } catch (NumberFormatException e) {
                errors = true;
            }
        }
        else errors=true;

        JOptionPane.showMessageDialog(
                null, scrollPane,
                "Column number", JOptionPane.PLAIN_MESSAGE);
        info = textArea.getText();

        //mirar q haya puesto algo
        if(!info.equals("") ) {
            try {
                column= Integer.parseInt(info); //mirar si es una letra

            } catch (NumberFormatException e) {
                errors = true;
            }
        }
        else errors=true;

       if(!errors) k.createCell(row,column,"?");
       if(errors) JOptionPane.showMessageDialog(null,
               "Incorrect values");
    }

    public void updateJPanel(){
        setTablero(); //volver a hacer el tablero para que nos haga los nuevos panels con suma
        cardPanel.add(tablero);
        panels.last(cardPanel);
    }

    public void setTablero(){

        int altura = k.getAltura();
        int anchura = k.getAnchura();
        lastCell = new JTextField();  //esto es para volver a dejar el color de la celda como estaba (≖ᴗ≖✿)
        tableroCeldas = new JPanel[altura][anchura];
        tablero = new JPanel(new GridLayout(altura, anchura));
        setHelp();
        initializeTablero();
    }

    public void setHelp(){
        help = "**************************************";
        help = help + "\r\n";
        help = help + "*------------------HELP-----------------*";
        help = help + "\r\n";
        help = help + "*------- Black Cell - Press b -------*";
        help = help + "\r\n";
        help = help + "*------- White Cell - Press w -------*";
        help = help + "\r\n";
        help = help + "*-------- Sum Cell - Press s --------*";
        help = help + "\r\n";
        help = help + "* s just works if the cell is black *";
        help = help + "\r\n";
        help = help + "*-------------------------------------------*";
        help = help + "\r\n";
        help = help + "**************************************";

    }

    public void setKakuro(){
        int altura = k.getAltura();
        int anchura = k.getAnchura();
        for (int i=0; i<altura; i++){
            for(int j=0; j<anchura; j++){
                String aux = Celdas[i * anchura + j].getText();
                Color back = Celdas[i * anchura + j].getBackground();
                if(aux.equals("") && back==Color.white) k.createCell(i,j,"?");
                else if (aux.equals("") && back==Color.black) k.createCell(i,j,"*");
                else if(!aux.equals("")) k.createCell(i,j,aux);
                else k.createCell(i,j,k.getCellValue(i,j));
            }
        }
    }

    public String[] Sum(int a, int b) { //esto es para mirar los valores de suma
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

    public JPanel setSumCell(JLabel label, JLabel label2){

        JPanel suma = new ExtensionJPanel(1);
        suma.setPreferredSize( new Dimension( 70, 70 ) );
        suma.setBorder(new EmptyBorder(5, 7, 5, 7) );
        suma.setLayout(new GridLayout(2, 1, 8, 8));

        label.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);

        label.setFont(new Font("Arial", Font.PLAIN, 22));
        label2.setFont(new Font("Arial", Font.PLAIN, 22));

        suma.add(label);
        suma.add(label2);

        return suma;
    }

    public JPanel setCelda(JTextField text, int i, int j){

        JPanel celda = new JPanel();
        celda.setPreferredSize( new Dimension( 70, 70) );
        celda.setBackground(Color.WHITE);
        celda.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.black));
        celda.setLayout(new BoxLayout(celda, BoxLayout.PAGE_AXIS));
        //para poder selecionar la celda!! JPanel no es seleccionable :C
        text.setEditable(false);
        //hacer texto bonito para escribir suma!!
        text.setHorizontalAlignment(JTextField.CENTER);
        text.setFont(new Font("Arial", Font.PLAIN, 15));
        text.setForeground(Color.white);

        Celdas[i*k.getAnchura()+j]=text;
        celda.add(text);
        return celda;
    }

    public void textListeners(JTextField text){
        //q sea rojo cuando lo seleccionas
        text.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if (lastCell!=null){
                    if(lastCell.getBackground()==Color.black) lastCell.setBorder(BorderFactory.createMatteBorder(
                            1, 1, 1, 1, Color.white));
                    else lastCell.setBorder(BorderFactory.createMatteBorder(
                            1, 1, 1, 1, Color.black));

                }
                text.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
                lastCell=text;
            }
        });
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyChar() == 'b' ) { //cell -> black
                    text.setBackground(Color.black);
                }

                else if(ke.getKeyChar() == 'w' ){ //cell -> white
                    text.setBackground(Color.white);

                }
                if(ke.getKeyChar() == 'h' ){ //help
                    JOptionPane.showMessageDialog(null,
                            help);
                }
                //suma
                if(ke.getKeyChar() == 's' && text.getBackground()==Color.black ){
                    JTextArea textArea = new JTextArea();
                    textArea.setEditable(true);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.requestFocus();
                    textArea.requestFocusInWindow();
                    scrollPane.setPreferredSize(new Dimension(30, 30));
                    //cojemos la suma de columna q quiere
                    JOptionPane.showMessageDialog(
                            null, scrollPane,
                            "Sum of column", JOptionPane.PLAIN_MESSAGE);
                    String info = textArea.getText();
                    boolean sum=false;
                    if(!info.equals("") && !info.equals("0")) {
                        try{
                            Integer.parseInt(info); //mirar si es una letra
                            sum=true;
                            text.setText("C"+info);
                        } catch (NumberFormatException ignored) {

                        }
                    }
                    //cojemos suma de fila q quiere
                    JOptionPane.showMessageDialog(
                            null, scrollPane,
                            "Sum of row", JOptionPane.PLAIN_MESSAGE);
                    String info2 = textArea.getText();

                    if(!info2.equals("") && !info2.equals("0")) {

                        try{
                            Integer.parseInt(info2); //mirar si es una letra
                            sum=true;
                            text.setText(text.getText()+"F"+info2);
                        } catch (NumberFormatException ignored) {

                        }
                    }
                    if(sum){
                        setKakuro();
                        updateJPanel(); //asi nos salen las sumas con una celda bonita
                    }
                }
            }
        });
    }

    public void initializeTablero(){

        int altura = k.getAltura();
        int anchura = k.getAnchura();
        Celdas = new JTextField[(altura*anchura)]; //para acceder a todas las casillas
        SimpleAttributeSet cell = new SimpleAttributeSet();
        StyleConstants.setAlignment(cell, 1);

        for(int i = 0; i < altura; ++i) {
            for(int j = 0; j < anchura; ++j) {

                //celda de suma
                JLabel label = new JLabel("",SwingConstants.RIGHT); //suma fila
                JLabel label2 = new JLabel(""); //suma columna
                JPanel suma = setSumCell(label, label2);

                //celda
                JTextField text = new JTextField(1);
                JPanel celda = setCelda(text,i,j);
                textListeners(text);


                String aux = k.getCellValue(i,j);
                if (aux.equals("?")) { //celda blanca
                    text.setBackground(Color.white);
                    this.tableroCeldas[i][j] = celda;
                }
                else if (aux.equals("*")) { //celda negra
                    text.setBackground(Color.black);
                    this.tableroCeldas[i][j] = celda;
                }
                else { //celda de suma
                    String[] aux2 = Sum(i,j);
                    if(aux2[0]!=null) label.setText(aux2[0]);
                    else label.setText("");if (aux2[1]!=null) label2.setText(aux2[1]);
                    else label2.setText("");
                    this.tableroCeldas[i][j] = suma;
                }
                this.tablero.add(this.tableroCeldas[i][j]);
            }
        }
    }

    public void doneListener(){
        done.addActionListener(e -> { //cargar galeria
            setKakuro(); //actualizar kakuro
            CtrlCreateValidator val = new CtrlCreateValidator(k); //mirar si hay algun fallo en el formato
            if (!val.Validate()) JOptionPane.showMessageDialog(null,
                    "Wrong Kakuro Format");
            else {
              kakurosolver ks = new kakurosolver(k);
              if(ks.Solve()==0)JOptionPane.showMessageDialog(null,
                      "This Kakuro has no solution");
              else{
                  //esto es para poder recoger datos con el popup ヽ(͡◕ ͜ʖ ͡◕)ﾉ
                  //el kakuro es valido y obtenemos el nombre del kakuro
                  JTextArea textArea = new JTextArea();
                  textArea.setEditable(true);
                  JScrollPane scrollPane = new JScrollPane(textArea);
                  scrollPane.requestFocus();
                  textArea.requestFocusInWindow();
                  scrollPane.setPreferredSize(new Dimension(30, 30));
                  JOptionPane.showMessageDialog(
                          null, scrollPane,
                          "Please introduce the name of your kakuro", JOptionPane.PLAIN_MESSAGE);
                  String name = textArea.getText(); //cojemos datos del usuario
                  if(!name.equals("")){
                      GestionGaleria GG = new GestionGaleria();
                      k.setNombre(name);
                      GG.guardarKakuro(p,k,"FACIL");

                      //redireccionar a la galeria
                      frame.dispose();
                      try {
                          new VistaGaleria(p);
                      } catch (IOException | FontFormatException ioException) {
                          ioException.printStackTrace();
                      }
                  }
              }
            }
        });
    }

    public JToolBar setToolBar(Font font) throws IOException {

        Font sizedFont = font.deriveFont(17f);
        //para los botones de arriba (❍ᴥ❍ʋ)
        JToolBar tools = new JToolBar();
        tools.setBorder(new EmptyBorder(5, 0, 5, 0));
        tools.setFloatable(false);
        tools.setBackground(Color.decode("#1A0029"));
        //---------------DELETE SUM--------------
        JButton sum=new JButton("DELETE SUM");
        sum.setFont(sizedFont);
        tools.add(sum); //
        tools.addSeparator();
        sum.addActionListener(e -> {
            getRowColumn(); //que suma quiere borrar?
            updateJPanel();
        });
        //---------------DONE--------------
        Image aux = ImageIO.read((ExtensionJPanel.class.getResource("/done.png")));
        Icon doneicon = new ImageIcon(aux);
        done = new JButton(doneicon);
        done.setContentAreaFilled(false);
        done.setBorderPainted(false);
        done.setBorder(null);
        tools.add(done); // TODO se a de validar, mirar si tiene solucion i ademas guardar i le pone un nombre
        tools.addSeparator();
        doneListener();
        //---------------EXIT--------------
        tools.add(Box.createHorizontalGlue());
        Image aux2 = ImageIO.read((ExtensionJPanel.class.getResource("/exit.png")));
        Icon icon = new ImageIcon(aux2);
        JButton exit = new JButton(icon);
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        tools.add(exit);
        exit.addActionListener(e -> { //cargar galeria
           frame.dispose();
            try {
                new VistaGaleria(p);
            } catch (IOException | FontFormatException ioException) {
                ioException.printStackTrace();
            }
        });
        return tools;
        }



    }

