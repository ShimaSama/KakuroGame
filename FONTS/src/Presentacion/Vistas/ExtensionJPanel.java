package Presentacion.Vistas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ExtensionJPanel extends JPanel {

    private Image image;

    public ExtensionJPanel(int i) {

       if(i==1) { //para crear un panel con background de suma
           try {


               image = ImageIO.read((ExtensionJPanel.class.getResource("/sum2.png")));
               image = image.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
           } catch (IOException ex) {
               ex.printStackTrace();
           }
       }
       else if(i==2){ //para crear panel con galeria general de fondo
           try {

               image = ImageIO.read((ExtensionJPanel.class.getResource("/general.png")));
               image = image.getScaledInstance(700, 700, Image.SCALE_DEFAULT);
           } catch (IOException ex) {
               ex.printStackTrace();
           }
       }
       else{ //para crear un panel con galeria local de fondo
            try {

                image = ImageIO.read((ExtensionJPanel.class.getResource("/local.png")));
                image = image.getScaledInstance(700, 700, Image.SCALE_DEFAULT);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
