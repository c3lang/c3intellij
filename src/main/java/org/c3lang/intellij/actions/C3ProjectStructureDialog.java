package org.c3lang.intellij.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SimpleTextAttributes;
import org.c3lang.intellij.C3SettingsState;
import org.c3lang.intellij.project.C3ProjectModel;
import org.c3lang.intellij.project.C3ProjectJsonParser;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.List;

final class C3ProjectStructureDialog extends DialogWrapper
{
	private static final String PROJECT_CARD = "project";
	private static final String STDLIB_CARD = "stdlib";

	private final @NotNull C3ProjectService projectService;
	private final @NotNull JTextField versionTextField;
	private final @NotNull JTextField authorsTextField;
	private final @NotNull JComboBox<CompilerOption> compilerComboBox;
	private final @NotNull JCheckBox overrideStdlibCheckBox = new JCheckBox("Override stdlib");
	private final @NotNull TextFieldWithBrowseButton stdlibOverrideField = new TextFieldWithBrowseButton();
	private final @NotNull CardLayout cardLayout = new CardLayout();
	private final @NotNull JPanel cards = new JPanel(cardLayout);
	private JTree navigationTree;

	C3ProjectStructureDialog(@NotNull Project project, @NotNull C3ProjectModel projectModel)
	{
		super(project);
		projectService = C3ProjectService.getInstance(project);
		versionTextField = new JTextField(projectModel.getVersion());
		versionTextField.setColumns("1000.1000.10000".length());
		Dimension versionSize = versionTextField.getPreferredSize();
		versionTextField.setMinimumSize(versionSize);
		versionTextField.setPreferredSize(versionSize);
		versionTextField.setMaximumSize(versionSize);
		authorsTextField = new JTextField(C3ProjectJsonParser.formatAuthorsText(projectModel.getAuthors()));
		authorsTextField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				validateAuthorsOnFocusLost();
			}
		});
		compilerComboBox = createCompilerComboBox(projectModel.getCompilerName());
		overrideStdlibCheckBox.setSelected(projectModel.hasStdlibOverride());
		stdlibOverrideField.setText(
			projectModel.hasStdlibOverride()
				? projectModel.getStdlibOverridePath()
				: getSelectedCompilerStdlibPath()
		);
		stdlibOverrideField.addBrowseFolderListener(new TextBrowseFolderListener(
			FileChooserDescriptorFactory.createSingleFolderDescriptor()
				.withTitle("Select C3 Standard Library Directory")
		));
		compilerComboBox.addActionListener(event -> updateStdlibOverrideFieldState());
		overrideStdlibCheckBox.addActionListener(event -> updateStdlibOverrideFieldState());
		updateStdlibOverrideFieldState();
		setTitle("C3 Project Structure");
		init();
	}

	@Override
	protected @Nullable JComponent createCenterPanel()
	{
		JBSplitter splitter = new JBSplitter(false, 0.18f);
		splitter.setFirstComponent(new JScrollPane(createNavigationTree()));
		splitter.setSecondComponent(createPages());
		splitter.setPreferredSize(new Dimension(900, 520));
		return splitter;
	}

	private @NotNull JTree createNavigationTree()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NavigationNode("", null, true));
		DefaultMutableTreeNode projectSettings = group("Project Settings");
		DefaultMutableTreeNode compilerSettings = group("Compiler Settings");
		DefaultMutableTreeNode project = page("Project", PROJECT_CARD);
		DefaultMutableTreeNode stdlib = page("Stdlib", STDLIB_CARD);

		projectSettings.add(project);
		compilerSettings.add(stdlib);
		root.add(projectSettings);
		root.add(compilerSettings);

		navigationTree = new JTree(new DefaultTreeModel(root));
		navigationTree.setRootVisible(false);
		navigationTree.setShowsRootHandles(false);
		navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		navigationTree.setCellRenderer(new NavigationTreeCellRenderer());
		navigationTree.addTreeSelectionListener(event -> selectNavigationNode());
		navigationTree.expandRow(0);
		navigationTree.expandRow(2);
		navigationTree.setSelectionPath(new TreePath(project.getPath()));
		return navigationTree;
	}

	private @NotNull JComponent createPages()
	{
		cards.add(createProjectPage(), PROJECT_CARD);
		cards.add(createStdlibPage(), STDLIB_CARD);
		cardLayout.show(cards, PROJECT_CARD);
		return cards;
	}

	private @NotNull JComponent createProjectPage()
	{
		JPanel panel = new JPanel(new BorderLayout(0, 12));
		panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

		JLabel title = new JLabel("Project");
		title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize2D() + 2));
		panel.add(title, BorderLayout.NORTH);

		JPanel content = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		content.add(new JLabel("Version:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		content.add(versionTextField, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		content.add(new JLabel("Authors:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		content.add(authorsTextField, constraints);

		panel.add(content, BorderLayout.NORTH);
		return panel;
	}

	private @NotNull JComponent createStdlibPage()
	{
		JPanel panel = new JPanel(new BorderLayout(0, 12));
		panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

		JLabel title = new JLabel("Stdlib");
		title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize2D() + 2));
		panel.add(title, BorderLayout.NORTH);

		JPanel content = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		content.add(new JLabel("C3 compiler:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		content.add(compilerComboBox, constraints);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		content.add(overrideStdlibCheckBox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		content.add(new JLabel("Stdlib location:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		content.add(stdlibOverrideField, constraints);

		panel.add(content, BorderLayout.NORTH);
		return panel;
	}

	private void selectNavigationNode()
	{
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null) return;

		NavigationNode navigationNode = (NavigationNode) node.getUserObject();
		if (navigationNode.isGroup())
		{
			if (node.getChildCount() > 0)
			{
				navigationTree.setSelectionPath(new TreePath(((DefaultMutableTreeNode) node.getChildAt(0)).getPath()));
			}
			return;
		}
		cardLayout.show(cards, navigationNode.card());
	}

	private @Nullable DefaultMutableTreeNode getSelectedNode()
	{
		TreePath path = navigationTree.getSelectionPath();
		if (path == null) return null;
		Object component = path.getLastPathComponent();
		return component instanceof DefaultMutableTreeNode node ? node : null;
	}

	@Override
	public @Nullable JComponent getPreferredFocusedComponent()
	{
		return versionTextField;
	}

	@Override
	protected @Nullable ValidationInfo doValidate()
	{
		try
		{
			getAuthors();
		}
		catch (IllegalArgumentException e)
		{
			return new ValidationInfo(e.getMessage(), authorsTextField);
		}
		if (overrideStdlibCheckBox.isSelected() && stdlibOverrideField.getText().trim().isEmpty())
		{
			return new ValidationInfo("Stdlib override path must not be empty.", stdlibOverrideField);
		}
		return null;
	}

	@Override
	protected void doOKAction()
	{
		try
		{
			projectService.saveProjectSettings(versionTextField.getText(), getAuthors());
			projectService.saveCompilerSettings(getSelectedCompilerName(), getStdlibOverridePath());
			super.doOKAction();
		}
		catch (IllegalArgumentException e)
		{
			setErrorText(e.getMessage(), authorsTextField);
		}
		catch (IOException e)
		{
			setErrorText("Unable to save project.json: " + e.getMessage(), authorsTextField);
		}
	}

	private void validateAuthorsOnFocusLost()
	{
		try
		{
			getAuthors();
			setErrorText(null);
		}
		catch (IllegalArgumentException e)
		{
			setErrorText(e.getMessage(), authorsTextField);
		}
	}

	private @NotNull List<String> getAuthors()
	{
		return C3ProjectJsonParser.parseAuthorsText(authorsTextField.getText());
	}

	private @NotNull String getSelectedCompilerName()
	{
		Object selected = compilerComboBox.getSelectedItem();
		if (selected instanceof CompilerOption option)
		{
			return option.name();
		}
		return "";
	}

	private @NotNull String getSelectedCompilerStdlibPath()
	{
		Object selected = compilerComboBox.getSelectedItem();
		if (selected instanceof CompilerOption option)
		{
			return option.stdlibPath();
		}
		return "";
	}

	private @NotNull String getStdlibOverridePath()
	{
		return overrideStdlibCheckBox.isSelected() ? stdlibOverrideField.getText().trim() : "";
	}

	private void updateStdlibOverrideFieldState()
	{
		boolean override = overrideStdlibCheckBox.isSelected();
		if (!override)
		{
			stdlibOverrideField.setText(getSelectedCompilerStdlibPath());
		}
		stdlibOverrideField.setEnabled(override);
	}

	private static @NotNull JComboBox<CompilerOption> createCompilerComboBox(@NotNull String selectedCompilerName)
	{
		JComboBox<CompilerOption> comboBox = new JComboBox<>();
		List<C3SettingsState.CompilerProfile> profiles = C3SettingsState.getInstance().getCompilerProfiles();
		for (C3SettingsState.CompilerProfile profile : profiles)
		{
			comboBox.addItem(new CompilerOption(profile.name, profile.stdlibPath));
		}
		if (comboBox.getItemCount() == 0)
		{
			comboBox.addItem(new CompilerOption(C3SettingsState.DEFAULT_COMPILER_NAME, ""));
		}
		for (int i = 0; i < comboBox.getItemCount(); i++)
		{
			CompilerOption option = comboBox.getItemAt(i);
			if (option.name().equals(selectedCompilerName))
			{
				comboBox.setSelectedIndex(i);
				return comboBox;
			}
		}
		comboBox.setSelectedIndex(0);
		return comboBox;
	}

	private record CompilerOption(@NotNull String name, @NotNull String stdlibPath)
	{
		@Override
		public String toString()
		{
			return name;
		}
	}

	private static @NotNull DefaultMutableTreeNode group(@NotNull String name)
	{
		return new DefaultMutableTreeNode(new NavigationNode(name, null, true));
	}

	private static @NotNull DefaultMutableTreeNode page(@NotNull String name, @NotNull String card)
	{
		return new DefaultMutableTreeNode(new NavigationNode(name, card, false));
	}

	private record NavigationNode(@NotNull String name, @Nullable String card, boolean isGroup)
	{
		@Override
		public String toString()
		{
			return name;
		}
	}

	private static final class NavigationTreeCellRenderer extends ColoredTreeCellRenderer
	{
		@Override
		public void customizeCellRenderer(
				@NotNull JTree tree,
				Object value,
				boolean selected,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus)
		{
			if (!(value instanceof DefaultMutableTreeNode treeNode)
				|| !(treeNode.getUserObject() instanceof NavigationNode node))
			{
				append(String.valueOf(value));
				return;
			}

			SimpleTextAttributes attributes = node.isGroup()
				? SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES
				: SimpleTextAttributes.REGULAR_ATTRIBUTES;
			append(node.name(), attributes);
		}
	}
}
