package dev.nachwahl.bteginstaller;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LoadingForm{
    private JProgressBar progressBar1;
    public JPanel LoadingForm;
    private JLabel installiereLabel;
    private InstallUtil installUtil;

    public LoadingForm(InstallUtil installUtil, JFrame frame, JDialog loading, String modpackVersion) {

        this.installUtil = installUtil;
        InstallTask installTask = new InstallTask(installUtil,progressBar1,installiereLabel,loading, modpackVersion);
        SwingUtilities.invokeLater(() -> {
            try {
                installTask.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                installTask.cancel(true);
                super.windowClosing(e);
            }
        });


    }

}
