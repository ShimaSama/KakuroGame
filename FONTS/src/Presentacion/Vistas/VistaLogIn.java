package Presentacion.Vistas;

import javax.swing.*;
import javax.swing.JOptionPane;
import Dominio.ControladorDeDominio;
import Dominio.Perfil;

import java.awt.*;
import java.io.IOException;

public class VistaLogIn extends JFrame {
    private JPanel panel1;
    private JPanel basepanel;
    private JTextField Username;
    private JPasswordField Password;
    private JButton loginButton;
    private JButton newUserButton;
    private Perfil p;
    private static ControladorDeDominio cd;


    public VistaLogIn(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);
        this.pack();

        Listeners();

    }

    public void Listeners(){

        newUserButton.addActionListener(e -> {

            VistaRegister itemloader=new VistaRegister();
            itemloader.setSize(700,700);
            itemloader.setVisible(true);
            itemloader.setLocationRelativeTo(null); //centro pantalla
            dispose();

        });
        loginButton.addActionListener(e -> {

            if(Username.getText().equals(""))JOptionPane.showMessageDialog(null,
                    "Please introduce a username");
            else if(Password.getText().equals(""))JOptionPane.showMessageDialog(null,
                    "Please introduce a password");
            else {
                cd =  new ControladorDeDominio();
                p = new Perfil(Username.getText(),Password.getText());
                String error = cd.loginPerfil("Perfiles/Perfiles.txt", p);
                System.out.println(error);
                if(!error.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            error);
                }
                else{ //cargar menu


                    VistaMenu itemloader= null;
                    try {
                        itemloader = new VistaMenu(p);
                        itemloader.setSize(700,700);
                        itemloader.setLocationRelativeTo(null); //centro pantalla
                        itemloader.setVisible(true);
                        dispose();
                    } catch (IOException | FontFormatException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    public void createUIComponents() {
        // TODO: place custom component creation code here
        basepanel = new JPanel();
        basepanel.setSize(500,500);
    }

    public static void main (String[] args){
        JFrame frame = new VistaLogIn();
        frame.setSize(700,700);
        frame.setLocationRelativeTo(null); //centro pantalla
        frame.setVisible(true);

    }
}
