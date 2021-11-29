package Presentacion.Vistas;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

/**
 * Glass pane used to blur the content of the window.
 */
@SuppressWarnings("serial")
class GlassPane extends JPanel implements FocusListener {

    private int x;
    private int y;
    /**
     * Creates new GlassPane.
     */
    public GlassPane(int x, int y) throws IOException, FontFormatException {

        this.x = x;
        this.y = y;
        // (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ COOL FONT ✧ﾟ･: *ヽ(◕ヮ◕ヽ)
        InputStream is = GlassPane.class.getResourceAsStream("/Presentacion/FONTS/ghostclan.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);

        configure();
        setJLabels(font);
    }
    private void configure(){

        addMouseListener(new MouseAdapter() {});
        addMouseMotionListener(new MouseAdapter() {});
        addFocusListener(this);
        setOpaque(false);
        setFocusable(true);
        setLayout(null);
        setBackground(new Color(10, 0, 30, 240));
    }

    private void setJLabels(Font font){

        Font continueFont = font.deriveFont(x/20f);
        Font pauseFont = font.deriveFont(x/10f);

        JLabel paused = new JLabel("PAUSED",JLabel.CENTER);
        paused.setFont(pauseFont);
        paused.setForeground(Color.white);
        paused.setBounds(0, 0, x, y-y/10);
        add(paused);

        JLabel cont = new JLabel("PRESS ENTER TO CONTINUE",JLabel.CENTER);
        cont.setFont(continueFont);
        cont.setForeground(Color.white);
        cont.setBounds(0, y/10, x, y-y/10);
        add(cont);

    }


    @Override
    public final void setVisible(boolean v) {
        // Make sure we grab the focus so that key events don't go astray.
        if (v) {
            requestFocus();
        }
        super.setVisible(v);
    }

    // Once we have focus, keep it if we're visible
    @Override
    public final void focusLost(FocusEvent fe) {
        if (isVisible()) {
            requestFocus();
        }
    }

    @Override
    public final void paint(Graphics g) {
        final Color old = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, getSize().width, getSize().height);
        g.setColor(old);
        super.paint(g);
    }

    @Override
    public void focusGained(FocusEvent fe) {
        // nothing to do
    }


}