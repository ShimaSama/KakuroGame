package Presentacion.Vistas;

import javax.swing.*;
import Dominio.ControladorDeDominio;
import Dominio.Perfil;

import java.awt.*;
import java.io.IOException;

public class VistaRegister extends JFrame{

    private JPanel basePanel;
    private JTextField Username;
    private JPasswordField Password;
    private JPasswordField RepeatPassword;
    private JButton registerButton;
    private JButton backButton;
    private static ControladorDeDominio cd;
    private Perfil p;

    public VistaRegister(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(basePanel);
        this.pack();
        listeners();

    }
    public void listeners(){
        backButton.addActionListener(e -> {
            //cargar vista de login
            VistaLogIn itemloader=new VistaLogIn();
            itemloader.setSize(700,700);
            itemloader.setLocationRelativeTo(null); //centro pantalla
            itemloader.setVisible(true);
            dispose();
        });
        registerButton.addActionListener(e -> {

            //algunos errores
            if(Username.getText().equals(""))JOptionPane.showMessageDialog(null,
                    "Please introduce a username");
            else if(Password.getText().equals(""))JOptionPane.showMessageDialog(null,
                    "Please introduce a password");
            else if(!Password.getText().equals(RepeatPassword.getText()))JOptionPane.showMessageDialog(null,
                    "Password doesn't match");

            else { //mirar si el user existe o no
                cd = new ControladorDeDominio();
                String Perfilname = Username.getText();
                String pwd= Password.getText();
                p = new Perfil(Perfilname,pwd);
                String error = cd.registerPerfil("Perfiles/Perfiles.txt","Perfiles",  p);

                if (error.equals("")) { //si no hay error cargamos menu
                    VistaMenu itemloader;
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
                else{ //muestra error
                    JOptionPane.showMessageDialog(null,
                            error);
                }
            }
        });
    }

    
}
