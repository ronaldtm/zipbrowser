package zipbrowser.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import zipbrowser.server.ServerSettings;

/**
 * @author Tetsuo
 */
public abstract class AdvancedSettingsPanel extends JPanel {
    private final BaseAction closeAction = new BaseAction("Close", KeyEvent.VK_C) {
        private static final long serialVersionUID = 1L;
        @Override
        public void actionPerformed(ActionEvent evt) {
            onClose();
        }
    };
    private final JCheckBox listenLocalhostOnlyField = new JCheckBox();
    private final ServerSettings serverSettings;
    public AdvancedSettingsPanel(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        buildLayout();
        connectEvents();
    }
    abstract void onClose();
    private void buildLayout() {
        JPanel fieldsPanel = new JPanel(new SpringLayout());
        fieldsPanel.add(new JLabel("Listen to localhost only"));
        fieldsPanel.add(listenLocalhostOnlyField);
        SpringUtilities.makeCompactGrid(fieldsPanel, 1, 2, 5, 0, 5, 5);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonsPanel.add(new JButton(closeAction));

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(fieldsPanel, BorderLayout.NORTH);
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);

        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout(5, 5));
        this.add(formPanel, BorderLayout.CENTER);
    }
    private void connectEvents() {
        listenLocalhostOnlyField.setSelected(serverSettings.isListenLocalhostOnly());
        listenLocalhostOnlyField.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                serverSettings.setListenLocalhostOnly(listenLocalhostOnlyField.isSelected());
            }
        });
    }
}
