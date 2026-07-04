package org.c3lang.intellij.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import org.c3lang.intellij.C3CompilerDetector;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class C3SettingsComponent
{
    private final JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
    private final DefaultListModel<C3SettingsState.CompilerProfile> compilerListModel = new DefaultListModel<>();
    private final JBList<C3SettingsState.CompilerProfile> compilerList = new JBList<>(compilerListModel);
    private final JTextField nameField = new JTextField();
    private final TextFieldWithBrowseButton binaryPathField = new TextFieldWithBrowseButton();
    private final JTextField versionField = new JTextField();
    private final TextFieldWithBrowseButton stdlibPathField = new TextFieldWithBrowseButton();
    private final Timer binaryDetectionTimer = new Timer(800, event -> detectCompilerDetailsIfNeeded());

    private boolean loadingProfile;
    private String lastLoadedBinaryPath = "";

    public C3SettingsComponent()
    {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        binaryDetectionTimer.setRepeats(false);

        compilerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        compilerList.setEmptyText("No compilers configured");
        compilerList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting())
            {
                loadSelectedProfile();
            }
        });

        JComponent compilerListPanel = ToolbarDecorator.createDecorator(compilerList)
            .setAddAction(button -> addCompilerProfile())
            .setRemoveAction(button -> removeSelectedCompilerProfile())
            .disableUpDownActions()
            .createPanel();

        versionField.setEditable(false);
        versionField.setForeground(JBColor.GRAY);
        binaryPathField.addBrowseFolderListener(new TextBrowseFolderListener(createCompilerDescriptor()));
        stdlibPathField.addBrowseFolderListener(new TextBrowseFolderListener(
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Select C3 Standard Library Directory")
        ));

        DocumentListener fieldListener = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                updateSelectedProfile();
                scheduleBinaryDetection(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                updateSelectedProfile();
                scheduleBinaryDetection(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                updateSelectedProfile();
                scheduleBinaryDetection(e);
            }
        };
        nameField.getDocument().addDocumentListener(fieldListener);
        binaryPathField.getTextField().getDocument().addDocumentListener(fieldListener);
        stdlibPathField.getTextField().getDocument().addDocumentListener(fieldListener);

        binaryPathField.getTextField().addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                detectCompilerDetailsIfNeeded();
            }
        });

        mainPanel.add(compilerListPanel, BorderLayout.WEST);
        mainPanel.add(createDetailsPanel(), BorderLayout.CENTER);
    }

    public JPanel getMainPanel()
    {
        return mainPanel;
    }

    public @NotNull JComponent getPreferredFocusedComponent()
    {
        return compilerList;
    }

    public @NotNull List<C3SettingsState.CompilerProfile> getCompilerProfiles()
    {
        updateSelectedProfile();
        ArrayList<C3SettingsState.CompilerProfile> profiles = new ArrayList<>();
        for (int i = 0; i < compilerListModel.size(); i++)
        {
            profiles.add(copyProfile(compilerListModel.get(i)));
        }
        return profiles;
    }

    public void setCompilerProfiles(@NotNull List<C3SettingsState.CompilerProfile> profiles)
    {
        loadingProfile = true;
        compilerListModel.clear();
        for (C3SettingsState.CompilerProfile profile : profiles)
        {
            compilerListModel.addElement(copyProfile(profile));
        }
        loadingProfile = false;

        if (compilerListModel.isEmpty())
        {
            clearDetails();
            return;
        }

        compilerList.setSelectedIndex(0);
        loadSelectedProfile();
    }

    public void commitEditing()
    {
        updateSelectedProfile();
        detectCompilerDetailsIfNeeded();
    }

    private @NotNull JPanel createDetailsPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insets(0, 0, 8, 8);

        addLabeledField(panel, gbc, 0, "Name:", nameField);
        addLabeledField(panel, gbc, 1, "Binary location:", binaryPathField);
        addLabeledField(panel, gbc, 2, "Version:", versionField);
        addLabeledField(panel, gbc, 3, "Stdlib location:", stdlibPathField);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalGlue(), gbc);
        return panel;
    }

    private static void addLabeledField(
            @NotNull JPanel panel,
            @NotNull GridBagConstraints gbc,
            int row,
            @NotNull String label,
            @NotNull JComponent component)
    {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private static @NotNull FileChooserDescriptor createCompilerDescriptor()
    {
        return new FileChooserDescriptor(true, false, false, false, false, false)
            .withTitle("Select C3 Compiler")
            .withFileFilter((VirtualFile file) -> !file.isDirectory() && Files.isExecutable(file.toNioPath()));
    }

    private void addCompilerProfile()
    {
        C3SettingsState.CompilerProfile profile = new C3SettingsState.CompilerProfile(
            nextCompilerName(),
            "",
            "",
            ""
        );
        compilerListModel.addElement(profile);
        compilerList.setSelectedIndex(compilerListModel.size() - 1);
        nameField.requestFocusInWindow();
        nameField.selectAll();
    }

    private void removeSelectedCompilerProfile()
    {
        int selectedIndex = compilerList.getSelectedIndex();
        if (selectedIndex < 0) return;

        compilerListModel.remove(selectedIndex);
        if (compilerListModel.isEmpty())
        {
            clearDetails();
            return;
        }

        compilerList.setSelectedIndex(Math.min(selectedIndex, compilerListModel.size() - 1));
    }

    private void loadSelectedProfile()
    {
        C3SettingsState.CompilerProfile profile = compilerList.getSelectedValue();
        if (profile == null)
        {
            clearDetails();
            return;
        }

        loadingProfile = true;
        nameField.setText(nullToEmpty(profile.name));
        binaryPathField.setText(nullToEmpty(profile.binaryPath));
        versionField.setText(nullToEmpty(profile.version));
        stdlibPathField.setText(nullToEmpty(profile.stdlibPath));
        lastLoadedBinaryPath = binaryPathField.getText();
        loadingProfile = false;
    }

    private void clearDetails()
    {
        loadingProfile = true;
        nameField.setText("");
        binaryPathField.setText("");
        versionField.setText("");
        stdlibPathField.setText("");
        lastLoadedBinaryPath = "";
        loadingProfile = false;
    }

    private void updateSelectedProfile()
    {
        if (loadingProfile) return;

        int selectedIndex = compilerList.getSelectedIndex();
        if (selectedIndex < 0) return;

        C3SettingsState.CompilerProfile profile = compilerListModel.get(selectedIndex);
        profile.name = nameField.getText().trim();
        profile.binaryPath = binaryPathField.getText().trim();
        profile.version = versionField.getText().trim();
        profile.stdlibPath = stdlibPathField.getText().trim();
        compilerList.repaint();
    }

    private void detectCompilerDetailsIfNeeded()
    {
        if (loadingProfile) return;

        String binaryPath = binaryPathField.getText().trim();
        if (binaryPath.isBlank()) return;
        if (!isLikelyRunnableCompiler(binaryPath)) return;
        boolean binaryChanged = !binaryPath.equals(lastLoadedBinaryPath);
        if (!binaryChanged && !versionField.getText().isBlank() && !stdlibPathField.getText().isBlank()) return;

        C3CompilerDetector.DetectionResult result = C3CompilerDetector.detect(binaryPath);
        if (!result.hasAnyValue()) return;

        loadingProfile = true;
        versionField.setText(result.versionOr(versionField.getText()));
        stdlibPathField.setText(result.stdlibPathOr(stdlibPathField.getText()));
        lastLoadedBinaryPath = binaryPath;
        loadingProfile = false;
        updateSelectedProfile();
    }

    private void scheduleBinaryDetection(@NotNull DocumentEvent event)
    {
        if (loadingProfile) return;
        if (event.getDocument() != binaryPathField.getTextField().getDocument()) return;
        binaryDetectionTimer.restart();
    }

    private static boolean isLikelyRunnableCompiler(@NotNull String binaryPath)
    {
        if (!binaryPath.contains("/") && !binaryPath.contains("\\")) return true;
        try
        {
            return Files.isExecutable(Path.of(binaryPath));
        }
        catch (InvalidPathException ignored)
        {
            return false;
        }
    }

    private @NotNull String nextCompilerName()
    {
        return compilerListModel.isEmpty()
            ? C3SettingsState.DEFAULT_COMPILER_NAME
            : "C3 Compiler " + (compilerListModel.size() + 1);
    }

    private static @NotNull C3SettingsState.CompilerProfile copyProfile(
            @NotNull C3SettingsState.CompilerProfile profile)
    {
        return new C3SettingsState.CompilerProfile(
            nullToEmpty(profile.name),
            nullToEmpty(profile.binaryPath),
            nullToEmpty(profile.version),
            nullToEmpty(profile.stdlibPath)
        );
    }

    private static @NotNull String nullToEmpty(String value)
    {
        return value == null ? "" : value;
    }
}
