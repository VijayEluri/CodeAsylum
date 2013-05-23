/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011, 10212, 2013 David Berkman
 * 
 * This file is part of the CodeAsylum Code Project.
 * 
 * The CodeAsylum Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The CodeAsylum Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with The CodeAsylum Code Project. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under the GNU Affero GPL version 3 section 7
 * ------------------------------------------------------------------
 * If you modify this Program, or any covered work, by linking or
 * combining it with other code, such other code is not for that reason
 * alone subject to any of the requirements of the GNU Affero GPL
 * version 3.
 */
package com.codeasylum.liquibase;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.ParserConfigurationException;
import com.codeasylum.liquibase.menu.LiquidateMenuHandler;
import org.smallmind.liquibase.spring.Goal;
import org.smallmind.liquibase.spring.Source;
import org.smallmind.liquibase.spring.SpringLiquibase;
import org.smallmind.nutsnbolts.io.StenographWriter;
import org.smallmind.nutsnbolts.lang.FormattedRuntimeException;
import org.smallmind.nutsnbolts.lang.UnknownSwitchCaseException;
import org.smallmind.nutsnbolts.layout.Alignment;
import org.smallmind.nutsnbolts.layout.Constraint;
import org.smallmind.nutsnbolts.layout.Gap;
import org.smallmind.nutsnbolts.layout.Justification;
import org.smallmind.nutsnbolts.layout.ParallelBox;
import org.smallmind.nutsnbolts.layout.SerialBox;
import org.smallmind.nutsnbolts.util.EnumerationIterator;
import org.smallmind.nutsnbolts.util.StringUtilities;
import org.smallmind.persistence.sql.DriverManagerDataSource;
import org.smallmind.swing.button.EventCoalescingButtonGroup;
import org.smallmind.swing.button.GroupedActionEvent;
import org.smallmind.swing.dialog.InfoDialog;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.dialog.WarningDialog;
import org.smallmind.swing.file.DirectoryChooserDialog;
import org.smallmind.swing.file.FileChooserDialog;
import org.smallmind.swing.file.FileChooserState;
import org.smallmind.swing.layout.ParaboxLayoutManager;
import org.smallmind.swing.menu.MenuDelegateFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

public class Liquidate extends JFrame implements ActionListener, ItemListener, DocumentListener {

  private static final ImageIcon BROWSE_ICON = new ImageIcon(ClassLoader.getSystemResource("com/codeasylum/liquibase/folder_view_16.png"));
  private ExtendedProfileLoader extensionLoader;
  private LiquidateConfig config;
  private MenuDelegateFactory menuDelegateFactory;
  private JButton browseButton;
  private JButton startButton;
  private JComboBox databaseCombo;
  private EventCoalescingButtonGroup sourceButtonGroup;
  private EventCoalescingButtonGroup goalButtonGroup;
  private JPasswordField passwordField;
  private JTextField hostTextField;
  private JTextField portTextField;
  private JTextField schemaTextField;
  private JTextField userTextField;
  private JTextField changeLogTextField;
  private JTextField outputTextField;
  private boolean changeSensitive = true;

  public Liquidate () {

    super("Liquidate");

    ParaboxLayoutManager layout;
    ParallelBox goalHorizontalBox;
    SerialBox sourceHorizontalBox;
    ParallelBox sourceVerticalBox;
    SerialBox goalVerticalBox;
    JSeparator buttonSeparator;
    JRadioButton[] sourceButtons;
    JRadioButton[] goalButtons;
    JLabel databaseLabel;
    JLabel hostLabel;
    JLabel colonLabel;
    JLabel schemaLabel;
    JLabel userLabel;
    JLabel passwordLabel;
    JLabel sourceLabel;
    JLabel goalLabel;
    JLabel outputLabel;
    int sourceIndex = 0;
    int goalIndex = 0;

    config = new LiquidateConfig();

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(layout = new ParaboxLayoutManager(getContentPane()));

    databaseLabel = new JLabel("Database:");
    databaseCombo = new JComboBox(Database.values());
    databaseCombo.addItemListener(this);

    hostLabel = new JLabel("Host and Port:");
    hostTextField = new JTextField();
    hostTextField.getDocument().addDocumentListener(this);

    portTextField = new JTextField();
    portTextField.setHorizontalAlignment(JTextField.RIGHT);
    portTextField.setPreferredSize(new Dimension(50, (int)portTextField.getPreferredSize().getHeight()));
    portTextField.setMaximumSize(portTextField.getPreferredSize());
    portTextField.getDocument().addDocumentListener(this);
    colonLabel = new JLabel(":");

    schemaLabel = new JLabel("Schema:");
    schemaTextField = new JTextField();
    schemaTextField.getDocument().addDocumentListener(this);

    userLabel = new JLabel("User:");
    userTextField = new JTextField();
    userTextField.getDocument().addDocumentListener(this);

    passwordLabel = new JLabel("Password:");
    passwordField = new JPasswordField();
    passwordField.getDocument().addDocumentListener(this);

    sourceLabel = new JLabel("Change Log:");
    sourceButtonGroup = new EventCoalescingButtonGroup();
    sourceButtons = new JRadioButton[Source.values().length];
    for (Source source : Source.values()) {
      sourceButtonGroup.add(sourceButtons[sourceIndex] = new JRadioButton(StringUtilities.toDisplayCase(source.name(), '_')));
      sourceButtons[sourceIndex++].setActionCommand(source.name());
    }
    sourceButtons[0].setSelected(true);
    sourceButtonGroup.addActionListener(this);

    changeLogTextField = new JTextField();
    changeLogTextField.getDocument().addDocumentListener(this);

    goalLabel = new JLabel("Goal:");
    goalButtonGroup = new EventCoalescingButtonGroup();
    goalButtons = new JRadioButton[Goal.values().length - 1];
    for (Goal goal : Goal.values()) {
      if (!goal.equals(Goal.NONE)) {
        goalButtonGroup.add(goalButtons[goalIndex] = new JRadioButton(StringUtilities.toDisplayCase(goal.name(), '_')));
        goalButtons[goalIndex++].setActionCommand(goal.name());
      }
    }
    goalButtons[0].setSelected(true);
    goalButtonGroup.addActionListener(this);

    outputLabel = new JLabel("Output:");
    browseButton = new JButton("Browse...", BROWSE_ICON);
    browseButton.setMargin(new Insets(2, 2, 2, 2));
    browseButton.setFocusable(false);
    browseButton.setToolTipText("browse for a file");
    browseButton.addActionListener(this);
    browseButton.setEnabled(false);
    outputTextField = new JTextField();
    outputTextField.getDocument().addDocumentListener(this);
    outputTextField.setEnabled(false);

    buttonSeparator = new JSeparator(JSeparator.HORIZONTAL);
    buttonSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)buttonSeparator.getPreferredSize().getHeight()));

    startButton = new JButton("Start");
    startButton.addActionListener(this);

    layout.setHorizontalBox(layout.parallelBox()
      .add(layout.sequentialBox().add(layout.parallelBox(Alignment.TRAILING)
        .add(databaseLabel).add(hostLabel).add(schemaLabel).add(userLabel).add(passwordLabel).add(sourceLabel).add(goalLabel).add(outputLabel))
        .add(goalHorizontalBox = layout.parallelBox().add(databaseCombo, Constraint.expand())
          .add(layout.sequentialBox(3).add(hostTextField, Constraint.expand()).add(colonLabel).add(portTextField))
          .add(schemaTextField, Constraint.expand()).add(userTextField, Constraint.expand()).add(passwordField, Constraint.expand())
          .add(sourceHorizontalBox = layout.sequentialBox()).add(changeLogTextField, Constraint.expand())
          .add(layout.parallelBox(Alignment.TRAILING).add(outputTextField, Constraint.expand()).add(browseButton))))
      .add(buttonSeparator, Constraint.expand()).add(layout.sequentialBox(Justification.LAST, true).add(startButton)));

    for (JRadioButton sourceButton : sourceButtons) {
      sourceHorizontalBox.add(sourceButton);
    }

    for (JRadioButton goalButton : goalButtons) {
      goalHorizontalBox.add(goalButton);
    }

    layout.setVerticalBox(layout.sequentialBox()
      .add(layout.sequentialBox()
        .add(layout.parallelBox(Alignment.BASELINE).add(databaseLabel).add(databaseCombo))
        .add(layout.parallelBox(Alignment.BASELINE).add(hostLabel).add(hostTextField).add(colonLabel).add(portTextField))
        .add(layout.parallelBox(Alignment.BASELINE).add(schemaLabel).add(schemaTextField))
        .add(layout.parallelBox(Alignment.BASELINE).add(userLabel).add(userTextField))
        .add(layout.parallelBox(Alignment.BASELINE).add(passwordLabel).add(passwordField)))
      .add(layout.sequentialBox(3)
        .add(layout.parallelBox(Alignment.CENTER).add(sourceLabel).add(sourceVerticalBox = layout.parallelBox()))
        .add(changeLogTextField))
      .add(goalVerticalBox = layout.sequentialBox(Gap.NONE)
        .add(layout.parallelBox(Alignment.BASELINE).add(goalLabel).add(goalButtons[0]))));

    for (JRadioButton sourceButton : sourceButtons) {
      sourceVerticalBox.add(sourceButton);
    }

    for (int count = 1; count < goalButtons.length; count++) {
      goalVerticalBox.add(goalButtons[count]);
    }

    layout.getVerticalBox()
      .add(layout.sequentialBox(Gap.RELATED).add(layout.parallelBox(Alignment.BASELINE).add(outputLabel).add(outputTextField)).add(browseButton))
      .add(layout.sequentialBox(Gap.RELATED).add(buttonSeparator).add(startButton));

    setSize(new Dimension(((int)getLayout().preferredLayoutSize(this).getWidth()) + 150, ((int)getLayout().preferredLayoutSize(this).getHeight()) + 50));
    setResizable(false);
    setLocationByPlatform(true);
  }

  public static void main (String... args) {

    ExtendedProfileLoader extensionLoader = null;
    boolean init = false;

    try {
      extensionLoader = new ExtendedProfileLoader();
      init = true;
    }
    catch (Exception exception) {
      JavaErrorDialog.showJavaErrorDialog(null, null, exception);
    }

    if (init) {

      final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/codeasylum/liquibase/liquidate.xml");

      Liquidate liquidate = applicationContext.getBean("liquidate", Liquidate.class);

      try {
        liquidate.addWindowListener(new WindowAdapter() {

          @Override
          public void windowClosed (WindowEvent windowEvent) {

            applicationContext.close();

          }
        });

        liquidate.init(extensionLoader).setVisible(true);
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog(liquidate, liquidate, exception);
        liquidate.dispose();
      }
    }
  }

  private Liquidate init (ExtendedProfileLoader extensionLoader)
    throws IOException, SAXException, ParserConfigurationException {

    this.extensionLoader = extensionLoader;

    new LiquidateMenuHandler(this, menuDelegateFactory);

    return this;
  }

  public LiquidateConfig getConfig () {

    return config;
  }

  public synchronized void setConfig (LiquidateConfig config) {

    this.config = config;

    changeSensitive = false;
    try {
      databaseCombo.setSelectedItem((config.getDatabase() == null) ? Database.MYSQL : config.getDatabase());
      hostTextField.setText((config.getHost() == null) ? "" : config.getHost());
      portTextField.setText((config.getPort() == 0) ? "" : String.valueOf(config.getPort()));
      schemaTextField.setText((config.getSchema() == null) ? "" : config.getSchema());
      userTextField.setText((config.getUser() == null) ? "" : config.getUser());
      passwordField.setText((config.getPassword() == null) ? "" : config.getPassword());

      for (AbstractButton button : new EnumerationIterator<AbstractButton>(sourceButtonGroup.getElements())) {
        if (button.getActionCommand().equals((config.getSource() == null) ? Source.FILE.name() : config.getSource().name())) {
          sourceButtonGroup.setSelected(button.getModel(), true);
          break;
        }
      }

      changeLogTextField.setText((config.getChangeLog() == null) ? "" : config.getChangeLog());

      for (AbstractButton button : new EnumerationIterator<AbstractButton>(goalButtonGroup.getElements())) {
        if (button.getActionCommand().equals((config.getGoal() == null) ? Goal.PREVIEW.name() : config.getGoal().name())) {
          goalButtonGroup.setSelected(button.getModel(), true);
          break;
        }
      }

      outputTextField.setText(config.getOutput());
      outputTextField.setEnabled(config.getGoal().equals(Goal.GENERATE) || config.getGoal().equals(Goal.DOCUMENT));
      browseButton.setEnabled(config.getGoal().equals(Goal.GENERATE) || config.getGoal().equals(Goal.DOCUMENT));
    }
    finally {
      changeSensitive = true;
    }
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent instanceof GroupedActionEvent) {
      if (changeSensitive) {
        if (((GroupedActionEvent)actionEvent).getButtonGroup() == sourceButtonGroup) {
          config.setSource(Source.valueOf(sourceButtonGroup.getSelection().getActionCommand()));
        }
        else if (((GroupedActionEvent)actionEvent).getButtonGroup() == goalButtonGroup) {

          Goal goal;

          config.setGoal(goal = Goal.valueOf(goalButtonGroup.getSelection().getActionCommand()));
          outputTextField.setEnabled(goal.equals(Goal.GENERATE) || goal.equals(Goal.DOCUMENT));
          browseButton.setEnabled(goal.equals(Goal.GENERATE) || goal.equals(Goal.DOCUMENT));

          outputTextField.setText("");
          config.setOutput("");
        }
      }
    }
    else if (actionEvent.getSource() == browseButton) {

      Goal goal;

      switch (goal = Goal.valueOf(goalButtonGroup.getSelection().getActionCommand())) {
        case NONE:
          throw new FormattedRuntimeException("There should be no browse functionality for goal(%s)", goal.name());
        case PREVIEW:
          throw new FormattedRuntimeException("There should be no browse functionality for goal(%s)", goal.name());
        case DOCUMENT:

          File directory;

          if ((directory = DirectoryChooserDialog.showDirectoryChooserDialog(this)) != null) {
            outputTextField.setText(directory.getAbsolutePath());
          }
          break;
        case GENERATE:

          File file;

          if ((file = FileChooserDialog.showFileChooserDialog(this, FileChooserState.OPEN)) != null) {
            outputTextField.setText(file.getAbsolutePath());
          }
          break;
        case UPDATE:
          throw new FormattedRuntimeException("There should be no browse functionality for goal(%s)", goal.name());
        default:
          throw new UnknownSwitchCaseException(goal.name());
      }
    }
    else if (actionEvent.getSource() == startButton) {

      SpringLiquibase springLiquibase;
      Database database;
      Goal goal;
      boolean outputValidated = true;

      springLiquibase = new SpringLiquibase(extensionLoader.getClassLoader());
      springLiquibase.setGoal(goal = Goal.valueOf(goalButtonGroup.getSelection().getActionCommand()));

      switch (goal) {
        case NONE:
          break;
        case PREVIEW:
          break;
        case DOCUMENT:
          if ((config.getOutput() != null) && (config.getOutput().length() > 0)) {

            File file;

            if (!(file = new File(config.getOutput())).isDirectory()) {
              outputValidated = false;
              WarningDialog.showWarningDialog(this, "Liquibase documentation requires that an output location be an existing folder");
            }
            else {
              springLiquibase.setOutputDir(file.getAbsolutePath());
            }
          }
          break;
        case GENERATE:
          if ((config.getOutput() == null) || (config.getOutput().length() == 0)) {
            outputValidated = false;
            WarningDialog.showWarningDialog(this, "Liquibase state generation requires an output file");
          }
          else if (!config.getOutput().contains(System.getProperty("file.separator"))) {
            outputValidated = false;
            WarningDialog.showWarningDialog(this, "Liquibase state generation requires that the output location refer to a file, and not just a folder");
          }
          else {

            File file = new File(config.getOutput());

            if (!file.getParentFile().isDirectory()) {
              outputValidated = false;
              WarningDialog.showWarningDialog(this, "Liquibase state generation requires that an output location refer to an existing folder");
            }
            else {
              springLiquibase.setOutputDir(file.getParent());
              springLiquibase.setOutputLog(file.getName());
            }
          }
          break;
        case UPDATE:
          break;
        default:
          throw new UnknownSwitchCaseException(goal.name());
      }

      if (outputValidated) {

        if (goal.equals(Goal.PREVIEW)) {

          StenographWriter previewWriter;

          springLiquibase.setPreviewWriter(previewWriter = new StenographWriter());
          PreviewDialog.showPreviewDialog(this, previewWriter);
        }

        springLiquibase.setSource(Source.valueOf(sourceButtonGroup.getSelection().getActionCommand()));
        springLiquibase.setChangeLog(changeLogTextField.getText());

        database = (Database)databaseCombo.getSelectedItem();

        try {
          springLiquibase.setDataSource(new DriverManagerDataSource(database.getDriver().getName(), database.getUrl(hostTextField.getText(), portTextField.getText(), schemaTextField.getText()), userTextField.getText(), new String(passwordField.getPassword())));
          springLiquibase.afterPropertiesSet();

          InfoDialog.showInfoDialog(this, "Liquibase update completed...");
        }
        catch (Exception exception) {
          JavaErrorDialog.showJavaErrorDialog(this, this, exception);
        }
      }
    }
  }

  @Override
  public synchronized void itemStateChanged (ItemEvent itemEvent) {

    if (changeSensitive) {
      config.setDatabase((Database)databaseCombo.getSelectedItem());
    }
  }

  private void documentUpdate (DocumentEvent documentEvent) {

    if (changeSensitive) {
      if (documentEvent.getDocument() == hostTextField.getDocument()) {
        config.setHost(hostTextField.getText());
      }
      else if (documentEvent.getDocument() == portTextField.getDocument() && (portTextField.getText() != null) && (portTextField.getText().length() > 0)) {
        config.setPort(Integer.parseInt(portTextField.getText()));
      }
      else if ((documentEvent.getDocument() == schemaTextField.getDocument())) {
        config.setSchema(schemaTextField.getText());
      }
      else if (documentEvent.getDocument() == userTextField.getDocument()) {
        config.setUser(userTextField.getText());
      }
      else if (documentEvent.getDocument() == passwordField.getDocument()) {
        config.setPassword(new String(passwordField.getPassword()));
      }
      else if (documentEvent.getDocument() == changeLogTextField.getDocument()) {
        config.setChangeLog(changeLogTextField.getText());
      }
      else if (documentEvent.getDocument() == outputTextField.getDocument()) {
        config.setOutput(outputTextField.getText());
      }
    }
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    documentUpdate(documentEvent);
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    documentUpdate(documentEvent);
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    documentUpdate(documentEvent);
  }

  public synchronized void setMenuDelegateFactory (MenuDelegateFactory menuDelegateFactory) {

    this.menuDelegateFactory = menuDelegateFactory;
  }
}
