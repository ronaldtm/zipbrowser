package zipbrowser.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import zipbrowser.core.MountPoint;
import zipbrowser.core.MountPointEvent;
import zipbrowser.core.MountPointListener;
import zipbrowser.core.MountPointSettings;

/**
 * @author Tetsuo
 */
public abstract class MountPointSettingsPanel extends JPanel {
    private static final Logger log = Logger.getAnonymousLogger();
    private final ListSelectionModel mountPointsSelection = new DefaultListSelectionModel();
    private final JTextField identifierField = new JTextField();
    private final JTextField pathField = new JTextField();
    private final MountPointSettings mountPointSettings;
    private final JList mountPointsList;
    private final BaseAction saveAction = new BaseAction("Save", KeyEvent.VK_S) {
        private static final long serialVersionUID = 1L;
        @Override
        public void actionPerformed(ActionEvent evt) {
            saveSelectedMountPoint();
        }
    };
    private final BaseAction removeAction = new BaseAction("Remove", KeyEvent.VK_R) {
        private static final long serialVersionUID = 1L;
        @Override
        public void actionPerformed(ActionEvent evt) {
            removeSelectedMountPoint();
        }
    };
    private final BaseAction closeAction = new BaseAction("Close", KeyEvent.VK_C) {
        private static final long serialVersionUID = 1L;
        @Override
        public void actionPerformed(ActionEvent evt) {
            onClose();
        }
    };
    private final BaseAction browseAction = new BaseAction("Browse...", KeyEvent.VK_B) {
        private static final long serialVersionUID = 1L;
        @Override
        public void actionPerformed(ActionEvent evt) {
            browse();
        }
    };
    public MountPointSettingsPanel(final MountPointSettings mountPointSettings) {
        this.mountPointSettings = mountPointSettings;
        this.mountPointsList = new JList(new MountPointListModel());
        this.mountPointsList.setCellRenderer(new MountPointListCellRenderer());

        buildLayout();
        connectEvents();
    }
    abstract void onClose();
    private void saveSelectedMountPoint() {
        String id = identifierField.getText();
        String path = pathField.getText();
        if (id != null && !id.isEmpty() && path != null && !path.isEmpty()) {
            File dir = new File(path);
            String fullPath;
            try {
                fullPath = dir.getCanonicalPath();
            } catch (IOException ex) {
                log.log(Level.FINE, ex.getMessage(), ex);
                fullPath = dir.getAbsolutePath();
            }
            mountPointSettings.registerMountPoint(new MountPoint(id, fullPath));
        }
        mountPointsSelection.clearSelection();
    }
    private void removeSelectedMountPoint() {
        String id = identifierField.getText();
        if (mountPointSettings.containsMountPoint(id)) {
            mountPointSettings.unregisterMountPoint(id);
        }
        mountPointsSelection.clearSelection();
    }
    private void buildLayout() {
        JPanel pathFieldPanel = new JPanel(new BorderLayout());
        pathField.setEditable(false);
        pathFieldPanel.add(pathField, BorderLayout.CENTER);
        pathFieldPanel.add(new JButton(browseAction), BorderLayout.EAST);

        JPanel fieldsPanel = new JPanel(new SpringLayout());
        fieldsPanel.add(new JLabel("Identifier"));
        fieldsPanel.add(identifierField);
        fieldsPanel.add(new JLabel("Path"));
        fieldsPanel.add(pathFieldPanel);
        SpringUtilities.makeCompactGrid(fieldsPanel, 2, 2, 5, 0, 5, 5);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonsPanel.add(new JButton(saveAction));
        buttonsPanel.add(new JButton(removeAction));
        buttonsPanel.add(new JButton(closeAction));
        int maxWidth = 0;
        for (Component c : buttonsPanel.getComponents()) {
            maxWidth = Math.max(maxWidth, c.getPreferredSize().width);
        }
        for (Component c : buttonsPanel.getComponents()) {
            Dimension psize = c.getPreferredSize();
            psize.width = maxWidth;
            c.setPreferredSize(psize);
        }

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout(5, 5));
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.add(new JScrollPane(mountPointsList), BorderLayout.CENTER);
        this.add(formPanel, BorderLayout.SOUTH);
    }
    private void connectEvents() {

        mountPointsList.setSelectionModel(mountPointsSelection);
        mountPointsSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mountPointsSelection.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                ListSelectionModel model = (ListSelectionModel) evt.getSource();
                if (!evt.getValueIsAdjusting()) {
                    int index = model.getMaxSelectionIndex();
                    boolean hasSelection = (index != -1);
                    if (hasSelection) {
                        MountPoint mountPoint = (MountPoint) mountPointsList.getModel().getElementAt(index);
                        identifierField.setText(mountPoint.getId());
                        pathField.setText(mountPoint.getPath());
                    } else {
                        identifierField.setText("");
                        pathField.setText("");
                    }
                }
            }
        });
    }
    private void browse() {
        JFileChooser fc = new JFileChooser();
        if (!pathField.getText().isEmpty()) {
            fc.setCurrentDirectory(new File(pathField.getText()));
        }
        fc.setDialogTitle("Select mount point");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(false);
        fc.showOpenDialog(this);
        File file = fc.getSelectedFile();
        if (file != null) {
            try {
                pathField.setText(file.getCanonicalPath());
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }
    }
    private class MountPointListModel implements ListModel {
        private Map<ListDataListener, MountPointListener> listenersMap = new HashMap<ListDataListener, MountPointListener>();
        @Override
        public Object getElementAt(int index) {
            return new ArrayList<MountPoint>(mountPointSettings.getMountPoints()).get(index);
        }
        @Override
        public int getSize() {
            return mountPointSettings.getMountPoints().size();
        }
        @Override
        public void addListDataListener(final ListDataListener listDataListener) {
            MountPointListener mountPointListener = new MountPointListener() {
                @Override
                public void mountPointUnregistered(MountPointEvent evt) {
                    SwingUtilities.invokeLater(new ContentsChangedCommand());
                }
                @Override
                public void mountPointRegistered(MountPointEvent evt) {
                    SwingUtilities.invokeLater(new ContentsChangedCommand());
                }
                class ContentsChangedCommand implements Runnable {
                    @Override
                    public void run() {
                        listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
                    }
                }
            };
            listenersMap.put(listDataListener, mountPointListener);
            mountPointSettings.addMountPointListener(mountPointListener);
        }
        @Override
        public void removeListDataListener(ListDataListener listDataListener) {
            mountPointSettings.removeMountPointListener(listenersMap.remove(listDataListener));
        }
    }
    private static class MountPointListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            MountPoint mountPoint = (MountPoint) value;

            String text = (mountPoint != null) ? mountPoint.getId() : "";
            super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);

            setToolTipText((mountPoint != null) ? mountPoint.getPath() : "");
            return this;
        }
    }
}
