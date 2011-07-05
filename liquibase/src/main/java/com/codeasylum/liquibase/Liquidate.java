/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011 David Berkman
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
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
import org.smallmind.liquibase.spring.Goal;
import org.smallmind.liquibase.spring.Source;
import org.smallmind.liquibase.spring.SpringLiquibase;
import org.smallmind.nutsnbolts.util.EnumerationIterator;
import org.smallmind.nutsnbolts.util.StringUtilities;
import org.smallmind.persistence.orm.sql.DriverManagerDataSource;
import org.smallmind.swing.button.EventCoalescingButtonGroup;
import org.smallmind.swing.button.GroupedActionEvent;
import org.smallmind.swing.dialog.JavaErrorDialog;
import org.smallmind.swing.menu.MenuDelegateFactory;
import org.smallmind.swing.menu.MenuHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Liquidate extends JFrame implements ActionListener, ItemListener, DocumentListener {

  private LiquidateConfig config;
  private MenuDelegateFactory menuDelegateFactory;
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
  private boolean changed;

  public Liquidate () {

    super("Liquidate");

    GroupLayout layout;
    GroupLayout.ParallelGroup sourceVerticalGroup;
    GroupLayout.ParallelGroup goalHorizontalGroup;
    GroupLayout.SequentialGroup sourceHorizontalGroup;
    GroupLayout.SequentialGroup goalVerticalGroup;
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
    int sourceIndex = 0;
    int goalIndex = 0;

    config = new LiquidateConfig();
    changed = false;

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(layout = new GroupLayout(getContentPane()));

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

    buttonSeparator = new JSeparator(JSeparator.HORIZONTAL);
    buttonSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)buttonSeparator.getPreferredSize().getHeight()));

    startButton = new JButton("Start");
    startButton.addActionListener(this);

    layout.setAutoCreateContainerGaps(true);

    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
      .addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        .addGroup(layout.createSequentialGroup().addComponent(databaseLabel).addGap(10))
        .addGroup(layout.createSequentialGroup().addComponent(hostLabel).addGap(10))
        .addGroup(layout.createSequentialGroup().addComponent(schemaLabel).addGap(10))
        .addGroup(layout.createSequentialGroup().addComponent(userLabel).addGap(10))
        .addGroup(layout.createSequentialGroup().addComponent(passwordLabel).addGap(10))
        .addGroup(layout.createSequentialGroup().addComponent(sourceLabel).addGap(10))
        .addGroup(layout.createSequentialGroup().addComponent(goalLabel).addGap(10)))
        .addGroup(goalHorizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(databaseCombo)
          .addGroup(layout.createSequentialGroup().addComponent(hostTextField).addGap(2).addComponent(colonLabel).addGap(2).addComponent(portTextField))
          .addComponent(schemaTextField).addComponent(userTextField).addComponent(passwordField)
          .addGroup(sourceHorizontalGroup = layout.createSequentialGroup()).addComponent(changeLogTextField)))
      .addComponent(buttonSeparator).addComponent(startButton));

    for (JRadioButton sourceButton : sourceButtons) {
      sourceHorizontalGroup.addComponent(sourceButton);
    }

    for (JRadioButton goalButton : goalButtons) {
      goalHorizontalGroup.addComponent(goalButton);
    }

    layout.setVerticalGroup(goalVerticalGroup = layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(databaseLabel).addComponent(databaseCombo)).addGap(8)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(hostLabel).addComponent(hostTextField).addComponent(colonLabel).addComponent(portTextField)).addGap(8)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(schemaLabel).addComponent(schemaTextField)).addGap(8)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(userLabel).addComponent(userTextField)).addGap(8)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(passwordLabel).addComponent(passwordField)).addGap(8)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(sourceLabel).addGroup(sourceVerticalGroup = layout.createParallelGroup())).addGap(8)
      .addComponent(changeLogTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addGap(8)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(goalLabel).addComponent(goalButtons[0])));

    for (JRadioButton sourceButton : sourceButtons) {
      sourceVerticalGroup.addComponent(sourceButton);
    }

    for (int count = 1; count < goalButtons.length; count++) {
      goalVerticalGroup.addComponent(goalButtons[count]);
    }

    goalVerticalGroup.addGap(15).addComponent(buttonSeparator).addGap(8).addComponent(startButton);

    setSize(new Dimension(((int)getLayout().preferredLayoutSize(this).getWidth()) + 150, ((int)getLayout().preferredLayoutSize(this).getHeight()) + 50));
    setResizable(false);
    setLocationByPlatform(true);
  }

  private Liquidate init ()
    throws IOException, SAXException, ParserConfigurationException {

    new MenuHandler(this, menuDelegateFactory, new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/codeasylum/liquibase/menu.xml")));

    return this;
  }

  public boolean isChanged () {

    return changed;
  }

  public LiquidateConfig getConfig () {

    return config;
  }

  public void setConfig (LiquidateConfig config) {

    this.config = config;

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

    changed = false;
  }

  @Override
  public synchronized void actionPerformed (ActionEvent actionEvent) {

    if (actionEvent instanceof GroupedActionEvent) {
      if (((GroupedActionEvent)actionEvent).getButtonGroup() == sourceButtonGroup) {
        config.setSource(Source.valueOf(sourceButtonGroup.getSelection().getActionCommand()));
        changed = true;
      }
      else if (((GroupedActionEvent)actionEvent).getButtonGroup() == goalButtonGroup) {
        config.setGoal(Goal.valueOf(goalButtonGroup.getSelection().getActionCommand()));
        changed = true;
      }
    }
    else if (actionEvent.getSource() == startButton) {

      SpringLiquibase springLiquibase;
      Database database;

      springLiquibase = new SpringLiquibase();
      springLiquibase.setSource(Source.valueOf(sourceButtonGroup.getSelection().getActionCommand()));
      springLiquibase.setChangeLog(changeLogTextField.getText());
      springLiquibase.setGoal(Goal.valueOf(goalButtonGroup.getSelection().getActionCommand()));

      database = (Database)databaseCombo.getSelectedItem();

      try {
        springLiquibase.setDataSource(new DriverManagerDataSource(database.getDriver().getName(), database.getUrl(hostTextField.getText(), portTextField.getText(), schemaTextField.getText()), userTextField.getText(), new String(passwordField.getPassword())));
        springLiquibase.afterPropertiesSet();
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog(this, this, exception);
      }
    }
  }

  @Override
  public synchronized void itemStateChanged (ItemEvent itemEvent) {

    config.setDatabase((Database)databaseCombo.getSelectedItem());
    changed = true;
  }

  private void documentUpdate (DocumentEvent documentEvent) {

    if (documentEvent.getDocument() == hostTextField.getDocument()) {
      config.setHost(hostTextField.getText());
    }
    else if (documentEvent.getDocument() == portTextField.getDocument()) {
      config.setPort(Integer.parseInt(portTextField.getText()));
    }
    else if (documentEvent.getDocument() == schemaTextField.getDocument()) {
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

    changed = true;
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

  public static void main (String... args) {

    boolean init = false;

    try {
      new ExtendedProfileLoader();
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

        liquidate.init().setVisible(true);
      }
      catch (Exception exception) {
        JavaErrorDialog.showJavaErrorDialog(liquidate, liquidate, exception);
        liquidate.dispose();
      }
    }
  }
}
