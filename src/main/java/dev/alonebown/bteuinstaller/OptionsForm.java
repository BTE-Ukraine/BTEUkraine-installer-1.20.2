package dev.alonebown.bteuinstaller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsForm {
    public JPanel OptionFormPanel;
    private JCheckBox commandMacrosCheckBox;
    private JButton speichernButton;

    private JCheckBox replayModCheckBox;
    private JCheckBox panoramicaModCheckBox;
    private JDialog dialog;

    public OptionsForm(JDialog dialog,final InstallUtil installUtil) {
        this.dialog = dialog;
        commandMacrosCheckBox.setSelected(installUtil.isOptionalModEnabled(OptionalMod.COMMAND_MACROS));
        replayModCheckBox.setSelected(installUtil.isOptionalModEnabled(OptionalMod.REPLAY_MOD));
        panoramicaModCheckBox.setSelected(installUtil.isOptionalModEnabled(OptionalMod.PANORAMICA));
        commandMacrosCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                installUtil.setOptionalMod(OptionalMod.COMMAND_MACROS,commandMacrosCheckBox.isSelected());
            }
        });

        replayModCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                installUtil.setOptionalMod(OptionalMod.REPLAY_MOD,replayModCheckBox.isSelected());
            }
        });
        panoramicaModCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                installUtil.setOptionalMod(OptionalMod.PANORAMICA,panoramicaModCheckBox.isSelected());
            }
        });

        speichernButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.dispose();
            }
        });
    }
}
