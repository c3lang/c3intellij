package org.c3lang.intellij.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
	private static final String TARGETS_CARD = "targets";
	private static final String STDLIB_CARD = "stdlib";

	private final @NotNull Project project;
	private final @NotNull C3ProjectService projectService;
	private final @NotNull JTextField versionTextField;
	private final @NotNull JTextField authorsTextField;
	private final @NotNull DefaultListModel<TargetEditorModel> targetListModel = new DefaultListModel<>();
	private final @NotNull JList<TargetEditorModel> targetList = new JList<>(targetListModel);
	private final @NotNull JTextField targetNameTextField = new JTextField();
	private final @NotNull JComboBox<String> targetTypeComboBox = createTargetTypeComboBox(C3ProjectJsonParser.DEFAULT_TARGET_TYPE);
	private final @NotNull JComboBox<C3ProjectJsonParser.OptimizationLevel> targetOptimizationComboBox =
			createOptimizationComboBox("");
	private final @NotNull JComboBox<CompilerOption> compilerComboBox;
	private final @NotNull JCheckBox overrideStdlibCheckBox = new JCheckBox("Override stdlib");
	private final @NotNull TextFieldWithBrowseButton stdlibOverrideField = new TextFieldWithBrowseButton();
	private final @NotNull CardLayout cardLayout = new CardLayout();
	private final @NotNull JPanel cards = new JPanel(cardLayout);
	private boolean updatingTargetFields;
	private JTree navigationTree;

	C3ProjectStructureDialog(@NotNull Project project, @NotNull C3ProjectModel projectModel)
	{
		super(project);
		this.project = project;
		projectService = C3ProjectService.getInstance(project);
		for (C3ProjectJsonParser.TargetDefinition target : projectModel.getTargets())
		{
			targetListModel.addElement(new TargetEditorModel(target));
		}
		initializeTargetEditor();
		if (!targetListModel.isEmpty()) targetList.setSelectedIndex(0);
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
		compilerComboBox = createCompilerComboBox(projectService.getCompilerName());
		overrideStdlibCheckBox.setSelected(projectService.hasStdlibOverride());
		stdlibOverrideField.setText(projectService.hasStdlibOverride() ? projectService.getStdlibOverridePath() : getSelectedCompilerStdlibPath()
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
		DefaultMutableTreeNode targets = page("Targets", TARGETS_CARD);
		DefaultMutableTreeNode stdlib = page("Stdlib", STDLIB_CARD);

		projectSettings.add(project);
		projectSettings.add(targets);
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
		cards.add(createTargetsPage(), TARGETS_CARD);
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

	private @NotNull JComponent createTargetsPage()
	{
		JPanel panel = new JPanel(new BorderLayout(0, 12));
		panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

		JLabel title = new JLabel("Targets");
		title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize2D() + 2));
		panel.add(title, BorderLayout.NORTH);

		JBSplitter splitter = new JBSplitter(false, 0.28f);
		JPanel targetListPanel = new JPanel(new BorderLayout(0, 6));
		targetListPanel.add(new JScrollPane(targetList), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		JButton addButton = new JButton("+");
		addButton.addActionListener(event -> addTarget());
		JButton removeButton = new JButton("-");
		removeButton.addActionListener(event -> removeSelectedTarget());
		toolbar.add(addButton);
		toolbar.add(removeButton);
		targetListPanel.add(toolbar, BorderLayout.SOUTH);

		splitter.setFirstComponent(targetListPanel);

		JPanel targetSettings = new JPanel(new GridBagLayout());
		targetSettings.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		targetSettings.add(new JLabel("Name:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		targetSettings.add(targetNameTextField, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		targetSettings.add(new JLabel("Type:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		targetSettings.add(targetTypeComboBox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		targetSettings.add(new JLabel("Optimization:"), constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		targetSettings.add(targetOptimizationComboBox, constraints);

		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		targetSettings.add(Box.createVerticalGlue(), constraints);
		splitter.setSecondComponent(targetSettings);

		panel.add(splitter, BorderLayout.CENTER);
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
		try
		{
			getTargets();
		}
		catch (IllegalArgumentException e)
		{
			return new ValidationInfo(e.getMessage(), targetNameTextField);
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
			projectService.saveTargets(getTargets());
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

	private void initializeTargetEditor()
	{
		targetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		targetList.addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting())
			{
				resetSelectedTargetEditor();
			}
		});
		targetNameTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				updateSelectedTargetName();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				updateSelectedTargetName();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				updateSelectedTargetName();
			}
		});
		targetTypeComboBox.addActionListener(event -> updateSelectedTargetType());
		targetOptimizationComboBox.addActionListener(event -> updateSelectedTargetOptimization());
		resetSelectedTargetEditor();
	}

	private void resetSelectedTargetEditor()
	{
		TargetEditorModel selected = targetList.getSelectedValue();
		updatingTargetFields = true;
		try
		{
			if (selected == null)
			{
				targetNameTextField.setText("");
				targetTypeComboBox.setSelectedItem(C3ProjectJsonParser.DEFAULT_TARGET_TYPE);
				selectOptimization(targetOptimizationComboBox, "");
			}
			else
			{
				targetNameTextField.setText(selected.name);
				targetTypeComboBox.setSelectedItem(selected.type);
				selectOptimization(targetOptimizationComboBox, selected.optimization);
			}
		}
		finally
		{
			updatingTargetFields = false;
		}
		boolean hasTarget = selected != null;
		targetNameTextField.setEnabled(hasTarget);
		targetTypeComboBox.setEnabled(hasTarget);
		targetOptimizationComboBox.setEnabled(hasTarget);
	}

	private void updateSelectedTargetName()
	{
		if (updatingTargetFields) return;
		TargetEditorModel selected = targetList.getSelectedValue();
		if (selected == null) return;
		selected.name = targetNameTextField.getText();
		targetList.repaint();
	}

	private void updateSelectedTargetType()
	{
		if (updatingTargetFields) return;
		TargetEditorModel selected = targetList.getSelectedValue();
		Object selectedType = targetTypeComboBox.getSelectedItem();
		if (selected == null || !(selectedType instanceof String type)) return;
		selected.type = type;
	}

	private void updateSelectedTargetOptimization()
	{
		if (updatingTargetFields) return;
		TargetEditorModel selected = targetList.getSelectedValue();
		Object selectedOptimization = targetOptimizationComboBox.getSelectedItem();
		if (selected == null || !(selectedOptimization instanceof C3ProjectJsonParser.OptimizationLevel level)) return;
		selected.optimization = level.key();
	}

	private void addTarget()
	{
		TargetDialog dialog = new TargetDialog(project, collectTargetNames());
		if (!dialog.showAndGet()) return;

		TargetEditorModel target = new TargetEditorModel(
			"",
			dialog.getTargetName(),
			dialog.getTargetType(),
			dialog.getOptimization()
		);
		targetListModel.addElement(target);
		targetList.setSelectedIndex(targetListModel.size() - 1);
	}

	private void removeSelectedTarget()
	{
		int selectedIndex = targetList.getSelectedIndex();
		if (selectedIndex < 0) return;

		TargetEditorModel target = targetListModel.getElementAt(selectedIndex);
		int result = Messages.showYesNoDialog(
			project,
			"Remove target '" + target.name.trim() + "'?",
			"Remove Target",
			Messages.getQuestionIcon()
		);
		if (result != Messages.YES) return;

		targetListModel.remove(selectedIndex);
		if (!targetListModel.isEmpty())
		{
			targetList.setSelectedIndex(Math.min(selectedIndex, targetListModel.size() - 1));
		}
		else
		{
			resetSelectedTargetEditor();
		}
	}

	private @NotNull List<C3ProjectJsonParser.TargetDefinition> getTargets()
	{
		List<C3ProjectJsonParser.TargetDefinition> targets = new java.util.ArrayList<>();
		for (int i = 0; i < targetListModel.size(); i++)
		{
			targets.add(targetListModel.getElementAt(i).toTargetDefinition());
		}
		return C3ProjectJsonParser.normalizeTargets(targets);
	}

	private @NotNull List<String> collectTargetNames()
	{
		List<String> names = new java.util.ArrayList<>();
		for (int i = 0; i < targetListModel.size(); i++)
		{
			String name = targetListModel.getElementAt(i).name.trim();
			if (!name.isEmpty()) names.add(name);
		}
		return List.copyOf(names);
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

	private static @NotNull JComboBox<String> createTargetTypeComboBox(@NotNull String selectedType)
	{
		JComboBox<String> comboBox = new JComboBox<>();
		for (String type : C3ProjectJsonParser.TARGET_TYPES)
		{
			comboBox.addItem(type);
		}
		comboBox.setSelectedItem(
			C3ProjectJsonParser.isValidTargetType(selectedType)
				? selectedType.trim()
				: C3ProjectJsonParser.DEFAULT_TARGET_TYPE
		);
		return comboBox;
	}

	private static @NotNull JComboBox<C3ProjectJsonParser.OptimizationLevel> createOptimizationComboBox(
			@NotNull String selectedOptimization)
	{
		JComboBox<C3ProjectJsonParser.OptimizationLevel> comboBox = new JComboBox<>();
		for (C3ProjectJsonParser.OptimizationLevel level : C3ProjectJsonParser.OPTIMIZATION_LEVELS)
		{
			comboBox.addItem(level);
		}
		selectOptimization(comboBox, selectedOptimization);
		return comboBox;
	}

	private static void selectOptimization(
			@NotNull JComboBox<C3ProjectJsonParser.OptimizationLevel> comboBox,
			@NotNull String selectedOptimization)
	{
		String normalizedOptimization = C3ProjectJsonParser.isValidOptimizationLevel(selectedOptimization)
				? selectedOptimization.trim()
				: "";
		for (int i = 0; i < comboBox.getItemCount(); i++)
		{
			if (comboBox.getItemAt(i).key().equals(normalizedOptimization))
			{
				comboBox.setSelectedIndex(i);
				return;
			}
		}
		comboBox.setSelectedIndex(0);
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

	private static final class TargetEditorModel
	{
		private final @NotNull String originalName;
		private @NotNull String name;
		private @NotNull String type;
		private @NotNull String optimization;

		private TargetEditorModel(@NotNull C3ProjectJsonParser.TargetDefinition target)
		{
			this(target.originalName(), target.name(), target.type(), target.optimization());
		}

		private TargetEditorModel(
				@NotNull String originalName,
				@NotNull String name,
				@NotNull String type,
				@NotNull String optimization)
		{
			this.originalName = originalName;
			this.name = name;
			this.type = type;
			this.optimization = optimization;
		}

		private @NotNull C3ProjectJsonParser.TargetDefinition toTargetDefinition()
		{
			return new C3ProjectJsonParser.TargetDefinition(name, type, optimization, originalName);
		}

		@Override
		public String toString()
		{
			String trimmedName = name.trim();
			return trimmedName.isEmpty() ? "(unnamed)" : trimmedName;
		}
	}

	private static final class TargetDialog extends DialogWrapper
	{
		private final @NotNull JTextField nameTextField = new JTextField();
		private final @NotNull JComboBox<String> typeComboBox =
				createTargetTypeComboBox(C3ProjectJsonParser.DEFAULT_TARGET_TYPE);
		private final @NotNull JComboBox<C3ProjectJsonParser.OptimizationLevel> optimizationComboBox =
				createOptimizationComboBox("");
		private final @NotNull List<String> existingNames;

		private TargetDialog(@NotNull Project project, @NotNull List<String> existingNames)
		{
			super(project);
			this.existingNames = existingNames;
			setTitle("Add Target");
			init();
		}

		@Override
		protected @Nullable JComponent createCenterPanel()
		{
			JPanel panel = new JPanel(new GridBagLayout());
			panel.setPreferredSize(new Dimension(420, 130));
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.insets = new Insets(0, 0, 10, 10);
			constraints.anchor = GridBagConstraints.WEST;
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 0;
			constraints.fill = GridBagConstraints.NONE;
			panel.add(new JLabel("Name:"), constraints);

			constraints.gridx = 1;
			constraints.weightx = 1.0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			panel.add(nameTextField, constraints);

			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.weightx = 0;
			constraints.fill = GridBagConstraints.NONE;
			panel.add(new JLabel("Type:"), constraints);

			constraints.gridx = 1;
			constraints.weightx = 1.0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			panel.add(typeComboBox, constraints);

			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.weightx = 0;
			constraints.fill = GridBagConstraints.NONE;
			panel.add(new JLabel("Optimization:"), constraints);

			constraints.gridx = 1;
			constraints.weightx = 1.0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			panel.add(optimizationComboBox, constraints);
			return panel;
		}

		@Override
		public @Nullable JComponent getPreferredFocusedComponent()
		{
			return nameTextField;
		}

		@Override
		protected @Nullable ValidationInfo doValidate()
		{
			String name = getTargetName();
			if (name.isEmpty())
			{
				return new ValidationInfo("Target name must not be empty", nameTextField);
			}
			if (existingNames.contains(name))
			{
				return new ValidationInfo("Duplicate target name: " + name, nameTextField);
			}
			return null;
		}

		private @NotNull String getTargetName()
		{
			return nameTextField.getText().trim();
		}

		private @NotNull String getTargetType()
		{
			Object selected = typeComboBox.getSelectedItem();
			return selected instanceof String type ? type : C3ProjectJsonParser.DEFAULT_TARGET_TYPE;
		}

		private @NotNull String getOptimization()
		{
			Object selected = optimizationComboBox.getSelectedItem();
			return selected instanceof C3ProjectJsonParser.OptimizationLevel level ? level.key() : "";
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
		public @NotNull String toString()
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
