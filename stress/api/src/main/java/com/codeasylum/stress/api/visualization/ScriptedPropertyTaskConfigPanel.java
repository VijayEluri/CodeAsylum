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
package com.codeasylum.stress.api.visualization;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.codeasylum.stress.api.ScriptedPropertyTask;
import com.codeasylum.stress.api.TestPlan;
import org.smallmind.swing.ColorUtilities;
import org.smallmind.swing.text.FormulaTextField;

public class ScriptedPropertyTaskConfigPanel extends JPanel implements ItemListener, DocumentListener {

  private TestPlan testPlan;
  private ScriptedPropertyTask task;
  private JTextField keyTextField;
  private FormulaTextField valueTextField;

  public ScriptedPropertyTaskConfigPanel (TestPlan testPlan, ScriptedPropertyTask task) {

    GroupLayout groupLayout;
    JLabel keyLabel;
    JLabel valueLabel;

    this.testPlan = testPlan;
    this.task = task;

    setLayout(groupLayout = new GroupLayout(this));

    keyLabel = new JLabel("Key", JLabel.LEFT);
    keyLabel.setOpaque(true);
    keyLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    keyLabel.setForeground(ColorUtilities.TEXT_COLOR);
    keyLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtilities.TEXT_COLOR), BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR)));

    valueLabel = new JLabel("Script", JLabel.LEFT);
    valueLabel.setOpaque(true);
    valueLabel.setBackground(ColorUtilities.INVERSE_TEXT_COLOR);
    valueLabel.setForeground(ColorUtilities.TEXT_COLOR);
    valueLabel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ColorUtilities.INVERSE_TEXT_COLOR));

    keyTextField = new JTextField((task.getKey() == null) ? "" : task.getKey());
    keyTextField.getDocument().addDocumentListener(this);

    valueTextField = new FormulaTextField(task.getValueAttribute().getScript(), 15, task.getValueAttribute().isFormula());
    valueTextField.addItemListener(this);
    valueTextField.addDocumentListener(this);

    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
      .addGroup(groupLayout.createSequentialGroup().addComponent(keyLabel, 200, 200, 200).addComponent(valueLabel, 200, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      .addGroup(groupLayout.createSequentialGroup().addComponent(keyTextField, 200, 200, 200).addComponent(valueTextField)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
      .addGroup(groupLayout.createParallelGroup().addComponent(keyLabel).addComponent(valueLabel))
      .addGroup(groupLayout.createParallelGroup().addComponent(keyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(valueTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
  }

  @Override
  public void itemStateChanged (ItemEvent itemEvent) {

    task.getValueAttribute().setFormula(itemEvent.getStateChange() == ItemEvent.SELECTED);
    testPlan.setChanged(true);
  }

  private void updateTaskAttribute (DocumentEvent documentEvent) {

    if (documentEvent.getDocument() == keyTextField.getDocument()) {
      task.setKey(keyTextField.getText().trim());
    }
    else {
      task.getValueAttribute().setScript(valueTextField.getText().trim());
    }

    testPlan.setChanged(true);
  }

  @Override
  public synchronized void insertUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute(documentEvent);
  }

  @Override
  public synchronized void removeUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute(documentEvent);
  }

  @Override
  public synchronized void changedUpdate (DocumentEvent documentEvent) {

    updateTaskAttribute(documentEvent);
  }
}
