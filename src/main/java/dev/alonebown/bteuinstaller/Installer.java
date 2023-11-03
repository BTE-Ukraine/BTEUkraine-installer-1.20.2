package dev.alonebown.bteuinstaller;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Installer {
    public static void main(String[] args) {

        FlatOneDarkIJTheme.install(new FlatOneDarkIJTheme());
        final JFrame frame = new JFrame("BTE Ukraine Installer");
        try {
            frame.setIconImage(ImageIO.read(MainForm.class.getResourceAsStream("/logo.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InstallUtil installUtil = new InstallUtil(frame);
        frame.setContentPane(new MainForm(frame, installUtil).MainFormPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JMenuBar mb = new JMenuBar();
        JMenu about = new JMenu("About");
        about.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                Desktop desktop = java.awt.Desktop.getDesktop();
                try {

                    URI uri = new URI(
                            "https://gist.github.com/AloneBown/73c8906718cc3cd563102b283b6e6dac");
                    desktop.browse(uri);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


            }

            public void menuDeselected(MenuEvent e) {

            }

            public void menuCanceled(MenuEvent e) {

            }
        });
        mb.add(about);

        frame.setJMenuBar(mb);



        frame.setVisible(true);
    }
}

